package com.seb.topologyMgt.test;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

import com.seb.topologyMgt.GeoIndex;
import com.seb.topologyMgt.GeoIndexAllocator;
import com.seb.topologyMgt.GeoLocation;

public class GeoIndexTest {
	private boolean _displayIndexes = false;
	
	private long _latestLatitudeSliceIndex;
	private long _latestLongitudeSliceIndex;
	
	@Test
	public void testSimpleSlice() {
		GeoIndex testIndex = new GeoIndex(20,10);
		
		long latitudeIndex = testIndex.getLatitudeSliceIndex(-90.0);
		org.junit.Assert.assertEquals(0, latitudeIndex);

		latitudeIndex = testIndex.getLatitudeSliceIndex(-70.0);
		org.junit.Assert.assertEquals(0, latitudeIndex);

		latitudeIndex = testIndex.getLatitudeSliceIndex(-69.0);
		org.junit.Assert.assertEquals(1, latitudeIndex);

		latitudeIndex = testIndex.getLatitudeSliceIndex(-50.0);
		org.junit.Assert.assertEquals(1, latitudeIndex);

		latitudeIndex = testIndex.getLatitudeSliceIndex(-49.0);
		org.junit.Assert.assertEquals(2, latitudeIndex);

		latitudeIndex = testIndex.getLatitudeSliceIndex(90.0);
		org.junit.Assert.assertEquals(8, latitudeIndex);

		latitudeIndex = testIndex.getLatitudeSliceIndex(15.3424234);
		org.junit.Assert.assertEquals(5, latitudeIndex);

		
		long longitudeIndex = testIndex.getLongitudeSliceIndex(-180.0);
		org.junit.Assert.assertEquals(0, longitudeIndex);

	    longitudeIndex = testIndex.getLongitudeSliceIndex(180.0);
		org.junit.Assert.assertEquals(35, longitudeIndex);
	}
		
	@Test
	public void testGeoIndexToLatLong() {
		GeoIndex testIndex = new GeoIndex(1,1);
		
		long latitudeIndex = testIndex.getLatitudeSliceIndex(-90.0);
		org.junit.Assert.assertEquals(0, latitudeIndex);

		double latitudeMax = testIndex.getMaxLatitudeForSliceIndex(latitudeIndex);
		double latitudeMin = testIndex.getMinLatitudeForSliceIndex(latitudeIndex);
		
		org.junit.Assert.assertEquals(-89, latitudeMax, 0.001);
		org.junit.Assert.assertEquals(-90, latitudeMin, 0.001);
	
		long longitudeIndex = testIndex.getLongitudeSliceIndex(-180.0);
		org.junit.Assert.assertEquals(0, longitudeIndex);

		double longitudeMax = testIndex.getMaxLongitudeForSliceIndex(longitudeIndex);
		double longitudeMin = testIndex.getMinLongitudeForSliceIndex(longitudeIndex);
		
		org.junit.Assert.assertEquals(-179, longitudeMax, 0.001);
		org.junit.Assert.assertEquals(-180, longitudeMin, 0.001);
		
	    latitudeIndex = testIndex.getLatitudeSliceIndex(90.0);
		org.junit.Assert.assertEquals(179, latitudeIndex);

		latitudeMax = testIndex.getMaxLatitudeForSliceIndex(latitudeIndex);
		latitudeMin = testIndex.getMinLatitudeForSliceIndex(latitudeIndex);
		
		org.junit.Assert.assertEquals(90, latitudeMax, 0.001);
		org.junit.Assert.assertEquals(89, latitudeMin, 0.001);


		longitudeIndex = testIndex.getLongitudeSliceIndex(180.0);
		org.junit.Assert.assertEquals(359, longitudeIndex);

		longitudeMax = testIndex.getMaxLongitudeForSliceIndex(longitudeIndex);
		longitudeMin = testIndex.getMinLongitudeForSliceIndex(longitudeIndex);
		
		org.junit.Assert.assertEquals(180, longitudeMax, 0.001);
		org.junit.Assert.assertEquals(179, longitudeMin, 0.001);
		
		// Test with slices of 0.01
		testIndex = new GeoIndex(0.01, 0.01);

		latitudeIndex = testIndex.getLatitudeSliceIndex(-90.0);
		org.junit.Assert.assertEquals(0, latitudeIndex);

		latitudeMax = testIndex.getMaxLatitudeForSliceIndex(latitudeIndex);
		latitudeMin = testIndex.getMinLatitudeForSliceIndex(latitudeIndex);

		org.junit.Assert.assertEquals(-89.99, latitudeMax, 0.001);
		org.junit.Assert.assertEquals(-90, latitudeMin, 0.001);

		longitudeIndex = testIndex.getLongitudeSliceIndex(-180.0);
		org.junit.Assert.assertEquals(0, longitudeIndex);

		longitudeMax = testIndex.getMaxLongitudeForSliceIndex(longitudeIndex);
		longitudeMin = testIndex.getMinLongitudeForSliceIndex(longitudeIndex);

		org.junit.Assert.assertEquals(-179.99, longitudeMax, 0.001);
		org.junit.Assert.assertEquals(-180, longitudeMin, 0.001);

		latitudeIndex = testIndex.getLatitudeSliceIndex(90.0);
		org.junit.Assert.assertEquals(17999, latitudeIndex);

		latitudeMax = testIndex.getMaxLatitudeForSliceIndex(latitudeIndex);
		latitudeMin = testIndex.getMinLatitudeForSliceIndex(latitudeIndex);

		org.junit.Assert.assertEquals(90, latitudeMax, 0.001);
		org.junit.Assert.assertEquals(89.99, latitudeMin, 0.001);


		longitudeIndex = testIndex.getLongitudeSliceIndex(180.0);
		org.junit.Assert.assertEquals(35999, longitudeIndex);

		longitudeMax = testIndex.getMaxLongitudeForSliceIndex(longitudeIndex);
		longitudeMin = testIndex.getMinLongitudeForSliceIndex(longitudeIndex);

		org.junit.Assert.assertEquals(180, longitudeMax, 0.001);
		org.junit.Assert.assertEquals(179.99, longitudeMin, 0.001);
		
		// Test with slices of 0.4
		testIndex = new GeoIndex(0.4, 0.4);

		latitudeIndex = testIndex.getLatitudeSliceIndex(-90.0);
		org.junit.Assert.assertEquals(0, latitudeIndex);

		latitudeMax = testIndex.getMaxLatitudeForSliceIndex(latitudeIndex);
		latitudeMin = testIndex.getMinLatitudeForSliceIndex(latitudeIndex);

		org.junit.Assert.assertEquals(-89.6, latitudeMax, 0.1);
		org.junit.Assert.assertEquals(-90, latitudeMin, 0.1);

		longitudeIndex = testIndex.getLongitudeSliceIndex(-180.0);
		org.junit.Assert.assertEquals(0, longitudeIndex);

		longitudeMax = testIndex.getMaxLongitudeForSliceIndex(longitudeIndex);
		longitudeMin = testIndex.getMinLongitudeForSliceIndex(longitudeIndex);

		org.junit.Assert.assertEquals(-179.6, longitudeMax, 0.1);
		org.junit.Assert.assertEquals(-180, longitudeMin, 0.1);

		
		latitudeIndex = testIndex.getLatitudeSliceIndex(90.0); // 0.4 equals 2/5 => multiply by 5 and divide by 2
		org.junit.Assert.assertEquals(((180 * 5) / 2) - 1, latitudeIndex);

		latitudeMax = testIndex.getMaxLatitudeForSliceIndex(latitudeIndex);
		latitudeMin = testIndex.getMinLatitudeForSliceIndex(latitudeIndex);

		org.junit.Assert.assertEquals(90, latitudeMax, 0.1);
		org.junit.Assert.assertEquals(89.6, latitudeMin, 0.1);


		longitudeIndex = testIndex.getLongitudeSliceIndex(180.0);
		org.junit.Assert.assertEquals(((360 * 5) / 2) - 1, longitudeIndex);

		longitudeMax = testIndex.getMaxLongitudeForSliceIndex(longitudeIndex);
		longitudeMin = testIndex.getMinLongitudeForSliceIndex(longitudeIndex);

		org.junit.Assert.assertEquals(180, longitudeMax, 0.1);
		org.junit.Assert.assertEquals(179.6, longitudeMin, 0.1);
		
	}
	
