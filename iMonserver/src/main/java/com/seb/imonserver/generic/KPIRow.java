package com.seb.imonserver.generic;


/**
 * @author Sebastien Brugalieres
 *
 */
public class KPIRow {
	private String _KPIName;
	private String _KPIValues;
	
	public KPIRow(String theRow) {
		int indexComa = theRow.indexOf(',');
		int indexParenthesis = theRow.indexOf('(');
		_KPIName = theRow.substring(0, indexParenthesis);
		_KPIValues = theRow.substring(indexComa+1, theRow.length());
	}
	public KPIRow(String KPIName, String KPIValues) {
		_KPIName = KPIName;
		_KPIValues = KPIValues;
	}
	
	public String getKPIName() {
		return _KPIName;
	}

	public String getKPIValues() {
		return _KPIValues;
	}
}
