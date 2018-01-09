package com.seb.imonserver.datamodel;

/**
 * Used to build a JSON object to return attribute name/value/section
 * 
 * @author Sebastien Brugalieres
 *
 */
public class AttrNameValue {
	private String _name;
	private String _value;
	private String _section;
	
	public AttrNameValue(String name, String value, String section) {
		_name = name;
		_value = value;
		_section = section;
	}
	
	public String getName() {
		return _name;
	}
	
	public String getValue() {
		return _value;
	}
	
	public String getSection() {
		return _section;
	}
}
