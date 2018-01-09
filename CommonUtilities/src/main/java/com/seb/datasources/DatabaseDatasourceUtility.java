package com.seb.datasources;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.seb.databaseAccess.DatabaseHelper;
import com.seb.datasources.Datasource;
import com.seb.datasources.DatasourceTopology;

public class DatabaseDatasourceUtility {
	private static final Logger LOG = LogManager.getLogger(DatabaseDatasourceUtility.class);

	private DatabaseDatasourceUtility() {}
	/**
	 * Create the datasource table (create the tables)
	 * 
	 * @param DatabaseFullPathName
	 * @return Connection on the created database else null is returned
	 */
	public static Connection createDatabase(String DatabaseFullPathName) {
		Connection theConnection = null;

		theConnection = DatabaseHelper.openDatabaseConnection(DatabaseFullPathName);
		if (theConnection != null) {
			if (DatabaseDatasourceUtility.createTablesForDatasource(theConnection) == true) {
				return theConnection;
			} else {
				return null;
			}
		}

		return theConnection;
	}
	
	public static boolean deleteDatabase(String DatabaseFullPathName) {
		
		File database = new File(DatabaseFullPathName);
		
		try {
			return database.delete();
		}
		catch (Exception ex) {
			LOG.error(ex);
			return false;
		}

	}


	/**
	 * Get the id of the last inserted row for the connection 
	 * 
	 * @param theConnection
	 * @return value of the last created index by an insert. 0 is returned in case of error
	 */
	public static int getLastRowId(Connection theConnection) {
		Statement stmt = null;
		try {
			stmt = theConnection.createStatement();
			ResultSet rs = stmt.executeQuery("select last_insert_rowid();");
			int value = rs.getInt(1);
			stmt.close();
			
			return value;
		} catch ( Exception ex ) {
			LOG.error(ex);
			return 0;
		}		
	}
	
	/**
	 * Create the datasources tables in the database 
	 * 
	 * @param theConnection
	 * @return true when the tables have been created otherwise false
	 */
	private static boolean createTablesForDatasource(Connection theConnection) {
		try(Statement stmt = theConnection.createStatement()) {

			String sql = DatasourceSQLRequestBuilder.createTableDatasource();
			stmt.executeUpdate(sql);

			sql = DatasourceSQLRequestBuilder.createTableDatasourceTopology();
			stmt.executeUpdate(sql);

			// Index on CELLS table
			sql = DatasourceSQLRequestBuilder.createIndexTableDatasource();
			stmt.executeUpdate(sql);
			sql = DatasourceSQLRequestBuilder.createIndexDatasourceTopology();
			stmt.executeUpdate(sql);
			return true;
		} catch ( Exception ex ) {
			LOG.error(ex);
			return false;
		}		
	}
	
	/**
	 * Insert a Datasource in the database
	 * 
	 * @param theConnection
	 * @param theDatasource
	 * @return true when the datasource has been saved in the database otherwise it returns false
	 */
	public static boolean insertDatasource(Connection theConnection, Datasource theDatasource) {
		
		String sql = DatasourceSQLRequestBuilder.insertDatasource(theDatasource);

		try (Statement stmt = theConnection.createStatement()) {
			stmt.executeUpdate(sql);
			return true;
		}
		catch (SQLException ex) {
			LOG.error("Failure for Datasource:" + theDatasource.getName(), ex);
			return false;
		}
	}
	
	/**
	 * Insert a DatasourceTopology in the database
	 * @param theConnection
	 * @param theDatasourceTopology
	 * @return true when the DatasourceTopology has been saved in the database otherwise false is returned
	 */
	public static boolean insertDatasourceTopology(Connection theConnection, DatasourceTopology theDatasourceTopology) {
		
		String sql = DatasourceSQLRequestBuilder.insertDatasourceTopology(theDatasourceTopology);

		try(Statement stmt = theConnection.createStatement()) {
			
			stmt.executeUpdate(sql);
			return true;
		}
		catch (SQLException ex) {
			LOG.error("Failure for Datasource:" + theDatasourceTopology.getName(), ex);
			return false;
		}
	}
	
	/**
	 * Extract all Datasources and datasourceTopology children from the database
	 * 
	 * @param theConnection
	 * @return The List of Datasource otherwise null is returned
	 */
	public static List<Datasource> extractDatasourceObjects(String databaseFullPathName) {
		List<Datasource> datasourceList= new ArrayList<Datasource>();
		
		String sql = DatasourceSQLRequestBuilder.extractDatasourceObjects();

		try (Connection theConnection = DatabaseHelper.openDatabaseConnection(databaseFullPathName);
			 Statement stmt = theConnection.createStatement();
			 ResultSet rs = stmt.executeQuery(sql)) {

			while (rs.next()) {
				Datasource newDatasource = createDatasourceFromResultSet(rs);
				
				List<DatasourceTopology> topologyDS = extractDatasourceTopologyObjects(theConnection, newDatasource);
				newDatasource.setTopologyDatasources(topologyDS);
				
				datasourceList.add(newDatasource);
			}
			return datasourceList;
		} 
		catch (Exception ex) {
			LOG.error(ex);
			return null;
		}

	}
	