	public void testSliceIndexesFomCombinedIndex(GeoIndex testIndex, double latitude, double longitude, double sliceSize) {
		long expectedSliceIndexLatitude = expectedSliceIndexForLatitude(latitude, sliceSize);
		long expectedSliceIndexLongitude = expectedSliceIndexForLongitude(longitude, sliceSize);
		
		testSliceIndexesFomCombinedIndex(testIndex, latitude, longitude, expectedSliceIndexLatitude, expectedSliceIndexLongitude);
	}
	
	public void testSliceIndexesFomCombinedIndex(GeoIndex testIndex, double latitude, double longitude, long expectedSliceIndexLatitude, long expectedSliceIndexLongitude) {
		GeoLocation location = GeoLocation.fromDegrees(latitude, longitude);
		Long combinedIndex = testIndex.getIndex(location);
		
		long latitudeSliceIndex = testIndex.getLatitudeSliceIndexFromCombinedIndex(combinedIndex);
		long longitudeSliceIndex = testIndex.getLongitudeSliceIndexFromCombinedIndex(combinedIndex);
		
		org.junit.Assert.assertEquals(latitudeSliceIndex, expectedSliceIndexLatitude);
		org.junit.Assert.assertEquals(longitudeSliceIndex, expectedSliceIndexLongitude);
		
		
		double maxLatitude = testIndex.getMaxLatitudeForSliceIndex(latitudeSliceIndex);
		double maxLongitude = testIndex.getMaxLongitudeForSliceIndex(longitudeSliceIndex);
		
		double minLatitude = testIndex.getMinLatitudeForSliceIndex(latitudeSliceIndex);
		double minLongitude = testIndex.getMinLongitudeForSliceIndex(longitudeSliceIndex);
		
		org.junit.Assert.assertTrue(latitude <= maxLatitude);
		org.junit.Assert.assertTrue(latitude >= minLatitude);
		org.junit.Assert.assertTrue(longitude <= maxLongitude);
		org.junit.Assert.assertTrue(longitude >= minLongitude);
		
		_latestLatitudeSliceIndex = latitudeSliceIndex;
		_latestLongitudeSliceIndex = longitudeSliceIndex;
	}
	
