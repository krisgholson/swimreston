package org.rsta.swimreston.server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.rsta.swimreston.client.RecordDetailsService;
import org.rsta.swimreston.shared.RecordDetail;

import com.google.appengine.api.rdbms.AppEngineDriver;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class RecordDetailsServiceImpl extends RemoteServiceServlet implements
		RecordDetailsService {

	private static final long serialVersionUID = 1L;

	@Override
	public List<RecordDetail> getRecordDetails(String name) {
		return selectFromDb(name);
	}

	private List<RecordDetail> selectFromDb(String name) {

		List<RecordDetail> recordDetails = new ArrayList<RecordDetail>();
		Connection c = null;
		try {

			DriverManager.registerDriver(new AppEngineDriver());
			c = DriverManager.getConnection(ServerConstants.DB_URL);

			String statement = "select * from record where record_name = ?";
			PreparedStatement stmt = c.prepareStatement(statement);
			stmt.setString(1, name);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {

				RecordDetail detail = new RecordDetail();

				detail.setName(rs.getString("record_name"));
				detail.setDescription(rs.getString("record_description"));
				detail.setLowAge(rs.getInt("low_age"));
				detail.setHighAge(rs.getInt("high_age"));
				detail.setText(rs.getString("text"));
				detail.setDate(rs.getDate("date"));
				detail.setTime(rs.getInt("time"));
				detail.setTeam(rs.getString("team"));
				detail.setDistance(rs.getInt("distance"));
				detail.setStroke(rs.getInt("stroke"));
				detail.setSex(rs.getString("sex"));
				detail.setRelay(rs.getBoolean("is_relay"));

				recordDetails.add(detail);
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
		return recordDetails;

	}

}
