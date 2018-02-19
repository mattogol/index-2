package model;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;


public class Team {

	public String name;
	public Integer code;
	
	public ArrayList<Float> results; // Lista contenente i punteggi della squadra ad ogni giornata.
	public ArrayList<Integer> goals;
	public ArrayList<Integer[]> points; // Lista di array di tutti i possibili punti ottenuti
	public ArrayList<Float> points_mean; // Lista di tutti punti medi ottenuti
	public ArrayList<Float[]> points_FF; // Lista di array di tutti i possibili punti ottenibili. Calcolo FINE (FF)
	public ArrayList<Float> points_mean_FF; // Lista di tutti punti medi ottenibili
	public ArrayList<Integer> points_fatti; // Lista di tutti punti reali ottenuti
	public ArrayList<Float> indexes;
	public ArrayList<Integer> avversari;
	
	public Float punti_classifica;
	public Integer punti_classifica_real;
	public Float punti_classifica_FF;
	
	public Team(String name, Integer code) {
		this.name = name;
		this.code = code;
		this.results = new ArrayList<Float>();
		this.goals = new ArrayList<Integer>();
		this.points = new ArrayList<Integer[]>();
		this.points_mean = new ArrayList<Float>();
		this.points_FF = new ArrayList<Float[]>();
		this.points_mean_FF = new ArrayList<Float>();
		this.points_fatti = new ArrayList<Integer>();
		this.indexes = new ArrayList<Float>();
		this.punti_classifica = 0f;
		this.punti_classifica_real = 0;
		this.punti_classifica_FF = 0f;
		
		this.avversari = new ArrayList<Integer>();
		File file = new File("src/main/resources/calendar.txt");
        Scanner scnr = null;
        try {
            scnr = new Scanner(file);
        } catch (FileNotFoundException e) {
        	e.printStackTrace();
        }
        while(scnr.hasNext()) {
            String line = scnr.nextLine();
            String[] ids = line.split("-");
            if (Integer.parseInt(ids[0]) == this.code) {
            	this.avversari.add(Integer.parseInt(ids[1]));
            	if (ids.length > 2) {
            		this.results.add(Float.parseFloat(ids[2]));
            		this.goals.add(getGoals(Float.parseFloat(ids[2])));
            	}
            } else if (Integer.parseInt(ids[1]) == this.code) {
            	this.avversari.add(Integer.parseInt(ids[0]));
            	if (ids.length > 2) {
            		this.results.add(Float.parseFloat(ids[3]));
            		this.goals.add(getGoals(Float.parseFloat(ids[3])));
            	}
            }
        }
	}
	
	private Integer getGoals(Float result){
		Integer goals = new Integer(0);
		if (result < 66 ) return goals;
		else if (result >= 66 && result < 72) goals = 1;
		else if (result >= 72 && result < 78) goals = 2;
		else if (result >= 78 && result < 84) goals = 3;
		else if (result >= 84 && result < 90) goals = 4;
		else if (result >= 90 && result < 96) goals = 5;
		else if (result >= 96 && result < 102) goals = 6;
		else if (result >= 102 && result < 108) goals = 7;
		else System.out.println("ATTENZIONE: Punteggio troppo alto non gestito: "+result);
		
		return goals;
	}
	
	public Integer getPuntiPartita(Integer goalsAvv, Integer day){
		return this.goals.get(day.intValue()) > goalsAvv ? 3 : this.goals.get(day.intValue()).equals(goalsAvv) ? 1 : 0;
	}
	
	public Float getPuntiPartita_FF(Float resultAvv, Integer day, Integer pti_fatti){
		Float delta = this.results.get(day) - resultAvv;
		
		if (delta>=6) {
			return 3f; // Vinco
		} else if (delta<=-6) {
			return 0f; // Perdo
		} else if (delta==0) {
			return 1f; // pareggio
		} else if (delta > 0) {
			return (delta/6)*3 + (6-delta)/6; // Vinco o Pareggio
		} else if (delta < 0) {
			return (-delta/6)*0 + (6+delta)/6; // Perdo o Pareggio
		} else {
			System.out.println("QUI NON CI ANDRA' MAI");
		}
		return null;
	}
	
	public Float getSommaPunti(){
		Float somma_punti = 0f;
		
		for(Float punti : this.results){
			somma_punti += punti;
		}
		return somma_punti;
	}
	
	public Float getClassisfica(){
		Float f = 0f;
		
		for(int i=0;i<this.points_fatti.size();i++){
			f += this.points_fatti.get(i) - this.points_mean_FF.get(i);
		}
		return f;
	}
}
