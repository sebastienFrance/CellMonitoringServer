// Asynchronous method to get KPI Dictionaries
function getKPIsForCell(theCell) {
	var cellTechno = theCell.techno;
	// look for the KPIs of the same technology in the default dictionary
	  //  KPIDictionaries[x].KPIs[y].techno 
	for (var i = 0; i < KPIDictionaries[0].KPIs.length; i++) {
		if (KPIDictionaries[0].KPIs[i].techno == cellTechno) {
			// iterate on all KPIs to build the string
			var KPIStringList ="";
			for (var j = 0; j < KPIDictionaries[0].KPIs[i].KPIs.length; j++) {
				if (j == (KPIDictionaries[0].KPIs[i].KPIs.length -1)) {
					KPIStringList += KPIDictionaries[0].KPIs[i].KPIs[j].internalName;
				} else {
					KPIStringList += KPIDictionaries[0].KPIs[i].KPIs[j].internalName + ",";
				}
			}
			return KPIStringList;
		}
	}
	return null;
}


function getKPI(KPIInternalName, theCell) {
	var cellTechno = theCell.techno;
	// look for the KPIs of the same technology in the default dictionary
	  //  KPIDictionaries[x].KPIs[y].techno 
	for (var i = 0; i < KPIDictionaries[0].KPIs.length; i++) {
		if (KPIDictionaries[0].KPIs[i].techno == cellTechno) {
			// iterate on all KPIs to find the right KPI
			for (var j = 0; j < KPIDictionaries[0].KPIs[i].KPIs.length; j++) {
				if (KPIDictionaries[0].KPIs[i].KPIs[j].internalName == KPIInternalName) {
					return KPIDictionaries[0].KPIs[i].KPIs[j];
				}
			}
			return null;
		}
	}
	return null;
	
}
