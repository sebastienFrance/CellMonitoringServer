package com.seb.networkGenerator.CartoRadio;

public class CartoRadioGeoCoordinate {

	private String _latitude;
	private String _longitude;
	
	public CartoRadioGeoCoordinate(String latitude, String longitude) {
		_latitude = latitude;
		_longitude = longitude;
	}
	
	public String getLatitude() {
		return _latitude;
	}
	
	public String getLongitude() {
		return _longitude;
	}
}
