package com.seb.imonserver.generic;


/**
 * Used to build JSON object that contains for given cell and a given KPI its list of values
 * 
 * @author Sebastien Brugalieres
 *
 */
public class CellKPIRow {
	private String _cellName;
	private String _KPIName;
	
	private String[] _KPIValues;
	
	public CellKPIRow(String cellName, String KPIName, String KPIValues) {
		_cellName = cellName;
		_KPIName = KPIName;
		
		// remove the end "," to avoid empty value
		String valuesToSplit = KPIValues.substring(0, KPIValues.length());
		valuesToSplit = valuesToSplit.replaceAll("%", "");
		_KPIValues = valuesToSplit.split(",");
		
	}
	
	public String getCellName() {
		return _cellName;
	}

	public String getKPIName() {
		return _KPIName;
	}

	public String[] getKPIValues() {
		return _KPIValues;
	}
	
}
