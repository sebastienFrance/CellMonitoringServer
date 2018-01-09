package com.seb.topologyMgt;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GeoIndexAllocator {
	private static final Logger LOG = LogManager.getLogger(GeoIndexAllocator.class);

	static private GeoIndexAllocator _instance = new GeoIndexAllocator();

	public enum GeoIndexSize { LARGE, MEDIUM, SMALL };

	private static final double LARGE_SIZE = 0.4;
	private static final double MEDIUM_SIZE = 0.1;
	private static final double SMALL_SIZE = 0.01;

	private GeoIndex _largeGeoIndex = new GeoIndex(LARGE_SIZE, LARGE_SIZE);
	private GeoIndex _mediumGeoIndex = new GeoIndex(MEDIUM_SIZE, MEDIUM_SIZE);
	private GeoIndex _smallGeoIndex = new GeoIndex(SMALL_SIZE, SMALL_SIZE);

	private GeoIndexAllocator() {
	}

	public static GeoIndexAllocator getInstance() {
		return _instance;
	}
	
	public void initSliceGeoIndex(GeoIndexSize size, double latitudeSlices, double longitudeSlices) {
		switch  (size) {
		case LARGE: {
			_largeGeoIndex = new GeoIndex(latitudeSlices, longitudeSlices);
			break;
		}
		case MEDIUM: {
			_mediumGeoIndex = new GeoIndex(latitudeSlices, longitudeSlices);
			break;

		}
		case SMALL: {
			_smallGeoIndex = new GeoIndex(latitudeSlices, longitudeSlices);
			break;

		}
		}
	}
	
	public boolean isFullyContainedIn(long index, GeoIndexSize indexSize, GeoLocation location, double distance) {
		GeoLocation[] boundingBox = location.boundingCoordinates(distance, GeoIndex.EARTH_RADIUS);
		
		// compute bounding box from the index
		GeoLocation[] boundingBoxFromGeoIndex = boundingBoxFrom(index, indexSize);
		
		return isBoundingBoxContains(boundingBox, boundingBoxFromGeoIndex);
	}
	
	private static boolean isBoundingBoxContains(GeoLocation[] large, GeoLocation[] small) {
		if (small[0].getLatitudeInDegrees()  >= large[0].getLatitudeInDegrees()  && 
			small[0].getLongitudeInDegrees() >= large[0].getLongitudeInDegrees() &&
			small[1].getLatitudeInDegrees()  <= large[1].getLatitudeInDegrees()  &&
			small[1].getLongitudeInDegrees() <= large[1].getLongitudeInDegrees()) {
			return true;
		} else {
			return false;
		}
	}
	
	private GeoLocation[] boundingBoxFrom(long index, GeoIndexSize indexSize) {
		GeoIndex indexBuilder = getGeoIndex(indexSize);
		
		return indexBuilder.getBoundingBoxFor(index);
	}
	
	public Long getIndex(GeoIndexSize size, GeoLocation location) {
		GeoIndex selectedIndex = getGeoIndex(size);
		return selectedIndex.getIndex(location);
	}
	
	public Long getIndex(GeoIndexSize size, double latitude, double longitude) {
		GeoIndex selectedIndex = getGeoIndex(size);
		return selectedIndex.getIndex(latitude, longitude);
	}
	
	public GeoIndexSize getBestGeoIndexSizeFor(GeoLocation[] boundingBox) {
		double distance = boundingBox[0].distanceTo(boundingBox[1], GeoIndex.EARTH_RADIUS);
		if (distance >= 10.0) {
			return GeoIndexSize.LARGE;
		} else if (distance >= 1.0) {
			return GeoIndexSize.MEDIUM;
		} else return GeoIndexSize.SMALL;
	}
	
	public List<Long> indexesForBoundingBox(GeoIndexSize indexSize, GeoLocation[] boundingBox) {

		double maxLatitude = boundingBox[1].getLatitudeInDegrees();
		double maxLongitude = boundingBox[1].getLongitudeInDegrees();
		double minLatitude = boundingBox[0].getLatitudeInDegrees();
		double minLongitude = boundingBox[0].getLongitudeInDegrees();

		return indexesForBoundingBox(indexSize, minLatitude, minLongitude, maxLatitude, maxLongitude);
	}
	
	public List<Long> indexesForBoundingBox(GeoIndexSize size, double minLatitude, double minLongitude, double maxLatitude, double maxLongitude) {
		GeoIndex selectedIndex = getGeoIndex(size);
		return selectedIndex.indexesForBoundingBox(minLatitude, minLongitude, maxLatitude, maxLongitude);
	}
	
	public double getGeoIndexSize(GeoIndexSize size) {
		switch  (size) {
		case LARGE: {
			return LARGE_SIZE;
		}
		case MEDIUM: {
			return MEDIUM_SIZE;
		}
		case SMALL: {
			return SMALL_SIZE;
		}
		default: {
			LOG.warn("getGeoIndexSize:: called with unknown index!");
			 return LARGE_SIZE;
		}
		}	
		
	}
	
	private GeoIndex getGeoIndex(GeoIndexSize size) {
		switch  (size) {
		case LARGE: {
			return _largeGeoIndex;
		}
		case MEDIUM: {
			return _mediumGeoIndex;
		}
		case SMALL: {
			return _smallGeoIndex;
		}
		default: {
			LOG.warn("GeoIndexAllocator::getIndex called with unknown index!");
			 return _largeGeoIndex;
		}
		}	
	}
}
