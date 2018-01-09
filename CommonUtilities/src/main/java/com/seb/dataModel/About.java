package com.seb.dataModel;

/**
 * It is just used to build a JSON object 
 * 
 * 
 * @author Sebastien Brugalieres
 *
 */
public class About {
	private int _LTECellCount;
	private int _LTENeighborCount;
	private int _WCDMACellCount;
	private int _WCDMANeighborCount;
	private int _GSMCellCount;
	private int _GSMNeighborCount;
	
	public About(int LTECellCount, int LTENeighborCount, int WCDMACellCount, int WCDMANeighborCount, 
			int GSMCellCount, int GSMNeighborCount) {
		_LTECellCount = LTECellCount;
		_LTENeighborCount = LTENeighborCount;
		_WCDMACellCount = WCDMACellCount;
		_WCDMANeighborCount = WCDMANeighborCount;
		_GSMCellCount = GSMCellCount;
		_GSMNeighborCount = GSMNeighborCount;
	}
	
	
	public int getLTECellCount() {
		return _LTECellCount;
	}
	
	public int getLTENeighborCount() {
		return _LTENeighborCount;
	}

	public int getWCDMACellCount() {
		return _WCDMACellCount;
	}
	
	public int getWCDMANeighborCount() {
		return _WCDMANeighborCount;
	}

	public int getGSMCellCount() {
		return _GSMCellCount;
	}
	
	public int getGSMNeighborCount() {
		return _GSMNeighborCount;
	}
}