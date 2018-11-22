package hattrick;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.jsoup.select.Selector.SelectorParseException;

public class HattrickCalculator {

	public static final String hattrickCookie = "7F9DD65DFD4A3B7F8DBD96968E3A62A993E61E973EEC84FF2BF988217C52729F808B9DDEF1B37F42F278F8469C76D1E669B4427282673FFD5FE0BEBE03BAE346E0B08FB1C6E3230DF23B32080D2ACC7EE2B95F58757F06D9E8FB85F682EEF4BB2DC2D21251E921B9A407BA95F1D3314E81FFD7AA";
	public static final String hattrickLoginUrl = "https://www.hattrick.org";
	public Map<String,Player> playersMap = new HashMap<String,Player>();
	private HashMap<String, Map<String,Position>> positionsArchive = new HashMap<String, Map<String,Position>>(); // <playerId<'dd-MM-yyyy',Position>>
	private HashMap<Integer,Player> injured = new HashMap<Integer,Player>(); // map event-id, player injured. nulled at the end of each match
	
	public void mungeMatches() throws IOException {
		
		/** DATA MUNGING */
		
//		List<Configuration> configurations;
		
		try{
            
//            Response response = 
//                    Jsoup.connect(loginUrl + "/Startpage3.aspx")
//                    .userAgent("Mozilla/5.0")
////                    .timeout(10 * 1000)
//                    .method(Method.POST)
//                    .data("ctl00$CPContent$ucLogin$txtUserName", "mattogol")
//                    .data("ctl00$CPContent$ucLogin$txtPassword", "mttgln181089")
//                    .followRedirects(true)
//                    .execute();
//            
//            System.out.println(response.url());
//            
//            //parse the document from response
//            Document document = response.parse();
//            
////            System.out.println(document.html());
//            
//            //get cookies
//            Map<String, String> mapCookies = response.cookies();
//            
//            /*
//             * You may need to send all the cookies you received
//             * from the post response to the subsequent requests.
//             * 
//             * You can do that using cookies method of Connection
//             */
//            
////            response = Jsoup.connect("https://www92.hattrick.org/?authCode=2iHQj++IG4lexRWCpIE0eb2Vxd/5ea0BWYeejhCE70n1dDewjuqRmudIOa0UxTvxL5+QyItZAwoJtpNz5MHbTiykCUgRjASNlbADIrdhJAPQEnozZnSJ/NJco661A08iTWLgsyXQ98eLDxc3z8t1kZN+F9/BnG+IOIj2pnJ/CiOvOcDI/AY/T7QQNf+euxfzv3sy/XB0Depr4SuYif2mUAI639EH8BXkk13Y/HSDb5nGXWt226YNH6IOsQ0dR2Ktk7R/b5FlmPfqr6Xd6e6xLbYBlKly3aJkEQgAQRgXrIHBwYJqSfMEOhgQUcSP96D61/MwRHFduYCYFXiBL84xlO1Tund63XlS").execute();
////            mapCookies = response.cookies();
//            
//            System.out.println("COOK: " + mapCookies.get("ASP.NET_SessionId"));
//            System.out.println("COOK: " + mapCookies.get("CrossServerSession"));
//            System.out.println("COOK: " + mapCookies.get("InitialOrigin"));
//            System.out.println("COOKIE SIZE: " + mapCookies.size());
//            System.out.println("COOKIES: " + mapCookies.keySet());
            
    		Document calendar = 
    				Jsoup.connect(hattrickLoginUrl + "/World/Series/Fixtures.aspx?LeagueLevelUnitID=724&Season=69&TeamId=-1")
    				.cookie("hattrick.org", hattrickCookie)
    				.get();
    		
//    		System.out.println(calendar.html());
    		Elements calendar_links = calendar.select("a[href^='/Club/Matches/Match.']"); // get match links

    		print("\nLinks: (%d)", calendar_links.size());
            for (Element link : calendar_links) {
//        		Element link = calendar_links.get(0);

            	System.out.println("\n MATCH: " + link.outerHtml());
            	Document match = Jsoup.connect(hattrickLoginUrl + link.attr("href"))
            			.cookie("hattrick.org", hattrickCookie)
            			.get();
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
            	
            	List<MatchFragment> fragments = new ArrayList<>();
            	MatchFragment currentFragment = new MatchFragment(0);
            	
            	for (Element event : events) {
            		
            		String code = event.attr("data-eventtype").split("_")[0];
            		Event eventEnum = Event.getFromCode(code);
            		
            		// Lineups
            		if (eventEnum == Event.LINEUPS) {
                    	List<Player> players = getPlayersFromEvent(event, matchDate);
                    	for (Player player : players) {
                			currentFragment.addPyr(player);
                    	}
            		} 
            		
            		// Injury
            		else if (eventEnum!= null && isInjury(eventEnum.getCode())) {
            			
            			//get involved players
            			Integer eventId = Integer.valueOf(event.attr("data-eventindex"));
            			List<Player> pyrs = getPlayersFromEvent(event, matchDate);
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
            			if(pyrs.size() != 1) System.out.println("ERROR - injury substitution "+pyrs.size()+" players. Event: " + event.outerHtml());
            			
            			if(injured.get(eventId-1) != null)
            				currentFragment.substitution(pyrs.get(0), injured.get(eventId-1));
            			else
            				System.out.println("ERROR - non found injured to replace: " + event.outerHtml());
            			
            		} 
            		
            		// Substitutions
            		else if (eventEnum == Event.PYR_SUB_AHEAD
            				|| eventEnum == Event.PYR_SUB_BEHIND
            				|| eventEnum == Event.PYR_SUB_MINUTE) {

            			currentFragment = getCurrentFragment(event, fragments, currentFragment);
            			
            			//get involved players
            			List<Player> pyrs = getPlayersFromEvent(event, matchDate);
            			if(pyrs.size() != 2) System.out.println("ERROR - substitution with "+pyrs.size()+" players. Event: " + event.outerHtml());
            			
            			currentFragment.substitution(pyrs.get(0), pyrs.get(1));
            		} 
            		
            		// Injuries no substitution
            		else if (  eventEnum == Event.INJURED_NO_REPLACEMENT
            				|| eventEnum == Event.INJURED_NO_REPLACEMENT_2
            				|| eventEnum == Event.INJURED_FOUL_NO_REPLACEMENT) {
            			
            			currentFragment = getCurrentFragment(event, fragments, currentFragment);

            			Integer eventId = Integer.valueOf(event.attr("data-eventindex"));
            			
            			if(injured.get(eventId-1) != null)
            				currentFragment.removeInjured(injured.get(eventId-1));
            			else
            				System.out.println("ERROR - non found injured to replace: " + event.outerHtml());
            		}
            		
            		// Red card
            		else if (  eventEnum == Event.RED_CARD_1
            				|| eventEnum == Event.RED_CARD_2
            				|| eventEnum == Event.RED_CARD_3) {
            			
            			currentFragment = getCurrentFragment(event, fragments, currentFragment);
            			
            			System.out.println("TODO:" + event.outerHtml() );
            		}
            		
            		// Innjury GK
            		else if (  eventEnum == Event.INJURED_GK
            				|| eventEnum == Event.INJURED_GK_2
            				) {
            			
            			currentFragment = getCurrentFragment(event, fragments, currentFragment);
            			
            			System.out.println("TODO:" + event.outerHtml() );
            		}
            		
            		// Swap position
            		else if ( eventEnum == Event.PYR_POSITION_BEHIND
            				|| eventEnum == Event.PYR_POSITION_AHEAD
            				|| eventEnum == Event.PYR_POSITION_MINUTE
            				) {
            			
            			currentFragment = getCurrentFragment(event, fragments, currentFragment);
            			
            			System.out.println("TODO:" + event.outerHtml() );
            		}
            		
            		// Stop match (walkover, ...)
            		else if ( eventEnum == Event.WALKOVER_BOTH
            				|| eventEnum == Event.WALKOVER_AWAY
            				|| eventEnum == Event.WALKOVER_HOME
            				|| eventEnum == Event.BREAK_GAME_BOTH
            				|| eventEnum == Event.BREAK_GAME_AWAY
            				|| eventEnum == Event.BREAK_GAME_HOME
            				) {
            			
            			currentFragment = getCurrentFragment(event, fragments, currentFragment);
            			
            			System.out.println("TODO:" + event.outerHtml() );
            		}
            		
            		// SE
            		else if ( eventEnum == Event.SE_UNPR_LONG_PASS
            				|| eventEnum == Event.SE_UNPR_LONG_PASS_NOGOAL
            				|| eventEnum == Event.SE_UNPR_SCORES
            				|| eventEnum == Event.SE_UNPR_SCORES_NOGOAL
            				|| eventEnum == Event.SE_UNPR_SPECIAL_ACTION
            				|| eventEnum == Event.SE_UNPR_SPECIAL_ACTION_NOGOAL
            				|| eventEnum == Event.SE_UNPR_MISTAKE
            				|| eventEnum == Event.SE_UNPR_MISTAKE_NOGOAL
            				|| eventEnum == Event.SE_UNPR_OWNGOAL
            				|| eventEnum == Event.SE_UNPR_OWNGOAL_NOGOAL
            				
            				|| eventEnum == Event.SE_NOSPEC_LONG_SHOT
            				|| eventEnum == Event.SE_NOSPEC_LONG_SHOT_NOGOAL
            				|| eventEnum == Event.SE_NOSPEC_TIRED_DEFENDER
            				|| eventEnum == Event.SE_NOSPEC_TIRED_DEFENDER_NOGOAL
            				|| eventEnum == Event.SE_NOSPEC_CORNER
            				|| eventEnum == Event.SE_NOSPEC_CORNER_NOGOAL
            				|| eventEnum == Event.SE_NOSPEC_EXP_FORWARD
            				|| eventEnum == Event.SE_NOSPEC_EXP_FORWARD_NOGOAL
            				|| eventEnum == Event.SE_NOSPEC_INEXP_DEFENDER
            				|| eventEnum == Event.SE_NOSPEC_INEXP_DEFENDER_NOGOAL
            				|| eventEnum == Event.SE_NOSPEC_FROM_WINGER
//            				|| eventEnum == Event.SE_NOSPEC_FROM_WINGER_NOGOAL

            				|| eventEnum == Event.SE_TECN_VS_HEAD
            				|| eventEnum == Event.SE_TECN_VS_HEAD_NOGOAL
            				
            				|| eventEnum == Event.SE_POW_FORWARD
            				|| eventEnum == Event.SE_POW_FORWARD_NOGOAL
            				|| eventEnum == Event.SE_POW_PRESSING
            				
            				|| eventEnum == Event.SE_SUPPORT
            				|| eventEnum == Event.SE_SUPPORT_FAIL
            				|| eventEnum == Event.SE_SUPPORT_FAIL_DAMAGE
            				) {
            			
//            			currentFragment = getCurrentFragment(event, fragments, currentFragment);
            			
            			System.out.println("TODO:" + event.outerHtml() );
            		}
            		
            		// End Match
            		else if (eventEnum == Event.SE_NOSPEC_FROM_WINGER_NOGOAL) {
            			
//            			Elements elPlayers = getPlayersFromEvent(event, matchDate);
            			
            			System.out.println("");
            		}
            		
            		// End Match
            		else if (eventEnum == Event.MATCH_END) {
            			currentFragment.setEndingMinute(Integer.valueOf(event.attr("data-match-minute")));
            			fragments.add(currentFragment);
            			System.out.println("Match ended, total fragments: "+fragments.size());
            		}
            		
            		// SE HEAD
            		else if (  eventEnum == Event.SE_HEAD_CORNER
            				|| eventEnum == Event.SE_HEAD_CORNER_NOGOAL
            				|| eventEnum == Event.SE_HEAD_FROM_WINGER
            				) {
            			
            			currentFragment = getCurrentFragment(event, fragments, currentFragment);
            			
            			System.out.println("TODO:" + event.outerHtml() );
            		}
            		
            		// SE SPEED
            		else if (  eventEnum == Event.SE_QUICK_PASS
            				|| eventEnum == Event.SE_QUICK_PASS_NOGOAL
            				|| eventEnum == Event.SE_QUICK_SCORES
            				|| eventEnum == Event.SE_QUICK_SCORES_NOGOAL
            				|| eventEnum == Event.SE_QUICK_VS_QUICK
            				) {
            			
            			currentFragment = getCurrentFragment(event, fragments, currentFragment);
            			
            			System.out.println("TODO:" + event.outerHtml() );
            		}
            		
            		else {
//            			if ( !eventToSkip(code))
//            				System.out.println("NOT MANAGED EVENT: "+event.outerHtml());
            		}
            	}
            	injured = new HashMap<Integer,Player>();;
            }
            	
        }catch(IOException ioe){
            print("Exception: " + ioe.getMessage());
        }

		
		// I-II-III Serie: 724 --> 744 comprese
		
		// SE: snapshot rose ---> return List SE possibili, SE avvenuto
		
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
		
		
		if (playersMap.get(id) != null) {
			if (positionsArchive.get(id).get(ddMMyyyy) == null)
				System.out.println("ERRORE 1: Non trovata partita "+ddMMyyyy+" per giocatore " +id);
			return new Player(id, positionsArchive.get(id).get(ddMMyyyy), playersMap.get(id).getSpeciality(), isHome);
		} else {
			
			// special
			Document playerPage = Jsoup.connect(hattrickLoginUrl + player.attr("href"))
					.cookie("hattrick.org", hattrickCookie)
					.get();
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
			// cycle rows
			for(Element el : playerPage.select("tr[id$='_tableRow']")) {
				String date = null;
				for (Element span : el.select("span")) {
					if (span.attr("data-dateiso") != null && !span.attr("data-dateiso").isEmpty()) {
						date = span.attr("data-dateiso");
					} else if (span.id().endsWith("Position")) {
//						int indexData = html.indexOf(date);
//						int indexSpan1 = html.indexOf("span ", indexData);
//						int indexSpan2 = html.indexOf("span ", indexSpan1 + 5);
//						int indexEndTag = html.indexOf(">", indexSpan2 + 5);
//						int indexSpace = html.indexOf(" ", indexEndTag);
//						String role = html.substring(indexEndTag+1, indexSpace);
	//					System.out.println("PROVA PROVA PROVA: Pyr "+id+" trovata data e ruolo (potrebbe anche essere vuoto) ---> " + date + ", "+ role);
						
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
				System.out.println("ERRORE 2: Non trovata partita "+ddMMyyyy+" per giocatore " +id);
				
			Player pyr = new Player(id,map.get(ddMMyyyy), getSpecialityFromMunge(spec), isHome);
			playersMap.put(id, pyr);
			positionsArchive.put(id, map);
			System.out.println("Successfully loaded player info: "+ pyr);
			return pyr;
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
			return Speciality.FAST;
		case ("Imprevedibile"):
			return Speciality.UNPREDICTABLE;
		case ("Tecnico"):
			return Speciality.TECNIC;
		case ("Colpo di testa"):
			return Speciality.HEADER;
		case ("Potente"):
			return Speciality.POWERFUL;
		case ("Trascinatore"):
			return Speciality.SUPPORT;
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
			case ("Centrocampista"):
				return Position.MID;
			case ("AD"):
				return Position.W_R;
			case ("AS"):
				return Position.W_L;
			case ("At"):
				return Position.FW;
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