	private long expectedSliceIndexForLatitude(double latitude, double slice) {
		long base = (long)Math.ceil((90 + latitude) / slice);
		if (base > 0) {
			return base - 1;
		} else {
			return 0;
		}
	}
	
	private long expectedSliceIndexForLongitude(double longitude, double slice) {
		long base = (long)Math.ceil((180 + longitude) / slice);
		if (base > 0) {
			return base - 1;
		} else {
			return 0;
		}		
	}
	
	@Test
	public void debugGeoIndex() {
		/*
		2015-06-13 22:37:10.481 [pool-3-thread-1] INFO  com.seb.imonserver.database.WorkerExtractCellsFromGeoIndex - run:: GeoIndex: 311854
		2015-06-13 22:37:10.482 [pool-3-thread-1] INFO  com.seb.imonserver.database.WorkerExtractCellsFromGeoIndex - run:: IndexSize: LARGE
		2015-06-13 22:37:10.482 [pool-3-thread-1] INFO  com.seb.imonserver.database.WorkerExtractCellsFromGeoIndex - run:: center Latitude: 48.856898 / Long: 2.350844
		2015-06-13 22:37:10.482 [pool-3-thread-1] INFO  com.seb.imonserver.database.WorkerExtractCellsFromGeoIndex - run:: distance: 40.0
		*/
		
		GeoLocation centerLocation = GeoLocation.fromDegrees(48.856898, 2.350844);
		GeoLocation[] centerBoundingBox = centerLocation.boundingCoordinates(40.0, GeoIndex.EARTH_RADIUS);
		System.err.println("BoundingBox Min Latitude: " + centerBoundingBox[0].getLatitudeInDegrees());
		System.err.println("BoundingBox Min Longitude: " + centerBoundingBox[0].getLongitudeInDegrees());
		System.err.println("BoundingBox Max Latitude: " + centerBoundingBox[1].getLatitudeInDegrees());
		System.err.println("BoundingBox Max Longitude: " + centerBoundingBox[1].getLongitudeInDegrees());

		GeoIndexAllocator indexAllocator = GeoIndexAllocator.getInstance();
		List<Long> indexes = indexAllocator.indexesForBoundingBox(GeoIndexAllocator.GeoIndexSize.LARGE, centerBoundingBox);
		double indexSize = indexAllocator.getGeoIndexSize(GeoIndexAllocator.GeoIndexSize.LARGE);
		GeoIndex testIndex = new GeoIndex(indexSize, indexSize);
		for (Long current : indexes) {
			System.err.println("============= GeoIndex: " + current + " ===============" );
			
			Long latitudeSliceIndex = testIndex.getLatitudeSliceIndexFromCombinedIndex(current);
			Long longitudeSliceIndex = testIndex.getLongitudeSliceIndexFromCombinedIndex(current);
			double maxLatitude = testIndex.getMaxLatitudeForSliceIndex(latitudeSliceIndex);
			double maxLongitude = testIndex.getMaxLongitudeForSliceIndex(longitudeSliceIndex);
			
			double minLatitude = testIndex.getMinLatitudeForSliceIndex(latitudeSliceIndex);
			double minLongitude = testIndex.getMinLongitudeForSliceIndex(longitudeSliceIndex);
			
			System.err.println("Min Latitude: " + minLatitude);
			System.err.println("Min Longitude: " + minLongitude);
			System.err.println("Max Latitude: " + maxLatitude);
			System.err.println("Max Longitude: " + maxLongitude);
		}
		Long latitudeSliceIndex = testIndex.getLatitudeSliceIndexFromCombinedIndex(312755);
		Long longitudeSliceIndex = testIndex.getLongitudeSliceIndexFromCombinedIndex(312755);
		double maxLatitude = testIndex.getMaxLatitudeForSliceIndex(latitudeSliceIndex);
		double maxLongitude = testIndex.getMaxLongitudeForSliceIndex(longitudeSliceIndex);
		
		double minLatitude = testIndex.getMinLatitudeForSliceIndex(latitudeSliceIndex);
		double minLongitude = testIndex.getMinLongitudeForSliceIndex(longitudeSliceIndex);
		
		System.err.println("Min Latitude: " + minLatitude);
		System.err.println("Min Longitude: " + minLongitude);
		System.err.println("Max Latitude: " + maxLatitude);
		System.err.println("Max Longitude: " + maxLongitude);
		
	
		org.junit.Assert.assertTrue(centerBoundingBox[0].getLatitudeInDegrees() <= minLatitude);
		org.junit.Assert.assertTrue(centerBoundingBox[0].getLongitudeInDegrees() <= minLongitude);
		org.junit.Assert.assertTrue(centerBoundingBox[1].getLatitudeInDegrees() >= maxLatitude);
		org.junit.Assert.assertTrue(centerBoundingBox[1].getLongitudeInDegrees() >= maxLongitude);
		
		
		
		boolean isContained = indexAllocator.isFullyContainedIn(312755, GeoIndexAllocator.GeoIndexSize.LARGE, centerLocation, 40.0);
		org.junit.Assert.assertTrue(isContained);
	}
	
