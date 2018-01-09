package com.seb.imonserver.generic;


/**
 * Used to build a JSON object??? that contains the user credentials
 * @author Sebastien Brugalieres
 *
 */
public class Credentials {

	private String _ipAddress;
	private String _userName;
	private String _password;
	private int _portNumber;
	
	public Credentials(String IPAddress, int portNumber, String userName, String password) {
		_ipAddress = IPAddress;
		_portNumber = portNumber;
		_userName = userName;
		_password = password;
	}
	
	public String getIPAddress() {
		return _ipAddress;
	}
	
	public int getPortNumber() {
		return _portNumber;
	}

	public String getUserName() {
		return _userName;
	}
	
	public String getPassword() {
		return _password;
	}
}
