package com.seb.networkTopology.generic;

import java.sql.Connection;

import com.seb.utilities.Technology;


public interface ParseTechnoItf {
	
	public Technology getTechnology();
	public void setDeltaCoord(double latitude, double longitude);
	public void setPrefix(String prefix);
	public void parse(Connection theConnection);
	
	public int getCellCount();
	
	public int getNeighborCount();

}
