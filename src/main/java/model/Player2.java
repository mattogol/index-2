package model;

import java.util.List;
import java.util.Vector;

public class Player2 {

	private Integer id; 
	private Role role;
	private String name;
	private List<Float> vote;
	private List<Integer> gf;
	private List<Integer> gs;
	private List<Integer> rp;
	private List<Integer> rs;
	private List<Integer> rf;
	private List<Integer> au;
	private List<Integer> amm;
	private List<Integer> esp;
	private List<Integer> ass;
	private List<Integer> asf;
	private List<Integer> gdv;
	private List<Integer> gdp;
	
	private List<Float> fantavote;

	@SuppressWarnings("serial")
	public Player2(Integer id, Role role, String name) {
		super();
		this.id = id;
		this.role = role;
		this.name = name;
		this.vote = new Vector<Float>() {{setSize(38);}};
		this.gf = new Vector<Integer>() {{setSize(38);}};
		this.gs = new Vector<Integer>() {{setSize(38);}};
		this.rp = new Vector<Integer>() {{setSize(38);}};
		this.rs = new Vector<Integer>() {{setSize(38);}};
		this.rf = new Vector<Integer>() {{setSize(38);}};
		this.au = new Vector<Integer>() {{setSize(38);}};
		this.amm = new Vector<Integer>() {{setSize(38);}};
		this.esp = new Vector<Integer>() {{setSize(38);}};
		this.ass = new Vector<Integer>() {{setSize(38);}};
		this.asf = new Vector<Integer>() {{setSize(38);}};
		this.gdv = new Vector<Integer>() {{setSize(38);}};
		this.gdp = new Vector<Integer>() {{setSize(38);}};
		this.fantavote = new Vector<Float>() {{setSize(38);}};
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Float> getVote() {
		return vote;
	}

	public void setVote(List<Float> vote) {
		this.vote = vote;
	}

	public List<Integer> getGf() {
		return gf;
	}

	public void setGf(List<Integer> gf) {
		this.gf = gf;
	}

	public List<Integer> getGs() {
		return gs;
	}

	public void setGs(List<Integer> gs) {
		this.gs = gs;
	}

	public List<Integer> getRp() {
		return rp;
	}

	public void setRp(List<Integer> rp) {
		this.rp = rp;
	}

	public List<Integer> getRs() {
		return rs;
	}

	public void setRs(List<Integer> rs) {
		this.rs = rs;
	}

	public List<Integer> getRf() {
		return rf;
	}

	public void setRf(List<Integer> rf) {
		this.rf = rf;
	}

	public List<Integer> getAu() {
		return au;
	}

	public void setAu(List<Integer> au) {
		this.au = au;
	}

	public List<Integer> getAmm() {
		return amm;
	}

	public void setAmm(List<Integer> amm) {
		this.amm = amm;
	}

	public List<Integer> getEsp() {
		return esp;
	}

	public void setEsp(List<Integer> esp) {
		this.esp = esp;
	}

	public List<Integer> getAss() {
		return ass;
	}

	public void setAss(List<Integer> ass) {
		this.ass = ass;
	}

	public List<Integer> getAsf() {
		return asf;
	}

	public void setAsf(List<Integer> asf) {
		this.asf = asf;
	}

	public List<Integer> getGdv() {
		return gdv;
	}

	public void setGdv(List<Integer> gdv) {
		this.gdv = gdv;
	}

	public List<Integer> getGdp() {
		return gdp;
	}

	public void setGdp(List<Integer> gdp) {
		this.gdp = gdp;
	}

	public List<Float> getFantavote() {
		return fantavote;
	}

	public void setFantavote(List<Float> fantavote) {
		this.fantavote = fantavote;
	}
	
	public boolean hasBonusMalus(int day) {
		return  gf.get(day-1) != null || 
				gs.get(day -1) != null || 
				rp.get(day -1) != null || 
				rs.get(day -1) != null || 
				rf.get(day -1) != null || 
				au.get(day -1) != null || 
				amm.get(day -1) != null || 
				esp.get(day -1) != null || 
				ass.get(day -1) != null || 
				asf.get(day -1) != null || 
				gdv.get(day -1) != null || 
				gdp.get(day -1) != null;
	}
	
}
