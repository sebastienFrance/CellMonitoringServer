
package com.seb.imonserver.geographical;

import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;

import com.seb.imonserver.datamodel.Cell;
import com.seb.userManagement.UserDescription;
import com.seb.topologyMgt.GeoLocation;


/**
 * @author Sebastien Brugalieres
 *
 */
public class BasicGeographicalTopologyData implements GeographicalTopologyDataItf {

	List<Cell> _cells;

	public BasicGeographicalTopologyData() {
		_cells = new ArrayList<Cell>();
	}
	
	@Override
	public void addCell(Cell theCell) {
		_cells.add(theCell); 
	}
	
	@Override
	public List<Cell> getCellsAroundPosition(double latitude, double longitude, double distance, UserDescription user) {
		
		ArrayList<Cell> matchingCells = new ArrayList<Cell>();
		
		  Iterator<Cell> it = _cells.iterator();

		  GeoLocation centerLocation = GeoLocation.fromDegrees(latitude, longitude);
		  GeoLocation[] boundingBox = centerLocation.boundingCoordinates(distance, 6371.01);
		  
		  double maxNorthLat = boundingBox[1].getLatitudeInDegrees();
		  double maxEastLong = boundingBox[1].getLongitudeInDegrees();
		  double maxSouthLat = boundingBox[0].getLatitudeInDegrees();
		  double maxWestLong = boundingBox[0].getLongitudeInDegrees();
		  
		  while (it.hasNext()) {
			  Cell currentCell =  it.next();
//			  if (user.hasAccess(currentCell) == false) {
//				  continue;
//			  }
			  
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
			  double distanceInKm = cellLocation.distanceTo(centerLocation, 6371.01);

			  if (distanceInKm < distance) {
				  // add the cell in the Array !
				  matchingCells.add(currentCell);
			  }
		  }
		return matchingCells;
	}

}
