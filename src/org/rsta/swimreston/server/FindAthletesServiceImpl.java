package org.rsta.swimreston.server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.rsta.swimreston.client.FindAthletesService;
import org.rsta.swimreston.shared.Athlete;
import org.rsta.swimreston.shared.Team;

import com.google.appengine.api.rdbms.AppEngineDriver;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class FindAthletesServiceImpl extends RemoteServiceServlet implements
		FindAthletesService {

	private static final long serialVersionUID = 1L;

	@Override
	public List<Athlete> findAthletes(String namePart) {
		return selectFromDb(namePart);
	}

	private List<Athlete> selectFromDb(String namePart) {

		List<Athlete> athletes = new ArrayList<Athlete>();
		Connection c = null;
		try {

			DriverManager.registerDriver(new AppEngineDriver());
			c = DriverManager
					.getConnection(ServerConstants.DB_URL);
			String statement = "select * from athlete where last_name like ? or first_name like ? order by last_name, first_name, middle_initial";
			PreparedStatement stmt = c.prepareStatement(statement);
			String likeNameQuery = "%" + namePart + "%";
			stmt.setString(1, likeNameQuery);
			stmt.setString(2, likeNameQuery);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {

				Athlete athlete = new Athlete();
				athlete.setId(rs.getInt("id"));
				athlete.setFirstName(rs.getString("first_name"));
				athlete.setMiddleInitial(rs.getString("middle_initial"));
				athlete.setLastName(rs.getString("last_name"));
				athlete.setSex(rs.getString("sex"));
				athlete.setAge(rs.getInt("age"));
				athlete.setYear(rs.getInt("year"));

				Team team = new Team();
				team.setId(rs.getInt("team_id"));
				team.setCode(rs.getString("team_code"));
				team.setName(rs.getString("team_name"));
				team.setShortName(rs.getString("team_short_name"));
				team.setYear(rs.getInt("year"));

				athlete.setTeam(team);

				athletes.add(athlete);
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
		return athletes;

	}
}
