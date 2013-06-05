package com.rsta.data.db;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

import javax.swing.DefaultListModel;

import com.rsta.app.eventData;

public class dataAccess {
	private Connection dbConnection;
	private Properties dbProperties;
	private boolean isConnected;
	private String dbName;
	private PreparedStatement stmtSaveImportRecord;
	private PreparedStatement stmtSaveScoreRecord;
	private PreparedStatement stmtFindImportRecord;
	private PreparedStatement stmtFindSwimmerRecord;
	private PreparedStatement stmtInsertSwimmerRecord;
	private PreparedStatement stmtSaveEventRecord;
	private PreparedStatement stmtUpdateExistingRecord;

	public dataAccess(String dbName) {

		this.setDBSystemDir();
		dbProperties = this.loadDBProperties();
		String driverName = dbProperties.getProperty("derby.driver");
		loadDatabaseDriver(driverName);

		this.dbName = dbName;
		if (!this.dbExists()) {
			this.createDatabase();
		}
	}

	private boolean dbExists() {
		boolean bExists = false;
		String dbLocation = this.getDatabaseLocation();
		File dbFileDir = new File(dbLocation);
		if (dbFileDir.exists()) {
			bExists = true;
		}
		return bExists;
	}

	private void setDBSystemDir() {
		// decide on the db system directory
		String userHomeDir = System.getProperty("user.home", ".");
		String systemDir = userHomeDir + "/rsta_data";
		System.setProperty("derby.system.home", systemDir);

		// create the db system directory
		File fileSystemDir = new File(systemDir);
		fileSystemDir.mkdir();
	}

	private void loadDatabaseDriver(String driverName) {
		// load Derby driver
		try {
			Class.forName(driverName);
		} catch (ClassNotFoundException ex) {
			ex.printStackTrace();
		}

	}

