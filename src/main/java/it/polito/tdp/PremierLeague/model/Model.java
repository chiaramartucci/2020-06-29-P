package it.polito.tdp.PremierLeague.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import it.polito.tdp.PremierLeague.db.PremierLeagueDAO;

public class Model {
	
	private PremierLeagueDAO dao = new PremierLeagueDAO ();
	private Graph <Match, DefaultWeightedEdge> grafo;
	private Map<Integer, Match> idMapMatch;
	private Integer peso;
	
	private List<Match> longest;
	
	public void creaGrafo (Integer mese, Integer minuti) {
		this.grafo = new SimpleWeightedGraph <Match, DefaultWeightedEdge> (DefaultWeightedEdge.class);
		
		// vertici
		List<Match> vertici = dao.listAllMatches(mese);
		Graphs.addAllVertices(this.grafo, vertici);
		
		this.idMapMatch = new HashMap<Integer, Match> ();
		for(Match m: vertici) {
			idMapMatch.put(m.getMatchID(), m);
		}
		
		// archi
		for(Arco a: dao.listArchi(mese, minuti, idMapMatch)) {
			Graphs.addEdge(this.grafo, a.getMatch1(), a.getMatch2(), a.getPeso());
		}
	}

	public Graph<Match, DefaultWeightedEdge> getGrafo() {
		return grafo;
	}
	
	public List<DefaultWeightedEdge> getConnessioneMassima (){
		double pesoMax =0.0;
		List<DefaultWeightedEdge> result = new ArrayList<>();
		
		for(DefaultWeightedEdge e: this.grafo.edgeSet()) {
			if(this.grafo.getEdgeWeight(e) > pesoMax) {
				pesoMax = this.grafo.getEdgeWeight(e);
			}
		}
		
		for(DefaultWeightedEdge e: this.grafo.edgeSet()) {
			if(this.grafo.getEdgeWeight(e) == pesoMax) {
				result.add(e);
			}
		}
		
		return result;
	}
	
	public List<Match> trovaPercorso (Match partenza, Match arrivo){
		this.longest = new ArrayList<>();
		List<Match> parziale = new ArrayList<>();
		
		// inizializzo 
		parziale.add(partenza);
		ricorsione(parziale, arrivo, 0);
		this.peso = 0;
		return this.longest;
	}
	
	private void ricorsione(List<Match> parziale, Match arrivo, Integer pesoParziale) {
		
		if (parziale.contains(arrivo)) {
			if(pesoParziale>this.peso) {
				this.peso = pesoParziale;
				this.longest= new ArrayList<>(parziale);
			}
			return;
		}
		
		Match ultimo = parziale.get(parziale.size()-1);
		List<Match> vicini = Graphs.neighborListOf(this.grafo, ultimo);
		Integer t1 = parziale.get(parziale.size()-1).getTeamHomeID();
		Integer t2 = parziale.get(parziale.size()-1).getTeamAwayID();
		
		for(Match vicino: vicini) {
			
			Integer v1 = vicino.getTeamHomeID();
			Integer v2 = vicino.getTeamAwayID();
			
			if(!parziale.contains(vicino) && (t1!=v1 && t1 != v2) && (t2!= v1 && t2!=v2)) {
				parziale.add(vicino);
				int nuovoPeso = (int) (pesoParziale + this.grafo.getEdgeWeight(this.grafo.getEdge(ultimo, vicino)));
				ricorsione(parziale, arrivo, nuovoPeso);
				parziale.remove(vicino);
				
			}
		}
		
	}

	public List<Match> getLongest() {
		return longest;
	}
}
