package hattrick;

import java.util.ArrayList;
import java.util.List;

public class MatchFragment {

	private int startingMinute;
	private int endingMinute;
	
	private List<Player> home;
	private List<Player> away;
	
	public MatchFragment(int start) {
		this.startingMinute = start;
		home = new ArrayList<>();
		away = new ArrayList<>();
	}

	public int getStartingMinute() {
		return startingMinute;
	}
	public void setEndingMinute(int endingMinute) {
		this.endingMinute = endingMinute;
	}
	public int getTotalMinutes() {
		return endingMinute-startingMinute;
	}
	public List<Player> getHome() {
		return home;
	}
	public void addPyr(Player pyr) {
		if(pyr.isHome())
			home.add(pyr);
		if(!pyr.isHome())
			away.add(pyr);
	}

	public List<Player> getAway() {
		return away;
	}
	public MatchFragment cloneFollowing() {
		MatchFragment clone = new MatchFragment(endingMinute);
		home.stream().forEach(pyr -> clone.addPyr(pyr));
		away.stream().forEach(pyr -> clone.addPyr(pyr));
		return clone;
	}
	public void substitution(Player one, Player two) {

		// one is a home player in field
		if (this.home.stream().map(p -> p.getId()).filter(id -> id.equals(one.getId())).findAny().isPresent()) {
			substitution(home, one.getId(), two);
			if(two!=null)
				System.out.println("home EXIT: " + one.getId() + ". ENTER: "+ two.getId());
			else
				System.out.println("home EXIT injured: " + one.getId());
			return;
		} 
		
		// one is a away player in field
		else if (this.away.stream().map(p -> p.getId()).filter(id -> id.equals(one.getId())).findAny().isPresent()) {
			substitution(away, one.getId(), two);
			
			if(two!=null)
				System.out.println("away EXIT: " + one.getId() + ". ENTER: "+ two.getId());
			else
				System.out.println("away EXIT injured: " + one.getId());
			return;
		}
		
		if (two!=null) { // if null is a injury removal
			// two is a home player in field
			if (this.home.stream().map(p -> p.getId()).filter(id -> id.equals(two.getId())).findAny().isPresent()) {
				substitution(home, two.getId(), one);
				System.out.println("HOME EXIT: " + two.getId() + ". ENTER: "+ one.getId());
				return;
			} 
			
			// two is a away player in field
			else if (this.away.stream().map(p -> p.getId()).filter(id -> id.equals(two.getId())).findAny().isPresent()) {
				substitution(away, two.getId(), one);
				System.out.println("AWAY EXIT: " + two.getId() + ". ENTER: "+ one.getId());
				return;
			}
		}
	}
	private void substitution(List<Player> list, String id_exit, Player enter) {
		
		int index_to_remove = -1;
		for (int i=0;i<list.size();i++) {
			if(list.get(i).getId().equals(id_exit)) {
				index_to_remove = i;
				continue;
			}	
		}
		list.remove(index_to_remove);
		if (enter != null)
			list.add(enter);
	}

	public void removeInjured(Player player) {
		
		substitution(player, null);
	}
}