	private Properties loadDBProperties() {
		InputStream dbPropInputStream = null;
		dbPropInputStream = dataAccess.class
				.getResourceAsStream("Configuration.properties");
		dbProperties = new Properties();
		try {
			dbProperties.load(dbPropInputStream);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return dbProperties;
	}

	private boolean createTables(Connection dbConnection) {
		boolean bCreatedTables = false;
		Statement statement = null;
		try {
			statement = dbConnection.createStatement();
			createTeamsTable(dbConnection);

			// Create the tables
			statement.execute(strCreateSwimmersTable);
			statement.execute(strCreateImportTable);
			statement.execute(strCreateEventsTable);
			statement.execute(strCreateScoresTable);

			// Create the Views
			createViews();

			bCreatedTables = true;
		} catch (SQLException ex) {
			ex.printStackTrace();
		}

		return bCreatedTables;
	}

	public void createViews() {
		Statement statement = null;
		try {

			statement = dbConnection.createStatement();

			// Drop the views
			statement.execute("drop view RSTA.BEST_TIMES_FLY");
			statement.execute("drop view RSTA.BEST_TIMES_BACK");
			statement.execute("drop view RSTA.BEST_TIMES_BREAST");
			statement.execute("drop view RSTA.BEST_TIMES_FREE");

			// Create the Views
			statement.execute(strCreateFlyView);
			statement.execute(strCreateBackView);
			statement.execute(strCreateBreastView);
			statement.execute(strCreateFreeView);

		} catch (SQLException ex) {
			ex.printStackTrace();
		}
	}

	private void createTeamsTable(Connection dbConnection) {
		Statement statement = null;
		try {
			statement = dbConnection.createStatement();
			// Create the Teams Table
			statement.execute(strCreateTeamsTable);

			// Create a prepared statement for the insert
			String sql = "INSERT INTO RSTA.TEAMS (ID,TEAM_CODE,TEAM_NAME) VALUES(?,?,?)";
			PreparedStatement pstmt = dbConnection.prepareStatement(sql);

			// Lake Anne Stingrays
			pstmt.clearParameters();
			pstmt.setInt(1, 6);
			pstmt.setString(2, "AN");
			pstmt.setString(3, "Lake Anne Stingrays");
			pstmt.executeUpdate();

			// Autumnwood Piranhas
			pstmt.clearParameters();
			pstmt.setInt(1, 3);
			pstmt.setString(2, "AW");
			pstmt.setString(3, "Autumnwood Piranhas");
			pstmt.executeUpdate();

			// Glade Gators
			pstmt.clearParameters();
			pstmt.setInt(1, 7);
			pstmt.setString(2, "GL");
			pstmt.setString(3, "Glade Gators");
			pstmt.executeUpdate();

			// Hunters Woods Blue Marlins
			pstmt.clearParameters();
			pstmt.setInt(1, 1);
			pstmt.setString(2, "HW");
			pstmt.setString(3, "Hunters Woods Blue Marlins");
			pstmt.executeUpdate();

			// Lake Audubon Barracudas
			pstmt.clearParameters();
			pstmt.setInt(1, 8);
			pstmt.setString(2, "LA");
			pstmt.setString(3, "Lake Audubon Barracudas");
			pstmt.executeUpdate();

			// Lake Newport Lightning
			pstmt.clearParameters();
			pstmt.setInt(1, 2);
			pstmt.setString(2, "LN");
			pstmt.setString(3, "Lake Newport Lightning");
			pstmt.executeUpdate();

			// Newbridge Dolphins
			pstmt.clearParameters();
			pstmt.setInt(1, 9);
			pstmt.setString(2, "NB");
			pstmt.setString(3, "Newbridge Dolphins");
			pstmt.executeUpdate();

			// North Hills Hurricanes
			pstmt.clearParameters();
			pstmt.setInt(1, 4);
			pstmt.setString(2, "NH");
			pstmt.setString(3, "North Hills Hurricanes");
			pstmt.executeUpdate();

			// Ridge Heights Sharks
			pstmt.clearParameters();
			pstmt.setInt(1, 5);
			pstmt.setString(2, "RH");
			pstmt.setString(3, "Ridge Heights Sharks");
			pstmt.executeUpdate();

		} catch (SQLException ex) {
			ex.printStackTrace();
		}

	}

	private boolean createDatabase() {
		boolean bCreated = false;
		Connection dbConnection = null;

		String dbUrl = getDatabaseUrl();
		dbProperties.put("create", "true");

		try {
			dbConnection = DriverManager.getConnection(dbUrl, dbProperties);
			bCreated = createTables(dbConnection);
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
		dbProperties.remove("create");
		return bCreated;
	}

	public boolean connect() {
		String dbUrl = getDatabaseUrl();
		try {

			dbConnection = DriverManager.getConnection(dbUrl, dbProperties);
			stmtSaveImportRecord = dbConnection.prepareStatement(strSaveImport,
					Statement.RETURN_GENERATED_KEYS);
			stmtInsertSwimmerRecord = dbConnection.prepareStatement(
					strInsertSwimmer, Statement.RETURN_GENERATED_KEYS);
			stmtSaveEventRecord = dbConnection.prepareStatement(strSaveEvent,
					Statement.RETURN_GENERATED_KEYS);
			stmtUpdateExistingRecord = dbConnection
					.prepareStatement(strUpdateSwimmer);
			stmtSaveScoreRecord = dbConnection.prepareStatement(strSaveScore,
					Statement.RETURN_GENERATED_KEYS);

			isConnected = dbConnection != null;
		} catch (SQLException ex) {
			isConnected = false;
		}
		return isConnected;
	}

	public void setCommit(boolean lcommit) {
		if (isConnected) {
			try {
				dbConnection.setAutoCommit(lcommit);
			} catch (SQLException ex) {
				// ex.printStackTrace();
			}
		}
	}

	public void commit() {
		if (isConnected) {
			try {
				dbConnection.commit();
			} catch (SQLException ex) {
				// ex.printStackTrace();
			}
		}
	}

	public void disconnect() {
		if (isConnected) {
			String dbUrl = getDatabaseUrl();
			dbProperties.put("shutdown", "true");
			try {
				DriverManager.getConnection(dbUrl, dbProperties);
			} catch (SQLException ex) {
				// ex.printStackTrace();
			}
			isConnected = false;
		}
	}

	public String getDatabaseLocation() {
		String dbLocation = System.getProperty("derby.system.home") + "/"
				+ dbName;
		return dbLocation;
	}

	public String getDatabaseUrl() {
		String dbUrl = dbProperties.getProperty("derby.url") + dbName;
		return dbUrl;
	}

	public static void main(String[] args) {
		final String dbName;
		if (args.length > 0) {
			dbName = args[0];
		} else {
			dbName = "RSTA";
		}
		dataAccess db = new dataAccess(dbName);
		System.out.println(db.getDatabaseLocation());
		System.out.println(db.getDatabaseUrl());
		db.connect();
		db.disconnect();
	}

	public boolean swimmerRecordExists(eventData record) {
		boolean found = false;
		try {

			stmtFindSwimmerRecord = dbConnection
					.prepareStatement(strGetSwimmer);
			stmtFindSwimmerRecord.setString(1, record.id);
			ResultSet results = stmtFindSwimmerRecord.executeQuery();

			if (results.next()) {
				found = true;
			}

		} catch (SQLException sqle) {
			sqle.printStackTrace();
		}
		return found;
	}

	public int insertSwimmerRow(eventData record) {
		int id = -1;
		try {

			stmtInsertSwimmerRecord.clearParameters();
			stmtInsertSwimmerRecord.setString(1, record.id);
			stmtInsertSwimmerRecord.setString(2, record.fullName);
			stmtInsertSwimmerRecord.setString(3, record.lastName);
			stmtInsertSwimmerRecord.setString(4, record.firstName);
			stmtInsertSwimmerRecord.setInt(5, record.age);
			stmtInsertSwimmerRecord.setString(6, record.ageGroup);
			stmtInsertSwimmerRecord.setString(7, record.ageCode);
			stmtInsertSwimmerRecord.setString(8, record.genderCode);
			stmtInsertSwimmerRecord.setString(9, record.genderName);
			stmtInsertSwimmerRecord.setInt(10, record.teamId);
			stmtInsertSwimmerRecord.setString(11, record.teamCode);
			stmtInsertSwimmerRecord.setString(12, record.teamName);

			stmtInsertSwimmerRecord.setString(13, "Y"); // All Stars

			stmtInsertSwimmerRecord.setString(14, "Y"); // Prefer Fly
			stmtInsertSwimmerRecord.setString(15, "N"); // Selected Fly

			stmtInsertSwimmerRecord.setString(16, "Y"); // Prefer Back
			stmtInsertSwimmerRecord.setString(17, "N"); // Selected Back

			stmtInsertSwimmerRecord.setString(18, "Y"); // Prefer Breast
			stmtInsertSwimmerRecord.setString(19, "N"); // Selected Breast

			stmtInsertSwimmerRecord.setString(20, "Y"); // Prefer Free
			stmtInsertSwimmerRecord.setString(21, "N"); // Selected Free

			stmtInsertSwimmerRecord.executeUpdate();
			ResultSet results = stmtInsertSwimmerRecord.getGeneratedKeys();
			if (results.next()) {
				id = results.getInt(1);
			}

		} catch (SQLException sqle) {
			sqle.printStackTrace();
		}
		return id;
	}

	public boolean importRecordExists(eventData record) {
		boolean found = false;
		try {

			stmtFindImportRecord = dbConnection
					.prepareStatement(strFindImportRecord);
			stmtFindImportRecord.setString(1, record.meetId);
			ResultSet results = stmtFindImportRecord.executeQuery();

			if (results.next()) {
				found = true;
			}
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		}
		return found;
	}

	public int saveImportRow(eventData record) {
		int id = -1;
		try {
			stmtSaveImportRecord.clearParameters();
			stmtSaveImportRecord.setString(1, record.fullName);
			stmtSaveImportRecord.setString(2, record.lastName);
			stmtSaveImportRecord.setString(3, record.firstName);
			stmtSaveImportRecord.setInt(4, record.age);
			stmtSaveImportRecord.setString(5, record.ageGroup);
			stmtSaveImportRecord.setString(6, record.ageCode);
			stmtSaveImportRecord.setString(7, record.genderCode);
			stmtSaveImportRecord.setString(8, record.genderName);
			stmtSaveImportRecord.setInt(9, record.teamId);
			stmtSaveImportRecord.setString(10, record.teamCode);
			stmtSaveImportRecord.setString(11, record.teamName);
			stmtSaveImportRecord.setString(12, record.meetId);
			stmtSaveImportRecord.setString(13, record.meetName);
			stmtSaveImportRecord.setDate(14, record.meetDate);
			stmtSaveImportRecord.setString(15, record.eventName);
			stmtSaveImportRecord.setString(16, record.eventDistance);
			stmtSaveImportRecord.setString(17, record.eventTimeDisp);
			stmtSaveImportRecord.setDouble(18, record.eventTime);
			stmtSaveImportRecord.setString(19, record.eventStandard);
			stmtSaveImportRecord.setInt(20, record.eventPlace);
			stmtSaveImportRecord.setDouble(21, record.eventPoints);
			stmtSaveImportRecord.setInt(22, record.eventNumber);

			stmtSaveImportRecord.executeUpdate();
			ResultSet results = stmtSaveImportRecord.getGeneratedKeys();
			if (results.next()) {
				id = results.getInt(1);
			}

		} catch (SQLException sqle) {
			sqle.printStackTrace();
		}
		return id;
	}

	public int saveScore(String MeetName, MeetTeam record) {
		int id = -1;
		try {
			stmtSaveScoreRecord.clearParameters();

			stmtSaveScoreRecord.setString(1, MeetName);
			stmtSaveScoreRecord.setString(2, record.getTeamCode());
			stmtSaveScoreRecord.setString(3, record.getTeamName());
			stmtSaveScoreRecord.setDouble(4, record.getTotalPoints());
			stmtSaveScoreRecord.setDouble(5, record.getApoints());
			stmtSaveScoreRecord.setDouble(6, record.getBpoints());
			stmtSaveScoreRecord.setDouble(7, record.getCpoints());
			stmtSaveScoreRecord.setDouble(8, record.getU8points());
			stmtSaveScoreRecord.setDouble(9, record.get910points());
			stmtSaveScoreRecord.setDouble(10, record.get1112points());
			stmtSaveScoreRecord.setDouble(11, record.get1314points());
			stmtSaveScoreRecord.setDouble(12, record.get1518points());
			stmtSaveScoreRecord.setDouble(13, record.getRelaypoints());

			stmtSaveScoreRecord.setInt(14, record.getWin_Meet());
			stmtSaveScoreRecord.setInt(15, record.getWin_A());
			stmtSaveScoreRecord.setInt(16, record.getWin_B());
			stmtSaveScoreRecord.setInt(17, record.getWin_C());
			stmtSaveScoreRecord.setInt(18, record.getU8());
			stmtSaveScoreRecord.setInt(19, record.get910());
			stmtSaveScoreRecord.setInt(20, record.get1112());
			stmtSaveScoreRecord.setInt(21, record.get1314());
			stmtSaveScoreRecord.setInt(22, record.get1518());

			stmtSaveScoreRecord.executeUpdate();
			ResultSet results = stmtSaveScoreRecord.getGeneratedKeys();
			if (results.next()) {
				id = results.getInt(1);
			}

		} catch (SQLException sqle) {
			sqle.printStackTrace();
		}
		return id;
	}

	public int saveEventRow(eventData record) {
		int id = -1;
		try {
			// "(SWIM_ID, MEET_ID, MEET_NAME, MEET_DATE, EVENT_NAME, EVENT_DIST, EVENT_TIME, EVENT_SEC, EVENT_STD, EVENT_PLACE, EVENT_PTS, EVENT_NUM) "
			stmtSaveEventRecord.clearParameters();
			stmtSaveEventRecord.setString(1, record.id);
			stmtSaveEventRecord.setString(2, record.meetName);
			stmtSaveEventRecord.setDate(3, record.meetDate);
			stmtSaveEventRecord.setString(4, record.eventName);
			stmtSaveEventRecord.setString(5, record.eventDistance);
			stmtSaveEventRecord.setString(6, record.eventTimeDisp);
			stmtSaveEventRecord.setDouble(7, record.eventTime);
			stmtSaveEventRecord.setString(8, record.eventStandard);
			stmtSaveEventRecord.setInt(9, record.eventPlace);
			stmtSaveEventRecord.setDouble(10, record.eventPoints);
			stmtSaveEventRecord.setInt(11, record.eventNumber);

			stmtSaveEventRecord.executeUpdate();
			ResultSet results = stmtSaveEventRecord.getGeneratedKeys();
			if (results.next()) {
				id = results.getInt(1);
			}

		} catch (SQLException sqle) {
			sqle.printStackTrace();
		}
		return id;
	}

	/**
	 * Return a DefaultListModel of Meets that have been loaded into the system.
	 * This is called by the Delete Meet Dialog Box.
	 */
	public DefaultListModel<String> getMeetEntries() {
		DefaultListModel<String> listModel = new DefaultListModel<String>();
		Statement queryStatement = null;
		ResultSet results = null;
		SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yy");

		try {
			queryStatement = dbConnection.createStatement();
			results = queryStatement.executeQuery(strGetMeetEntries);
			while (results.next()) {
				String MeetName = results.getString(1);
				Date MeetDate = results.getDate(2);
				listModel.addElement(formatter.format(MeetDate) + " ~ "
						+ MeetName);
			}
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		}
		return listModel;
	}

	/**
	 * Return a DefaultListModel of Meet Date that have been loaded into the
	 * system. This is called by....
	 */
	public Vector<String> getMeetDates() {
		Vector<String> dates = new Vector<String>();
		Statement queryStatement = null;
		ResultSet results = null;
		// SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yy");
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		dates.add(""); // Lead with a blank date

		try {
			queryStatement = dbConnection.createStatement();
			results = queryStatement.executeQuery(strGetMeetDates);
			while (results.next()) {
				Date MeetDate = results.getDate(1);
				dates.add(formatter.format(MeetDate));
			}
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		}
		return dates;

	}

	public void getBestTimesByTeam(String ageCode, String gender, String teams,
			BestTimesList list) {
		PreparedStatement stmtBestTimeFly = null;
		PreparedStatement stmtBestTimeBack = null;
		PreparedStatement stmtBestTimeBreast = null;
		PreparedStatement stmtBestTimeFree = null;

		Boolean continueFly = true;
		Boolean continueBack = true;
		Boolean continueBreast = true;
		Boolean continueFree = true;

		try {
			stmtBestTimeFly = dbConnection
					.prepareStatement("SELECT b.SWIM_ID, b.FULL_NAME, b.ALLSTARS, b.TEAM_CODE, b.AGE, a.EVENT_TIME, a.EVENT_SEC, a.EVENT_STD from BEST_TIMES_FLY a JOIN RSTA.SWIMMERS b ON a.SWIM_ID = b.SWIM_ID where a.AGE_CODE=? and a.SEX =? and b.TEAM_CODE IN ("
							+ teams + ") order by EVENT_SEC");
			stmtBestTimeBack = dbConnection
					.prepareStatement("SELECT b.SWIM_ID, b.FULL_NAME, b.ALLSTARS, b.TEAM_CODE, b.AGE, a.EVENT_TIME, a.EVENT_SEC, a.EVENT_STD from BEST_TIMES_BACK a JOIN RSTA.SWIMMERS b ON a.SWIM_ID = b.SWIM_ID where a.AGE_CODE=? and a.SEX =? and b.TEAM_CODE IN ("
							+ teams + ") order by EVENT_SEC");
			stmtBestTimeBreast = dbConnection
					.prepareStatement("SELECT b.SWIM_ID, b.FULL_NAME, b.ALLSTARS, b.TEAM_CODE, b.AGE, a.EVENT_TIME, a.EVENT_SEC, a.EVENT_STD from BEST_TIMES_BREAST a JOIN RSTA.SWIMMERS b ON a.SWIM_ID = b.SWIM_ID where a.AGE_CODE=? and a.SEX =? and b.TEAM_CODE IN ("
							+ teams + ") order by EVENT_SEC");
			stmtBestTimeFree = dbConnection
					.prepareStatement("SELECT b.SWIM_ID, b.FULL_NAME, b.ALLSTARS, b.TEAM_CODE, b.AGE, a.EVENT_TIME, a.EVENT_SEC, a.EVENT_STD from BEST_TIMES_FREE a JOIN RSTA.SWIMMERS b ON a.SWIM_ID = b.SWIM_ID where a.AGE_CODE=? and a.SEX =? and b.TEAM_CODE IN ("
							+ teams + ") order by EVENT_SEC");

			// Get the results set for fly
			stmtBestTimeFly.setString(1, ageCode);
			stmtBestTimeFly.setString(2, gender);
			// stmtBestTimeFly.setString(3, teams);
			ResultSet flyResults = stmtBestTimeFly.executeQuery();

			// Get the results set for Back
			stmtBestTimeBack.setString(1, ageCode);
			stmtBestTimeBack.setString(2, gender);
			// stmtBestTimeBack.setString(3, teams);
			ResultSet backResults = stmtBestTimeBack.executeQuery();

			// Get the results set for Breast
			stmtBestTimeBreast.setString(1, ageCode);
			stmtBestTimeBreast.setString(2, gender);
			// stmtBestTimeBreast.setString(3, teams);
			ResultSet breastResults = stmtBestTimeBreast.executeQuery();

			// Get the results set for Free
			stmtBestTimeFree.setString(1, ageCode);
			stmtBestTimeFree.setString(2, gender);
			// stmtBestTimeFree.setString(3, teams);
			ResultSet freeResults = stmtBestTimeFree.executeQuery();

			Integer nRow = 0;
			ColumnEntry flyValue, backValue, breastValue, freeValue;

			{
				while (continueFly || continueBack || continueBreast
						|| continueFree) {

					nRow++;

					// Get the next fly value
					flyValue = new ColumnEntry();
					if (continueFly) {
						continueFly = flyResults.next();
						if (continueFly) {
							flyValue.setId(flyResults.getString(1));
							flyValue.setName(flyResults.getString(2));
							flyValue.setAttending(flyResults.getString(3));
							flyValue.setTeam(flyResults.getString(4));
							flyValue.setAge(flyResults.getString(5));
							flyValue.setTime(flyResults.getString(6));
							flyValue.setStd(flyResults.getString(8));
						}
					}

					// Get the next back value
					backValue = new ColumnEntry();
					if (continueBack) {
						continueBack = backResults.next();
						if (continueBack) {
							backValue.setId(backResults.getString(1));
							backValue.setName(backResults.getString(2));
							backValue.setAttending(backResults.getString(3));
							backValue.setTeam(backResults.getString(4));
							backValue.setAge(backResults.getString(5));
							backValue.setTime(backResults.getString(6));
							backValue.setStd(backResults.getString(8));
						}
					}

					// Get the next breast value
					breastValue = new ColumnEntry();
					if (continueBreast) {
						continueBreast = breastResults.next();
						if (continueBreast) {
							breastValue.setId(breastResults.getString(1));
							breastValue.setName(breastResults.getString(2));
							breastValue
									.setAttending(breastResults.getString(3));
							breastValue.setTeam(breastResults.getString(4));
							breastValue.setAge(breastResults.getString(5));
							breastValue.setTime(breastResults.getString(6));
							breastValue.setStd(breastResults.getString(7));

						}
					}

					// Get the next free value
					freeValue = new ColumnEntry();
					if (continueFree) {
						continueFree = freeResults.next();
						if (continueFree) {
							freeValue.setId(freeResults.getString(1));
							freeValue.setName(freeResults.getString(2));
							freeValue.setAttending(freeResults.getString(3));
							freeValue.setTeam(freeResults.getString(4));
							freeValue.setAge(freeResults.getString(5));
							freeValue.setTime(freeResults.getString(6));
							freeValue.setStd(freeResults.getString(8));
						}
					}

					list.addEntry(new BestTimeEntry(nRow, flyValue, backValue,
							breastValue, freeValue));
				}
			}

		} catch (SQLException sqle) {
			sqle.printStackTrace();

		}

	}

	public void getBestTimes(String ageCode, String gender, BestTimesList list) {
		PreparedStatement stmtBestTimeFly = null;
		PreparedStatement stmtBestTimeBack = null;
		PreparedStatement stmtBestTimeBreast = null;
		PreparedStatement stmtBestTimeFree = null;

		Boolean continueFly = true;
		Boolean continueBack = true;
		Boolean continueBreast = true;
		Boolean continueFree = true;

		try {
			stmtBestTimeFly = dbConnection
					.prepareStatement("SELECT b.SWIM_ID, b.FULL_NAME, b.ALLSTARS, b.TEAM_CODE, b.AGE, a.EVENT_TIME, a.EVENT_SEC, a.EVENT_STD from BEST_TIMES_FLY a JOIN RSTA.SWIMMERS b ON a.SWIM_ID = b.SWIM_ID where a.AGE_CODE=? and a.SEX =? order by EVENT_SEC");
			stmtBestTimeBack = dbConnection
					.prepareStatement("SELECT b.SWIM_ID, b.FULL_NAME, b.ALLSTARS, b.TEAM_CODE, b.AGE, a.EVENT_TIME, a.EVENT_SEC, a.EVENT_STD from BEST_TIMES_BACK a JOIN RSTA.SWIMMERS b ON a.SWIM_ID = b.SWIM_ID where a.AGE_CODE=? and a.SEX =? order by EVENT_SEC");
			stmtBestTimeBreast = dbConnection
					.prepareStatement("SELECT b.SWIM_ID, b.FULL_NAME, b.ALLSTARS, b.TEAM_CODE, b.AGE, a.EVENT_TIME, a.EVENT_SEC, a.EVENT_STD from BEST_TIMES_BREAST a JOIN RSTA.SWIMMERS b ON a.SWIM_ID = b.SWIM_ID where a.AGE_CODE=? and a.SEX =? order by EVENT_SEC");
			stmtBestTimeFree = dbConnection
					.prepareStatement("SELECT b.SWIM_ID, b.FULL_NAME, b.ALLSTARS, b.TEAM_CODE, b.AGE, a.EVENT_TIME, a.EVENT_SEC, a.EVENT_STD from BEST_TIMES_FREE a JOIN RSTA.SWIMMERS b ON a.SWIM_ID = b.SWIM_ID where a.AGE_CODE=? and a.SEX =? order by EVENT_SEC");

			// Get the results set for fly
			stmtBestTimeFly.setString(1, ageCode);
			stmtBestTimeFly.setString(2, gender);
			ResultSet flyResults = stmtBestTimeFly.executeQuery();

			// Get the results set for Back
			stmtBestTimeBack.setString(1, ageCode);
			stmtBestTimeBack.setString(2, gender);
			ResultSet backResults = stmtBestTimeBack.executeQuery();

			// Get the results set for Breast
			stmtBestTimeBreast.setString(1, ageCode);
			stmtBestTimeBreast.setString(2, gender);
			ResultSet breastResults = stmtBestTimeBreast.executeQuery();

			// Get the results set for Free
			stmtBestTimeFree.setString(1, ageCode);
			stmtBestTimeFree.setString(2, gender);
			ResultSet freeResults = stmtBestTimeFree.executeQuery();

			Integer nRow = 0;
			ColumnEntry flyValue, backValue, breastValue, freeValue;

			{
				while (continueFly || continueBack || continueBreast
						|| continueFree) {

					nRow++;

					// Get the next fly value
					flyValue = new ColumnEntry();
					if (continueFly) {
						continueFly = flyResults.next();
						if (continueFly) {
							flyValue.setId(flyResults.getString(1));
							flyValue.setName(flyResults.getString(2));
							flyValue.setAttending(flyResults.getString(3));
							flyValue.setTeam(flyResults.getString(4));
							flyValue.setAge(flyResults.getString(5));
							flyValue.setTime(flyResults.getString(6));
							flyValue.setStd(flyResults.getString(8));
						}
					}

					// Get the next back value
					backValue = new ColumnEntry();
					if (continueBack) {
						continueBack = backResults.next();
						if (continueBack) {
							backValue.setId(backResults.getString(1));
							backValue.setName(backResults.getString(2));
							backValue.setAttending(backResults.getString(3));
							backValue.setTeam(backResults.getString(4));
							backValue.setAge(backResults.getString(5));
							backValue.setTime(backResults.getString(6));
							backValue.setStd(backResults.getString(8));
						}
					}

					// Get the next breast value
					breastValue = new ColumnEntry();
					if (continueBreast) {
						continueBreast = breastResults.next();
						if (continueBreast) {
							breastValue.setId(breastResults.getString(1));
							breastValue.setName(breastResults.getString(2));
							breastValue
									.setAttending(breastResults.getString(3));
							breastValue.setTeam(breastResults.getString(4));
							breastValue.setAge(breastResults.getString(5));
							breastValue.setTime(breastResults.getString(6));
							breastValue.setStd(breastResults.getString(8));
						}
					}

					// Get the next free value
					freeValue = new ColumnEntry();
					if (continueFree) {
						continueFree = freeResults.next();
						if (continueFree) {
							freeValue.setId(freeResults.getString(1));
							freeValue.setName(freeResults.getString(2));
							freeValue.setAttending(freeResults.getString(3));
							freeValue.setTeam(freeResults.getString(4));
							freeValue.setAge(freeResults.getString(5));
							freeValue.setTime(freeResults.getString(6));
							freeValue.setStd(freeResults.getString(8));
						}
					}

					list.addEntry(new BestTimeEntry(nRow, flyValue, backValue,
							breastValue, freeValue));
				}
			}

		} catch (SQLException sqle) {
			sqle.printStackTrace();

		}

	}

	public void getTeamEntries(String teamCode, TeamList teamList) {
		// List<SwimmerEntry> listEntries = new ArrayList<SwimmerEntry>();
		PreparedStatement stmtAllEvents = null;
		PreparedStatement stmtBestTimeFly = null;
		PreparedStatement stmtBestTimeBack = null;
		PreparedStatement stmtBestTimeBreast = null;
		PreparedStatement stmtBestTimeFree = null;
		Statement queryStatement = null;
		ResultSet results = null;
		String query = "Select SWIM_ID, FULL_NAME, LAST_NAME, FIRST_NAME, AGE, GENDER, TEAM_NAME, "
				+ "ALLSTARS, PRE_FLY, SEl_FLY, PRE_BACK, SEl_BACK, PRE_BREAST, SEl_BREAST, PRE_FREE, SEl_FREE "
				+ "from RSTA.SWIMMERS where TEAM_CODE= '"
				+ teamCode
				+ "' order by LAST_NAME";

		// System.out.println( query );
		// SELECT EVENT_TIME FROM RSTA.BEST_TIMES_BREAST where SWIM_ID like
		// 'Byrd%';
		try {

			stmtBestTimeFly = dbConnection
					.prepareStatement("SELECT EVENT_TIME FROM RSTA.BEST_TIMES_FLY where SWIM_ID=?");
			stmtBestTimeBack = dbConnection
					.prepareStatement("SELECT EVENT_TIME FROM RSTA.BEST_TIMES_BACK where SWIM_ID=?");
			stmtBestTimeBreast = dbConnection
					.prepareStatement("SELECT EVENT_TIME FROM RSTA.BEST_TIMES_BREAST where SWIM_ID=?");
			stmtBestTimeFree = dbConnection
					.prepareStatement("SELECT EVENT_TIME FROM RSTA.BEST_TIMES_FREE where SWIM_ID=?");
			stmtAllEvents = dbConnection
					.prepareStatement("select MEET_DATE, MEET_NAME, EVENT_NAME, EVENT_DIST, EVENT_TIME, EVENT_STD, EVENT_PLACE, EVENT_PTS from RSTA.EVENTS where SWIM_ID =? order by MEET_DATE, EVENT_NUM");

			queryStatement = dbConnection.createStatement();
			results = queryStatement.executeQuery(query);

			while (results.next()) {
				Person entry = new Person();
				entry.setId(results.getString(1));
				entry.setFullName(results.getString(2));
				entry.setLastName(results.getString(3));
				entry.setFirstName(results.getString(4));
				entry.setAge(results.getInt(5));
				entry.setGender(results.getString(6));
				entry.setTeam(results.getString(7));

				entry.setAttendAllStars(results.getString(8));

				entry.setPreferFly(results.getString(9));
				entry.setSelectedFly(results.getString(10));

				entry.setPreferBack(results.getString(11));
				entry.setSelectedBack(results.getString(12));

				entry.setPreferBreast(results.getString(13));
				entry.setSelectedBreast(results.getString(14));

				entry.setPreferFree(results.getString(15));
				entry.setSelectedFree(results.getString(16));

				{
					// Get the Top Fly time for the swimmer
					stmtBestTimeFly.setString(1, entry.getId());
					ResultSet flyResults = stmtBestTimeFly.executeQuery();
					if (flyResults.next()) {
						entry.setBestTime_fly(flyResults.getString(1));
					}

					// Get the Top Back Stroke time for the swimmer
					stmtBestTimeBack.setString(1, entry.getId());
					ResultSet backResults = stmtBestTimeBack.executeQuery();
					if (backResults.next()) {
						entry.setBestTime_back(backResults.getString(1));
					}

					// Get the Top Breast Stroke time for the swimmer
					stmtBestTimeBreast.setString(1, entry.getId());
					ResultSet BreastResults = stmtBestTimeBreast.executeQuery();
					if (BreastResults.next()) {
						entry.setBestTime_breast(BreastResults.getString(1));
					}

					// Get the Top Free time for the swimmer
					stmtBestTimeFree.setString(1, entry.getId());
					ResultSet FreeResults = stmtBestTimeFree.executeQuery();
					if (FreeResults.next()) {
						entry.setBestTime_free(FreeResults.getString(1));
					}
				}

				// Get all events the swimmer has participated in
				{
					stmtAllEvents.setString(1, entry.getId());
					ResultSet AllResults = stmtAllEvents.executeQuery();

					while (AllResults.next()) {

						EventRow event = new EventRow();
						event.setMeetDate(AllResults.getDate(1));
						event.setMeetName(AllResults.getString(2));
						event.setEventName(AllResults.getString(3));
						event.setEventDist(AllResults.getString(4));
						event.setEventTime(AllResults.getString(5));
						event.setEventStd(AllResults.getString(6));
						event.setEventPlace(AllResults.getInt(7));
						event.setEventPts(AllResults.getInt(8));
						entry.addEvent(event);
					}

				}
				entry.setInitialized(true);
				teamList.addPerson(entry);
			}

		} catch (SQLException sqle) {
			sqle.printStackTrace();

		}
	}

	public void getGroupEntries(String group, String gender, List<Person> list) {

		PreparedStatement stmtBestTimeFly = null;
		PreparedStatement stmtBestTimeBack = null;
		PreparedStatement stmtBestTimeBreast = null;
		PreparedStatement stmtBestTimeFree = null;
		Statement queryStatement = null;
		ResultSet results = null;
		String query = "Select SWIM_ID, FULL_NAME, LAST_NAME, FIRST_NAME, AGE, GENDER, TEAM_CODE, "
				+ "ALLSTARS, PRE_FLY, SEl_FLY, PRE_BACK, SEl_BACK, PRE_BREAST, SEl_BREAST, PRE_FREE, SEl_FREE "
				+ "from RSTA.SWIMMERS where AGE_CODE='"
				+ group
				+ "' AND SEX='"
				+ gender + "'";

		// System.out.println( query );

		try {

			stmtBestTimeFly = dbConnection
					.prepareStatement("SELECT EVENT_TIME, EVENT_SEC, EVENT_STD FROM RSTA.BEST_TIMES_FLY where SWIM_ID=?");
			stmtBestTimeBack = dbConnection
					.prepareStatement("SELECT EVENT_TIME, EVENT_SEC, EVENT_STD FROM RSTA.BEST_TIMES_BACK where SWIM_ID=?");
			stmtBestTimeBreast = dbConnection
					.prepareStatement("SELECT EVENT_TIME, EVENT_SEC, EVENT_STD FROM RSTA.BEST_TIMES_BREAST where SWIM_ID=?");
			stmtBestTimeFree = dbConnection
					.prepareStatement("SELECT EVENT_TIME, EVENT_SEC, EVENT_STD FROM RSTA.BEST_TIMES_FREE where SWIM_ID=?");

			queryStatement = dbConnection.createStatement();
			results = queryStatement.executeQuery(query);

			while (results.next()) {
				Person entry = new Person();
				entry.setId(results.getString(1));
				entry.setFullName(results.getString(2));
				entry.setLastName(results.getString(3));
				entry.setFirstName(results.getString(4));
				entry.setAge(results.getInt(5));
				entry.setGender(results.getString(6));
				entry.setTeam(results.getString(7));

				entry.setAttendAllStars(results.getString(8));

				entry.setPreferFly(results.getString(9));
				entry.setSelectedFly(results.getString(10));

				entry.setPreferBack(results.getString(11));
				entry.setSelectedBack(results.getString(12));

				entry.setPreferBreast(results.getString(13));
				entry.setSelectedBreast(results.getString(14));

				entry.setPreferFree(results.getString(15));
				entry.setSelectedFree(results.getString(16));

				{
					// Get the Top Fly time for the swimmer
					stmtBestTimeFly.setString(1, entry.getId());
					ResultSet flyResults = stmtBestTimeFly.executeQuery();
					if (flyResults.next()) {
						entry.setBestTime_fly(flyResults.getString(1));
						entry.setTimeFly(flyResults.getDouble(2));
						entry.setBest_fly_std(flyResults.getString(3));
					}

					// Get the Top Back Stroke time for the swimmer
					stmtBestTimeBack.setString(1, entry.getId());
					ResultSet backResults = stmtBestTimeBack.executeQuery();
					if (backResults.next()) {
						entry.setBestTime_back(backResults.getString(1));
						entry.setTimeBack(backResults.getDouble(2));
						entry.setBest_back_std(backResults.getString(3));
					}

					// Get the Top Breast Stroke time for the swimmer
					stmtBestTimeBreast.setString(1, entry.getId());
					ResultSet BreastResults = stmtBestTimeBreast.executeQuery();
					if (BreastResults.next()) {
						entry.setBestTime_breast(BreastResults.getString(1));
						entry.setTimeBreast(BreastResults.getDouble(2));
						entry.setBest_breast_std(BreastResults.getString(3));
					}

					// Get the Top Free time for the swimmer
					stmtBestTimeFree.setString(1, entry.getId());
					ResultSet FreeResults = stmtBestTimeFree.executeQuery();
					if (FreeResults.next()) {
						entry.setBestTime_free(FreeResults.getString(1));
						entry.setTimeFree(FreeResults.getDouble(2));
						entry.setBest_free_std(FreeResults.getString(3));
					}
				}

				entry.setInitialized(true);
				list.add(entry);
			}

		} catch (SQLException sqle) {
			sqle.printStackTrace();

		}
	}

	public void getSwimmer(Person person) {
		PreparedStatement stmtAllEvents = null;
		PreparedStatement stmtBestTimeFly = null;
		PreparedStatement stmtBestTimeBack = null;
		PreparedStatement stmtBestTimeBreast = null;
		PreparedStatement stmtBestTimeFree = null;
		Statement queryStatement = null;
		ResultSet results = null;

		String query = "Select SWIM_ID, FULL_NAME, LAST_NAME, FIRST_NAME, AGE, GENDER, TEAM_NAME, "
				+ "ALLSTARS, PRE_FLY, SEl_FLY, PRE_BACK, SEl_BACK, PRE_BREAST, SEl_BREAST, PRE_FREE, SEl_FREE "
				+ "from RSTA.SWIMMERS where SWIM_ID='" + person.getId() + "'";
		// System.out.println( query);
		try {

			stmtBestTimeFly = dbConnection
					.prepareStatement("SELECT EVENT_TIME FROM RSTA.BEST_TIMES_FLY where SWIM_ID=?");
			stmtBestTimeBack = dbConnection
					.prepareStatement("SELECT EVENT_TIME FROM RSTA.BEST_TIMES_BACK where SWIM_ID=?");
			stmtBestTimeBreast = dbConnection
					.prepareStatement("SELECT EVENT_TIME FROM RSTA.BEST_TIMES_BREAST where SWIM_ID=?");
			stmtBestTimeFree = dbConnection
					.prepareStatement("SELECT EVENT_TIME FROM RSTA.BEST_TIMES_FREE where SWIM_ID=?");
			stmtAllEvents = dbConnection
					.prepareStatement("select MEET_DATE, MEET_NAME, EVENT_NAME, EVENT_DIST, EVENT_TIME, EVENT_STD, EVENT_PLACE, EVENT_PTS from RSTA.EVENTS where SWIM_ID =? order by MEET_DATE, EVENT_NUM");

			queryStatement = dbConnection.createStatement();
			results = queryStatement.executeQuery(query);

			while (results.next()) {

				person.setFullName(results.getString(2));
				person.setLastName(results.getString(3));
				person.setFirstName(results.getString(4));
				person.setAge(results.getInt(5));
				person.setGender(results.getString(6));
				person.setTeam(results.getString(7));

				person.setAttendAllStars(results.getString(8));

				person.setPreferFly(results.getString(9));
				person.setSelectedFly(results.getString(10));

				person.setPreferBack(results.getString(11));
				person.setSelectedBack(results.getString(12));

				person.setPreferBreast(results.getString(13));
				person.setSelectedBreast(results.getString(14));

				person.setPreferFree(results.getString(15));
				person.setSelectedFree(results.getString(16));

				{
					// Get the Top Fly time for the swimmer
					stmtBestTimeFly.setString(1, person.getId());
					ResultSet flyResults = stmtBestTimeFly.executeQuery();
					if (flyResults.next()) {
						person.setBestTime_fly(flyResults.getString(1));
					}

					// Get the Top Back Stroke time for the swimmer
					stmtBestTimeBack.setString(1, person.getId());
					ResultSet backResults = stmtBestTimeBack.executeQuery();
					if (backResults.next()) {
						person.setBestTime_back(backResults.getString(1));
					}

					// Get the Top Breast Stroke time for the swimmer
					stmtBestTimeBreast.setString(1, person.getId());
					ResultSet BreastResults = stmtBestTimeBreast.executeQuery();
					if (BreastResults.next()) {
						person.setBestTime_breast(BreastResults.getString(1));
					}

					// Get the Top Free time for the swimmer
					stmtBestTimeFree.setString(1, person.getId());
					ResultSet FreeResults = stmtBestTimeFree.executeQuery();
					if (FreeResults.next()) {
						person.setBestTime_free(FreeResults.getString(1));
					}
				}

				// Get all events the swimmer has participated in
				{
					stmtAllEvents.setString(1, person.getId());
					ResultSet AllResults = stmtAllEvents.executeQuery();

					while (AllResults.next()) {
						EventRow event = new EventRow();
						event.setMeetDate(AllResults.getDate(1));
						event.setMeetName(AllResults.getString(2));
						event.setEventName(AllResults.getString(3));
						event.setEventDist(AllResults.getString(4));
						event.setEventTime(AllResults.getString(5));
						event.setEventStd(AllResults.getString(6));
						event.setEventPlace(AllResults.getInt(7));
						event.setEventPts(AllResults.getInt(8));
						person.addEvent(event);
					}
				}
				person.setInitialized(true);
			}

		} catch (SQLException sqle) {
			sqle.printStackTrace();

		}
	}

	public void deleteMeet(String meetName) {
		Statement statement = null;
		String deleteEvents = "DELETE FROM RSTA.EVENTS WHERE MEET_NAME ='"
				+ meetName + "'";
		String deleteImport = "DELETE FROM RSTA.IMPORT WHERE MEET_NAME ='"
				+ meetName + "'";
		try {
			statement = dbConnection.createStatement();
			// Create the Meet Records
			statement.execute(deleteEvents);
			statement.execute(deleteImport);

		} catch (SQLException sqle) {
			sqle.printStackTrace();
		}
	}

	public boolean updateSwimmer(Person person) {
		boolean bEdited = false;
		try {
			stmtUpdateExistingRecord.clearParameters();
			stmtUpdateExistingRecord.setString(1, person.allStars());
			stmtUpdateExistingRecord.setString(2, person.pre_fly());
			stmtUpdateExistingRecord.setString(3, person.pre_back());
			stmtUpdateExistingRecord.setString(4, person.pre_breast());
			stmtUpdateExistingRecord.setString(5, person.pre_free());
			stmtUpdateExistingRecord.setString(6, person.getId());
			stmtUpdateExistingRecord.executeUpdate();
			bEdited = true;
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		}
		return bEdited;
	}

	public void getMultipleEventWinners(String MeetDate,
			HashMap<String, EventWinner> winners) {
		Statement queryStatement = null;
		ResultSet results = null;
		String query = "SELECT a.SWIM_ID, a.FULL_NAME, a.AGE, a.TEAM_NAME, c.EVENT_PLACE, c.MEET_DATE, c.MEET_NAME from RSTA.SWIMMERS a, RSTA.EVENTS c WHERE c.MEET_DATE='"
				+ MeetDate
				+ "' AND c.EVENT_PLACE=1 AND a.SWIM_ID = c.SWIM_ID  order by a.SWIM_ID";
		try {
			queryStatement = dbConnection.createStatement();
			results = queryStatement.executeQuery(query);
			while (results.next()) {
				String swimId = results.getString(1);
				if (winners.containsKey(swimId)) {
					winners.get(swimId).increment();
				} else {
					winners.put(swimId,
							new EventWinner(swimId, results.getString(2),
									results.getInt(3), results.getString(4),
									results.getString(7)));
				}
			}
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		}
	}

	private static final String strUpdateSwimmer = "UPDATE RSTA.SWIMMERS "
			+ "SET AllSTARS = ?, " + "    PRE_FLY = ?, " + "    PRE_BACK = ?, "
			+ "    PRE_BREAST = ?, " + "    PRE_FREE = ? "
			+ "WHERE SWIM_ID = ?";

	private static final String strCreateTeamsTable = "create table RSTA.TEAMS ("
			+ "    ID          INTEGER NOT NULL PRIMARY KEY,"
			+ "    TEAM_CODE   CHAR(2), "
			+ "    TEAM_NAME   VARCHAR(30) "
			+ ")";

	private static final String strCreateSwimmersTable = "create table RSTA.SWIMMERS ("
			+ " SWIM_ID VARCHAR(60) NOT NULL,"
			+ " FULL_NAME VARCHAR(60),"
			+ " LAST_NAME VARCHAR(30),"
			+ " FIRST_NAME VARCHAR(30),"
			+ " AGE INT,"
			+ " AGE_GROUP VARCHAR(10),"
			+ " AGE_CODE VARCHAR(10),"
			+ " SEX VARCHAR(1),"
			+ " GENDER VARCHAR(6),"
			+ " TEAM_ID INT,"
			+ " TEAM_CODE VARCHAR(2),"
			+ " TEAM_NAME VARCHAR(60),"
			+ " AllSTARS VARCHAR(1),"
			+ " PRE_FLY VARCHAR(1),"
			+ " SEL_FLY VARCHAR(1),"
			+ " PRE_BACK VARCHAR(1),"
			+ " SEL_BACK VARCHAR(1),"
			+ " PRE_BREAST VARCHAR(1),"
			+ " SEL_BREAST VARCHAR(1),"
			+ " PRE_FREE VARCHAR(1),"
			+ " SEL_FREE VARCHAR(1)," + " PRIMARY KEY (SWIM_ID)" + ")";

	private static final String strInsertSwimmer = "INSERT INTO RSTA.SWIMMERS "
			+ "(SWIM_ID, FULL_NAME, LAST_NAME, FIRST_NAME, AGE, AGE_GROUP, AGE_CODE, SEX, GENDER, TEAM_ID, TEAM_CODE, TEAM_NAME, "
			+ "AllSTARS, PRE_FLY, SEL_FLY, PRE_BACK, SEL_BACK, PRE_BREAST, SEL_BREAST, PRE_FREE, SEL_FREE)"
			+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

	private static final String strCreateEventsTable = "create table RSTA.EVENTS ("
			+ " ID INTEGER NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),"
			+ // 0
			" SWIM_ID VARCHAR(60) NOT NULL,"
			+ " MEET_NAME VARCHAR(60),"
			+ " MEET_DATE DATE,"
			+ " EVENT_NAME VARCHAR(10),"
			+ " EVENT_DIST VARCHAR(10),"
			+ " EVENT_TIME VARCHAR(10),"
			+ " EVENT_SEC NUMERIC(8,2),"
			+ " EVENT_STD VARCHAR(5),"
			+ " EVENT_PLACE INT,"
			+ " EVENT_PTS NUMERIC(8,2),"
			+ " EVENT_NUM INT,"
			+ " FOREIGN KEY (SWIM_ID) REFERENCES RSTA.SWIMMERS (SWIM_ID)" + ")";

	private static final String strSaveEvent = "INSERT INTO RSTA.EVENTS "
			+ "(SWIM_ID, MEET_NAME, MEET_DATE, EVENT_NAME, EVENT_DIST, EVENT_TIME, EVENT_SEC, EVENT_STD, EVENT_PLACE, EVENT_PTS, EVENT_NUM) "
			+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

	private static final String strCreateImportTable = "create table RSTA.IMPORT ("
			+ " ID INTEGER NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),"
			+ // 0
			" FULL_NAME VARCHAR(60)," + // 1
			" LAST_NAME VARCHAR(30)," + // 2
			" FIRST_NAME VARCHAR(30)," + // 3
			" AGE INT," + // 4
			" AGE_GROUP VARCHAR(10)," + // 5
			" AGE_CODE VARCHAR(10)," + // 6
			" SEX VARCHAR(1)," + // 7
			" GENDER VARCHAR(6)," + // 8
			" TEAM_ID INT," + // 9
			" TEAM_CODE VARCHAR(2)," + // 10
			" TEAM_NAME VARCHAR(60)," + // 11
			" MEET_ID VARCHAR(60)," + // 12
			" MEET_NAME VARCHAR(60)," + // 13
			" MEET_DATE DATE," + // 14
			" EVENT_NAME VARCHAR(10)," + // 15
			" EVENT_DIST VARCHAR(10)," + // 16
			" EVENT_TIME VARCHAR(10)," + // 17
			" EVENT_SEC NUMERIC(8,2)," + // 18
			" EVENT_STD VARCHAR(5)," + // 19
			" EVENT_PLACE INT," + // 20
			" EVENT_PTS NUMERIC(8,2)," + // 21
			" EVENT_NUM INT" + // 22
			")";

	private static final String strSaveImport = "INSERT INTO RSTA.IMPORT "
			+ "   (FULL_NAME, LAST_NAME, FIRST_NAME, AGE, AGE_GROUP, AGE_CODE, SEX, GENDER, TEAM_ID, TEAM_CODE, TEAM_NAME, "
			+ "    MEET_ID, MEET_NAME, MEET_DATE, EVENT_NAME, EVENT_DIST, EVENT_TIME, EVENT_SEC, EVENT_STD, EVENT_PLACE, EVENT_PTS, EVENT_NUM) "
			+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

	private static final String strFindImportRecord = "SELECT * FROM RSTA.IMPORT WHERE MEET_ID=?";

	private static final String strCreateScoresTable = "create table RSTA.SCORES ("
			+ " ID INTEGER NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),"
			+ " MEET_NAME VARCHAR(60),"
			+ " TEAM_CODE VARCHAR(2),"
			+ " TEAM_NAME VARCHAR(60),"
			+ " P_TOTAL NUMERIC(8,2),"
			+ " P_A NUMERIC(8,2),"
			+ " P_B NUMERIC(8,2),"
			+ " P_C NUMERIC(8,2),"
			+ " P_608 NUMERIC(8,2),"
			+ " P_910 NUMERIC(8,2),"
			+ " P_1112 NUMERIC(8,2),"
			+ " P_1314 NUMERIC(8,2),"
			+ " P_1518 NUMERIC(8,2),"
			+ " P_RELAY NUMERIC(8,2),"
			+ " W_MEET INT,"
			+ " W_A INT,"
			+ " W_B INT,"
			+ " W_C INT,"
			+ " W_608 INT,"
			+ " W_910 INT,"
			+ " W_1112 INT," + " W_1314 INT," + " W_1518 INT" + ")";

	private static final String strSaveScore = "INSERT INTO RSTA.SCORES "
			+ "   (MEET_NAME, TEAM_CODE, TEAM_NAME, P_TOTAL, P_A, P_B, P_C, P_608, P_910, P_1112, "
			+ "P_1314, P_1518, P_RELAY, W_MEET, W_A, W_B, W_C, W_608, W_910, W_1112, W_1314, W_1518) "
			+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

	private static final String strGetSwimmer = "SELECT * FROM RSTA.SWIMMERS "
			+ "WHERE SWIM_ID = ?";

	private static final String strCreateFlyView = "CREATE VIEW RSTA.BEST_TIMES_FLY AS "
			+ "SELECT DISTINCT a.SWIM_ID, a.AGE_CODE, a.SEX, a.TEAM_CODE, c.EVENT_NAME, c.EVENT_TIME, c.EVENT_SEC, c.EVENT_STD from RSTA.SWIMMERS a, RSTA.EVENTS c "
			+ "WHERE a.SWIM_ID = c.SWIM_ID AND "
			+ "c.EVENT_NAME='Fly' AND "
			+ "c.EVENT_SEC = "
			+ "(SELECT MIN(b.EVENT_SEC) FROM RSTA.EVENTS b WHERE b.EVENT_SEC > 0.00 AND b.SWIM_ID=a.SWIM_ID AND b.EVENT_NAME='Fly')";

	private static final String strCreateBackView = "CREATE VIEW RSTA.BEST_TIMES_BACK AS "
			+ "SELECT DISTINCT a.SWIM_ID, a.AGE_CODE, a.SEX, a.TEAM_CODE, c.EVENT_NAME, c.EVENT_TIME, c.EVENT_SEC, c.EVENT_STD from RSTA.SWIMMERS a, RSTA.EVENTS c "
			+ "WHERE a.SWIM_ID = c.SWIM_ID AND "
			+ "c.EVENT_NAME='Back' AND "
			+ "c.EVENT_SEC = "
			+ "(SELECT MIN(b.EVENT_SEC) FROM RSTA.EVENTS b WHERE b.EVENT_SEC > 0.00 AND b.SWIM_ID=a.SWIM_ID AND b.EVENT_NAME='Back')";

	private static final String strCreateBreastView = "CREATE VIEW RSTA.BEST_TIMES_BREAST AS "
			+ "SELECT DISTINCT a.SWIM_ID, a.AGE_CODE, a.SEX, a.TEAM_CODE, c.EVENT_NAME, c.EVENT_TIME, c.EVENT_SEC, c.EVENT_STD from RSTA.SWIMMERS a, RSTA.EVENTS c "
			+ "WHERE a.SWIM_ID = c.SWIM_ID AND "
			+ "c.EVENT_NAME='Breast' AND "
			+ "c.EVENT_SEC = "
			+ "(SELECT MIN(b.EVENT_SEC) FROM RSTA.EVENTS b WHERE b.EVENT_SEC > 0.00 AND b.SWIM_ID=a.SWIM_ID AND b.EVENT_NAME='Breast')";

	private static final String strCreateFreeView = "CREATE VIEW RSTA.BEST_TIMES_FREE AS "
			+ "SELECT DISTINCT a.SWIM_ID, a.AGE_CODE, a.SEX, a.TEAM_CODE, c.EVENT_NAME, c.EVENT_TIME, c.EVENT_SEC, c.EVENT_STD from RSTA.SWIMMERS a, RSTA.EVENTS c "
			+ "WHERE a.SWIM_ID = c.SWIM_ID AND "
			+ "c.EVENT_NAME='Free' AND "
			+ "c.EVENT_SEC = "
			+ "(SELECT MIN(b.EVENT_SEC) FROM RSTA.EVENTS b WHERE b.EVENT_SEC > 0.00 AND b.SWIM_ID=a.SWIM_ID AND b.EVENT_NAME='Free')";

	private static final String strGetMeetEntries = "Select distinct MEET_NAME, MEET_DATE from RSTA.IMPORT order by MEET_DATE ASC";
	private static final String strGetMeetDates = "Select distinct MEET_DATE from RSTA.IMPORT order by MEET_DATE ASC";

}
