package com.seb.imonserver.datamodel;

/**
 * Used to build a JSON object that contains a NR
 * 
 * @author Sebastien Brugalieres
 *
 */
public class Adjacency {
	public String _targetCell = null;
	public String _noHo = null;
	public String _noRemove = null;
	public String _dlFrequency = null;
	public String _measuredByANR = null;
	public String _technoTarget = null;

	public Adjacency(String targetCell, String noHo, String noRemove, String dlFrequency, String measuredByANR, String technoTarget) {
		_targetCell = targetCell;
		_noHo = noHo;
		_noRemove = noRemove; 
		_dlFrequency = dlFrequency;
		_measuredByANR = measuredByANR;
		_technoTarget = technoTarget;
	}
	
	public String getTargetCell() {
		return _targetCell;
	}
	
	public String getNoHo() {
		return _noHo;
	}
	
	public String getNoRemove() {
		return _noRemove;
	}
	
	public String getMeasuredByANR() {
		return _measuredByANR;
	}
	
	public String getDlFrequency() {
		return _dlFrequency;
	}
	
	public String getTechnoTarget() {
		return _technoTarget;
	}

}