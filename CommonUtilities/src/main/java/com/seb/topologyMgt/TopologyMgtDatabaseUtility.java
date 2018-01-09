package com.seb.topologyMgt;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.seb.dataModel.About;

public class TopologyMgtDatabaseUtility {
	private static final Logger LOG = LogManager.getLogger(TopologyMgtDatabaseUtility.class);

	private TopologyMgtDatabaseUtility() {}
	
	public static About createAboutFromResultSet(ResultSet rs) {
		try {
			// TODO: use index to get column value, will be more efficient
		return new About(rs.getInt("lte_cells_count"),
				rs.getInt("lte_neighbors_count"),
				rs.getInt("wcdma_cells_count"),
				rs.getInt("wcdma_neighbors_count"),
				rs.getInt("gsm_cells_count"),
				rs.getInt("gsm_neighbors_count"));
		}
		catch (SQLException ex) {
			LOG.error(ex);
		}
		return null;
		
	}
	
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

}
