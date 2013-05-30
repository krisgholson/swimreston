package org.rsta.swimreston.server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.rsta.swimreston.client.RecordsService;
import org.rsta.swimreston.shared.Record;

import com.google.appengine.api.rdbms.AppEngineDriver;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class RecordsServiceImpl extends RemoteServiceServlet implements
		RecordsService {

	private static final long serialVersionUID = 1L;

	public List<Record> getRecords() {
		return selectFromDb();
	}

	private List<Record> selectFromDb() {

		List<Record> records = new ArrayList<Record>();
		Connection c = null;
		try {

			DriverManager.registerDriver(new AppEngineDriver());
			c = DriverManager
					.getConnection("jdbc:google:rdbms://swimreston:swimreston/rstadb");

			String statement = "SELECT DISTINCT (record_name), record_description FROM record ORDER BY record_name";
			PreparedStatement stmt = c.prepareStatement(statement);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				records.add(new Record(rs.getString("record_name"), rs
						.getString("record_description")));
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
		return records;

	}
}
