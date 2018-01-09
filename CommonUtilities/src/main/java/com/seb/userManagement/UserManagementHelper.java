package com.seb.userManagement;

import java.sql.Connection;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class UserManagementHelper {
	private static final Logger LOG = LogManager.getLogger(UserManagementHelper.class);

	private String _databaseFullPathName;
	
	private static UserManagementHelper _instance = null;
	
	private UserManagementHelper() {
	}
	
	/**
	 * Singleton DatasourceHelper
	 * 
	 * @return The DatasourceHelper to read the content of the Datasource database
	 */
	public static UserManagementHelper getInstance() {
		if (_instance == null) {
			_instance = new UserManagementHelper();
		}
		
		return _instance;
	}
	
	
	/**
	 * Must be called before to use the accessors to get data
	 * 
	 * @param databaseFullPathName
	 * @return true when the data have been correctly loaded / initialized else it returns false
	 */
	public boolean initialize(String databaseFullPathName) {
		_databaseFullPathName = databaseFullPathName;
		
		try (Connection theNewConnection = DatabaseUserManagement.createDatabase(_databaseFullPathName)) {
			if (theNewConnection != null) {
				LOG.info("initialize::database has been created!");
				return true;
			}
		} catch (Exception ex) {
			LOG.error(ex);;
		}

		return false;
	}

	public UserDescription getUser(String userName) {
		return DatabaseUserManagement.extractUserFromDatabase(_databaseFullPathName, userName);
	}


	public List<UserDescription> getAllUsers() {
		return DatabaseUserManagement.extractAllUsersFromDatabase(_databaseFullPathName);
	}

	public boolean updateUserPassword(UserDescription user) {
		return DatabaseUserManagement.updateUserPassword(_databaseFullPathName, user);
	}
	
	public boolean updateUser(UserDescription user, boolean updatePassword) {
		return DatabaseUserManagement.updateUser(_databaseFullPathName, user, updatePassword);
	}

	public boolean addUser(UserDescription newUser) {
		return DatabaseUserManagement.insertUser(_databaseFullPathName, newUser);
	}
	
	public boolean deleteUser(UserDescription newUser) {
		return deleteUser(newUser.getName());
	}

	public boolean deleteUser(String userName) {
		return DatabaseUserManagement.deleteUser(_databaseFullPathName, userName);
	}

}
