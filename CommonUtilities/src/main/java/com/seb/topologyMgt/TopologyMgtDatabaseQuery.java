package com.seb.topologyMgt;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sqlite.SQLiteConfig;

import com.seb.dataModel.About;

public class TopologyMgtDatabaseQuery {
	private static final Logger LOG = LogManager.getLogger(TopologyMgtDatabaseQuery.class);

	private TopologyMgtDatabaseQuery() {}
	/**
	 * Return About from the database
	 * 
	 * @param theConnection connection to the database
	 * @return About with the initialized with the data from the database otherwise null is returned
	 */
	public static About getAbout(String networkDatabaseName) {
		try(Connection theConnection = TopologyMgtDatabaseUtility.openDatabaseConnection(networkDatabaseName)) {
			return getAbout(theConnection);
		} 
		catch (Exception ex) {
			LOG.error(ex);
		}

		return null;		
	}
	
	public static About getAbout(Connection theConnection) {
		String sql = TopologyMgtSQLRequestBuilder.getAbout();
		try(Statement stmt = theConnection.createStatement();
				ResultSet rs = stmt.executeQuery(sql)) {

			return TopologyMgtDatabaseUtility.createAboutFromResultSet(rs);
		} 
		catch (Exception ex) {
			LOG.error(ex);
		}

		return null;		
	}

	public static Connection createNetworkDatabase(String outputDatabaseDir, String outputDatabaseName) {
		Connection theConnection = null;
		try {
			 SQLiteConfig config = new SQLiteConfig();
			 config.enableLoadExtension(true);
			 Class.forName("org.sqlite.JDBC");
				theConnection = DriverManager.getConnection("jdbc:sqlite:" + outputDatabaseDir + "/" + outputDatabaseName , config.toProperties());
				LOG.info("DatabaseUtility::Opened database successfully");

			createTablesForTopology(theConnection);

			theConnection.setAutoCommit(false);
		} catch ( Exception e ) {
			LOG.fatal(e);
			System.exit(0);
		}
		return theConnection;
	}
	
	private static void createTablesForTopology(Connection theConnection) {
		try(Statement stmt = theConnection.createStatement()) {
			LOG.info("DatabaseUtility::After load extension");

			stmt.executeUpdate(TopologyMgtSQLRequestBuilder.createAboutTable());
			stmt.executeUpdate(TopologyMgtSQLRequestBuilder.createCellsTable());
			stmt.executeUpdate(TopologyMgtSQLRequestBuilder.createCellsAttributesTable());
			stmt.executeUpdate(TopologyMgtSQLRequestBuilder.createCellsNRTable());
			
			// Index on CELLS table
			stmt.executeUpdate(TopologyMgtSQLRequestBuilder.createIndexCellsTableOnId());
			stmt.executeUpdate(TopologyMgtSQLRequestBuilder.createIndexCellsTableOnTelecomId());
			stmt.executeUpdate(TopologyMgtSQLRequestBuilder.createIndexCellsTableOnLatitude());
			stmt.executeUpdate(TopologyMgtSQLRequestBuilder.createIndexCellsTableOnLongitude());
			stmt.executeUpdate(TopologyMgtSQLRequestBuilder.createIndexCellsTableOnSiteId());
			stmt.executeUpdate(TopologyMgtSQLRequestBuilder.createIndexCellsTableOnGeoIndex(GeoIndexAllocator.GeoIndexSize.LARGE));
			stmt.executeUpdate(TopologyMgtSQLRequestBuilder.createIndexCellsTableOnGeoIndex(GeoIndexAllocator.GeoIndexSize.MEDIUM));
			stmt.executeUpdate(TopologyMgtSQLRequestBuilder.createIndexCellsTableOnGeoIndex(GeoIndexAllocator.GeoIndexSize.SMALL));
			
			// Index on CELLS_ATTRIBUTES table
			stmt.executeUpdate(TopologyMgtSQLRequestBuilder.createIndexCellsAttributesTable());
			
			// Indexes on CELLS_NR
			stmt.executeUpdate(TopologyMgtSQLRequestBuilder.createIndexCellsNRTable());
		} catch ( Exception e ) {
			LOG.fatal(e);
			System.exit(0);
		}
	}
	
	
	
	public static void insertAbout(Connection theConnection,int LTECellCount, int LTENRCount, int WCDMACellCount, int WCDMANRCount, int GSMCellCount, int GSMNRCount) {
		try(Statement stmt = theConnection.createStatement()) {
			String sql = TopologyMgtSQLRequestBuilder.insertAbout(LTECellCount, LTENRCount, WCDMACellCount, WCDMANRCount, GSMCellCount, GSMNRCount);
			stmt.executeUpdate(sql);
		}
		catch (SQLException ex) {
			LOG.error(ex);
		}
		
	}
	
	public static void updateAbout(Connection theConnection,int LTECellCount, int LTENRCount, int WCDMACellCount, int WCDMANRCount, int GSMCellCount, int GSMNRCount) {
		try(Statement stmt = theConnection.createStatement()) {
			String sql = TopologyMgtSQLRequestBuilder.updateAbout(LTECellCount, LTENRCount, WCDMACellCount, WCDMANRCount, GSMCellCount, GSMNRCount);
			stmt.executeUpdate(sql);
		}
		catch (SQLException ex) {
			LOG.error(ex);
		}
	}


}
