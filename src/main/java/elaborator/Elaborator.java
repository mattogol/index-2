package elaborator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

import model.Team;


public class Elaborator {

	public static final int NUM_SQUADRE = 10;
	public static final int GIORNATE_GIOCATE = 36;
	
	public static void main(String[] args) {
		
//		Window w = new Window();
//		w.setVisible(true);
		
		/** nomi squadra 
		 * 															  |  0     |    1      |     2      |    3        |    4     |   5     |  6     |  7     |  8     |   9       |  */
		ArrayList<String> nomi = new ArrayList<String>(Arrays.asList("granchi","depcan","libertas","rispetto","stella","ureri","culi","orga","fiat","bonanza"));
		
		/** HM delle squadre */
		ArrayList<Team> teams = new ArrayList<Team>();
		for(int i=0;i<NUM_SQUADRE;i++) {
			teams.add(new Team(nomi.get(i),i));
		}
		
		/** Ciclo giornate */
		Integer[] punti_gg;
		Float[] punti_gg_FF;
		for (int i=0;i<GIORNATE_GIOCATE;i++){
			
			/** Ciclo Teams */
			for (Team team : teams){
				punti_gg = new Integer[9] ;
				punti_gg_FF = new Float[9] ;
				int count_avv = 0;
				
				/** Ciclo avversari */
				for (Team avv : teams){
					if (avv.code != team.code) {
						punti_gg[count_avv] = team.getPuntiPartita(avv.goals.get(i), i);
						punti_gg_FF[count_avv] = team.getPuntiPartita_FF(avv.results.get(i), i, punti_gg[count_avv]);
						if(avv.code==team.avversari.get(i)) {
							Integer punti = team.getPuntiPartita(avv.goals.get(i), i);
							team.points_fatti.add(punti);
							team.punti_classifica_real += punti;
						}
						
						// calcolo punteggio medio di giornata (normale e FF)
						if (count_avv==8){
							Float pti_mean = 0f;
							for (Integer pti : punti_gg) {
								pti_mean += pti;
							}
							team.points_mean.add(pti_mean/9);
							team.punti_classifica += pti_mean/9;
							
							//FF
							Float pti_mean_FF = 0f;
							for (Float pti_FF : punti_gg_FF) {
								pti_mean_FF += pti_FF;
							}
							team.points_mean_FF.add(pti_mean_FF/9);
							team.punti_classifica_FF += pti_mean_FF/9;
						}
						
						count_avv++;
					}
				}
				team.points.add(punti_gg);
			}
		}
		
		System.out.printf("NOME TEAM \t PUNTEGGIO \t GOAL \t PTI_REALI   PTI_MEDI \t GIUSTI  DIFF \n");
		
		// ordino teams per fantapunti giusti
		Collections.sort(teams, new Comparator<Team>() {
			public int compare(Team c1, Team c2) {
				if (c1.punti_classifica_FF > c2.punti_classifica_FF) return -1;
				if (c1.punti_classifica_FF < c2.punti_classifica_FF) return 1;
				return 0;
			}});
		
		for (int i=GIORNATE_GIOCATE-1;i<GIORNATE_GIOCATE;i++){
			for(Team team : teams){
				System.out.printf("%10s \t %.2f  \t %d \t %d \t     %.2f \t %.2f \t %.2f \n",team.name,team.results.get(i),team.goals.get(i),team.points_fatti.get(i),team.points_mean.get(i),team.points_mean_FF.get(i),team.points_fatti.get(i)-team.points_mean_FF.get(i));
			}
			System.out.println("");
		}
		
		System.out.printf("NOME TEAM \t SOMMA PUNTI \t REALI \t MEDI \t GIUSTI  DIFF \n");
		for(Team team : teams){
			System.out.printf("%10s \t %.2f \t %d \t %.2f \t %.2f \t %.2f \n",team.name,team.getSommaPunti(),team.punti_classifica_real,team.punti_classifica,team.punti_classifica_FF, team.getClassisfica());
		}
		
		
	}

}
