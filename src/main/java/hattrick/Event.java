package hattrick;

public enum Event {

//	ENTER_FIELD("19"), //Players enter the field
//	E20 //Tactical disposition
	LINEUPS("21"), //Player names in lineup
//	22, //Players from neighborhood used
//	23, //Same formation both teams
//	24, //Team formations (different)
//	25, //Regional derby
//	26, //Neutral ground
//	27, //Away is actually home
//	30, //Spectators/venue - rain
//	31, //Spectators/venue - cloudy
//	32, //Spectators/venue - fair weather
//	33, //Spectators/venue - sunny
//	35, //Arena extended with temporary seats
//	36, //Only venue - rain
//	37, //Only venue - cloudy
//	38, //Only venue - fair weather
//	39, //Only venue - sunny
//	40, //Dominated
//	41, //Best player
//	42, //Worst player
//	45, //Half time results
//	46, //Hat-trick comment
//	47, //No team dominated
//	55, //Penalty contest: Goal by Technical (no nerves)
//	56, //Penalty contest: Goal, no nerves
//	57, //Penalty contest: Goal in spite of nerves
//	58, //Penalty contest: No goal because of nerves
//	59, //Penalty contest: No goal in spite of no nerves
//	60, //Underestimation
//	61, //Organization breaks
//	62, //Withdraw
//	63, //Remove underestimation at pause
//	64, //Reorganize
//	65, //Nerves in important thrilling game
//	66, //Remove underestimation at pause (goaldiff = 0)
//	67, //Remove underestimation at pause (goaldiff = 1)
//	68, //Successful pressing
//	69, //Remove underestimation
//	70, //Extension
//	71, //Penalty contest (after extension)
//	72, //Extension decided
//	73, //After 22 penalties tossing coin!
//	75, //Added time
//	76, //No added time
//	80, //New captain
//	81, //New set pieces taker
//	90, //Injured but keeps playing
//	91, //Moderately injured, leaves field
//	92, //Badly injured, leaves field
	INJURED_NO_REPLACEMENT("93"), //Injured and no replacement existed
//	94, //Injured after foul but continues
//	95, //Injured after foul and exits
	INJURED_FOUL_NO_REPLACEMENT("96"), //Injured after foul and no replacement existed
	INJURED_GK("97"), //Keeper injured, field player has to take his place
//	100, //Reducing goal home team free kick
//	101, //Reducing goal home team middle
//	102, //Reducing goal home team left wing
//	103, //Reducing goal home team right wing
//	104, //Reducing goal home team penalty kick normal
	SE_UNPR_LONG_PASS("105"), //SE: Goal Unpredictable long pass
	SE_UNPR_SCORES("106"), //SE: Goal Unpredictable scores on his own
	SE_NOSPEC_LONG_SHOT("107"), //SE: Goal longshot
	SE_UNPR_SPECIAL_ACTION("108"), //SE: Goal Unpredictable special action
	SE_UNPR_MISTAKE("109"), //SE: Goal Unpredictable mistake
//	110, //Equalizer goal home team free kick
//	111, //Equalizer goal home team middle
//	112, //Equalizer goal home team left wing
//	113, //Equalizer goal home team right wing
//	114, //Equalizer goal home team penalty kick normal
	SE_QUICK_SCORES("115"), //SE: Quick scores after rush
	SE_QUICK_PASS("116"), //SE: Quick rushes, passes and receiver scores
	SE_NOSPEC_TIRED_DEFENDER("117"), //SE: Tired defender mistake, striker scores
	SE_NOSPEC_CORNER("118"), //SE Goal: Corner to anyone
	SE_HEAD_CORNER("119"), //SE: Goal Corner: Head specialist
//	120, //Goal to take lead home team free kick
//	121, //Goal to take lead home team middle
//	122, //Goal to take lead home team left wing
//	123, //Goal to take lead home team right wing
//	124, //Goal to take lead home team penalty kick normal
	SE_UNPR_OWNGOAL("125"), //SE: Goal: Unpredictable, own goal
//	130, //Increase goal home team free kick
//	131, //Increase goal home team middle
//	132, //Increase goal home team left wing
//	133, //Increase goal home team right wing
//	134, //Increase goal home team penalty kick normal
	SE_NOSPEC_EXP_FORWARD("135"), //SE: Experienced forward scores
	SE_NOSPEC_INEXP_DEFENDER("136"), //SE: Inexperienced defender causes goal
	SE_HEAD_FROM_WINGER("137"), //SE: Winger to Head spec. Scores
	SE_NOSPEC_FROM_WINGER("138"), //SE: Winger to anyone Scores
	SE_TECN_VS_HEAD("139"), //SE: Technical goes around head player
//	140, //Counter attack goal, free kick
//	141, //Counter attack goal, middle
//	142, //Counter attack goal, left
//	143, //Counter attack goal, right
//	150, //Reducing goal away team free kick
//	151, //Reducing goal away team middle
//	152, //Reducing goal away team left wing
//	153, //Reducing goal away team right wing
//	154, //Reducing goal away team penalty kick normal
//	160, //Equalizer goal away team free kick
//	161, //Equalizer goal away team middle
//	162, //Equalizer goal away team left wing
//	163, //Equalizer goal away team right wing
//	164, //Equalizer goal away team penalty kick normal
//	170, //Goal to take lead away team free kick
//	171, //Goal to take lead away team middle
//	172, //Goal to take lead away team left wing
//	173, //Goal to take lead away team right wing
//	174, //Goal to take lead away team penalty kick normal
//	180, //Increase goal away team free kick
//	181, //Increase goal away team middle
//	182, //Increase goal away team left wing
//	183, //Increase goal away team right wing
//	184, //Increase goal away team penalty kick normal
//	185, //Goal indirect free kick
//	186, //Counter attack goal, indirect free kick
//	187, //Goal long shot
	SE_POW_FORWARD("190"), //SE: Goal: Powerful normal forward generates extra chance
//	200, //No reducing goal home team free kick
//	201, //No reducing goal home team middle
//	202, //No reducing goal home team left wing
//	203, //No reducing goal home team right wing
//	204, //No reducing goal home team penalty kick normal
	SE_UNPR_LONG_PASS_NOGOAL("205"), //SE: No Goal Unpredictable long pass
	SE_UNPR_SCORES_NOGOAL("206"), //SE: Unpredictable no goal
	SE_NOSPEC_LONG_SHOT_NOGOAL("207"), //SE: Goal longshot
	SE_UNPR_SPECIAL_ACTION_NOGOAL("208"), //SE: Goal Unpredictable special action
	SE_UNPR_MISTAKE_NOGOAL("209"), //SE: Goal Unpredictable mistake
//	210, //No equalizer goal home team free kick
//	211, //No equalizer goal home team middle
//	212, //No equalizer goal home team left wing
//	213, //No equalizer goal home team right wing
//	214, //No equalizer goal home team penalty kick normal
	SE_QUICK_SCORES_NOGOAL("215"), //SE: Speedy misses after rush
	SE_QUICK_PASS_NOGOAL("216"), //SE: Quick rushes, passes but receiver fails
	SE_NOSPEC_TIRED_DEFENDER_NOGOAL("217"), //SE: Tired defender mistake but no goal
	SE_NOSPEC_CORNER_NOGOAL("218"), //SE No goal: Corner to anyone
	SE_HEAD_CORNER_NOGOAL("219"), //SE: No Goal Corner: Head specialist
//	220, //No goal to take lead home team free kick
//	221, //No goal to take lead home team middle
//	222, //No goal to take lead home team left wing
//	223, //No goal to take lead home team right wing
//	224, //No goal to take lead home team penalty kick normal
	SE_UNPR_OWNGOAL_NOGOAL("225"), //SE: No goal: Unpredictable, own goal almost
//	230, //No increase goal home team free kick
//	231, //No increase goal home team middle
//	232, //No increase goal home team left wing
//	233, //No increase goal home team right wing
//	234, //No increase goal home team penalty kick normal
	SE_NOSPEC_EXP_FORWARD_NOGOAL("235"), //SE: Experienced forward fails to score
	SE_NOSPEC_INEXP_DEFENDER_NOGOAL("236"), //SE: Inexperienced defender almost causes goal
	SE_NOSPEC_FROM_WINGER_NOGOAL("237"), //SE: Winger to someone: No goal //TODO cerca winger to head no goal
	SE_TECN_VS_HEAD_NOGOAL("239"), //SE: Technical goes around head player, no goal
//	240, //Counter attack, no goal, free kick
//	241, //Counter attack, no goal, middle
//	242, //Counter attack, no goal, left
//	243, //Counter attack, no goal, right
//	250, //No reducing goal away team free kick
//	251, //No reducing goal away team middle
//	252, //No reducing goal away team left wing
//	253, //No reducing goal away team right wing
//	254, //No reducing goal away team penalty kick normal
//	260, //No equalizer goal away team free kick
//	261, //No equalizer goal away team middle
//	262, //No equalizer goal away team left wing
//	263, //No equalizer goal away team right wing
//	264, //No equalizer goal away team penalty kick normal
//	270, //No goal to take lead away team free kick
//	271, //No goal to take lead away team middle
//	272, //No goal to take lead away team left wing
//	273, //No goal to take lead away team right wing
//	274, //No goal to take lead away team penalty kick normal
//	280, //No increase goal away team free kick
//	281, //No increase goal away team middle
//	282, //No increase goal away team left wing
//	283, //No increase goal away team right wing
//	284, //No increase goal away team penalty kick normal
//	285, //No goal indirect free kick
//	286, //Counter attack, no goal, indirect free kick
//	287, //No goal long shot
//	288, //No goal long shot, defended
	SE_QUICK_VS_QUICK("289"), //SE: Quick rushes, stopped by quick defender
	SE_POW_FORWARD_NOGOAL("290"), //SE: No Goal: Powerful normal forward generates extra chance
//	301, //SE: Technical suffers from rain
//	302, //SE: Powerful thrives in rain
//	303, //SE: Technical thrives in sun
//	304, //SE: Powerful suffers from sun
//	305, //SE: Quick loses in rain
//	306, //SE: Quick loses in sun
	SE_SUPPORT("307"), //SE: Support player boost succeeded
	SE_SUPPORT_FAIL_DAMAGE("308"), //SE: Support player boost failed and organization dropped
	SE_SUPPORT_FAIL("309"), //SE: Support player boost failed
	SE_POW_PRESSING("310"), //SE: Powerful defensive inner presses chance
//	331, //Tactic Type: Pressing
//	332, //Tactic Type: Counter-attacking
//	333, //Tactic Type: Attack in middle
//	334, //Tactic Type: Attack on wings
//	335, //Tactic Type: Play creatively //TODO
//	336, //Tactic Type: Long shots
//	343, //Tactic: Attack in middle used
//	344, //Tactic: Attack on wings used
	PYR_SUB_BEHIND("350"), //Player substitution: team is behind
	PYR_SUB_AHEAD("351"), //Player substitution: team is ahead
	PYR_SUB_MINUTE("352"), //Player substitution: minute
//	360, //Change of tactic: team is behind
//	361, //Change of tactic: team is ahead
//	362, //Change of tactic: minute
	PYR_POSITION_BEHIND("370"), //Player position swap: team is behind
	PYR_POSITION_AHEAD("371"), //Player position swap: team is ahead
	PYR_POSITION_MINUTE("372"), //Player position swap: minute
//	380, //Man marking success, short distance
//	381, //Man marking success, long distance
//	382, //Man marked changed from short to long distance
//	383, //Man marked changed from long to short distance
//	385, //Man marker changed from short to long distance
//	386, //Man marker changed from long to short distance
//	387, //Man marker penalty, man marked not in marking position
//	388, //Man marker penalty, man marker not in marking position
//	390, //Rainy weather - Many players affected
//	391, //Sunny weather - Many players affected
	INJ1("401"), //Injury: Knee left
	INJ2("402"), //Injury: Knee right
	INJ3("403"), //Injury: Thigh left
	INJ4("404"), //Injury: Thigh right
	INJ5("405"), //Injury: Foot left
	INJ6("406"), //Injury: Foot right
	INJ7("407"), //Injury: Ankle left
	INJ8("408"), //Injury: Ankle right
	INJ9("409"), //Injury: Calf left
	INJ10("410"), //Injury: Calf right
	INJ11("411"), //Injury: Groin left
	INJ12("412"), //Injury: Groin right
	INJ13("413"), //Injury: Collarbone
	INJ14("414"), //Injury: Back
	INJ15("415"), //Injury: Hand left
	INJ16("416"), //Injury: Hand right
	INJ17("417"), //Injury: Arm left
	INJ18("418"), //Injury: Arm right
	INJ19("419"), //Injury: Shoulder left
	INJ20("420"), //Injury: Shoulder right
	INJ21("421"), //Injury: Rib
	INJ22("422"), //Injury: Head
	INJ23("423"), //Injured by foul
	INJURED_REPLACEMENT("424"), //Injured player replaced
	INJURED_NO_REPLACEMENT_2("425"), //No replacement for injured player
	INJURED_GK_2("426"), //Field player has to take injured keeper's place
//	427, //Player injured was regainer so got bruised instead
//	450, //Player got third yellow card misses next match
//	451, //With this standing team x will relegate to cup y
//	452, //Player current team matches 100s anniversary
//	453, //Player possibly the last game in this team
//	454, //Doctor report of injury length
//	455, //New star player of the team
//	456, //Player career goals multiple of 50
//	457, //Player league goals this season
//	458, //Player cup goals this season
//	459, //Bench player warming up
//	460, //Fans shocked by losing
//	461, //Fans upset by losing
//	462, //Fans surprised by winning
//	463, //Fans excited by winning
//	464, //Exact number of spectators
//	465, //Team should win match to secure winning the league
//	466, //Team should win match to have chance of winning league
//	467, //The winner of this match (if there is one) can have a chance of winning the league
//	468, //Team should win match to make sure they don't demote
//	469, //Team should win match to have a chance of not demoting
//	470, //The loser of this match will demote
//	471, //Hometeam/Awayteam has most possession in beginning of match
//	472, //Equal possession in beginning of match
//	473, //Career ending injury
//	474, //Possession shifted
//	475, //Low attendance because of fan mood
//	476, //Extra security because of fan mood
//	477, //Both team’s fans are angry
//	478, //Team will have best cup run if win
//	479, //Both teams could have best cup run if win (competing)
//	480, //Current round is team's best cup run
//	481, //New formation today
//	482, //Teams using the same style of play
//	483, //Teams using different styles of play
//	484, //One team's style of play
//	485, //Team of oldies
//	486, //Team is aggressive
//	487, //Team has only homegrown players
//	488, //Team has all players from same country
//	489, //Comeback after a long injury
//	490, //Previous match (cup) similar outcome
//	491, //Previous match (cup) different outcome
//	492, //Previous match (league) similar outcome
//	493, //Previous match (league) different outcome
	WALKOVER_BOTH("500"), //Both teams walkover
	WALKOVER_AWAY("501"), //Home team walkover
	WALKOVER_HOME("502"), //Away team walkover
	BREAK_GAME_BOTH("503"), //Both teams break game (2 players remaining)
	BREAK_GAME_AWAY("504"), //Home team breaks game (2 players remaining)
	BREAK_GAME_HOME("505"), //Away team breaks game (2 players remaining)
//	510, //Yellow card nasty play
//	511, //Yellow card cheating
	RED_CARD_1("512"), //Red card (2nd warning) nasty play
	RED_CARD_2("513"), //Red card (2nd warning) cheating
	RED_CARD_3("514"), //Red card without warning
//	597, //Second half started
//	598, //Match started
	MATCH_END("599"); //Match finished
//	601, //Congratulations to the winner
//	602, //Winner advances to next cup round (no relegation cup for loser)
//	603, //Winner advances to next cup round and loser relegates to cup X
//	604, //Match ended in a tie
//	605, //End of match, congratulations team won the league
//	606, //End of match, sad that team will demote directly
//	650, //Hattrick Anniversary
//	651, //Team Anniversary
//	700, //Event-o-Matic: Manager taunts opponent
//	701, //Event-o-Matic: Manager praises opponent
//	702, //Event-o-Matic: Manager asks fans for support
//	703, //Event-o-Matic: Manager expects great show
//	704 //Event-o-Matic: Manager honours Club legacy

	private final String code;
	
	private Event(String code) 
    { 
        this.code = code; 
    } 
	
	public String getCode() {
		return code;
	}
	
	public static Event getFromCode(String code) {
		for(Event event : Event.values()) {
			if(event.getCode().equals(code))
				return event;
		}
		return null;
	}
	
}
