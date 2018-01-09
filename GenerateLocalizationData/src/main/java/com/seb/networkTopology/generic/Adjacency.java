package com.seb.networkTopology.generic;


import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;




public class Adjacency {
	private static final Logger LOG = LogManager.getLogger(Adjacency.class);

	public String _targetCell = null;
	public String _noHo = null;
	public String _noRemove = null;
	
	public String _dlFrequency = null;
	public String _measuredByANR = null;
	
	public String _technoTarget = null;

	public Adjacency(String targetCell, String noHo, String noRemove, String measuredByANR, String technoTarget) {
		_targetCell = targetCell;
		_noHo = noHo;
		_noRemove = noRemove; 
		_measuredByANR = measuredByANR;
		_technoTarget = technoTarget;
	}

	public Adjacency(String targetCell, String noHo, String noRemove, String measuredByANR, String technoTarget, String dlFrequency) {
		_targetCell = targetCell;
		_noHo = noHo;
		_noRemove = noRemove; 
		_measuredByANR = measuredByANR;
		_technoTarget = technoTarget;
		_dlFrequency = dlFrequency;
	}

	public Adjacency() {
		_noHo = "false";
		_noRemove = "false";
		_measuredByANR = "false";
		_dlFrequency ="0";
	}
	
	public void updateTargetCellWith(Map<String, String> cellIdToTelecomId) {
		String telecomId = cellIdToTelecomId.get(_targetCell);
		if (telecomId == null) {
			LOG.warn("updateTargetCellWith::Warning, cannot find telecomId for Neighbor with target: " + _targetCell);
		} else {
			_targetCell = telecomId;
		}
	}
	
	public void setTargetCell(String targetCell) {
		_targetCell = targetCell;
	}

	public void setNoHo(String noHo) {
		_noHo = noHo;
	}

	public void setNoRemove(String noRemove) {
		_noRemove = noRemove;
	}

	public void setMeasuredByANR(String measuredByANR) {
		_measuredByANR = measuredByANR;
	}

	public void setTechnoTarget(String technoTarget) {
		_technoTarget = technoTarget;
	}

	public void setDlFrequency(String dlFrequency) {
		_dlFrequency = dlFrequency;
	}

}