package hattrick;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.jsoup.select.Selector.SelectorParseException;

public class HattrickCalculator {

	public static final String hattrickCookie = "B79D0110E2DA2B22A8FA1C63A11976DCA43645D56293D6031501187FF5D03C6421E42FF6E3F5CA203A6FA1A6232837C487DF594D5485821CEDC4D38D389C6EF0B6A1809537DE161414BF170F9878591324909E915F3EC52E721243582FC78269CBECF74807123B9CF4D1F2CE3ED2C14A0C5B9EF0";
	public static final String hattrickLoginUrl = "https://www.hattrick.org";
	public List<String> pastPlayers = new ArrayList<>();
	public Map<String,Player> playersMap = new HashMap<String,Player>();
	private HashMap<String, Map<String,Position>> positionsArchive = new HashMap<String, Map<String,Position>>(); // <playerId<'dd-MM-yyyy',Position>>
	private HashMap<Integer,Player> injured = new HashMap<Integer,Player>(); // map event-id, player injured. nulled at the end of each match
	private List<MatchFragment> fragments = new ArrayList<>();
	
	public void mungeMatches() throws IOException {
		
		try{
			int leagueId=724; //serieA
			
			for (leagueId=724;leagueId<=744-20;leagueId++) {
				Document calendar = 
	    				Jsoup.connect(hattrickLoginUrl + "/World/Series/Fixtures.aspx?LeagueLevelUnitID="+leagueId+"&Season=69&TeamId=-1")
	    				.cookie("hattrick.org", hattrickCookie)
	    				.get();
	    		
	//    		System.out.println(calendar.html());
	    		Elements calendar_links = calendar.select("a[href^='/Club/Matches/Match.']"); // get match links
	
	    		int match_counter = 0;
	    		match:
	            for (Element link : calendar_links) {
	            	match_counter++;
	            	System.out.println("\n SERIE "+leagueId+" MATCH " + match_counter+ ": " + link.outerHtml());
	            	Document match = Jsoup.connect(hattrickLoginUrl + link.attr("href"))
	            			.cookie("hattrick.org", hattrickCookie)
	            			.get();
	            	
	            	if (thereIsABot(match)) {
	            		System.out.println("\n Skipping match because of the presence of bots");
	            		continue match;
	            	}
	            	
	//            	System.out.println(match.html());
	            	String matchDateString = match.select("div[class^='byline']").text();
	//            	System.out.println("matchDate: " + matchDateString);
	            	matchDateString = matchDateString.split(" ")[1];
	//            	System.out.println("matchDate: " + matchDateString);
	            	String[] splits = matchDateString.split("-");
	            	String matchDate = LocalDate.of(Integer.valueOf(splits[2]), Integer.valueOf(splits[1]), Integer.valueOf(splits[0])).format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
	            	
	            	// get all events
	            	Elements events = match.select("span[class^='matchevent']"); //[data-eventtype^='"+Event.LINEUPS.getCode()+"_']");
	//            	System.out.println(events.size());
	            	
	            	MatchFragment currentFragment = new MatchFragment(0);
	            	
	            	for (Element event : events) {
	            		
	            		String code = event.attr("data-eventtype").split("_")[0];
	            		Event eventEnum = Event.getFromCode(code);
	            		
	            		// Lineups
	            		if (eventEnum == Event.LINEUPS) {
	                    	List<Player> players = getPlayersFromEvent(event, matchDate);
	            			if (players.contains(null)) {
	            				System.out.println("Skipping match because an old player is present");
	            				resetMatchStuffs();
	            				continue match; // old player is present
	            			}

	                    	for (Player player : players) {
	                			currentFragment.addPyr(player);
	                    	}
	            		} 
	            		
	            		// Walkover
	            		if (   eventEnum == Event.WALKOVER_BOTH
	        				|| eventEnum == Event.WALKOVER_AWAY
	        				|| eventEnum == Event.WALKOVER_HOME) {
	            			
	            			continue match;
	            		}
	            		
	            		// Injury
	            		else if (eventEnum!= null && isInjury(eventEnum.getCode())) {
	            			
	            			//get involved players
	            			Integer eventId = Integer.valueOf(event.attr("data-eventindex"));
	            			List<Player> pyrs = getPlayersFromEvent(event, matchDate);
	            			if (pyrs.contains(null)) {
	            				System.out.println("Skipping match because an old player is present");
	            				resetMatchStuffs();
	            				continue match; // old player is present
	            			}
	            			
	            			if(pyrs.size() != 1) System.out.println("ERROR - injured "+pyrs.size()+" players. Event: " + event.outerHtml());
	//            			
	            			injured.put(eventId, pyrs.get(0));
	            		} 
	            		
	            		// Substitute injured
	            		else if ( eventEnum == Event.INJURED_REPLACEMENT) {
	            			
	            			currentFragment = getCurrentFragment(event, fragments, currentFragment);
	//            			
	            			//get involved players
	            			Integer eventId = Integer.valueOf(event.attr("data-eventindex"));
	            			List<Player> pyrs = getPlayersFromEvent(event, matchDate);
	            			if (pyrs.contains(null)) {
	            				System.out.println("Skipping match because an old player is present");
	            				resetMatchStuffs();
	            				continue match; // old player is present
	            			}
	            			
	            			if(pyrs.size() == 1) {
		            			if(injured.get(eventId-1) != null)
		            				currentFragment.substitution(pyrs.get(0), injured.get(eventId-1));
		            			else
		            				System.out.println("ERROR - non found injured to replace: " + event.outerHtml());
	            			} else if(pyrs.size() == 2) {
	            				if(!injured.get(eventId-1).getId().equals(pyrs.get(0).getId()) && !injured.get(eventId-1).getId().equals(pyrs.get(1).getId()))
	            					System.out.println("ERROR - injury ids CONFLICT. This is the saved id : "+injured.get(eventId-1)+" players. Event: " + event.outerHtml());
	            					
	            				currentFragment.substitution(pyrs.get(0), pyrs.get(1));
	            			} else {
	            				System.out.println("ERROR - injury substitution involves "+pyrs.size()+" players. Event: " + event.outerHtml());
	            			}
	            			
//	            			System.out.println("INFO - Sostituzione al minuto "+event.attr("data-match-minute"));
	            		} 
	            		
	            		// Substitutions
	            		else if (eventEnum == Event.PYR_SUB_AHEAD
	            				|| eventEnum == Event.PYR_SUB_BEHIND
	            				|| eventEnum == Event.PYR_SUB_MINUTE) {
	
	            			currentFragment = getCurrentFragment(event, fragments, currentFragment);
	            			
	            			//get involved players
	            			List<Player> pyrs = getPlayersFromEvent(event, matchDate);
	            			if (pyrs.contains(null)) {
	            				System.out.println("Skipping match because an old player is present");
	            				resetMatchStuffs();
	            				continue match; // old player is present
	            			}
	            			
	            			if(pyrs.size() != 2) System.out.println("ERROR - substitution with "+pyrs.size()+" players. Event: " + event.outerHtml());
	            			
	            			currentFragment.substitution(pyrs.get(0), pyrs.get(1));
	            			
//	            			System.out.println("INFO - Sostituzione al minuto "+event.attr("data-match-minute"));
	            		} 
	            		
	            		// Injuries no substitution
	            		else if (  eventEnum == Event.INJURED_NO_REPLACEMENT
	            				|| eventEnum == Event.INJURED_NO_REPLACEMENT_2
	            				|| eventEnum == Event.INJURED_FOUL_NO_REPLACEMENT) {
	            			
	            			currentFragment = getCurrentFragment(event, fragments, currentFragment);
	
	            			Integer eventId = Integer.valueOf(event.attr("data-eventindex"));
	            			
	            			if(injured.get(eventId-1) != null) {
	            				currentFragment.removeInjured(injured.get(eventId-1));
//	            				System.out.println("INFO - Esce infortunato al minuto "+event.attr("data-match-minute"));
	            			} else
	            				System.out.println("ERROR - non found injured to replace: " + event.outerHtml());
	            		}
	            		
	            		// Red card
	            		else if (  eventEnum == Event.RED_CARD_1
	            				|| eventEnum == Event.RED_CARD_2
	            				|| eventEnum == Event.RED_CARD_3) {
	            			
	            			currentFragment = getCurrentFragment(event, fragments, currentFragment);
	            			
	            			List<Player> list = getPlayersFromEvent(event, matchDate);
	            			if(list.size() != 1) System.out.println("ERROR RED CARD - player "+list.get(0).getId()+" is not unique SENT OUT. Event: " + event.outerHtml());
	            		
	            			currentFragment.removeInjured(list.get(0));
	            			
//	            			System.out.println("INFO - Esce espulso al minuto "+event.attr("data-match-minute"));
	            		}
	            		
	            		// Innjury GK
	            		else if (  eventEnum == Event.INJURED_GK
	            				|| eventEnum == Event.INJURED_GK_2
	            				) {
	            			
	            			currentFragment = getCurrentFragment(event, fragments, currentFragment);
	            			
	            			Integer eventId = Integer.valueOf(event.attr("data-eventindex"));
	            			
	            			if(injured.get(eventId-1) != null) {
	            				currentFragment.removeInjured(injured.get(eventId-1));
	            				System.out.println("INFO - Esce GK infortunato al minuto "+event.attr("data-match-minute"));
	            				List<Player> list = getPlayersFromEvent(event, matchDate);
	            				if (list.size() > 2) System.out.println("ERROR - GK replacement must involve only 2 players: "+ event.outerHtml());
	            				for (Player p : list) {
	            					if (p.getPosition() != Position.GK)
	            						currentFragment.updateRole(p.getId(), Position.GK);
	            				}
	            			} else
	            				System.out.println("ERROR - non found injured to replace: " + event.outerHtml());
	            		}
	            		
	            		// Swap position
	            		else if ( eventEnum == Event.PYR_POSITION_BEHIND
	            				|| eventEnum == Event.PYR_POSITION_AHEAD
	            				|| eventEnum == Event.PYR_POSITION_MINUTE
	            				) {
	            			
	            			currentFragment = getCurrentFragment(event, fragments, currentFragment);
	            			
	            			List<Player> list = getPlayersFromEvent(event, matchDate);
	            			if(list.size() != 2) System.out.println("ERROR SWAP POSITIONS: Not 2 players. Event: " + event.outerHtml());
	            			
	            			currentFragment.swapPositions(list.get(0), list.get(1));
	            			
//	            			System.out.println("INFO - Scambio posizioni al minuto "+event.attr("data-match-minute"));
	            		}
	            		
	            		// SE HEAD
	            		else if (  eventEnum == Event.SE_HEAD_CORNER
	            				|| eventEnum == Event.SE_HEAD_CORNER_NOGOAL
	            				) {
	            			
							List<Player> list = getPlayersFromEvent(event, matchDate);
							if(list.size() > 2) System.out.println("ERROR SE CORNER TO HEAD- More than 2 players involved . Event: " + event.outerHtml());
							
							if(!list.stream().map(p -> p.getSpeciality()).filter(s -> s == Speciality.HEADER).findFirst().isPresent()) System.out.println("ERROR SE CORNER TO HEAD - player "+list.get(0)+" is not HEADER. Event: " + event.outerHtml());
	
							currentFragment.addSE(eventEnum);
	            		}
	            		
	            		// SE QUICK
	            		else if (  eventEnum == Event.SE_QUICK_PASS
	            				|| eventEnum == Event.SE_QUICK_PASS_NOGOAL
	            				|| eventEnum == Event.SE_QUICK_VS_QUICK
	            				|| eventEnum == Event.SE_QUICK_SCORES
	            				|| eventEnum == Event.SE_QUICK_SCORES_NOGOAL
	            				) {
	            			
							List<Player> list = getPlayersFromEvent(event, matchDate);
							if(!list.stream().map(p->p.getSpeciality()).filter(s->s==Speciality.FAST).findFirst().isPresent()) System.out.println("ERROR SE QUICK - no QUICK player among "+list+". Event: " + event.outerHtml());
							currentFragment.addSE(eventEnum);
	            		}
						
	            		// SE UNPREDICT
	            		else if (  eventEnum == Event.SE_UNPR_SCORES
	            				|| eventEnum == Event.SE_UNPR_SCORES_NOGOAL
	            				|| eventEnum == Event.SE_UNPR_OWNGOAL
	            				|| eventEnum == Event.SE_UNPR_OWNGOAL_NOGOAL
	            				) {
	            			
	            			List<Player> list = getPlayersFromEvent(event, matchDate);
	            			if(list.get(0).getSpeciality() != Speciality.UNPREDICTABLE) System.out.println("ERROR SE UNPREDICT - player "+list.get(0)+" is not UNPREDICTABLE. Event: " + event.outerHtml());
	            			currentFragment.addSE(eventEnum);
	            		}
	            		
	            		// SE UNPREDICT PASS
	            		else if (  eventEnum == Event.SE_UNPR_LONG_PASS
	            				|| eventEnum == Event.SE_UNPR_LONG_PASS_NOGOAL
	            				|| eventEnum == Event.SE_UNPR_SPECIAL_ACTION
	            				|| eventEnum == Event.SE_UNPR_SPECIAL_ACTION_NOGOAL
	            				|| eventEnum == Event.SE_UNPR_MISTAKE
	            				|| eventEnum == Event.SE_UNPR_MISTAKE_NOGOAL
	            				) {
	            			
	            			List<Player> list = getPlayersFromEvent(event, matchDate);
//	            			if(list.size() != 2) System.out.println("ERROR SE UNPREDICT - not 2 players. Event: " + event.outerHtml());
	            			if(!list.stream().map(p->p.getSpeciality()).filter(s->s==Speciality.UNPREDICTABLE).findFirst().isPresent()) System.out.println("ERROR SE UNPREDICT - players "+list+" are not UNPREDICTABLE. Event: " + event.outerHtml());
	            			currentFragment.addSE(eventEnum);
	            		}
	            		
	            		// SE SUPPORT
	            		else if (  eventEnum == Event.SE_SUPPORT
	            				|| eventEnum == Event.SE_SUPPORT_FAIL
	            				|| eventEnum == Event.SE_SUPPORT_FAIL_DAMAGE
	            				) {
	            			
	            			List<Player> list = getPlayersFromEvent(event, matchDate);
	            			if(list.size() != 1) System.out.println("ERROR SE SUPPORT - player "+list.get(0).getId()+" is not unique in SE. Event: " + event.outerHtml());
	            			if(list.get(0).getSpeciality() != Speciality.SUPPORT) System.out.println("ERROR SE SUPPORT - player "+list.get(0)+" is not SUPPORT. Event: " + event.outerHtml());
	            			currentFragment.addSE(eventEnum);
	            		}
						
	            		// SE POW
	            		else if (  eventEnum == Event.SE_POW_FORWARD
	            				|| eventEnum == Event.SE_POW_FORWARD_NOGOAL
	            				|| eventEnum == Event.SE_POW_PRESSING
	            				) {
	            			
	            			List<Player> list = getPlayersFromEvent(event, matchDate);
	            			if(list.size() != 1) System.out.println("ERROR SE POW - player "+list.get(0).getId()+" is not unique in SE. Event: " + event.outerHtml());
	            			if(list.get(0).getSpeciality() != Speciality.POWERFUL) System.out.println("ERROR SE POW - player "+list.get(0)+" is not POWERFUL. Event: " + event.outerHtml());
	            			currentFragment.addSE(eventEnum);
	            		}
	            		
	            		// SE TEC VS HEAD
	            		else if (  eventEnum == Event.SE_TECN_VS_HEAD
	            				|| eventEnum == Event.SE_TECN_VS_HEAD_NOGOAL
								) {
	            			
							List<Player> list = getPlayersFromEvent(event, matchDate);
							if(list.size() != 2) System.out.println("ERROR SE - Not 2 players in SE. Event: " + event.outerHtml());
							if(list.get(0).getSpeciality() != Speciality.TECNIC && list.get(1).getSpeciality() != Speciality.TECNIC) System.out.println("ERROR SE TEC VS HEAD - player "+list.get(0)+" is not TECNIC. Event: " + event.outerHtml());
							if(list.get(0).getSpeciality() != Speciality.HEADER && list.get(1).getSpeciality() != Speciality.HEADER) System.out.println("ERROR SE TEC VS HEAD - player "+list.get(1)+" is not HEADER. Event: " + event.outerHtml());
							
							currentFragment.addSE(eventEnum);
	            		}
						
						// SE HEAD FROM WINGER
	            		else if (  eventEnum == Event.SE_HEAD_FROM_WINGER
								) {
	            			
							List<Player> list = getPlayersFromEvent(event, matchDate);
							if(list.size() != 2) System.out.println("ERROR SE HEAD WING- Not 2 players in SE. Event: " + event.outerHtml());
							if((list.get(0).getPosition() != Position.W_R && list.get(0).getPosition()!=Position.W_L) 
									&& (list.get(1).getPosition() != Position.W_R && list.get(1).getPosition()!=Position.W_L)) System.out.println("ERROR SE HEAD WING - player "+list.get(0)+" is not a WINGER. Event: " + event.outerHtml());
							if(list.get(0).getSpeciality() != Speciality.HEADER && list.get(1).getSpeciality() != Speciality.HEADER) System.out.println("ERROR SE HEAD WING - player "+list.get(1)+" is not HEADER. Event: " + event.outerHtml());
	
							currentFragment.addSE(eventEnum);
	            		}
	            		
	            		// SE NOSPEC FROM WINGER
	            		else if (  eventEnum == Event.SE_NOSPEC_FROM_WINGER_NOGOAL
	            				|| eventEnum == Event.SE_NOSPEC_FROM_WINGER
	            				) {
	            			
	            			List<Player> list = getPlayersFromEvent(event, matchDate);
	            			
	            			if(list.size() < 2) System.out.println("ERROR SE NOSP WING - Less than 2 players in SE. Event: " + event.outerHtml());
	            			if(!list.stream().map(p->p.getPosition()).filter(p -> p==Position.W_R || p==Position.W_L ).findFirst().isPresent()) System.out.println("ERROR SE NOSP WING - player "+list.get(0)+" is not a WINGER. Event: " + event.outerHtml());
	            			
	            			currentFragment.addSE(eventEnum);
	            		}
						
	            		// SE GENERIC no check
	            		else if (  eventEnum == Event.SE_NOSPEC_CORNER
	            				|| eventEnum == Event.SE_NOSPEC_CORNER_NOGOAL
	            				|| eventEnum == Event.SE_NOSPEC_LONG_SHOT
	            				|| eventEnum == Event.SE_NOSPEC_LONG_SHOT_NOGOAL
	            				|| eventEnum == Event.SE_NOSPEC_TIRED_DEFENDER
	            				|| eventEnum == Event.SE_NOSPEC_TIRED_DEFENDER_NOGOAL
	            				|| eventEnum == Event.SE_NOSPEC_EXP_FORWARD
	            				|| eventEnum == Event.SE_NOSPEC_EXP_FORWARD_NOGOAL
	            				|| eventEnum == Event.SE_NOSPEC_INEXP_DEFENDER
	            				|| eventEnum == Event.SE_NOSPEC_INEXP_DEFENDER_NOGOAL
								) {
	            			
							currentFragment.addSE(eventEnum);
	            		}
	            		
	            		// End Match
	            		else if (  eventEnum == Event.MATCH_END 
	            				|| eventEnum == Event.BREAK_GAME_BOTH
	            				|| eventEnum == Event.BREAK_GAME_AWAY
	            				|| eventEnum == Event.BREAK_GAME_HOME) {
	            			
	            			currentFragment.setEndingMinute(Integer.valueOf(event.attr("data-match-minute")));
	            			fragments.add(currentFragment);
//	            			System.out.println("Match ended, total fragments: "+fragments.size());
	            		}
	            		
	            		else {
	//            			if ( !eventToSkip(code))
	//            				System.out.println("NOT MANAGED EVENT: "+event.outerHtml());
	            		}
	            	}
    				resetMatchStuffs();
	            }
			}
            	
        }catch(IOException ioe){
            print("Exception: " + ioe.getMessage());
        }

	}
	