	@Test
	public void testSliceIndexesFromCombinedIndex() {
		// Test with slices of 0.4 
		// Warning: 0.4 equals 2/5 => multiply by 5 and divide by 2
		GeoIndex testIndex = new GeoIndex(0.4, 0.4);
		
		testSliceIndexesFomCombinedIndex(testIndex, -90.0, -180.0, 0.4);
		testSliceIndexesFomCombinedIndex(testIndex, -90.0, 180.0, 0.4);
		testSliceIndexesFomCombinedIndex(testIndex, 90.0, -180.0, 0.4);
		testSliceIndexesFomCombinedIndex(testIndex, 90.0, 180.0, 0.4);
		testSliceIndexesFomCombinedIndex(testIndex, 0.0, 0.0, 0.4);
		testSliceIndexesFomCombinedIndex(testIndex, 0.0, 0.3, 0.4);
		testSliceIndexesFomCombinedIndex(testIndex, 0.0, 0.4, 0.4);
		testSliceIndexesFomCombinedIndex(testIndex, 0.0, 0.5, 0.4);

		testSliceIndexesFomCombinedIndex(testIndex, 48.813313, 2.124704, 0.4);

		//  Latitude: 48.856898 Longitude: 2.350844
		
		// 49.21/2.124704 
		long previousLatitudeSliceIndex = _latestLatitudeSliceIndex;
		long previousLongitudeSliceIndex = _latestLongitudeSliceIndex;
		testSliceIndexesFomCombinedIndex(testIndex, 49.21, 2.124704, 0.4);
		
		org.junit.Assert.assertTrue(_latestLatitudeSliceIndex == (previousLatitudeSliceIndex + 1));
		org.junit.Assert.assertTrue(_latestLongitudeSliceIndex == previousLongitudeSliceIndex);
		
		// 49.21/2.41 
		previousLatitudeSliceIndex = _latestLatitudeSliceIndex;
		previousLongitudeSliceIndex = _latestLongitudeSliceIndex;
		testSliceIndexesFomCombinedIndex(testIndex, 49.21, 2.41, 0.4);

		org.junit.Assert.assertTrue(_latestLatitudeSliceIndex == previousLatitudeSliceIndex);
		org.junit.Assert.assertTrue(_latestLongitudeSliceIndex == (previousLongitudeSliceIndex + 1));
		
		// 49.21/2.41 
		previousLatitudeSliceIndex = _latestLatitudeSliceIndex;
		previousLongitudeSliceIndex = _latestLongitudeSliceIndex;
		testSliceIndexesFomCombinedIndex(testIndex, 48.79, 1.99, 0.4);

		org.junit.Assert.assertTrue(_latestLatitudeSliceIndex == (previousLatitudeSliceIndex - 2));
		org.junit.Assert.assertTrue(_latestLongitudeSliceIndex == (previousLongitudeSliceIndex - 2));
		
		// Test with slices of 0.01
	    testIndex = new GeoIndex(0.01, 0.01);
		
		testSliceIndexesFomCombinedIndex(testIndex, -90.0, -180.0, 0.01);
		testSliceIndexesFomCombinedIndex(testIndex, -90.0, 180.0, 0.01);
		testSliceIndexesFomCombinedIndex(testIndex, 90.0, -180.0, 0.01);
		testSliceIndexesFomCombinedIndex(testIndex, 90.0, 180.0, 0.01);
		testSliceIndexesFomCombinedIndex(testIndex, 0.0, 0.0, 0.01);
	}
	
	@Test
	public void testBoundingBoxFromCombinedIndex() {
		GeoIndex testIndex = new GeoIndex(1,1);

		Long index = testIndex.getIndex(-89.5, -179.5);
		org.junit.Assert.assertEquals(0, index.longValue());
		
		GeoLocation[] boundingBox = testIndex.getBoundingBoxFor(index);
		org.junit.Assert.assertEquals(-90.0, boundingBox[0].getLatitudeInDegrees(), 0.001);
		org.junit.Assert.assertEquals(-180.0, boundingBox[0].getLongitudeInDegrees(), 0.001);
		org.junit.Assert.assertEquals(-89.0, boundingBox[1].getLatitudeInDegrees(), 0.001);
		org.junit.Assert.assertEquals(-179.0, boundingBox[1].getLongitudeInDegrees(), 0.001);
		
		index = testIndex.getIndex(89.5, 179.5);
		org.junit.Assert.assertEquals((360 * 180) - 1, index.longValue());

		boundingBox = testIndex.getBoundingBoxFor(index);
		org.junit.Assert.assertEquals(89.0, boundingBox[0].getLatitudeInDegrees(), 0.001);
		org.junit.Assert.assertEquals(179.0, boundingBox[0].getLongitudeInDegrees(), 0.001);
		org.junit.Assert.assertEquals(90.0, boundingBox[1].getLatitudeInDegrees(), 0.001);
		org.junit.Assert.assertEquals(180.0, boundingBox[1].getLongitudeInDegrees(), 0.001);
		
		testIndex = new GeoIndex(0.01,0.01);

	    index = testIndex.getIndex(-89.995, -179.995);
		org.junit.Assert.assertEquals(0, index.longValue());
		
		boundingBox = testIndex.getBoundingBoxFor(index);
		org.junit.Assert.assertEquals(-90.0, boundingBox[0].getLatitudeInDegrees(), 0.001);
		org.junit.Assert.assertEquals(-180.0, boundingBox[0].getLongitudeInDegrees(), 0.001);
		org.junit.Assert.assertEquals(-89.99, boundingBox[1].getLatitudeInDegrees(), 0.001);
		org.junit.Assert.assertEquals(-179.99, boundingBox[1].getLongitudeInDegrees(), 0.001);
		
		index = testIndex.getIndex(89.995, 179.995);
		org.junit.Assert.assertEquals((36000 * 18000) - 1, index.longValue());

		boundingBox = testIndex.getBoundingBoxFor(index);
		org.junit.Assert.assertEquals(89.99, boundingBox[0].getLatitudeInDegrees(), 0.001);
		org.junit.Assert.assertEquals(179.99, boundingBox[0].getLongitudeInDegrees(), 0.001);
		org.junit.Assert.assertEquals(90.0, boundingBox[1].getLatitudeInDegrees(), 0.001);
		org.junit.Assert.assertEquals(180.0, boundingBox[1].getLongitudeInDegrees(), 0.001);
	}
	
