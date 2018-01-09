package com.seb.imonserver.userManagement;

public class ConnectResponse {

	private int _connectionStatus;
	private boolean _isAdmin;
	
	public ConnectResponse(int connectionStatus, boolean isAdmin) {
		_connectionStatus = connectionStatus;
		_isAdmin = isAdmin;
	}

	public ConnectResponse(int connectionStatus) {
		_connectionStatus = connectionStatus;
		_isAdmin = false;
	}

	
	public int getConnectionStatus() {
		return _connectionStatus;
	}
	
	public boolean getIsAdmin() {
		return _isAdmin;
	}
	
}
