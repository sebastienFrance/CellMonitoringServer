package com.seb.datasources;

import java.util.List;

public class Datasource {
	
	public static final int DS_TECHNO_LTE = 0;
	public static final int DS_TECHNO_WCDMA = 1;
	public static final int DS_TECHNO_GSM = 2;
	
	private int _id;
	private String _name;
	private String _description;
	private int _techno;
	private String _primaryHost;
	private String _secondaryHost;
	private int _portNumber;
	private String _userName;
	private String _password;
	
	List<DatasourceTopology> _topologyDatasources;
	
	
	public Datasource(String name, String description, int techno, String primaryHost, String secondaryHost, int portNumber, String userName, String password) {
		_id = -1;
		_name = name;
		_description = description;
		_techno = techno;
		_primaryHost = primaryHost;
		_secondaryHost = secondaryHost;
		_portNumber = portNumber;
		_userName = userName;
		_password = password;
	}
	
	public Datasource(int ID, String name, String description, int techno, String primaryHost, String secondaryHost, int portNumber, String userName, String password) {
		_id = ID;
		_name = name;
		_description = description;
		_techno = techno;
		_primaryHost = primaryHost;
		_secondaryHost = secondaryHost;
		_portNumber = portNumber;
		_userName = userName;
		_password = password;
	}
	
	
	public List<DatasourceTopology> getTopologyDatasources() {
		return _topologyDatasources;
	}
	
	public void setTopologyDatasources(List<DatasourceTopology> topologyDatasources) {
		_topologyDatasources = topologyDatasources;
	}
	
	public int getId() {
		return _id;
	}

	public void setId(int id) {
		_id = id;
	}

	public String getName() {
		return _name;
	}

	public void setName(String name) {
		this._name = name;
	}

	public String getDescription() {
		return _description;
	}

	public void setDescription(String _description) {
		this._description = _description;
	}

	public int getTechno() {
		return _techno;
	}

	public void setTechno(int _techno) {
		this._techno = _techno;
	}

	public String getPrimaryHost() {
		return _primaryHost;
	}

	public void setPrimaryHost(String _primaryHost) {
		this._primaryHost = _primaryHost;
	}

	public String getSecondaryHost() {
		return _secondaryHost;
	}

	public void setSecondaryHost(String _secondaryHost) {
		this._secondaryHost = _secondaryHost;
	}

	public int getPortNumber() {
		return _portNumber;
	}

	public void setPortNumber(int _portNumber) {
		this._portNumber = _portNumber;
	}

	public String getUserName() {
		return _userName;
	}

	public void setUserName(String _userName) {
		this._userName = _userName;
	}

	public String getPassword() {
		return _password;
	}

	public void setPassword(String _password) {
		this._password = _password;
	}
	
	@Override
	public boolean equals(Object other) {
		if (other == null) return false;
	    if (other == this) return true;
	    
	    if (getClass() != other.getClass()) return false;
	    
	    Datasource otherDS = (Datasource) other;
	    
		if (_name.equals(otherDS.getName()) == false) {
			return false;
		} 
		
		if (_description.equals(otherDS.getDescription()) == false) {
			return false;
		}

		if (_techno != otherDS.getTechno()) {
			return false;
		}
		if (_primaryHost.equals(otherDS.getPrimaryHost()) == false) {
			return false;
		}
		if (_secondaryHost.equals(otherDS.getSecondaryHost()) == false) {
			return false;
		}
		if (_portNumber != otherDS.getPortNumber()) {
			return false;
		}
		if (_userName.equals(otherDS.getUserName()) == false) {
			return false;
		}
		if (_password.equals(otherDS.getPassword()) == false) {
			return false;
		}
		
		
		List<DatasourceTopology> otherTopologyDS = otherDS.getTopologyDatasources();
		
		if (_topologyDatasources == null) {
			if (otherTopologyDS != null) return false;
		} else {
			if (otherTopologyDS != null) {
				if (_topologyDatasources.size() != otherTopologyDS.size()) return false;
			} else {
				return false;
			}
		}
		
		return true;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + _name.hashCode();
		return result;
	}
}
