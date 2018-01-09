package com.seb.userManagement;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.seb.databaseAccess.DatabaseHelper;

public class DatabaseUserManagement {
	private static final Logger LOG = LogManager.getLogger(DatabaseUserManagement.class);

	private DatabaseUserManagement() {}
	
	/**
	 * Create the users table (create the tables)
	 * 
	 * @param DatabaseFullPathName
	 * @return Connection on the created database else null is returned
	 */
	public static Connection createDatabase(String DatabaseFullPathName) {
		Connection theConnection = null;

		theConnection = DatabaseHelper.openDatabaseConnection(DatabaseFullPathName);
		if (theConnection != null) {
			if (DatabaseUserManagement.createTablesForUserManagement(theConnection) == true) {
				return theConnection;
			} else {
				return null;
			}
		}

		return theConnection;
	}

	/**
	 * Create the USERS table in the database 
	 * 
	 * @param theConnection
	 * @return true when the tables have been created otherwise false
	 */
	private static boolean createTablesForUserManagement(Connection theConnection) {
		String sqlTable = UserMgtSQLRequestBuilder.createTable();
		String sqlIndex = UserMgtSQLRequestBuilder.createTableIndex();

		try (Statement stmt = theConnection.createStatement()) {

			stmt.executeUpdate(sqlTable);
			// Index on USERS table
			stmt.executeUpdate(sqlIndex);
			
			theConnection.commit();
			return true;
		} catch ( Exception e ) {
			LOG.error(e);;
			return false;
		}		
	}
	
	/**
	 * Insert a USERS in the database
	 * 
	 * @param theConnection
	 * @param theUser
	 * @return true when the user has been saved in the database otherwise it returns false
	 */
	public static boolean insertUser(String databaseFullPathName, UserDescription theUser) {
		String sql = UserMgtSQLRequestBuilder.insertUser(theUser);
		return executeUpdateAndCommit(databaseFullPathName, sql);
	}
	
	public static boolean deleteUser(String databaseFullPathName, String userName) {
		
		String sql = UserMgtSQLRequestBuilder.deleteUser(userName);
		return executeUpdateAndCommit(databaseFullPathName, sql);
	}
	
	/**
	 * Update user password in the database
	 * 
	 * @param theConnection
	 * @param theUser
	 * @return true when the user has been saved in the database otherwise it returns false
	 */
	public static boolean updateUserPassword(String databaseFullPathName, UserDescription theUser) {
		String sql = UserMgtSQLRequestBuilder.updateUserPassword(theUser);
		return executeUpdateAndCommit(databaseFullPathName, sql);
	}
	
	/**
	 * Update user for all parameters except the creation date and password
	 * 
	 * @param theConnection
	 * @param theUser
	 * @return true when the user has been saved in the database otherwise it returns false
	 */
	public static boolean updateUser(String databaseFullPathName, UserDescription theUser, boolean updatePassword) {
		String sql = UserMgtSQLRequestBuilder.updateUser(theUser, updatePassword);

		return executeUpdateAndCommit(databaseFullPathName, sql);

	}
	
	private static boolean executeUpdateAndCommit(String databaseFullPathName, String sql) {
		try(Connection theConnection = DatabaseHelper.openDatabaseConnection(databaseFullPathName);
			Statement stmt = theConnection.createStatement()) {
				stmt.executeUpdate(sql);
				theConnection.commit();
				return true;
			}
			catch (SQLException ex) {
				LOG.error(ex);
				return false;
			}		
	}

	
	/**
	 * Extract all Users from the database
	 *  
	 * @param theConnection
	 * @return List of UserDescription or null in case of error
	 */

	public static List<UserDescription> extractAllUsersFromDatabase(String databaseFullPathName) {
		String sql = UserMgtSQLRequestBuilder.extractAllUsersFromDatabase();

		List<UserDescription> datasourceList= new ArrayList<UserDescription>();
		
		try (Connection theConnection = DatabaseHelper.openDatabaseConnection(databaseFullPathName);
			 Statement stmt = theConnection.createStatement();
			 ResultSet rs = stmt.executeQuery(sql)) {
			while (rs.next()) {
				UserDescription newUser = createUserDescriptionFromResultSet(rs);
				datasourceList.add(newUser);
			}
			return datasourceList;
		} 
		catch (Exception ex) {
			LOG.error(ex);
			return null;
		}
	}
	
	/**
	 * Extract  User from the database
	 *  
	 * @param theConnection
	 * @param parentDS
	 * @return A UserDescription or null in case of error
	 */
	public static UserDescription extractUserFromDatabase(String databaseFullPathName, String userName) {
		String sql = UserMgtSQLRequestBuilder.extractUserFromDatabase(userName);

		try(Connection theConnection = DatabaseHelper.openDatabaseConnection(databaseFullPathName);
			Statement stmt = theConnection.createStatement();
			ResultSet rs = stmt.executeQuery(sql)) {

			if (rs.next()) {
				return  createUserDescriptionFromResultSet(rs);
			}
		} 
		catch (Exception ex) {
			LOG.error(ex);
		}

		return null;
	}


	
	/**
	 * Extract a Datasource from a ResultSet
	 * 
	 * @param rs
	 * @return Datasource or null in case of error
	 */
	private static UserDescription createUserDescriptionFromResultSet(ResultSet rs) {
		try {

			// TODO: use index to get column value, will be more efficient
			String name = rs.getString("name");
			String password = rs.getString("password");
			String description = rs.getString("description");

			String firstName = rs.getString("first_name");
			String lastName = rs.getString("last_name");
			String EMail = rs.getString("email");

			String creationDate = rs.getString("creation_date");
			String lastConnectionDate = rs.getString("last_connection_date");
			
			boolean isAdmin = DatabaseHelper.getBooleanFromDatabase(rs.getInt("IS_ADMIN"));
			boolean isPasswordMustBeChanged = DatabaseHelper.getBooleanFromDatabase(rs.getInt("IS_PASSWORD_TO_BE_CHANGED"));
			
			UserDescription newUser = new UserDescription(name, password, description, isAdmin, isPasswordMustBeChanged);
			newUser.setFirstName(firstName);
			newUser.setLastName(lastName);
			newUser.setEMail(EMail);
			
			newUser.setCreationDate(Long.parseLong(creationDate));
			newUser.setLastConnectionDate(Long.parseLong(lastConnectionDate));

			return newUser;
		}
		catch (SQLException ex) {
			LOG.error(ex);
		}
		return null;
	}
}
