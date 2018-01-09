package com.seb.topologyMgt;

import java.util.ArrayList;
import java.util.List;


import com.seb.topologyMgt.GeoLocation;


public class GeoIndex {

	private int _latitudeSlices;
	private int _longitudeSlices;
	private int _rowSize;
	private int _rowNumber;
	
	public static final double EARTH_RADIUS = 6371.01;
	
	private static final int MULTIPLIER_FACTOR = 1000;
	
	/**
	 * 
	 * @param latitudeSlices number of degree for latitudes slices (min is 0.001)
	 * @param longitudeSlices number of degree for longitude slices (min is 0.001)
	 * 
	 */
	public GeoIndex(double latitudeSlices, double longitudeSlices) {
		_latitudeSlices = (int) (latitudeSlices * MULTIPLIER_FACTOR);
		_longitudeSlices = (int) (longitudeSlices * MULTIPLIER_FACTOR);
		_rowSize = (int)(360/longitudeSlices);
		_rowNumber = (int)(180/latitudeSlices);
	}
	
	
	/**
	 * 
	 * @return Max index for latitude
	 */
	public long getLatitudeMaxSliceIndex() {
		return getLatitudeSliceIndex(90.0);
	}

	/**
	 * 
	 * @return max value for the combined index
	 */
	public long getMaxIndex() {
		return ((_rowSize * _rowNumber) -1);
	}
	
	/**
	 * 
	 * @param location a valid GeoLocation 
	 * @return combinedIndex for the given GeoLocation
	 */
	public Long getIndex(GeoLocation location) {
		return getIndex(location.getLatitudeInDegrees(), location.getLongitudeInDegrees());
	}
	
	public GeoLocation[] getBoundingBoxFor(long index) {
		long latitudeSliceIndex = getLatitudeSliceIndexFromCombinedIndex(index);
		long longitudeSliceIndex = getLongitudeSliceIndexFromCombinedIndex(index);
		
		return getBoundingBoxFromLatLongIndexes(latitudeSliceIndex, longitudeSliceIndex);
	}
	
	private GeoLocation[] getBoundingBoxFromLatLongIndexes(long latitudeSliceIndex, long longitudeSliceIndex) {
		double maxLatitude = getMaxLatitudeForSliceIndex(latitudeSliceIndex);
		double minLatitude = getMinLatitudeForSliceIndex(latitudeSliceIndex);
		double maxLongitude = getMaxLongitudeForSliceIndex(longitudeSliceIndex);
		double minLongitude = getMinLongitudeForSliceIndex(longitudeSliceIndex);
		
		GeoLocation minLatLong = GeoLocation.fromDegrees(minLatitude, minLongitude);
		GeoLocation maxLatLong = GeoLocation.fromDegrees(maxLatitude, maxLongitude);
	
		GeoLocation[] boundingBox = new GeoLocation[2];
		boundingBox[0] = minLatLong;
		boundingBox[1] = maxLatLong;
		
		return boundingBox;
	}

	/**
	 * 
	 * @param latitude latitude in degree from [-90.0..90.0]
	 * @param longitude longitude in degree from [-180.0..180.0]
	 * @return combined index for the give latitude / longitude
	 */
	public Long getIndex(double latitude, double longitude) {
		long latitudeIndex = getLatitudeSliceIndex(latitude);
		long longitudeIndex = getLongitudeSliceIndex(longitude);
			
		return computeCombinedIndex(latitudeIndex, longitudeIndex);
	}
	
	/**
	 * 
	 * @param latitudeIndex slice index for latitude
	 * @param longitudeIndex slice index for longitude
	 * @return combined index for the give slice indexes
	 */
	private Long computeCombinedIndex(long latitudeIndex, long longitudeIndex) {
		long combinedIndex = (latitudeIndex * _rowSize) + longitudeIndex;
		return new Long(combinedIndex);		
	}
	
	/**
	 * 
	 * @param combinedIndex the combined index from which the latitude slice index must be computed
	 * @return latitude slice index
	 */
	public long getLatitudeSliceIndexFromCombinedIndex(long combinedIndex) {
		return combinedIndex / _rowSize; 
	}
	
	/**
	 * 
	 * @param combinedIndex the combined index from which the longitude slice index must be computed
	 * @return longitude slice index
	 */
	public long getLongitudeSliceIndexFromCombinedIndex(long combinedIndex) {
		return combinedIndex % _rowSize;
	}
	
