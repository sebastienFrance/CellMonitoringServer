package com.seb.imonserver.generic.kpidictionaries;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Sebastien Brugalieres
 *
 */
public class KPIs {
	private List<KPI> _KPIs;
	
	private String _techno;
	
	public KPIs() {
		_KPIs = new ArrayList<KPI>();
	}
	
	public void setTechno(String techno) {
		_techno = techno;
	}
	
	public String getTechno() {
		return _techno;
	}

	public void addKPI(KPI theKPI) {
		_KPIs.add(theKPI);
	}
	
	public List<KPI> getKPIs() {
		return _KPIs;
	}
	
	
	public void dump() {
		
		for (KPI theKPI : _KPIs) {
			theKPI.dump();
		}
	}
}
