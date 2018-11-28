package elaborator;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Writer;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import hattrick.HattrickCalculator;
import model.Player2;
import model.Role;
import model.Team;
import util.Calculator;


public class Elaborator {

	public static final int NUM_SQUADRE = 10;
	public static final int GIORNATE_GIOCATE = 10;
	public static final int GIORNATE_TOT = 35;
	
	public static void main(String[] args) throws IOException, InterruptedException {
		
//		indexCalculation();
//		stats();
		
//		wind();
		
		HattrickCalculator ht = new HattrickCalculator();
		ht.mungeMatches();
		ht.elaborateData();
		
		}



	private static void stats() throws IOException {

		Map<Integer, Player2> data = new HashMap<>();
		
		for (int i=1; i<=GIORNATE_TOT; i++) {
			FileInputStream file = new FileInputStream(new File("src/main/resources/rates/Voti_Fantacalcio_Stagione_2017-18_Giornata_" + i + ".xlsx"));
			Workbook workbook = new XSSFWorkbook(file);
			
			Sheet sheet = workbook.getSheetAt(0);
			
			for (Row row : sheet) {
				
				if (row.getCell(0).getCellTypeEnum() == CellType.NUMERIC) {
					
					Integer id = new Integer((int) row.getCell(0).getNumericCellValue());
					Role role = Role.valueOf(row.getCell(1).getRichStringCellValue().toString());
					String name = row.getCell(2).getRichStringCellValue().toString();
					
					Player2 p = data.get(id);
					if (p == null) {
						p = new Player2( id, role, name);
					} else if (p.getRole() != role) {
						System.out.println("WARN ROLE: "+role.toString()+" vs "+p.getRole().toString()+ " for player:"+name);
					} else if (!p.getName().equals(name)) {
						System.out.println("WARN NAME: "+name+" vs "+p.getName()+ " for playerId:"+id);
					}
					
	//				System.out.println(Role.valueOf(row.getCell(1).getRichStringCellValue().toString()));
					
					if (row.getCell(3).getCellTypeEnum() == CellType.NUMERIC) {
						p.getVote().set(i-1, (float) row.getCell(3).getNumericCellValue());
					} else {
						String voteString = row.getCell(3).getRichStringCellValue().toString();
						if(voteString.equals("6*") && !p.hasBonusMalus(i)) {
							continue;
						} else {
							voteString = voteString.replace("*", "");
						}
								
						p.getVote().set(i-1, Float.valueOf(voteString));
					}
					p.getGf().set(i-1, (int) row.getCell(4).getNumericCellValue());
					p.getGs().set(i-1, (int) row.getCell(5).getNumericCellValue());
					p.getRp().set(i-1, (int) row.getCell(6).getNumericCellValue());
					p.getRs().set(i-1, (int) row.getCell(7).getNumericCellValue());
					p.getRf().set(i-1, (int) row.getCell(8).getNumericCellValue());
					p.getAu().set(i-1, (int) row.getCell(9).getNumericCellValue());
					p.getAmm().set(i-1, (int) row.getCell(10).getNumericCellValue());
					p.getEsp().set(i-1, (int) row.getCell(11).getNumericCellValue());
					p.getAss().set(i-1, (int) row.getCell(12).getNumericCellValue());
					p.getAsf().set(i-1, (int) row.getCell(13).getNumericCellValue());
					p.getGdv().set(i-1, (int) row.getCell(14).getNumericCellValue());
					p.getGdp().set(i-1, (int) row.getCell(15).getNumericCellValue());
					p.getFantavote().set(i-1, Calculator.fantavote(p,i));
					data.put(p.getId(), p);
				}
				
			}
			
			workbook.close();
		}
		
		Player2 fakePlayer = new Player2(99999, Role.A, "FAKE");
		fakePlayer.getVote().add(0, 6f);
		fakePlayer.getFantavote().set(0, 6f);
		fakePlayer.getVote().add(5, 6f);
		fakePlayer.getFantavote().set(5, 6f);
		fakePlayer.getVote().add(24, 6f);
		fakePlayer.getGf().add(24, 1);
		fakePlayer.getFantavote().set(24, 9f);
		
		data.put(fakePlayer.getId(), fakePlayer);
		
		for (Map.Entry<Integer, Player2> entry : data.entrySet()) {
			
			Float totContributo = (Calculator.average(entry.getValue().getFantavote()) - 6) 
									*  Calculator.count(entry.getValue().getFantavote()) ;
			
			System.out.println(entry.getKey() 
							 + ";" + entry.getValue().getName() 
							 + ";" + Calculator.count(entry.getValue().getVote()) 
							 + ";" + String.format("%.2f", Calculator.average(entry.getValue().getVote()))
							 + ";" + String.format("%.2f", Calculator.average(entry.getValue().getFantavote()))
							 + ";" + String.format("%.2f", Calculator.stdDeviation(entry.getValue().getFantavote()))
							 + ";" + String.format("%.2f", totContributo)
//							 + "," + Calculator.total(entry.getValue().getGf())
//							 + "," + Calculator.total(entry.getValue().getGs())
//							 + "," + Calculator.total(entry.getValue().getRp())
//							 + "," + Calculator.total(entry.getValue().getRf())
//							 + "," + Calculator.total(entry.getValue().getRs())
//							 + "," + Calculator.total(entry.getValue().getAss())
//							 + "," + Calculator.total(entry.getValue().getAsf())
//							 + "," + Calculator.total(entry.getValue().getAmm())
//							 + "," + Calculator.total(entry.getValue().getEsp())
//							 + "," + Calculator.total(entry.getValue().getAu())
							 );
		}
		
	}