	private boolean thereIsABot(Document match) throws IOException {
		
		// home
		Element homeTeam = match.select("a[class^='hometeam notByLine']").first();
		Document teamPage = Jsoup.connect(hattrickLoginUrl + homeTeam.attr("href"))
				.cookie("hattrick.org", hattrickCookie)
				.get();
		Elements bot = teamPage.select("div[id$='BotAlert']");
		if (bot.size() > 0)
			return true;
		
		// away
		Element awayTeam = match.select("a[class^='awayteam notByLine']").first();
		teamPage = Jsoup.connect(hattrickLoginUrl + awayTeam.attr("href"))
				.cookie("hattrick.org", hattrickCookie)
				.get();
		bot = teamPage.select("div[id$='BotAlert']");
		if (bot.size() > 0)
			return true;
		
		return false;
	}

	private void resetMatchStuffs() {
    	injured = new HashMap<Integer,Player>();
	}

	public void elaborateData() {
		// SE: snapshot rose ---> return List SE possibili, SE avvenuto
		List<HashMap<Speciality,Integer>> snapshots = new ArrayList<>();
		HashMap<Speciality,Integer> snapshot = null;
		
		for (MatchFragment fragment : fragments) {
			if(!fragment.getSpecialEvents().isEmpty()) {
				for(Event se : fragment.getSpecialEvents()) {
					snapshot = new HashMap<>();
					// ciclo giocatori dei team e costruisco snapshot specialità per quel dato SE
					for (Player pyr : fragment.getHome()) {
						snapshot.compute(pyr.getSpeciality(), (tokenKey, oldValue) -> oldValue == null ? 1 : oldValue + 1);
					}
					for (Player pyr : fragment.getAway()) {
						snapshot.compute(pyr.getSpeciality(), (tokenKey, oldValue) -> oldValue == null ? 1 : oldValue + 1);
					}
					
					String output = String.join("|", 
							se.toString(), 
							snapshot.get(Speciality.NOSPEC) != null ? snapshot.get(Speciality.NOSPEC).toString() : "",
							snapshot.get(Speciality.HEADER) != null ? snapshot.get(Speciality.HEADER).toString() : "",
							snapshot.get(Speciality.FAST) != null ? snapshot.get(Speciality.FAST).toString() : "",
							snapshot.get(Speciality.UNPREDICTABLE) != null ? snapshot.get(Speciality.UNPREDICTABLE).toString() : "",
							snapshot.get(Speciality.TECNIC) != null ? snapshot.get(Speciality.TECNIC).toString() : "",
							snapshot.get(Speciality.POWERFUL) != null ? snapshot.get(Speciality.POWERFUL).toString() : "",
							snapshot.get(Speciality.SUPPORT) != null ? snapshot.get(Speciality.SUPPORT).toString() : "",
							snapshot.get(Speciality.FAST_HEALING) != null ? snapshot.get(Speciality.FAST_HEALING).toString() : "");
					
//					System.out.println(output);
					
					// per ogni snapshot creo una lista: SE (possibile), % calcolato lineare, % reale (0% per tutti eccetto l'estratto)
					List<Event> possibleSE = new ArrayList<>();
					// nospec
					possibleSE.add(Event.SE_NOSPEC_CORNER);
					possibleSE.add(Event.SE_NOSPEC_EXP_FORWARD);
					possibleSE.add(Event.SE_NOSPEC_INEXP_DEFENDER);
					possibleSE.add(Event.SE_NOSPEC_LONG_SHOT);
					possibleSE.add(Event.SE_NOSPEC_TIRED_DEFENDER);
					
					// winger
					boolean hasHomeWingers = fragment.getHome().stream().filter(p -> (p.getPosition() == Position.W_L ) || (p.getPosition() == Position.W_R)).findAny().isPresent();
					boolean hasAwayWingers = fragment.getAway().stream().filter(p -> (p.getPosition() == Position.W_L ) || (p.getPosition() == Position.W_R)).findAny().isPresent();
					if (hasHomeWingers || hasAwayWingers) {
						possibleSE.add(Event.SE_NOSPEC_FROM_WINGER);
					}
					// winger + head
					boolean hasHomeHead = fragment.getHome().stream().filter(p -> p.getSpeciality() == Speciality.HEADER ).findAny().isPresent();
					boolean hasAwayHead = fragment.getAway().stream().filter(p -> p.getSpeciality() == Speciality.HEADER ).findAny().isPresent();
					if ( (hasHomeWingers && hasHomeHead) || (hasAwayWingers && hasAwayHead) ) {
						possibleSE.add(Event.SE_HEAD_FROM_WINGER);
					}
					
					// quick
					if (snapshot.get(Speciality.FAST) != null) {
						possibleSE.add(Event.SE_QUICK_PASS);
						possibleSE.add(Event.SE_QUICK_SCORES);
					}

//					possibleSE.add(Event.SE_QUICK_VS_QUICK); // TODO gestire
					
					// unpredict
					if (snapshot.get(Speciality.UNPREDICTABLE) != null) {
						possibleSE.add(Event.SE_UNPR_LONG_PASS);
						possibleSE.add(Event.SE_UNPR_MISTAKE);
						possibleSE.add(Event.SE_UNPR_OWNGOAL);
						possibleSE.add(Event.SE_UNPR_SCORES);
						possibleSE.add(Event.SE_UNPR_SPECIAL_ACTION);
					}
					
					// techinc
					if (snapshot.get(Speciality.TECNIC) != null) {
						if(possibleTecVsHead(fragment.getHome(), fragment.getAway())) {
							possibleSE.add(Event.SE_TECN_VS_HEAD);
						}
					}
					// head
					if (snapshot.get(Speciality.HEADER) != null) {
						possibleSE.add(Event.SE_HEAD_CORNER);
					}
					// support
					if (snapshot.get(Speciality.SUPPORT) != null) {
						possibleSE.add(Event.SE_SUPPORT);
					}
					// Alla fine avrò un elenco di SE, con % reale di estrazione (maggiore è lo scostamento dalla media più sarà chiaro che la % iniziale non è lineare)
					
					for (Event e : possibleSE) { //FIXME
						String prob = Double.valueOf(1/possibleSE.size()).toString();
						if (e.toString()==se.toString()) {
							output = String.join("|", e.toString(), prob, prob);
						} else {
							output = String.join("|", e.toString(), String.valueOf("0"), prob);
						}
						System.out.println(output);
					}
				}
			}
		}
		
	}
	