	/*
	 * The approximate conversions are:
	 *    Latitude: 1 deg = 110.574 km
	 *    Longitude: 1 deg = 111.320*cos(latitude) km (cos(0) = 1 / cos(-90°)= 0 / cos(90°) =0) => Max 1° = 111.320km
	 *    
	 *    Earth radius = 6 371km
	 *    Earth circonference = 40 075 km
	 */
	
	@Test
	public void testWithCenterBoundingBox() {
		
		// Slices of 10° => around 1200km
		GeoIndex testBoundingBox = new GeoIndex(10,10);
		
		GeoLocation location = GeoLocation.fromDegrees(0.0, 0.0);
		GeoLocation[] box = location.boundingCoordinates(0, GeoIndex.EARTH_RADIUS);
		
		double minLatitude = box[0].getLatitudeInDegrees();
		double maxLatitude = box[1].getLatitudeInDegrees();
		
		long minLatitudeIndex = testBoundingBox.getLatitudeSliceIndex(minLatitude);
		long maxLatitudeIndex = testBoundingBox.getLatitudeSliceIndex(maxLatitude);
		
		double minLongitude = box[0].getLongitudeInDegrees();
		double maxLongitude = box[1].getLongitudeInDegrees();
		
		long minLongitudeIndex = testBoundingBox.getLongitudeSliceIndex(minLongitude);
		long maxLongitudeIndex = testBoundingBox.getLongitudeSliceIndex(maxLongitude);

		org.junit.Assert.assertTrue("Min Latitude  equals max Latitude", minLatitudeIndex == maxLatitudeIndex);
		org.junit.Assert.assertTrue("Min Longitude equals max Longitude", minLongitudeIndex == maxLongitudeIndex);
	}

	@Test
	public void testWithBasicBoundingBox() {
		GeoIndex testBoundingBox = new GeoIndex(10,10);
		
		GeoLocation location = GeoLocation.fromDegrees(0.0, 0.0);
		GeoLocation[] box = location.boundingCoordinates(1500, GeoIndex.EARTH_RADIUS);
		
		double minLatitude = box[0].getLatitudeInDegrees();
		double maxLatitude = box[1].getLatitudeInDegrees();
		
		long minLatitudeIndex = testBoundingBox.getLatitudeSliceIndex(minLatitude);
		long maxLatitudeIndex = testBoundingBox.getLatitudeSliceIndex(maxLatitude);
		
		double minLongitude = box[0].getLongitudeInDegrees();
		double maxLongitude = box[1].getLongitudeInDegrees();
		
		long minLongitudeIndex = testBoundingBox.getLongitudeSliceIndex(minLongitude);
		long maxLongitudeIndex = testBoundingBox.getLongitudeSliceIndex(maxLongitude);

		org.junit.Assert.assertTrue("Min Latitude lower than max Latitude", minLatitudeIndex < maxLatitudeIndex);
		org.junit.Assert.assertTrue("Min Longitude lower than max Longitude", minLongitudeIndex < maxLongitudeIndex);
	}
	
	/*
	 * When we are at the extreme, the min Longitude is > than max Longitude
	 * The Surface include all degrees in longitude from min to 180° and then from 180° to max
	 */
	
	@Test
	public void testWithExtremeLongitudeWestBoundingBox() {
		GeoIndex testBoundingBox = new GeoIndex(1,1);
		
		GeoLocation location = GeoLocation.fromDegrees(0.0, -180.0);
		GeoLocation[] box = location.boundingCoordinates(5000, GeoIndex.EARTH_RADIUS);
		
		double minLatitude = box[0].getLatitudeInDegrees();
		double maxLatitude = box[1].getLatitudeInDegrees();
		
		long minLatitudeIndex = testBoundingBox.getLatitudeSliceIndex(minLatitude);
		long maxLatitudeIndex = testBoundingBox.getLatitudeSliceIndex(maxLatitude);
		
		double minLongitude = box[0].getLongitudeInDegrees();
		double maxLongitude = box[1].getLongitudeInDegrees();
		
		long minLongitudeIndex = testBoundingBox.getLongitudeSliceIndex(minLongitude);
		long maxLongitudeIndex = testBoundingBox.getLongitudeSliceIndex(maxLongitude);

		org.junit.Assert.assertTrue("Min Latitude lower than max Latitude", minLatitudeIndex < maxLatitudeIndex);
		org.junit.Assert.assertTrue("Min Longitude greater than max Longitude", minLongitudeIndex > maxLongitudeIndex);
	}
	
