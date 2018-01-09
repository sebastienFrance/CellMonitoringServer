package com.seb.networkTopology.database;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sqlite.SQLiteConfig;

import com.seb.networkGenerator.generic.AttrNameValue;
import com.seb.networkGenerator.generic.Cell;
import com.seb.networkTopology.generic.Adjacency;

public class DatabaseUtility {
	
	private static final Logger LOG = LogManager.getLogger(DatabaseUtility.class);
	
	private DatabaseUtility() {}
	
	public static Connection openDatabaseConnection(String outputDatabaseDir, String outputDatabaseName) {
		Connection theConnection = null;
		try {
			 SQLiteConfig config = new SQLiteConfig();
			 config.enableLoadExtension(true);

			Class.forName("org.sqlite.JDBC");
			theConnection = DriverManager.getConnection("jdbc:sqlite:" + outputDatabaseDir + "/" + outputDatabaseName , config.toProperties());
			theConnection.setAutoCommit(false);
		}
		catch (Exception ex) {
			LOG.error(ex);
		}
		return theConnection;
	}

	
	

	public static void insertCell(Connection theConnection, Cell theCell) {
		try(Statement stmt = theConnection.createStatement()) {
			String sql = TopologySQLRequestBuilder.insertCell(theCell);
			stmt.executeUpdate(sql);
		}
		catch (SQLException ex) {
			LOG.error(ex);
		}
	}
	

	public static void insertCellAttrNameValue(Connection theConnection, Cell theCell, AttrNameValue currAttrNameValue) {
		//CELLS_ATTRIBUTES
		try(Statement stmt = theConnection.createStatement()) {
			String sql = TopologySQLRequestBuilder.insertCellAttrNameValue(theCell, currAttrNameValue);
			stmt.executeUpdate(sql.toString());
		}
		catch (SQLException ex) {
			LOG.error(ex);
		}
	}
	
	public static void insertCellAttrNameValues(Connection theConnection, Cell theCell, List<AttrNameValue> AttrNameValues) {
		String insertAttrNameValueTemplate = TopologySQLRequestBuilder.getInsertAttrNameValueTemplate();

		try(PreparedStatement stmt = theConnection.prepareStatement(insertAttrNameValueTemplate)) {
			for (AttrNameValue currentAttrNameValue : AttrNameValues) {
				TopologySQLRequestBuilder.updateInsertAttrNameValueFor(stmt, theCell, currentAttrNameValue);
				stmt.executeUpdate();		
			}
		}
		catch (SQLException ex) {
			LOG.error(ex);
		}
	}

	
	public static void insertNeighbor(Connection theConnection, Cell sourceCell, Adjacency targetAdj) {
		try(Statement stmt = theConnection.createStatement()) {		
			String sql = TopologySQLRequestBuilder.insertNeighbor(sourceCell, targetAdj);
			stmt.executeUpdate(sql.toString());				
		}
		catch (SQLException ex) {
			LOG.error(ex);
		}

	}
	
	public static void insertNeighbors(Connection theConnection, Cell sourceCell, List<Adjacency> targetAdj) {
		String insertNeighborTemplate = TopologySQLRequestBuilder.getInsertNeighborTemplate();
		
		try(PreparedStatement stmt = theConnection.prepareStatement(insertNeighborTemplate)) {
			for (Adjacency currentAdj : targetAdj) {
				TopologySQLRequestBuilder.updateInsertNeighborFor(stmt, sourceCell, currentAdj);
				stmt.executeUpdate();		
			}
		}
		catch (SQLException ex) {
			LOG.error(ex);
		}

	}


}
