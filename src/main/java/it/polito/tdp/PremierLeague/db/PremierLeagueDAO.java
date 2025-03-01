package it.polito.tdp.PremierLeague.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import it.polito.tdp.PremierLeague.model.Action;
import it.polito.tdp.PremierLeague.model.Arco;
import it.polito.tdp.PremierLeague.model.Match;
import it.polito.tdp.PremierLeague.model.Player;

public class PremierLeagueDAO {
	
	public List<Player> listAllPlayers(){
		String sql = "SELECT * FROM Players";
		List<Player> result = new ArrayList<Player>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				Player player = new Player(res.getInt("PlayerID"), res.getString("Name"));
				
				result.add(player);
			}
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<Action> listAllActions(){
		String sql = "SELECT * FROM Actions";
		List<Action> result = new ArrayList<Action>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				Action action = new Action(res.getInt("PlayerID"),res.getInt("MatchID"),res.getInt("TeamID"),res.getInt("Starts"),res.getInt("Goals"),
						res.getInt("TimePlayed"),res.getInt("RedCards"),res.getInt("YellowCards"),res.getInt("TotalSuccessfulPassesAll"),res.getInt("totalUnsuccessfulPassesAll"),
						res.getInt("Assists"),res.getInt("TotalFoulsConceded"),res.getInt("Offsides"));
				
				result.add(action);
			}
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<Match> listAllMatches(Integer mese){
		String sql = "SELECT m.MatchID, m.TeamHomeID, m.TeamAwayID, m.teamHomeFormation, m.teamAwayFormation, m.resultOfTeamHome, m.date, t1.Name, t2.Name   "
				+ " FROM Matches m, Teams t1, Teams t2 "
				+ " WHERE m.TeamHomeID = t1.TeamID AND m.TeamAwayID = t2.TeamID"
				+ " AND MONTH(m.date) = ?";
		List<Match> result = new ArrayList<Match>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1, mese);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				
				Match match = new Match(res.getInt("m.MatchID"), res.getInt("m.TeamHomeID"), res.getInt("m.TeamAwayID"), res.getInt("m.teamHomeFormation"), 
							res.getInt("m.teamAwayFormation"),res.getInt("m.resultOfTeamHome"), res.getTimestamp("m.date").toLocalDateTime(), res.getString("t1.Name"),res.getString("t2.Name"));
				
				
				result.add(match);

			}
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List <Arco> listArchi (Integer mese, Integer minuti, Map<Integer, Match> idMap) {
		String sql= "SELECT m1.MatchID, m2.MatchID"
				+ " FROM matches AS m1, matches m2,  actions AS a1 , actions AS a2"
				+ " WHERE MONTH(m1.Date)=? AND MONTH(m2.Date)=? AND a1.MatchID=m1.MatchID "
				+ " AND a1.TimePlayed > ? AND a2.TimePlayed>? AND a2.MatchID=m2.MatchID "
				+ " AND m1.MatchID!=m2.MatchID AND m1.MatchID > m2.MatchID "
				+ " AND a1.PlayerID=a2.PlayerID"
				+ " GROUP BY m1.MatchID, m2.MatchID";
		
		List<Arco> result = new ArrayList<>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1, mese);
			st.setInt(2, mese);
			st.setInt(3, minuti);
			st.setInt(4, minuti);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				Arco arco = new Arco(idMap.get(res.getInt("m1.MatchID")), idMap.get(res.getInt("m2.MatchID")), getPeso(minuti, res.getInt("m1.MatchID"), res.getInt("m2.MatchID")));
				result.add(arco);
			}
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}

	}
	
	public Double getPeso(Integer minuti, Integer id1, Integer id2) {
		String sql = "SELECT a1.MatchID, a1.PlayerID, a2.MatchID, a2.PlayerID "
				+ "FROM matches AS m1, actions AS a1, matches AS m2, actions AS a2 "
				+ "WHERE m1.MatchID= ? AND m2.MatchID = ? AND m1.MatchID=a1.MatchID AND m2.MatchID=a2.MatchID "
				+ "AND a2.TimePlayed >= ? AND a1.TimePlayed >= ?  "
				+ "AND a1.PlayerID = a2.PlayerID "
				+ "GROUP BY a2.PlayerID, a1.PlayerID";
		
		Double result = 0.0;
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1, id1);
			st.setInt(2, id2);
			st.setInt(3, minuti);
			st.setInt(4, minuti);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				result ++;
				
			}
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		
	}
	
}
