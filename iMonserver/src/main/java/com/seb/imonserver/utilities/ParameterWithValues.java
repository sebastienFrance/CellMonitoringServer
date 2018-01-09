package com.seb.imonserver.utilities;


public class ParameterWithValues {
	private String _parameterName;
	private String[] _parameterValues;
	
	public ParameterWithValues(String parameterName, String[] parameterValues) {
		  _parameterName = parameterName;
		  _parameterValues = parameterValues;
	}
	
	public String getParameterName() {
		return _parameterName;
	}
	
	public String[] getParameterValues() {
		return _parameterValues;
	}
	
}
