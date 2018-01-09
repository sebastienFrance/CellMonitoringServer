// Asynchronous method to get KPI Dictionaries
function getKPIDictionaries() {

	$.post("http://" + window.location.hostname + ":" + window.location.port + "/iMonserver/App",
			{
		Method:"getKPIDictionaries",
		user:"seb",
		zip:"false",
		output: "json"
			},
			function(data,status){
				window.KPIDictionaries = eval ("(" + data + ")");
			});
}

function getZones() {
	$.post("http://" + window.location.hostname + ":" + window.location.port + "/iMonserver/App",
			{
		Method:"getZoneList",
		user:"seb",
		zip:"false"
			},
			function(data,status){
				window.zones = eval ("(" + data + ")");

				var myData = new Array();
			    for (var i = 0 ; i < window.zones.length; i++) {
			    	var newData = new Array();
			    	newData[0] = i;
			    	newData[1] = window.zones[i].name;
			    	myData[i] = newData;
			    }
			    
			    var store = Ext.create('Ext.data.ArrayStore', {
					fields: ['id', 'name'],
					data : myData
				});
			    
			    //var theCombo = Ext.getCmp('comboZones');
			    var comboStore = combo.store;
			    comboStore.loadData(store.getRange(), false);
			});
	
}

function getCellOfZones(theZoneName) {
	//Method=getCellsOfZone&zoneName
	Ext.getBody().mask('Processing ...');
	clearOverlays();
	clearNeighbors();
	$.post("http://" + window.location.hostname + ":" + window.location.port + "/iMonserver/App",
			{
		Method:"getCellsOfZone",
		zoneName:theZoneName,
		user:"seb",
		zip:"false"
			},
			function(data,status){
			      var obj = eval ("(" + data + ")");
			      for (var i = 0 ; i < obj.length; i++) {
			    	  createCellOnMapFromJSON(obj[i]);

			      }	
			      Ext.getBody().unmask();

			});
	
}



//Asynchronous method to get About data
function getAbout() {

	$.post("http://" + window.location.hostname + ":" + window.location.port + "/iMonserver/App",
			{
		Method:"about",
		user:"seb",
		zip:"false",
			},
			function(data,status){
				var aboutData = eval ("(" + data + ")");
			    displayAbout(aboutData);
			});
}


function getCellsAround(map, latLong) {
	Ext.getBody().mask('Processing ...');
	clearOverlays();
	clearNeighbors();
	
    $.post("http://" + window.location.hostname + ":" + window.location.port + "/iMonserver/App",
    {
      Method:"cellsAroundPosition",
      user:"seb",
      zip:"false",
      lat: latLong.lat(),
      long: latLong.lng(),
      dist:"20"
    },
    function(data,status){
      var obj = eval ("(" + data + ")");

      for (var i = 0 ; i < obj.length; i++) {
    	  createCellOnMapFromJSON(obj[i]);

      }	
      Ext.getBody().unmask();
    });
}

function getCellWithNeighbors(map, theCell) {
	
	var cellId = theCell.cellName;
	
	clearOverlays();
	clearNeighbors();
	
    $.post("http://" + window.location.hostname + ":" + window.location.port + "/iMonserver/App",
    {
      Method:"cellWithNeighbors",
      techno:theCell.techno,
      user:"seb",
      zip:"false",
      id:cellId
    },
    function(data,status){
      var obj = eval ("(" + data + ")");

      createCellOnMapFromJSON(obj[0]);
      var targetCells = obj[2].TargetCells;
	  for (var i = 0 ; i < targetCells.length; i++) {
	  	  createCellOnMapFromJSON(targetCells[i]);
	  }	
	  
	  var Neighbors = obj[1].NR;
	  createNeighborsOnMap(Neighbors, obj[0].latitude, obj[0].longitude, obj[0].techno); 
	  
    });
}

function getCellKPIs(theCell, theKPIs) {
	// 15mn TO BE COMPLETED
    $.post("http://" + window.location.hostname + ":" + window.location.port + "/iMonserver/App",
    	    {
    	      Method:"getCellKPIs",
    	      techno:"LTE",
    	      id:theCell.id,
    	      KPIs:theKPIs,
    	      periodicity:"15mn",
    	      startDate:"H-1",
    	      endDate:"H-24",
    	      user:"seb",
    	      zip:"false",
    	    },
    	    function(data,status){
    	      var obj = eval ("(" + data + ")");
    	      
    	      manageData(obj, theCell, 0);
    	    });

    $.post("http://" + window.location.hostname + ":" + window.location.port + "/iMonserver/App",
    {
      Method:"getCellKPIs",
      techno:"LTE",
      id:theCell.id,
      KPIs:theKPIs,
      periodicity:"h",
      startDate:"H-1",
      endDate:"H-24",
      user:"seb",
      zip:"false",
    },
    function(data,status){
      var obj = eval ("(" + data + ")");
      
      manageData(obj, theCell, 1);
    });
    
    // Daily
    $.post("http://" + window.location.hostname + ":" + window.location.port + "/iMonserver/App",
    	    {
    	      Method:"getCellKPIs",
    	      techno:"LTE",
    	      id:theCell.id,
    	      KPIs:theKPIs,
    	      periodicity:"d",
    	      startDate:"D-1",
    	      endDate:"D-7",
    	      user:"seb",
    	      zip:"false",
    	    },
    	    function(data,status){
    	      var obj = eval ("(" + data + ")");
    	      
    	      manageData(obj, theCell, 2);
    	    });
    
    // Weekly
    $.post("http://" + window.location.hostname + ":" + window.location.port + "/iMonserver/App",
    	    {
    	      Method:"getCellKPIs",
    	      techno:"LTE",
    	      id:theCell.id,
    	      KPIs:theKPIs,
    	      periodicity:"w",
    	      startDate:"W-1",
    	      endDate:"W-4",
    	      user:"seb",
    	      zip:"false",
    	    },
    	    function(data,status){
    	      var obj = eval ("(" + data + ")");
    	      
    	      manageData(obj, theCell, 3);
    	    });

    // Monthly
    $.post("http://" + window.location.hostname + ":" + window.location.port + "/iMonserver/App",
    	    {
    	      Method:"getCellKPIs",
    	      techno:"LTE",
    	      id:theCell.id,
    	      KPIs:theKPIs,
    	      periodicity:"m",
    	      startDate:"M-1",
    	      endDate:"M-6",
    	      user:"seb",
    	      zip:"false",
    	    },
    	    function(data,status){
    	      var obj = eval ("(" + data + ")");
    	      
    	      manageData(obj, theCell, 4);
    	    });

}

