package hattrick;

import java.util.ArrayList;
import java.util.List;

public class MatchFragment {

	private int startingMinute;
	private int endingMinute;
	
	private List<Player> home;
	private List<Player> away;
	
	private List<Event> specialEvents;
	
	public MatchFragment(int start) {
		this.startingMinute = start;
		home = new ArrayList<>();
		away = new ArrayList<>();
		specialEvents = new ArrayList<>();
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
			return;
		} 
		
		// one is a away player in field
		else if (this.away.stream().map(p -> p.getId()).filter(id -> id.equals(one.getId())).findAny().isPresent()) {
			substitution(away, one.getId(), two);
			return;
		}
		
		if (two!=null) { // if null is a injury removal
			// two is a home player in field
			if (this.home.stream().map(p -> p.getId()).filter(id -> id.equals(two.getId())).findAny().isPresent()) {
				substitution(home, two.getId(), one);
				return;
			} 
			
			// two is a away player in field
			else if (this.away.stream().map(p -> p.getId()).filter(id -> id.equals(two.getId())).findAny().isPresent()) {
				substitution(away, two.getId(), one);
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
	
	public void addSE(Event event) {
		this.specialEvents.add(event);
	}
	
	public void swapPositions(Player p1, Player p2) {

		Position temp = p1.getPosition();
		p1.setPosition(p2.getPosition());
		p2.setPosition(temp);
		
		// home
		if (home.stream().map(p -> p.getId()).filter(i -> i.equals(p1.getId())).findFirst().isPresent()) {
			int index1 = -1;
			int index2 = -1;
			for (int i=0;i<home.size();i++) {
				if(home.get(i).getId().equals(p1.getId())) {
					index1 = i;
				}	
				if(home.get(i).getId().equals(p2.getId())) {
					index2 = i;
				}	
			}
			home.set(index1, p1);
			home.set(index2, p2);
		}

		// away
		if (away.stream().map(p -> p.getId()).filter(i -> i.equals(p1.getId())).findFirst().isPresent()) {
			int index1 = -1;
			int index2 = -1;
			for (int i=0;i<away.size();i++) {
				if(away.get(i).getId().equals(p1.getId())) {
					index1 = i;
				}	
				if(away.get(i).getId().equals(p2.getId())) {
					index2 = i;
				}	
			}
			away.set(index1, p1);
			away.set(index2, p2);
		}
	}

	public List<Event> getSpecialEvents() {
		return specialEvents;
	}
	
}