	@Test
	public void testWithExtremeLongitudeEastBoundingBox() {
		GeoIndex testBoundingBox = new GeoIndex(10,10);
		
		GeoLocation location = GeoLocation.fromDegrees(0.0, 180.0);
		GeoLocation[] box = location.boundingCoordinates(4000, GeoIndex.EARTH_RADIUS);
		
		double minLatitude = box[0].getLatitudeInDegrees();
		double maxLatitude = box[1].getLatitudeInDegrees();
		
		long minLatitudeIndex = testBoundingBox.getLatitudeSliceIndex(minLatitude);
		long maxLatitudeIndex = testBoundingBox.getLatitudeSliceIndex(maxLatitude);
		
		double minLongitude = box[0].getLongitudeInDegrees();
		double maxLongitude = box[1].getLongitudeInDegrees();
		
		long minLongitudeIndex = testBoundingBox.getLongitudeSliceIndex(minLongitude);
		long maxLongitudeIndex = testBoundingBox.getLongitudeSliceIndex(maxLongitude);

		org.junit.Assert.assertTrue("Min Latitude lower than max Latitude", minLatitudeIndex < maxLatitudeIndex);
		org.junit.Assert.assertTrue("Min Longitude greater than max Longitude", minLongitudeIndex > maxLongitudeIndex);
	}
	
