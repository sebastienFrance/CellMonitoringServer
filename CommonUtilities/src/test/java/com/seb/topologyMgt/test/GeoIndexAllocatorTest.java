package com.seb.topologyMgt.test;

import static org.junit.Assert.*;

import org.junit.Test;

import com.seb.topologyMgt.GeoIndexAllocator;
import com.seb.topologyMgt.GeoLocation;

public class GeoIndexAllocatorTest {

	@Test
	public void test() {
		GeoIndexAllocator indexAllocator = GeoIndexAllocator.getInstance();
		
		Long index = indexAllocator.getIndex(GeoIndexAllocator.GeoIndexSize.LARGE, 0.0, 0.0);
		
		GeoLocation location = GeoLocation.fromDegrees(-0.2, -0.2);
		
		// GeoIndexAllocator.GeoIndexSize.LARGE is a square of around 40 km large
		
		// Square block built for a Circle of 10km Radius from the middle of the block must be contained in the block of 40km
		boolean isContained = indexAllocator.isFullyContainedIn(index, GeoIndexAllocator.GeoIndexSize.LARGE, location, 10.0);
		org.junit.Assert.assertTrue(isContained);

		// Square block built for a Circle of 30km Radius from the middle of the block must not be contained in the block of 40km
		isContained = indexAllocator.isFullyContainedIn(index, GeoIndexAllocator.GeoIndexSize.LARGE, location, 30.0);
		org.junit.Assert.assertFalse(isContained);
		
		
		// Somewhere in the positive lat/long

		index = indexAllocator.getIndex(GeoIndexAllocator.GeoIndexSize.LARGE, 2.0, 23.0);

		location = GeoLocation.fromDegrees(1.8, 23);

		// GeoIndexAllocator.GeoIndexSize.LARGE is a square of around 40 km large

		// Square block built for a Circle of 10km Radius from the middle of the block must be contained in the block of 40km
		isContained = indexAllocator.isFullyContainedIn(index, GeoIndexAllocator.GeoIndexSize.LARGE, location, 10.0);
		org.junit.Assert.assertTrue(isContained);

		// Square block built for a Circle of 30km Radius from the middle of the block must not be contained in the block of 40km
		isContained = indexAllocator.isFullyContainedIn(index, GeoIndexAllocator.GeoIndexSize.LARGE, location, 30.0);
		org.junit.Assert.assertFalse(isContained);

	}

}
