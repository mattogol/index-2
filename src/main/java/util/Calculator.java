package util;

import java.util.List;

import model.Player;

public class Calculator {

	public static final Integer GF_BONUS = 3;
	public static final Integer GS_MALUS = -1;
	public static final Integer RP_BONUS = 3;
	public static final Integer RS_MALUS = -3;
	public static final Integer RF_BONUS = 3;
	public static final Integer AU_MALUS = -2;
	public static final Float AMM_MALUS = -0.5f;
	public static final Integer ESP_MALUS = -1;
	public static final Integer ASS_MALUS = 1;
	public static final Integer ASF_MALUS = 1;
	public static final Integer GDV_MALUS = 0;
	public static final Integer GDP_MALUS = 0;
	
	public static Float fantavote(Player player, int day) {
		
		Float toReturn = new Float(0);
		toReturn += player.getVote().get(day-1);
		toReturn += player.getGf().get(day-1) * GF_BONUS;
		toReturn += player.getGs().get(day-1) * GS_MALUS;
		toReturn += player.getRp().get(day-1) * RP_BONUS;
		toReturn += player.getRs().get(day-1) * RS_MALUS;
		toReturn += player.getRf().get(day-1) * RF_BONUS;
		toReturn += player.getAu().get(day-1) * AU_MALUS;
		toReturn += player.getAmm().get(day-1) * AMM_MALUS;
		toReturn += player.getEsp().get(day-1) * ESP_MALUS;
		toReturn += player.getAss().get(day-1) * ASS_MALUS;
		toReturn += player.getAsf().get(day-1) * ASF_MALUS;
		toReturn += player.getGdv().get(day-1) * GDV_MALUS;
		toReturn += player.getGdp().get(day-1) * GDP_MALUS;
		
		return toReturn;
	}
	
	public static Float average(List<Float> list) {
		
		Float sum = 0f;
		int tot = 0;
		for(Float el : list) { 
			if (el != null) {
				sum += el;
				tot += 1;
			}
		}
		if (tot > 0)
			return sum / tot;
		else
			return null;
	}
	
	public static int total(List<Integer> list) {
		int tot = 0;
		for(Integer el : list) { 
			if (el != null) {
				tot += el;
			}
		}
		return tot;
	}
	
	public static int count(List<Float> list) {
		
		int tot = 0;
		for(Float el : list) { 
			if (el != null) {
				tot += 1;
			}
		}
		return tot;
	}

	public static Double stdDeviation(List<Float> list) {
		return Math.sqrt(variance(list));
	}
	
	public static double variance(List<Float> list) {

		Float mean = average(list);
        double temp = 0f;
        for(Float a : list) {
        	if (a != null)
        		temp += (a-mean)*(a-mean);
        }
        return temp/(count(list) - 1);
	}
}