	@Test
	public void testIndexes() {
		GeoIndex testBoundingBox = new GeoIndex(90,90); // It creates a Array that contains 2 x 4 buckets == 8 buckets

		
		Long combinedIndex = testBoundingBox.getIndex(-90.0, -180.0);
		org.junit.Assert.assertTrue("index (-90,-180) = 0" , combinedIndex.longValue() == 0);

		combinedIndex = testBoundingBox.getIndex(90.0, -180.0);
		org.junit.Assert.assertTrue("index (90.0, -180.0) = 4" , combinedIndex.longValue() == 4);

		combinedIndex = testBoundingBox.getIndex(-90.0, 180.0);
		org.junit.Assert.assertTrue("index (-90.0, 180.0) = 3" , combinedIndex.longValue() == 3);

		combinedIndex = testBoundingBox.getIndex(90.0, 180.0);
		org.junit.Assert.assertTrue("index (90.0, 180.0) = 7" , combinedIndex.longValue() == 7);

		combinedIndex = testBoundingBox.getIndex(0.0, 0.0);
		org.junit.Assert.assertTrue("index (0.0, 0.0) = 1" , combinedIndex.longValue() == 1);

		testBoundingBox = new GeoIndex(45,20); // It creates a Array that contains 4 x 18 buckets == 72 buckets

		combinedIndex = testBoundingBox.getIndex(-90.0, -180.0);
		org.junit.Assert.assertTrue("index (-90,-180) = 0" , combinedIndex.longValue() == 0);

		combinedIndex = testBoundingBox.getIndex(90.0, -180.0);
		org.junit.Assert.assertTrue("index (90.0, -180.0) = 54" , combinedIndex.longValue() == 54);

		combinedIndex = testBoundingBox.getIndex(-90.0, 180.0);
		org.junit.Assert.assertTrue("index (-90.0, 180.0) = 17" , combinedIndex.longValue() == 17);

		combinedIndex = testBoundingBox.getIndex(90.0, 180.0);
		org.junit.Assert.assertTrue("index (90.0, 180.0) = 63" , combinedIndex.longValue() == 71);

		combinedIndex = testBoundingBox.getIndex(0.0, 0.0);
		org.junit.Assert.assertTrue("index (0.0, 0.0) = 26" , combinedIndex.longValue() == 26);

		testBoundingBox = new GeoIndex(1,1); // It creates a Array that contains 180 x 360 buckets == 64800 buckets

		combinedIndex = testBoundingBox.getIndex(-90.0, -180.0);
		org.junit.Assert.assertTrue("index (-90,-180) = 0" , combinedIndex.longValue() == 0);

		combinedIndex = testBoundingBox.getIndex(90.0, -180.0);
		org.junit.Assert.assertTrue("index (90.0, -180.0) = 64440" , combinedIndex.longValue() == 64440);

		combinedIndex = testBoundingBox.getIndex(-90.0, 180.0);
		org.junit.Assert.assertTrue("index (-90.0, 180.0) = 359" , combinedIndex.longValue() == 359);

		combinedIndex = testBoundingBox.getIndex(90.0, 180.0);
		org.junit.Assert.assertTrue("index (90.0, 180.0) = 64799" , combinedIndex.longValue() == 64799);

		combinedIndex = testBoundingBox.getIndex(0.0, 0.0);
		org.junit.Assert.assertTrue("index (0.0, 0.0) = 32219" , combinedIndex.longValue() == 32219);

		testBoundingBox = new GeoIndex(0.1,0.1); // It creates a Array that contains 1800 x 3600 buckets == 6480000 buckets

		combinedIndex = testBoundingBox.getIndex(-90.0, -180.0);
		org.junit.Assert.assertTrue("index (-90,-180) = 0" , combinedIndex.longValue() == 0);

		combinedIndex = testBoundingBox.getIndex(90, -180.0);
		org.junit.Assert.assertTrue("index (90.0, -180.0) = 6476400" , combinedIndex.longValue() == 6476400);

		combinedIndex = testBoundingBox.getIndex(-90.0, 180.0);
		org.junit.Assert.assertTrue("index (-90.0, 180.0) = 3599" , combinedIndex.longValue() == 3599);

		combinedIndex = testBoundingBox.getIndex(90.0, 180.0);
		org.junit.Assert.assertTrue("index (90.0, 180.0) = ((899 * 3600) + 1800 - 1)" , combinedIndex.longValue() == (6480000 - 1));

		combinedIndex = testBoundingBox.getIndex(0.0, 0.0);
		System.err.println("Error: " +combinedIndex.longValue());
		org.junit.Assert.assertTrue("index (0.0, 0.0) = 32219" , combinedIndex.longValue() == ((899 * 3600) + 1800 - 1));
		
		testBoundingBox = new GeoIndex(0.01,0.01); // It creates a Array that contains 18000 x 36000 buckets == 648000000 buckets

		combinedIndex = testBoundingBox.getIndex(-90.0, -180.0);
		org.junit.Assert.assertTrue("index (-90,-180) = 0" , combinedIndex.longValue() == 0);

		combinedIndex = testBoundingBox.getIndex(90, -180.0);
		org.junit.Assert.assertTrue("index (90.0, -180.0) = ((180 * 100) -1) * (360 * 100)" , combinedIndex.longValue() == ((180 * 100) -1) * (360 * 100));

		combinedIndex = testBoundingBox.getIndex(-90.0, 180.0);
		org.junit.Assert.assertTrue("index (-90.0, 180.0) = (0 + (360 * 100) - 1)" , combinedIndex.longValue() == (0 + (360 * 100) - 1));

		combinedIndex = testBoundingBox.getIndex(90.0, 180.0);
		org.junit.Assert.assertTrue("index (90.0, 180.0) = ((180 * 100) * (360 * 100) -1)" , combinedIndex.longValue() == ((180 * 100) * (360 * 100) -1));

		combinedIndex = testBoundingBox.getIndex(0.0, 0.0);
		org.junit.Assert.assertTrue("index (0.0, 0.0) = ((8999 * 36000) + 18000 - 1)" , combinedIndex.longValue() == ((8999 * 36000) + 18000 - 1));

	
		testBoundingBox = new GeoIndex(0.4,0.4); // It creates a Array that contains 450 x 900 buckets == 405000 buckets

		combinedIndex = testBoundingBox.getIndex(-90.0, -180.0);
		org.junit.Assert.assertTrue("index (-90,-180) = 0" , combinedIndex.longValue() == 0);

		combinedIndex = testBoundingBox.getIndex(90, -180.0);
		org.junit.Assert.assertTrue("index (90.0, -180.0) = ((180 * 2.5) -1) * (360 * 2.5)" , combinedIndex.longValue() == ((180 * 2.5) -1) * (360 * 2.5));

		combinedIndex = testBoundingBox.getIndex(-90.0, 180.0);
		org.junit.Assert.assertTrue("index (-90.0, 180.0) = (0 + (360 * 2.5) - 1)" , combinedIndex.longValue() == (0 + (360 * 2.5) - 1));

		combinedIndex = testBoundingBox.getIndex(90.0, 180.0);
		org.junit.Assert.assertTrue("index (90.0, 180.0) = ((180 * 2.5) * (360 * 2.5) -1)" , combinedIndex.longValue() == ((180 * 2.5) * (360 * 2.5) -1));

		combinedIndex = testBoundingBox.getIndex(0.0, 0.0);
		org.junit.Assert.assertTrue("index (0.0, 0.0) = ((224 * 900) + 450 - 1)" , combinedIndex.longValue() == ((224 * 900) + 450 - 1));
}

