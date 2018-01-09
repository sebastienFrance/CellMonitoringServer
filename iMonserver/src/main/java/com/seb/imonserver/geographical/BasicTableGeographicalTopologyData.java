package com.seb.imonserver.geographical;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.seb.imonserver.datamodel.Cell;
import com.seb.userManagement.UserDescription;
import com.seb.topologyMgt.GeoLocation;


/**
 * @author Sebastien Brugalieres
 *
 */
public class BasicTableGeographicalTopologyData implements GeographicalTopologyDataItf {
	private static final Logger LOG = LogManager.getLogger(BasicTableGeographicalTopologyData.class);

	private List<ArrayList<Cell>> _ListOfTableCells;

	private final static int LONGITUDE_BUCKET_SIZE = 24; 
	
	public BasicTableGeographicalTopologyData() {
		_ListOfTableCells = new ArrayList<ArrayList<Cell>>(24);
		for (int i = 0 ; i < LONGITUDE_BUCKET_SIZE ; i++) {
			_ListOfTableCells.add(new ArrayList<Cell>());
		}
	}
	
	@Override
	public void addCell(Cell theCell) {
		
		int realIndex = getIndexFromLongitude(theCell.getLongitude());
		List<Cell> theList = null;
		try {
			theList = _ListOfTableCells.get(realIndex);
			theList.add(theCell);
		} catch (IndexOutOfBoundsException ex) {			
			  LOG.error(ex);
		}
	}
	
	private static int getIndexFromLongitude(double longitude) {
		int range = (int) Math.floor(longitude / LONGITUDE_BUCKET_SIZE);

		int realIndex;
		if (range >= 0) {
			realIndex = range + ((LONGITUDE_BUCKET_SIZE/2)-1);
		} else {
			realIndex = Math.abs(range);
		}
		return realIndex;
	}
	
	@Override
	public List<Cell> getCellsAroundPosition(double latitude, double longitude, double distance, UserDescription user) {
		
		List<Cell> matchingCells = new ArrayList<Cell>();

		GeoLocation centerLocation = GeoLocation.fromDegrees(latitude, longitude);
		GeoLocation[] boundingBox = centerLocation.boundingCoordinates(distance, 6371.01);

		double maxNorthLat = boundingBox[1].getLatitudeInDegrees();
		double maxEastLong = boundingBox[1].getLongitudeInDegrees();
		double maxSouthLat = boundingBox[0].getLatitudeInDegrees();
		double maxWestLong = boundingBox[0].getLongitudeInDegrees();
		
		// WEST should have index <= EAST because WEST have lower degree (longitude goes from -180 to 180)
		// SEB: To be check if there's an issue if we are looking cells around 180 or -180
		int firstIndex = getIndexFromLongitude(maxWestLong);
		int secondIndex = getIndexFromLongitude(maxEastLong);
		
		
		for (int i = firstIndex ; i <= secondIndex; i++) {
			List<Cell> currentBucketOfCell = _ListOfTableCells.get(i);
			LOG.info("getCellsAroundPosition:: number of cells in Bucket: " + currentBucketOfCell.size() + " (index " + i + ")");
			
			for (Cell currentCell : currentBucketOfCell) {
//				  if (user.hasAccess(currentCell) == false) {
//					  continue;
//				  }
				  
				  double cellLatitude = currentCell.getLatitude();
				  if ((cellLatitude > maxNorthLat) || (cellLatitude < maxSouthLat)) {
					  // ignore the cell and go to the next one
					  continue;
				  }

				  double cellLongitude = currentCell.getLongitude();
				  if ((cellLongitude > maxEastLong) || (cellLongitude < maxWestLong)) {
					  // ignore the cell and go to the next one
					  continue;
				  }

				  GeoLocation cellLocation = currentCell.extractLocation();
				  if ((cellLocation.getLatitudeInDegrees() == centerLocation.getLatitudeInDegrees()) && (cellLocation.getLongitudeInDegrees() == centerLocation.getLongitudeInDegrees())) {
					  matchingCells.add(currentCell);
					  continue;
				  }
				  
				  double distanceInKm = cellLocation.distanceTo(centerLocation, 6371.01);

				  if (distanceInKm <= distance) {
					  // add the cell in the Array !
					  matchingCells.add(currentCell);
				  }

			}
		}
		
		return matchingCells;
	}

}
