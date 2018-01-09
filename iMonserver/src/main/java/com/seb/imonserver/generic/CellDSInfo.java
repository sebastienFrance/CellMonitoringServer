package com.seb.imonserver.generic;

public class CellDSInfo {

	private String _cellName;
	private int _QOSDatasource;
	private int _topologyDatasource;
	
	public CellDSInfo(String cellName, int QOSDatasource, int topologyDatasource) {
		_cellName = cellName;
		_QOSDatasource = QOSDatasource;
		_topologyDatasource = topologyDatasource;
	}
	
	public String getCellName() {
		return _cellName;
	}
	
	public int getQOSDatasource() {
		return _QOSDatasource;
	}
	
	public int getTopologyDatasource() {
		return _topologyDatasource;
	}
	
}
