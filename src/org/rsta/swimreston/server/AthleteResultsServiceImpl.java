package org.rsta.swimreston.server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.rsta.swimreston.client.AthleteResultsService;
import org.rsta.swimreston.shared.Athlete;
import org.rsta.swimreston.shared.Meet;
import org.rsta.swimreston.shared.Result;
import org.rsta.swimreston.shared.Team;

import com.google.appengine.api.rdbms.AppEngineDriver;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class AthleteResultsServiceImpl extends RemoteServiceServlet implements
		AthleteResultsService {

	private static final long serialVersionUID = 1L;

	@Override
	public List<Result> findAthleteResults(Athlete athlete) {
		return selectFromDb(athlete.getId(), athlete.getYear());
	}

	private List<Result> selectFromDb(Integer athleteId, Integer year) {

		List<Result> results = new ArrayList<Result>();
		Connection c = null;
		try {

			DriverManager.registerDriver(new AppEngineDriver());
			c = DriverManager.getConnection(ServerConstants.DB_URL);
			String statement = "select * from result where athlete_id = ? and year = ? order by meet_date desc";
			PreparedStatement stmt = c.prepareStatement(statement);
			stmt.setInt(1, athleteId);
			stmt.setInt(2, year);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {

				int yr = rs.getInt("year");
				Result result = new Result();
				result.setId(rs.getInt("id"));
				result.setRelay(rs.getBoolean("is_relay"));
				result.setScore(rs.getInt("score"));
				result.setNoTime(rs.getBoolean("is_no_time"));
				result.setAge(rs.getInt("age"));
				result.setDistance(rs.getInt("distance"));
				result.setStroke(rs.getInt("stroke"));
				result.setPoints(rs.getInt("points"));
				result.setPlace(rs.getInt("place"));
				result.setRank(rs.getInt("rank"));
				result.setYear(yr);

				Meet meet = new Meet();
				meet.setId(rs.getInt("meet_id"));
				meet.setName(rs.getString("meet_name"));
				meet.setDate(rs.getDate("meet_date"));
				meet.setLocation(rs.getString("meet_location"));
				meet.setYear(yr);

				result.setMeet(meet);

				Team team = new Team();
				team.setId(rs.getInt("team_id"));
				team.setCode(rs.getString("team_code"));
				team.setName(rs.getString("team_name"));
				team.setShortName(rs.getString("team_short_name"));
				team.setYear(yr);

				result.setTeam(team);

				Athlete athlete = new Athlete();
				athlete.setId(rs.getInt("athlete_id"));
				athlete.setFirstName(rs.getString("athlete_first_name"));
				athlete.setMiddleInitial(rs.getString("athlete_middle_initial"));
				athlete.setLastName(rs.getString("athlete_last_name"));
				athlete.setSex(rs.getString("athlete_sex"));
				athlete.setAge(rs.getInt("athlete_age"));
				athlete.setTeam(team);

				result.setAthlete(athlete);

				results.add(result);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (c != null)
				try {
					c.close();
				} catch (SQLException ignore) {
				}
		}
		return results;

	}

}
