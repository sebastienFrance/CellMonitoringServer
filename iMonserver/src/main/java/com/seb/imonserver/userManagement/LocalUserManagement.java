package com.seb.imonserver.userManagement;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.seb.userManagement.UserDescription;
import com.seb.userManagement.UserManagementHelper;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class LocalUserManagement implements UserManagementItf {
	private static final Logger LOG = LogManager.getLogger(LocalUserManagement.class);

	private String _userDatabaseName;
	
	
	
	
	public LocalUserManagement(String userDatabaseName) {
		_userDatabaseName = userDatabaseName;
	}
	
	@Override
	public boolean initialize() {
		boolean result = UserManagementHelper.getInstance().initialize(_userDatabaseName);
		if (result == false) {
			return false;
		}
		
		return insertDefaultUser();
	}
	
	private boolean insertDefaultUser() {
		UserDescription theUser = UserManagementHelper.getInstance().getUser("admin");
		if (theUser == null) {
			// Default is iMonitoring1234
			String defaultPass = "85aeab0f861afe74c38dae02b1aadab7774ba78cdd2aab59fa5fb13f5cb9a170";
			                    //2a4ba5b7a5d144e8aa6547be02b16d7f84171bdf71ba3b46d562912d188953ab
			theUser = new UserDescription("admin", defaultPass, "Main administrator. Cannot be deleted", true, true);
			theUser.setFirstName("Administrator");
			theUser.setLastName("iMonitoring");
			
			boolean result = UserManagementHelper.getInstance().addUser(theUser);
			if (result == true) {
				LOG.info("insertDefaultUser::Default user: " + theUser.getName() + " has been successfully created");
			} else {
				LOG.error("insertDefaultUser::Default user: " + theUser.getName() + " failed to be created");
				return false;
			}
		} else {
			LOG.info("insertDefaultUser::Default user: " + theUser.getName() + " already exists");							
		}
		
		return true;
	}
	
	@Override
	public void addUser(UserDescription newUser, OutputStream out) {
		
		// Hash the password before to store it in the database
		String hashForPassword = hashPassword(newUser.extractPassword());
		
		newUser.resetPassword(hashForPassword);
		
		JSONObject newObject = new JSONObject();
		if (UserManagementHelper.getInstance().addUser(newUser) == true) {
			newObject.put("Status", AUTH_OK);			
			LOG.info("addUser::user: " + newUser.getName() + " has been successfully created");
		} else {
			newObject.put("Status", 3); // To be completed
			LOG.warn("addUser::user: " + newUser.getName() + " cannot be created");
		}

		sendJSONObject(newObject, out);
	}
	
	@Override
	public void updateUser(UserDescription newUser, OutputStream out) {
		
		// Hash the password before to store it in the database
		boolean updatePassword = false;
		if (newUser.extractPassword().equals("") == false) {
			String hashForPassword = hashPassword(newUser.extractPassword());
			newUser.resetPassword(hashForPassword);
			updatePassword = true;
		}
		
		JSONObject newObject = new JSONObject();
		if (UserManagementHelper.getInstance().updateUser(newUser, updatePassword) == true) {
			newObject.put("Status", AUTH_OK);			
			LOG.info("updateUser::user: " + newUser.getName() + " has been successfully updated");
		} else {
			newObject.put("Status", 3); // To be completed
			LOG.warn("updateUser::user: " + newUser.getName() + " cannot be updated");
		}

		sendJSONObject(newObject, out);
	}

	
	@Override
	public void deleteUser(String user, OutputStream out) {
		JSONObject newObject = new JSONObject();
		if (user.equals("admin")) {
			newObject.put("Status", 3); // To be completed
			LOG.warn("deleteUser::user: " + user + " cannot be deleted");			
		}
		
		
		if (UserManagementHelper.getInstance().deleteUser(user) == true) {
			newObject.put("Status", AUTH_OK);			
			LOG.info("addUser::user: " + user + " has been successfully deleted");
		} else {
			newObject.put("Status", 3); // To be completed
			LOG.warn("deleteUser::user: " + user + " cannot be deleted");
		}

		sendJSONObject(newObject, out);		
	}
	
	private void sendJSONObject(Object newObject, OutputStream out) {
		try {
			out.write(newObject.toString().getBytes());
			out.flush();
			out.close();
		} catch (IOException e) {
			  LOG.error(e);
		}		
	}
	
	
	@Override
	public void getUsers(String userName, OutputStream out) {
		// Should check if user is authorized to get it
		
		List<UserDescription> userList = UserManagementHelper.getInstance().getAllUsers();

		JSONArray newArray = new JSONArray();
		JSONObject newObject = null;
		for (UserDescription currentUser : userList) {
			newObject = JSONObject.fromObject(currentUser);
			newArray.add(newObject);
		}
		sendJSONObject(newArray, out);
	}

	
	/**
	 * Check Client App credentials and log the result. Credentials are checked based on the content of the 
	 * password file read during the initialization of the servlet
	 * 
	 * @param userName user name sent by the Client App
	 * @param password password sent by the Client App
	 * @param out Writer used to send a JSON message to grant or deny the connection
	 */

	@Override
	public void connect(String userName, String password, OutputStream out) {

		String hashForPassword = hashPassword(password);

		int status = UNKNOWN_USER;
		boolean isAdmin = false;
		UserDescription theUser = UserManagementHelper.getInstance().getUser(userName);
		
		if (theUser == null) {
			status = UNKNOWN_USER;
			LOG.info("Connect: Unknown user " + userName);	
		} else {
			
			theUser.updateLastConnectionDate();
			UserManagementHelper.getInstance().updateUser(theUser, false);
			
			if (theUser.extractPassword().equals(hashForPassword)) {
				status = AUTH_OK;
				isAdmin = theUser.isAdmin();
				LOG.info("Connect: User " + userName + " is connected!");			
			} else {
				status = INVALID_PASSWORD;
				LOG.info("Connect: Invalid password for " + userName);			
			}
		}

		ConnectResponse theResponse = new ConnectResponse(status, isAdmin);
		
		JSONObject newObject = JSONObject.fromObject(theResponse);
		sendJSONObject(newObject, out);
	}

	@Override
	public boolean isAuthenticatedUser(String userName, String password) {
		if (password == null) {
			LOG.info("isAuthenticatedUser: empty password for " + userName);			
			return false;			
		}
		
		String hashForPassword = hashPassword(password);

		UserDescription theUser = UserManagementHelper.getInstance().getUser(userName);
		
		if (theUser == null) {
			LOG.info("isAuthenticatedUser: Unknown user " + userName);	
			return false;
		} else {
			
			if (theUser.extractPassword().equals(hashForPassword)) {
				LOG.info("isAuthenticatedUser: User " + userName + " is connected!");			
				return true;
			} else {
				LOG.info("isAuthenticatedUser: Invalid password for " + userName);			
				return false;
			}
		}
	}
	
	@Override
	public void changePassword(String userName, String oldPassword, String newPassword, OutputStream out) {

		String hashForNewPassword = hashPassword(newPassword);
		String hashForOldPassword = hashPassword(oldPassword);
		
		JSONObject newObject = new JSONObject();
		UserDescription theUser = UserManagementHelper.getInstance().getUser(userName);
		if (theUser == null) {
			newObject.put("Status", UNKNOWN_USER);
			LOG.info("changePassword: Unknown user " + userName);	
		} else {
			if (theUser.extractPassword().equals(hashForOldPassword) == false) {
				newObject.put("Status", INVALID_PASSWORD);
				LOG.info("changePassword: user " + userName + " has invalid old password");	
			} else { 
				theUser.resetPassword(hashForNewPassword);
				if (UserManagementHelper.getInstance().updateUserPassword(theUser) == true) {				
					newObject.put("Status", AUTH_OK);
					LOG.info("changePassword: User " + userName + " is updated!");			
				} else {
					newObject.put("Status", UNKNOWN_USER);
					LOG.info("changePassword: Unknown user " + userName);	
				}
			}
		}

		sendJSONObject(newObject, out);
	}
	
	private static String hashPassword(String passwordToHash) {
		String sha256hex = DigestUtils.sha256Hex(passwordToHash);
		return sha256hex;
	}

}
