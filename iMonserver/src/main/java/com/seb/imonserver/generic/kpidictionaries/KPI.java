package com.seb.imonserver.generic.kpidictionaries;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * @author Sebastien Brugalieres
 *
 */
public class KPI {
	private static final Logger LOG = LogManager.getLogger(KPI.class);

	private String _name;
	private String _internalName;
	private String _shortDescription;
	private String _domain;
	private String _formula;
	private String _unit;
	private String _direction;
	private String _low;
	private String _medium;
	private String _high;
	private String _relatedKPI;
	
	public KPI() {
	}
	
	public void dump() {
		StringBuilder buff = new StringBuilder();
		
		buff.append(" _Name: " + _name);
		buff.append(" _internalName: " + _internalName);
		buff.append(" _shortDescription: " + _shortDescription);
		buff.append(" _domain: " + _domain);
		buff.append(" _formula: " + _formula);
		buff.append(" _unit: " + _unit);
		buff.append(" _direction: " + _direction);
		buff.append(" _low: " + _low);
		buff.append(" _medium: " + _medium);
		buff.append(" _high: " + _high);
		buff.append(" _relatedKPI: " + _relatedKPI);
		
		LOG.info("dump::" + buff.toString());
		
	}

	public String getName() {
		return _name;
	}

	public void setName(String _name) {
		this._name = _name;
	}

	public String getInternalName() {
		return _internalName;
	}

	public void setInternalName(String _internalName) {
		this._internalName = _internalName;
	}

	public String getShortDescription() {
		return _shortDescription;
	}

	public void setShortDescription(String _shortDescription) {
		this._shortDescription = _shortDescription;
	}

	public String getDomain() {
		return _domain;
	}

	public void setDomain(String _domain) {
		this._domain = _domain;
	}

	public String getFormula() {
		return _formula;
	}

	public void setFormula(String _formula) {
		this._formula = _formula;
	}

	public String getUnit() {
		return _unit;
	}

	public void setUnit(String _unit) {
		this._unit = _unit;
	}

	public String getDirection() {
		return _direction;
	}

	public void setDirection(String _direction) {
		this._direction = _direction;
	}

	public String getLow() {
		return _low;
	}

	public void setLow(String _low) {
		this._low = _low;
	}

	public String getMedium() {
		return _medium;
	}

	public void setMedium(String _medium) {
		this._medium = _medium;
	}

	public String getHigh() {
		return _high;
	}

	public void setHigh(String _high) {
		this._high = _high;
	}
	
	public String getRelatedKPI() {
		return _relatedKPI;
	}

	public void setRelatedKPI(String relatedKPI) {
		this._relatedKPI = relatedKPI;
	}
	
	

}
