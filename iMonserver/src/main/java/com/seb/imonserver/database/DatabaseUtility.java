package com.seb.imonserver.database;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
//import org.sqlite.SQLiteConfig;


import com.seb.dataModel.About;
import com.seb.imonserver.datamodel.Adjacency;
import com.seb.imonserver.datamodel.AttrNameValue;
import com.seb.imonserver.datamodel.Cell;
import com.seb.imonserver.generic.CellDSInfo;

public class DatabaseUtility {

	private static final Logger LOG = LogManager.getLogger(DatabaseUtility.class);

	public static Connection openDatabaseConnection(String networkDatabaseName) throws ClassNotFoundException, SQLException {
		 //SQLiteConfig config = new SQLiteConfig();
		// config.setDefaultCacheSize(10000);
		 //config.setCacheSize(10000);
		 //config.setPageSize(65536);
		// config.setReadOnly(false);
		

		 Class.forName("org.sqlite.JDBC");
		Connection theConnection = DriverManager.getConnection("jdbc:sqlite:"+ networkDatabaseName);
//		Connection theConnection = DriverManager.getConnection("jdbc:sqlite:"+ networkDatabaseName, config.toProperties());

		theConnection.setAutoCommit(false);

		return theConnection;
	}
	
	public static String[] getDatabaseListOrderedByDate(String NetworkDatabaseDirectory) {
		SortedSet<String> databaseList = new TreeSet<String>();
		
		File folder = new File(NetworkDatabaseDirectory);
		if (folder.isDirectory() == false) {
			LOG.warn("getDatabaseListOrderedByDate::Error: NetworkDatabaseDirectory is not a directory" + NetworkDatabaseDirectory);
			return new String[0];
		}
		
		// they are implicitely ordered by date due to the name format of the folder YYYYMMDD
		for (File currentFile : folder.listFiles()) {
			try {
				if (currentFile.isDirectory()) {
					if (matchDatabaseFolder(currentFile.getName())) {
						String currentFileName = currentFile.getAbsolutePath() ;
						databaseList.add(currentFileName);						
					} 
				}
			} catch (SecurityException ex) {
				LOG.error(ex);
			}
		}	
		
		return databaseList.toArray(new String[databaseList.size()]);
	}
	
	private static boolean matchDatabaseFolder(String folderName) {
		// if the directory name is a 8 digits we consider it's a database folder with format YYYYMMDD 
		if (folderName.matches("\\d{8}")) {
			return true;
		} else {
			return false;
		}
	}
	
	public static String buildFullDatabaseName(String databaseName) {
		return databaseName + File.separator + "NetworkTopology.db";
	}


	public static Cell createCellFromResultSet(ResultSet rs) {
		try {

			// TODO: use index to get column value, will be more efficient
			Cell newCell = new Cell(rs.getString("id"),
					rs.getDouble("longitude"),
					rs.getDouble("latitude"),
					rs.getInt("radius"),
					rs.getString("azimuth"),
					rs.getString("techno"),
					rs.getString("site"),
					rs.getString("site_id"),
					rs.getString("release"),
					rs.getString("dlfrequency"),
					rs.getString("telecomid"));

			newCell.setNumberInterRATNR(rs.getInt("numberinterratnr"));
			newCell.setNumberInterFreqNR(rs.getInt("numberinterfreqnr"));
			newCell.setNumberIntraFreqNR(rs.getInt("numberintrafreqnr"));
			return newCell;
		}
		catch (SQLException ex) {
			LOG.error(ex);
		}
		return null;
	}
	
	public static CellDSInfo createCellDSInfoFromResultSet(ResultSet rs) {
		try {
			CellDSInfo newCellDSInfo = new CellDSInfo(rs.getString("id"),
					rs.getInt("qos_ds"),
					rs.getInt("topology_ds"));
			return newCellDSInfo;
		}
		catch (SQLException ex) {
			LOG.error(ex);
		}
		return null;
	}

	public static AttrNameValue createAttrNameValueFromResultSet(ResultSet rs) {
		try {
			// TODO: use index to get column value, will be more effecient
		return new AttrNameValue(rs.getString("name"),
				rs.getString("value"),
				rs.getString("section"));
		}
		catch (SQLException ex) {
			LOG.error(ex);
		}
		return null;
	}

	
	public static Adjacency createNeighborFromResultSet(ResultSet rs) {
		try {
			// TODO: use index to get column value, will be more efficient
		return new Adjacency(rs.getString("targetcell"),
				rs.getString("noho"),
				rs.getString("noremove"),
				rs.getString("dlfrequency"),
				rs.getString("measuredbyanr"),
				rs.getString("techno"));
		}
		catch (SQLException ex) {
			LOG.error(ex);
		}
		return null;
	}
	

}
