package com.seb.utilities;

public enum Technology {
	LTE("LTE"), WCDMA("WCDMA"), GSM("GSM"), ALL("ALL");
	
	
	private String _technoString;
	
	private Technology(String techno) {
		_technoString = techno;
	}
	
	@Override
	public String toString() {
		return _technoString;
	}
}