	/**
	 * Extract the DatasourceTopology children of a Datasource
	 *  
	 * @param theConnection
	 * @param parentDS
	 * @return List of DatasourceTopology or null in case of error
	 */
	private static List<DatasourceTopology> extractDatasourceTopologyObjects(Connection theConnection, Datasource parentDS) {
		List<DatasourceTopology> datasourceList= new ArrayList<DatasourceTopology>();
		
		String sql = DatasourceSQLRequestBuilder.extractDatasourceTopologyObjects(parentDS);
		
		try(Statement stmt = theConnection.createStatement();
			ResultSet rs = stmt.executeQuery(sql)) {

			while ( rs.next() ) {
				DatasourceTopology newDatasource = createDatasourceTopologyFromResultSet(rs, parentDS);
				datasourceList.add(newDatasource);
			}
			
			if (datasourceList.size() == 0) {
				return null;
			} else {			
				return datasourceList;
			}
		} 
		catch (Exception ex) {
			LOG.error(ex);
			return null;
		}
	}

	
	/**
	 * Extract a Datasource from a ResultSet
	 * 
	 * @param rs
	 * @return Datasource or null in case of error
	 */
	private static Datasource createDatasourceFromResultSet(ResultSet rs) {
		try {

			// TODO: use index to get column value, will be more efficient
			Datasource newDS = new Datasource(rs.getInt("id"),
					rs.getString("name"),
					rs.getString("description"),
					rs.getInt("techno"),
					rs.getString("primary_host"),
					rs.getString("secondary_host"),
					rs.getInt("port_number"),
					rs.getString("username"),
					rs.getString("password"));

			return newDS;
		}
		catch (SQLException ex) {
			LOG.error(ex);
		}
		return null;
	}
	
	/**
	 * Extract a DatasourceTopology from a result set and attach it to the parent Datasource
	 * 
	 * @param rs
	 * @param parentDS
	 * @return DatasourceTopology or null in case of error
	 */
	private static DatasourceTopology createDatasourceTopologyFromResultSet(ResultSet rs, Datasource parentDS) {
		try {

			// TODO: use index to get column value, will be more efficient
			DatasourceTopology newDS = new DatasourceTopology(parentDS,
					rs.getInt("id"),
					rs.getString("name"),
					rs.getString("description"),
					rs.getString("primary_host"),
					rs.getString("secondary_host"),
					rs.getInt("ems_port_number"),
					rs.getString("ems_username"),
					rs.getString("ems_password"),
					rs.getString("ftp_username"),
					rs.getString("ftp_password"));

			return newDS;
		}
		catch (SQLException ex) {
			LOG.error(ex);
		}
		return null;
	}


	/**
	 * Create Fake datasources in database 
	 * 
	 * @param name
	 * @param techno
	 * @return Datasource
	 */
	public static Datasource createFakeDatasource(String name, int techno) {
		String DSName = name;
		String DSDescription = "Description of " + name;
		int DSTechno = techno;
		String DSPrimaryHost = "127.0.0.1";
		String DSSecondaryHost = "127.0.0.2";
		int DSPortNumber = 8443;
		String DSUserName = "Seb";
		String DSPassword = "TheSecret";
		
		Datasource newDS = new Datasource(DSName, DSDescription, DSTechno, DSPrimaryHost, DSSecondaryHost, DSPortNumber, DSUserName, DSPassword);
		
		return newDS;
	}
	
	/**
	 * Create Fake DatasourceTopology in database
	 * @param parentDS
	 * @param name
	 * @return
	 */
	public static DatasourceTopology createFakeDatasourceTopology(Datasource parentDS, String name) {
		
		String DSName = name;
		String DSDescription = "Description of " + name;
		String DSPrimaryHost = "255.0.0.1";
		String DSSecondaryHost = "255.0.0.2";
		int DSEMSPortNumber = 9443;
		String DSEMSUserName = "SebEMS";
		String DSEMSPassword = "TheEMSSecret";
		String DSFTPUserName = "SebFTP";
		String DSFTPPassword = "TheFTPSercret";
		
		DatasourceTopology newDSTopology = new DatasourceTopology(parentDS, DSName, DSDescription, DSPrimaryHost, DSSecondaryHost, 
				DSEMSPortNumber, DSEMSUserName, DSEMSPassword, DSFTPUserName, DSFTPPassword);
	
		return newDSTopology;
	}
	
	
}