	private boolean possibleTecVsHead(List<Player> home, List<Player> away) { 

		if (home.stream().filter(p -> p.getSpeciality()==Speciality.FAST && p.hasOffensiveRole()).findAny().isPresent()) {
			if (away.stream().filter(p -> p.getSpeciality()==Speciality.HEADER && p.hasDefensiveRole()).findAny().isPresent())
				return true;
		}
		if (away.stream().filter(p -> p.getSpeciality()==Speciality.FAST && p.hasOffensiveRole()).findAny().isPresent()) {
			if (home.stream().filter(p -> p.getSpeciality()==Speciality.HEADER && p.hasDefensiveRole()).findAny().isPresent())
				return true;
		}
		return false;
	}

	private boolean isInjury(String event) {
		Integer code = Integer.valueOf(event);
		return code >= 401 && code <= 423;
	}
	
	private List<Player> getPlayersFromEvent(Element event, String ddMMyyyy) throws IOException {
		
		ArrayList<Player> list = new ArrayList<Player>();
		
		for (Element el : event.select("a[href^='/Club/Players/Player.aspx']")) {
			list.add(retrievePlayerInfos(el, ddMMyyyy));
		}
		return list;
	}
	
	// Ritorna giocatore istanziato correttamente per quella partita
	private Player retrievePlayerInfos(Element player, String ddMMyyyy) throws IOException {
		
		String id = player.attr("href").substring(player.attr("href").indexOf("playerId=")+9);
		boolean isHome = player.hasClass("homeplayer");
		if (!isHome) {
			boolean isAway = player.hasClass("awayplayer");
			if (!isAway)
				System.out.println("ERROR: isHome="+isHome+" isAway="+isAway);
		}
		
		if (pastPlayers.contains(id)) {
			// giocatore licenziato o vecchia gloria. Impossibile recuperare le info.
			System.out.println("INFO - Needing infos for Player "+id+" which is a past player.");
			return null;
		} else {
			if (playersMap.get(id) != null) {
				if (positionsArchive.get(id).get(ddMMyyyy) == null)
					System.out.println("ERRORE 1: Non trovata partita "+ddMMyyyy+" per giocatore " +id);
				return new Player(id, positionsArchive.get(id).get(ddMMyyyy), playersMap.get(id).getSpeciality(), isHome);
			} else {
				
				// player page
				Document playerPage = Jsoup.connect(hattrickLoginUrl + player.attr("href"))
						.cookie("hattrick.org", hattrickCookie)
						.get();

				// active?
				String message = playerPage.select("div[id*='Notifications']").html();
				boolean vecchiaGloria = playerPage.select("head").html().contains("Vecchie Glorie");
				if (!message.isEmpty() || vecchiaGloria) {
					pastPlayers.add(id);
					System.out.println("INFO - Player "+id+" added to past players. Vecchia Gloria: "+ vecchiaGloria);
					return null;
				}
				
				// special
				String spec = null;
				try {
					spec = playerPage.select("i[class^='icon-speciality']").attr("title");
				} catch (SelectorParseException e) {}
				
				
				// roles
				String link_player = player.attr("href").replace("Player.aspx","PlayerStats.aspx") + "&activeTab=1&ShowAll=True";
				playerPage = Jsoup.connect(hattrickLoginUrl + link_player)
		    			.cookie("hattrick.org", hattrickCookie)
		    			.get();
				String html = playerPage.html();
				Map<String,Position> map = new HashMap<>();
				
				for(Element el : playerPage.select("tr[id$='_tableRow']")) {
					String date = null;
					for (Element span : el.select("span")) {
						if (span.attr("data-dateiso") != null && !span.attr("data-dateiso").isEmpty()) {
							date = span.attr("data-dateiso");
						} else if (span.id().endsWith("Position")) {
							String role = span.html().split(" ")[0];
							if (!role.contains("(0')")) {
								Position position = getRoleFromMunge(role);
								if (position!= null)
									map.put(date, position);
							}
						}
					}
				}
				
				// home/away
				
				if (map.get(ddMMyyyy) == null)
					System.out.println("ERRORE 2: Non parsata da html partita "+ddMMyyyy+" per giocatore " +id);
					
				Player pyr = new Player(id,map.get(ddMMyyyy), getSpecialityFromMunge(spec), isHome);
				playersMap.put(id, pyr);
				positionsArchive.put(id, map);
	//			System.out.println("Successfully loaded player info: "+ pyr);
				return pyr;
			}
		}
	}

