package com.seb.imonserver.geographical;

import java.util.List;

import com.seb.imonserver.datamodel.Cell;
import com.seb.userManagement.UserDescription;


/**
 * Define an interface that must be implemented to store and to retrieve network topology efficiently based on
 * cell geographical location
 * 
 * @author Sebastien Brugalieres
 *
 */
public interface GeographicalTopologyDataItf {
	public void addCell(Cell theCell);
	public List<Cell> getCellsAroundPosition(double latitude, double longitude, double distance, UserDescription user);
}