	private static void indexCalculation() {
		
		/** nomi squadra 
		 * 															  |  0     |    1      |     2      |    3        |    4     |   5     |  6     |  7     |  8     |   9       |  */
		ArrayList<String> nomi = new ArrayList<String>(Arrays.asList("granchi", "depcan",   "libertas",  "rispetto",   "stella",   "ureri",  "culi",  "orga",  "fiat",  "bonanza"));
		
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

	private void wind() throws Exception {
		
		// Download prediction values
				URL url = new URL("https://www.windfinder.com/forecast/porto_corsini");
				BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
				String line;
				
				List<String> predictWindDirections = new ArrayList<>();
				List<String> predictWindSpeeds = new ArrayList<>();
				while ((line = reader.readLine()) != null) {
					
					// 5h
					int h_block_start = line.indexOf("units-wd-dir");
					if (h_block_start > 0) {
						line = reader.readLine(); //riga dopo
						predictWindDirections.add(line.trim());
					}
					String to_find = "\"units-ws\">";
					h_block_start = line.indexOf(to_find);
					if (h_block_start > 0) {
						int start = h_block_start + to_find.length();
						int end = line.indexOf("<", start);
						predictWindSpeeds.add(line.substring(start, end));
					}
				}
				reader.close();
				
				HashMap<String,String> predictDirectMap = new HashMap<>();
				predictDirectMap.put("05", predictWindDirections.get(1));
				predictDirectMap.put("08", predictWindDirections.get(2));
				predictDirectMap.put("11", predictWindDirections.get(3));
				predictDirectMap.put("14", predictWindDirections.get(4));
				predictDirectMap.put("17", predictWindDirections.get(5));
				predictDirectMap.put("20", predictWindDirections.get(6));
				predictDirectMap.put("23", predictWindDirections.get(7));
				HashMap<String,String> predictSpeedMap = new HashMap<>();
				predictSpeedMap.put("05", predictWindSpeeds.get(3));
				predictSpeedMap.put("08", predictWindSpeeds.get(5));
				predictSpeedMap.put("11", predictWindSpeeds.get(7));
				predictSpeedMap.put("14", predictWindSpeeds.get(9));
				predictSpeedMap.put("17", predictWindSpeeds.get(11));
				predictSpeedMap.put("20", predictWindSpeeds.get(13));
				predictSpeedMap.put("23", predictWindSpeeds.get(15));
				
				// Download measured values
				while (true) {
					Date d = new Date();
					DateFormat f = new SimpleDateFormat("yyyy-MM-dd");
					String date = f.format(d);
					f = new SimpleDateFormat("HH:mm");
					String hour = f.format(d);
					
					url = new URL("http://www.adriaticowindclub.com/index.php");
					reader = new BufferedReader(new InputStreamReader(url.openStream()));

					boolean lineNow = false;
					boolean lineAverage = false;
					String windSpeed = "";
					String windSpeedAverage = "";
					String windDirection = "";
					while ((line = reader.readLine()) != null) {
//						 System.out.println(line);
						
						lineAverage = line.contains("Media:");
						
						if (lineNow) {
							// Wind speed
							String strong = "<strong><span class=\"temp\">";
							int start = line.indexOf(strong) + strong.length();
							int end = line.indexOf("<", start);
							windSpeed = line.substring(start, end);


							strong = "<strong>";
							start = line.lastIndexOf(strong) + strong.length();
							end = line.lastIndexOf(strong.replace("<", "</"));
							windDirection = line.substring(start, end);
							
							lineNow = false;
						} else if (lineAverage) {
							// Average speed
							String strong = "<strong>";
							int start = line.indexOf(strong) + strong.length();
							int end = line.indexOf("<", start);
							windSpeedAverage = line.substring(start, end);
							
							break;
						}
						
						if (!lineNow && line.contains("Velocit")) {
							lineNow = true;
						}
					}
					System.out.println(windSpeed + " " + windSpeedAverage + " " + windDirection);
					
					Writer output;
					output = new BufferedWriter(new FileWriter("porto_corsini.csv", true)); // clears file every time
					String key = hour.substring(0, 2);
					output.append(String.join(",", date, hour, windSpeed, windSpeedAverage, windDirection, predictSpeedMap.get(key), predictDirectMap.get(key)));
					output.write(System.getProperty("line.separator"));
					output.close();
					
					reader.close();
					TimeUnit.MINUTES.sleep(30);
					
					//TODO install on a server
					//TODO run forever
					
					//TODO locatrions: Porto Pollo, Campione del Garda, Santa Croce, 
				}
	};
}