	private static boolean eventToSkip(String code) {
		 switch(code) {
		 case "24":
		 case "31":
		 case "33":
		 case "40":
		 case "41":
		 case "42":
		 case "45":
		 case "75":
		 case "122":
		 case "142":
		 case "173":
		 case "181":
		 case "212":
		 case "213":
		 case "221":
		 case "241":
		 case "242":
		 case "272":
		 case "281":
		 case "332":
		 case "361":
		 case "380":
		 case "384":
		 case "391":
		 case "414":
		 case "510":
		 case "511":
			 return true;
		 default:
			 return false;
		 }
	}

	private static MatchFragment getCurrentFragment(Element event, List<MatchFragment> fragments, MatchFragment currentFragment) {
		
		int minute = Integer.valueOf(event.attr("data-match-minute"));
		if (minute != currentFragment.getStartingMinute()) { //diffenrent minute substitution
			currentFragment.setEndingMinute(minute);
			fragments.add(currentFragment);
			currentFragment = currentFragment.cloneFollowing();
		}
		return currentFragment;
	}

	private static Speciality getSpecialityFromMunge(String spec) {

		if (spec==null || spec.isEmpty())
			return Speciality.NOSPEC;
		
		switch (spec) {
		case ("Veloce"):
		case ("Quick"):
			return Speciality.FAST;
		case ("Imprevedibile"):
		case ("Unpredictable"):
			return Speciality.UNPREDICTABLE;
		case ("Tecnico"):
		case ("Technical"):
			return Speciality.TECNIC;
		case ("Colpo di testa"):
		case ("Head"):
			return Speciality.HEADER;
		case ("Potente"):
		case ("Powerful"):
			return Speciality.POWERFUL;
		case ("Trascinatore"):
			return Speciality.SUPPORT;
		case ("Guarigione Veloce"):
			return Speciality.FAST_HEALING;
		default:
			System.out.println("ERROR - Not managed speciality: "+ spec);
			return null;
		}
	}

	private static Position getRoleFromMunge(String role) {

		switch (role) {
			case ("Pr"):
				return Position.GK;
			case ("TD"):
				return Position.BW_R;
			case ("TS"):
				return Position.BW_L;
			case ("DC"):
				return Position.CD;
			case ("CC"):
				return Position.MID;
			case ("AD"):
				return Position.W_R;
			case ("AS"):
				return Position.W_L;
			case ("At"):
				return Position.FW;
			case ("Difensore"): // Walkover
			case ("Terzino"): // Walkover
			case ("Centrocampista"): // Walkover
			case ("Ala"): // Walkover
			case ("Portiere"): // Walkover
			case ("Attaccante"): // Walkover
			case (""):
				return null;
			default:
				System.out.println("ERROR - Munged not managed role: "+ role);
				return null;
		}
	}

	private static void print(String msg, Object... args) {
        System.out.println(String.format(msg, args));
    }
}
