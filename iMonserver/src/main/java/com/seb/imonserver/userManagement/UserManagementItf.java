package com.seb.imonserver.userManagement;

import java.io.OutputStream;

import com.seb.userManagement.UserDescription;

public interface UserManagementItf {

	public static int AUTH_OK = 0;
	public static int UNKNOWN_USER = 1;
	public static int INVALID_PASSWORD = 2;

	public boolean initialize();
	public boolean isAuthenticatedUser(String userName, String password);
	public void connect(String userName, String password, OutputStream out);
	//public void connect(UserDescription theUser, OutputStream out);
	public void changePassword(String userName, String oldPassword, String newPassword, OutputStream out);
	
	public void getUsers(String userName, OutputStream out);
	
	public void addUser(UserDescription newUser, OutputStream out);
	public void updateUser(UserDescription user, OutputStream out);

	public void deleteUser(String user, OutputStream out);

}
