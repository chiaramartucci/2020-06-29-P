package it.polito.tdp.PremierLeague.model;

public class Arco {
	private Match match1;
	private Match match2;
	private Double peso;
	
	public Arco(Match match1, Match match2, Double peso) {
		super();
		this.match1 = match1;
		this.match2 = match2;
		this.peso = peso;
	}

	public Match getMatch1() {
		return match1;
	}

	public void setMatch1(Match match1) {
		this.match1 = match1;
	}

	public Match getMatch2() {
		return match2;
	}

	public void setMatch2(Match match2) {
		this.match2 = match2;
	}

	public Double getPeso() {
		return peso;
	}

	public void setPeso(Double peso) {
		this.peso = peso;
	}

	@Override
	public String toString() {
		return "Arco [match1=" + match1 + ", match2=" + match2 + ", peso=" + peso + "]";
	}
	
	
}