	/**
	 * If the minLongitude > maxLongitude it's automatically managed by this method to return
	 * the appropriate indexes
	 * 
	 * @param minLatitude Latitude min of the bounding box (in degree from [-90.0..90.0])
	 * @param minLongitude Longitude min of the bounding box (in degree from [-180.0..180.0])
	 * @param maxLatitude Latitude max of the bounding box (in degree from [-90.0..90.0])
	 * @param maxLongitude Longitude max of the bounding box (in degree from [-180.0..180.0])
	 * 
	 * @return List of combined indexes that are contained in the bounding box
	 */
	public List<Long> indexesForBoundingBox(double minLatitude, double minLongitude, double maxLatitude, double maxLongitude) {
		List<Long> boundingBoxIndexes = new ArrayList<Long>();

		long minLatitudeIndex = getLatitudeSliceIndex(minLatitude);
		long minLongitudeIndex = getLongitudeSliceIndex(minLongitude);

		long maxLatitudeIndex = getLatitudeSliceIndex(maxLatitude);
		long maxLongitudeIndex = getLongitudeSliceIndex(maxLongitude);

		boolean crossBorder = false;
		long maxLongitudeIterationIndex = maxLongitudeIndex;
		if (minLongitude > maxLongitude) {
			crossBorder = true;
			maxLongitudeIterationIndex = getLongitudeMaxSliceIndex();
		} 

		for (long i = minLongitudeIndex; i <= maxLongitudeIterationIndex; i++) {
			for (long j = minLatitudeIndex; j <= maxLatitudeIndex; j++) {
				boundingBoxIndexes.add(computeCombinedIndex(j, i));
			}
		}

		if (crossBorder) {
			for (long i = 0; i <= maxLongitudeIndex; i++) {
				for (long j = minLatitudeIndex; j <= maxLatitudeIndex; j++) {
					boundingBoxIndexes.add(computeCombinedIndex(j, i));
				}
			}			
		}

		return boundingBoxIndexes;
	}

	/**
	 * 
	 * @return Max index for longitude
	 */
	public long getLongitudeMaxSliceIndex() {
		return getLongitudeSliceIndex(180.0);
	}

	/**
	 * 
	 * @param latitude latitude in degree from [-90.0..90.0]
	 * @return slice index for latitude
	 */
	public long getLatitudeSliceIndex(double latitude) {
		double fixedLatitude = latitude + 90.0;
		
		// return the closest long 
		long integerPart = Math.round(fixedLatitude * MULTIPLIER_FACTOR) ;
		long index = (int)(integerPart / _latitudeSlices); 
		
		if (index == 0) {
			return index;
		} else {
			if ((integerPart % _latitudeSlices) == 0 ) {
				return index -1;
			} else {
				return index;
			}
		}
	}
	
	/**
	 * 
	 * @param longitude longitude in degree from [-180.0..180.0]
	 * @return slice index for longitude
	 */
	public long getLongitudeSliceIndex(double longitude) {
		double fixedLongitude = longitude + 180.0;
		
		// return the closest long 
		long integerPart = Math.round(fixedLongitude * MULTIPLIER_FACTOR) ;		
		long index = (int)(integerPart / _longitudeSlices); 
		
		if (index == 0) {
			return index;
		} else {
			if ((integerPart % _longitudeSlices) == 0 ) {
				return index -1;
			} else {
				return index;
			}
		}
	}
	
	/**
	 * 
	 * @param index latitude index slice
	 * @return max latitude [-90.0..90.0] for the given latitude slice index 
	 */
	public double getMaxLatitudeForSliceIndex(long index) {
		long baseLatitude = ((index * _latitudeSlices) - 90 * MULTIPLIER_FACTOR) + _latitudeSlices;
		
		return ((double)baseLatitude / (double)MULTIPLIER_FACTOR);
	}

	/**
	 * 
	 * @param index latitude index slice
	 * @return min latitude [-90.0..90.0] for the given latitude slice index 
	 */
	public double getMinLatitudeForSliceIndex(long index) {
		long baseLatitude = (index * _latitudeSlices) - 90 * MULTIPLIER_FACTOR;
		return ((double)baseLatitude / (double)MULTIPLIER_FACTOR);
	}
	
	/**
	 * 
	 * @param index longitude index slice
	 * @return max longitude [-180.0..180.0] for the given longitude slice index 
	 */
	public double getMaxLongitudeForSliceIndex(long index) {
		long baseLongitude = ((index * _longitudeSlices) - 180 * MULTIPLIER_FACTOR) + _longitudeSlices;
		return ((double)baseLongitude / (double)MULTIPLIER_FACTOR);
	}
	
	/**
	 * 
	 * @param index longitude index slice
	 * @return max longitude [-180.0..180.0] for the given longitude slice index 
	 */
	public double getMinLongitudeForSliceIndex(long index) {
		long baseLongitude = (index * _longitudeSlices) - 180 * MULTIPLIER_FACTOR;
		return ((double)baseLongitude / (double)MULTIPLIER_FACTOR);
	}
}
