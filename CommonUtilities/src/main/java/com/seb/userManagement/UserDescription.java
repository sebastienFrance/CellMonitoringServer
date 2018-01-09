package com.seb.userManagement;

import java.util.Date;




public class UserDescription {
	private String _userName;
	private String _userPassword;
	private String _description;
	
	private String _firstName;
	private String _lastName;
	private String _email;
	
	private long _creationDate;
	private long _lastConnectionDate;
	
	private boolean _isAdmin;
	private boolean _isPasswordMustBeChanged;

	public UserDescription(String name, String password, String description, boolean isAdmin, boolean isPasswordMustBeChanged) {
		_userName = name;
		_userPassword = password;
		_description = description;
		_isAdmin = isAdmin;
		_isPasswordMustBeChanged = isPasswordMustBeChanged;
		
		_firstName = "";
		_lastName = "";
		_email = "";
		
		Date currentTime = new Date();		
		_creationDate = currentTime.getTime();
		_lastConnectionDate = 0;
	}
	
	public void updateLastConnectionDate() {
		Date currentTime = new Date();		
		_lastConnectionDate = currentTime.getTime();
	}
	
	public void updateUserInfos(String firstName, String lastName, String EMail) {
		  setFirstName(firstName);
		  setLastName(lastName);
		  setEMail(EMail);
	}
	
	public String getName() {
		return _userName;
	}

	// This class is also used to send user through JSON, we don't want to send password to App and so
	// this accessor cannot start with get !
	public String extractPassword() {
		return _userPassword;
	}
	
	// it's not a set because it's used by JSON. It will avoid a warning due to JSON introspection
	public void resetPassword(String newUserPassword) {
		_userPassword = newUserPassword;
	}
	
	public String getDescription() {
		return _description;
	}
	
	public boolean isAdmin() {
		return _isAdmin;
	}

	public boolean isPasswordMustBeChanged() {
		return _isPasswordMustBeChanged;
	}
	
	public void setFirstName(String firstName) {
		_firstName = firstName;
	}
	
	public String getFirstName() {
		return _firstName;
	}

	public void setLastName(String lastName) {
		_lastName = lastName;
	}
	
	public String getLastName() {
		return _lastName;
	}

	public void setEMail(String email) {
		_email = email;
	}
	
	public String getEMail() {
		return _email;
	}

	public long getCreationDate() {
		return _creationDate;
	}
	
	public void setCreationDate(long creationDate) {
		_creationDate = creationDate;
	}
	
	public long getLastConnectionDate() {
		return _lastConnectionDate;
	}
	
	public void setLastConnectionDate(long lastConnectionDate) {
		_lastConnectionDate = lastConnectionDate;
	}
	
	public void setInvalidConnectionDate() {
		_lastConnectionDate = -1;
	}
	
	public boolean hasInvalidConnectionDate() {
		if (_lastConnectionDate == -1) {
			return true;
		} else {
			return false;
		}
	}
 	
}

