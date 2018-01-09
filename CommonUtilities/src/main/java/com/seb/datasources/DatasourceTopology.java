package com.seb.datasources;

public class DatasourceTopology {
	
	private int _id;
	private String _name;
	private String _description;
	private String _primaryHost;
	private String _secondaryHost;
	private int _EMSPortNumber;
	private String _EMSUserName;
	private String _EMSPassword;
	private String _FTPUserName;
	private String _FTPPassword;
	
	private Datasource _parentDS;
	
	public DatasourceTopology(Datasource parentDS, int ID, String name, String description, String primaryHost, String secondaryHost, int EMSPortNumber, String EMSUserName, String EMSPassword, String FTPUserName, String FTPPassword) {
		_id = ID;
	
		_name = name;
		_parentDS = parentDS;
		_description = description;
		_primaryHost = primaryHost;
		_secondaryHost = secondaryHost;
		_EMSPortNumber = EMSPortNumber;
		_EMSUserName = EMSUserName;
		_EMSPassword = EMSPassword;
		_FTPUserName = FTPUserName;
		_FTPPassword = FTPPassword;
	}

	public DatasourceTopology(Datasource parentDS, String name, String description, String primaryHost, String secondaryHost, int EMSPortNumber, String EMSUserName, String EMSPassword, String FTPUserName, String FTPPassword) {
		_id =  -1;
	
		_name = name;
		_parentDS = parentDS;
		_description = description;
		_primaryHost = primaryHost;
		_secondaryHost = secondaryHost;
		_EMSPortNumber = EMSPortNumber;
		_EMSUserName = EMSUserName;
		_EMSPassword = EMSPassword;
		_FTPUserName = FTPUserName;
		_FTPPassword = FTPPassword;
	}
	
	public Datasource getParentDS() {
		return _parentDS;
	}
	
	public void setParentDS(Datasource parentDS) {
		_parentDS = parentDS;
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
	
	public int getEMSPortNumber() {
		return _EMSPortNumber;
	}
	
	public void setEMSPortNumber(int EMSPortNumber) {
		_EMSPortNumber = EMSPortNumber;
	}

	public String getEMSUserName() {
		return _EMSUserName;
	}

	public void setEMSUserName(String _EMSUserName) {
		this._EMSUserName = _EMSUserName;
	}

	public String getEMSPassword() {
		return _EMSPassword;
	}

	public void setEMSPassword(String _EMSPassword) {
		this._EMSPassword = _EMSPassword;
	}

	
	public String getFTPUserName() {
		return _FTPUserName;
	}

	public void setFTPUserName(String _FTPUserName) {
		this._FTPUserName = _FTPUserName;
	}

	public String getFTPPassword() {
		return _FTPPassword;
	}

	public void setFTPPassword(String _FTPPassword) {
		this._FTPPassword = _FTPPassword;
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) return true;
		if (object == null) return false;
		
		if (getClass() != object.getClass()) return false;
		
		DatasourceTopology other = (DatasourceTopology) object;
		
		if (_id != other.getId()) return false;
		if (_name.equals(other.getName()) == false) return false;
		if (_description.equals(other.getDescription()) == false) return false;
		if (_primaryHost.equals(other.getPrimaryHost()) == false) return false;
		if (_secondaryHost.equals(other.getSecondaryHost()) == false) return false;
		if (_EMSPortNumber != other.getEMSPortNumber()) return false;
		if (_EMSUserName.equals(other.getEMSUserName()) == false) return false;
		if (_EMSPassword.equals(other.getEMSPassword()) == false) return false;
		if (_FTPUserName.equals(other.getFTPUserName()) == false) return false;
		if (_FTPPassword.equals(other.getFTPPassword()) == false) return false;
		
		if (_parentDS != other.getParentDS()) return false;
		
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
