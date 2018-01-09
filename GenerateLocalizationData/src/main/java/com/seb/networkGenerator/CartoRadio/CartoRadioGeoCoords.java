package com.seb.networkGenerator.CartoRadio;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CartoRadioGeoCoords {
	private static final Logger LOG = LogManager.getLogger(CartoRadioGeoCoords.class);
	
	private static final int INDEX_SUPPORT = 0;
	private static final int INDEX_LONGITUDE = 1;
	private static final int INDEX_LATITUDE = 2;

	
	private Map<String, CartoRadioGeoCoordinate> _support2GeoCoordinate; 
	
	public CartoRadioGeoCoords(String geoCoordsFileName) {
		try(FileReader geoCoordinateFile = new FileReader(geoCoordsFileName);
			BufferedReader reader = new BufferedReader (geoCoordinateFile)) {

			_support2GeoCoordinate = new HashMap<String, CartoRadioGeoCoordinate>();
			
			String currentLine;

			while ((currentLine = reader.readLine()) != null) {
				String value = currentLine.trim();
				String[] listOfValues = value.split(";");
				CartoRadioGeoCoordinate currentGeoCoordinate = new CartoRadioGeoCoordinate(listOfValues[INDEX_LATITUDE], listOfValues[INDEX_LONGITUDE]);
				
				_support2GeoCoordinate.put(listOfValues[INDEX_SUPPORT], currentGeoCoordinate);
				
			}
			LOG.info("CartoRadioGeoCoords::CartoRadioGeoCoords found in " + geoCoordsFileName + " " + _support2GeoCoordinate.size() + " cells");
		} catch (Exception ex){
			LOG.error(ex);;
		}
	}
	
	public CartoRadioGeoCoordinate findGeoCoordinateForSupport(String support) {
		return _support2GeoCoordinate.get(support);
	}
}