	@Test
	public void testIndexesForBoundingBox() {
		GeoIndex testBoundingBox = new GeoIndex(90,90); // It creates a Array that contains 2 x 4 buckets == 8 buckets

		List<Long> boundingIndexes = testBoundingBox.indexesForBoundingBox(-45.0, -45.0, 10.0, 45.0);
		org.junit.Assert.assertTrue("bounding contains 4 indexes", boundingIndexes.size() == 4);
		org.junit.Assert.assertTrue("bounding contains index 1", boundingIndexes.contains(new Long(1)));
		org.junit.Assert.assertTrue("bounding contains index 2", boundingIndexes.contains(new Long(2)));
		org.junit.Assert.assertTrue("bounding contains index 5", boundingIndexes.contains(new Long(5)));
		org.junit.Assert.assertTrue("bounding contains index 6", boundingIndexes.contains(new Long(6)));

		displayIndexes(boundingIndexes);

		boundingIndexes = testBoundingBox.indexesForBoundingBox(-45.0, -45.0, 90.0, 180.0);
		org.junit.Assert.assertTrue("bounding contains 4 indexes", boundingIndexes.size() == 6);
		org.junit.Assert.assertTrue("bounding contains index 1", boundingIndexes.contains(new Long(1)));
		org.junit.Assert.assertTrue("bounding contains index 2", boundingIndexes.contains(new Long(2)));
		org.junit.Assert.assertTrue("bounding contains index 5", boundingIndexes.contains(new Long(5)));
		org.junit.Assert.assertTrue("bounding contains index 6", boundingIndexes.contains(new Long(6)));
		org.junit.Assert.assertTrue("bounding contains index 3", boundingIndexes.contains(new Long(3)));
		org.junit.Assert.assertTrue("bounding contains index 7", boundingIndexes.contains(new Long(7)));

		displayIndexes(boundingIndexes);

		boundingIndexes = testBoundingBox.indexesForBoundingBox(-45.0, -45.0, 90.0, -170.0);
		org.junit.Assert.assertTrue("bounding contains 8 indexes", boundingIndexes.size() == 8);
		org.junit.Assert.assertTrue("bounding contains index 1", boundingIndexes.contains(new Long(1)));
		org.junit.Assert.assertTrue("bounding contains index 2", boundingIndexes.contains(new Long(2)));
		org.junit.Assert.assertTrue("bounding contains index 5", boundingIndexes.contains(new Long(5)));
		org.junit.Assert.assertTrue("bounding contains index 6", boundingIndexes.contains(new Long(6)));
		org.junit.Assert.assertTrue("bounding contains index 3", boundingIndexes.contains(new Long(3)));
		org.junit.Assert.assertTrue("bounding contains index 7", boundingIndexes.contains(new Long(7)));
		org.junit.Assert.assertTrue("bounding contains index 0", boundingIndexes.contains(new Long(0)));
		org.junit.Assert.assertTrue("bounding contains index 4", boundingIndexes.contains(new Long(4)));

		displayIndexes(boundingIndexes);

		boundingIndexes = testBoundingBox.indexesForBoundingBox(-45.0, 170.0, 90.0, -170.0);
		org.junit.Assert.assertTrue("bounding contains 4 indexes", boundingIndexes.size() == 4);
		org.junit.Assert.assertTrue("bounding contains index 3", boundingIndexes.contains(new Long(3)));
		org.junit.Assert.assertTrue("bounding contains index 7", boundingIndexes.contains(new Long(7)));
		org.junit.Assert.assertTrue("bounding contains index 0", boundingIndexes.contains(new Long(0)));
		org.junit.Assert.assertTrue("bounding contains index 4", boundingIndexes.contains(new Long(4)));

		displayIndexes(boundingIndexes);

		boundingIndexes = testBoundingBox.indexesForBoundingBox(-45.0, 100.0, 0.0, 180.0);
		org.junit.Assert.assertTrue("bounding contains 1 indexes", boundingIndexes.size() == 1);
		org.junit.Assert.assertTrue("bounding contains index 3", boundingIndexes.contains(new Long(3)));

		displayIndexes(boundingIndexes);

		boundingIndexes = testBoundingBox.indexesForBoundingBox(-45.0, 100.0, 0.0, -10.0);
		org.junit.Assert.assertTrue("bounding contains 3 indexes", boundingIndexes.size() == 3);
		org.junit.Assert.assertTrue("bounding contains index 3", boundingIndexes.contains(new Long(3)));
		org.junit.Assert.assertTrue("bounding contains index 3", boundingIndexes.contains(new Long(0)));
		org.junit.Assert.assertTrue("bounding contains index 3", boundingIndexes.contains(new Long(1)));

		displayIndexes(boundingIndexes);

		testBoundingBox = new GeoIndex(0.01,0.01); // It creates a Array that contains 18000 x 36000 buckets == 648000000 buckets
		boundingIndexes = testBoundingBox.indexesForBoundingBox(-90.0, -180.0, -89.99, -179.98);
		org.junit.Assert.assertTrue("bounding contains 2 indexes", boundingIndexes.size() == 2);

		displayIndexes(boundingIndexes);

		boundingIndexes = testBoundingBox.indexesForBoundingBox(0.0, 0.0, 0.01, 0.01);
		org.junit.Assert.assertTrue("bounding contains 2 indexes", boundingIndexes.size() == 4);

		displayIndexes(boundingIndexes);
		
		testBoundingBox = new GeoIndex(0.4,0.4); // It creates a Array that contains 450 x 900 buckets == 405000 buckets
		String latitudeString = "48.813313";
		String longitudeString = "2.124704";
		
		double latitude = Double.parseDouble(latitudeString);
		double longitude = Double.parseDouble(longitudeString);
		
		GeoLocation centerLocation = GeoLocation.fromDegrees(latitude, longitude);
		GeoLocation[] boundingBox = centerLocation.boundingCoordinates(7.9, GeoIndex.EARTH_RADIUS);
		
		boundingIndexes = testBoundingBox.indexesForBoundingBox(boundingBox[0].getLatitudeInDegrees(), boundingBox[0].getLongitudeInDegrees(), boundingBox[1].getLatitudeInDegrees(), boundingBox[1].getLongitudeInDegrees());
		org.junit.Assert.assertTrue("bounding contains 2 indexes", boundingIndexes.size() == 2);

		displayIndexes(boundingIndexes);


	}
	
	public void displayIndexes(List<Long> indexes) {
		if (_displayIndexes) {
			System.err.println("---------------------------- (" + indexes.size() + ")");
			for (Long current : indexes) {
				System.err.println("Index value: " + current.longValue());
			}
		}
	}

}
