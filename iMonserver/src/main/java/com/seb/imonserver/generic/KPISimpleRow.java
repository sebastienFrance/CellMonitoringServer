package com.seb.imonserver.generic;

import java.util.List;

public class KPISimpleRow {
	private String _KPIName;
	private List<Float> _KPIValues;
	
	public KPISimpleRow(String KPIName, List<Float> KPIValues) {
		_KPIName = KPIName;
		_KPIValues = KPIValues;
	}

	public String getKPIName() {
		return _KPIName;
	}

	public List<Float> getKPIValues() {
		return _KPIValues;
	}
}
