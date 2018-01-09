var geocoder;
var map;
var theSelectedCell;
var markersArray = [];
var neighborsArray = [];
var currentInfoWindow;

//Cell structure:
//	attrNameValueList[x].name = generic attribute name
//attrNameValueList[x].value 
//attrNameValueList[x].section 
//cellName
//longitude
//latitude
//azimuth
//techno
//technoType
//site
//release
//dlFrequency
//telecomId
//numberIntraFreqNR
//numberInterFreqNR
//numberInterRATNR

var cellIndexedByTelecomId = new Object();
var cellIndexedByName = new Object();

//KPI dictionary structure:
//KPIDictionaries[x].name = name of the dictionary
//KPIDictionaries[x].descripton = description of the dictionary
//KPIDictionaries[x].KPIs[y].techno = Tehcnology of the KPIs
//KPIDictionaries[x].KPIs[y].KPIs[z].name = external name of the KPI
//KPIDictionaries[x].KPIs[y].KPIs[z].internalName
//KPIDictionaries[x].KPIs[y].KPIs[z].shortDescription 
//KPIDictionaries[x].KPIs[y].KPIs[z].domain
//KPIDictionaries[x].KPIs[y].KPIs[z].formula 
//KPIDictionaries[x].KPIs[y].KPIs[z].unit 
//KPIDictionaries[x].KPIs[y].KPIs[z].direction 
//KPIDictionaries[x].KPIs[y].KPIs[z].low 
//KPIDictionaries[x].KPIs[y].KPIs[z].medium 
//KPIDictionaries[x].KPIs[y].KPIs[z].high 
//KPIDictionaries[x].KPIs[y].KPIs[z].relatedKPI 

var KPIDictionaries = [];

// Zone structure
// zones[x].name
// zones[x].type
// zones[x].techno
// zones[x].description

var zones = [];
var combo;
//About structure:
//AboutData.LTECellCount = number of LTE Cells
//AboutData.LTENeighborCount = number of LTE Neighbors Cells
//AboutData.WCDMACellCount = number of WCDMA Cells
//AboutData.WCDMANeighborCount = number of WCDMA Neighbors Cells
//AboutData.GSMCellCount = number of GSM Cells
//AboutData.GSMNeighborCount = number of GSM Neighbors Cells

//var myMask;

function initialize() {
	geocoder = new google.maps.Geocoder();
	// Paris : 48.8574, 2.3454
	// Minneapolis: 44.9618, -93.267
	var latlng = new google.maps.LatLng(48.8574, 2.3454);
	var mapOptions = {
			zoom: 12,
			center: latlng,
			mapTypeId: google.maps.MapTypeId.ROADMAP
	}
	map = new google.maps.Map(document.getElementById('map_canvas'), mapOptions);

	getKPIDictionaries();
	getZones();
}


function codeAddress() {
	var address = document.getElementById('address').value;
	geocoder.geocode( { 'address': address}, function(results, status) {
		if (status == google.maps.GeocoderStatus.OK) {
			map.setCenter(results[0].geometry.location);
			getCellsAround(map, results[0].geometry.location);
		} else {
			alert('Geocode was not successful for the following reason: ' + status);
		}
	});
}


function reverseGeoCode(myLatlng, marker) {
	geocoder.geocode( { 'latLng': myLatlng}, function(results, status) {
		var resolvedAddress;
		if (status == google.maps.GeocoderStatus.OK) {
			if (results[0]) {
				resolvedAddress = results[0].formatted_address;
				//alert("found : " + resolvedAddress);
			} else {
				resolvedAddress = "No Address";
			}
		} else {
			resolvedAddress = "Address not found";	
		}
		
		theSelectedCell = cellIndexedByName[marker.title];

		var htmlContent = buildInfoWindowContent(theSelectedCell, resolvedAddress); 

		var infowindow = new google.maps.InfoWindow(
				{ content: marker.title,
				  size: new google.maps.Size(500,500)
				});
		currentInfoWindow = infowindow;
		infowindow.content = htmlContent;
		infowindow.open(map,marker);

	});
}


function buildInfoWindowContent(theCell, resolvedAddress) {
	var htmlContent = '<head><link rel="stylesheet" type="text/css" href="css/iMon.css"></head>';
	htmlContent += '<div id="MyInfoWindow">';
	htmlContent += '<h1>' + theCell.cellName;
	htmlContent += '(' + theCell.techno + ')';
	htmlContent += '</h1><br>';
	htmlContent += '<p>Site ' + theCell.site + '</p>';
	htmlContent += '<p>Release ' + theCell.release + '</p><br>';
	
	htmlContent += '<p>Address<br>' + resolvedAddress + '</p><br>';
	htmlContent += '<p>Neighbors relations</p>';
	htmlContent += '<ul class="b">';
	htmlContent += '<li>- Intra-Frquencies ' + theCell.numberIntraFreqNR + '</li>';
	htmlContent += '<li>- Inter-Frquencies ' + theCell.numberInterFreqNR + '</li>';
	htmlContent += '<li>- Inter-RAT ' + theCell.numberInterRATNR + '</li>';
	htmlContent += '<ul><br>';
	htmlContent += '<p>Frequency ' + theCell.dlFrequency + '</p>';
	htmlContent += '<p>Cell azimuth' + theCell.azimuth + '</p>';
	
	
	htmlContent += '<button class="button gray" onclick="getCellWithNeighbors(map, theSelectedCell)">Show Neighbors</button>';
	htmlContent += '<button class="button gray" onclick="showKPIsOfSelectedCell(theSelectedCell)">Show KPIs</button>';
	htmlContent += '</div>';

	return htmlContent;
}

function showKPIsOfSelectedCell(theCell) {
//	currentInfoWindow.close();
//	currentInfoWindow = null;
	var KPIString = getKPIsForCell(theCell);
	requestCounter = 5;
	Ext.getBody().mask('Processing ...');
	getCellKPIs(theCell, KPIString);
}


function loadContent(){
     document.getElementById('info_content').innerHtml = 'Now here you add your new code';
}



function clearOverlays() {
	if (markersArray) {
		for (i in markersArray) {
			markersArray[i].setMap(null);
		}
	}
}

function clearNeighbors() {
	if (neighborsArray) {
		for (i in neighborsArray) {
			neighborsArray[i].setMap(null);
		}
	}
}

function createNeighborsOnMap(Neighbors, srcCellLatitude, srcCellLongitude, srcCellTechno) {
	var srcCellLatLng = new google.maps.LatLng(srcCellLatitude, srcCellLongitude);
	for (var i = 0 ; i < Neighbors.length; i++) {

		var targetCell = cellIndexedByTelecomId[Neighbors[i].targetCell];
		if (targetCell == undefined) {
			alert("cannot find targetCell for " + Neighbors[i].targetCell);
		}

		var targetCellLatLong = new google.maps.LatLng(targetCell.latitude, targetCell.longitude);

		var NeighborCoordinates = [
		                           srcCellLatLng,
		                           targetCellLatLong
		                           ];

		   
		var neighborColor;
		var neighborOpacity = 0;
		
	    if (Neighbors[i].noHo == 'true') {
	        if (Neighbors[i].noRemove == 'true') {	        	
	            neighborColor = "FireBrick";
	       } else {
	           neighborColor = "Orange";
	        }
	    } else {
	        if (Neighbors[i].noRemove == 'true') {
	            neighborColor = "Green";
	        } else {
	            neighborColor = "Blue";
	        }
	    }
		
	    var neighborWeight = 2;
	    var lineSymbol;
	    if (srcCellTechno != targetCell.techno) {
	    	neighborWeight = 10;
	        //aView.lineDashPattern = [[NSArray alloc] initWithObjects:[NSNumber numberWithInt:10], [NSNumber numberWithInt:20], nil];
	        lineSymbol = {
		    		  path: 'M 0,-2 0,8',
		    		  strokeOpacity: 1,
		    		  scale: 2
		    		};
	    } else {
	        if (srcCellTechno.dlFrequency == targetCell.dlFrequency) {
	        	neighborWeight = 15;
	          //  aView.lineDashPattern = [[NSArray alloc] initWithObjects:[NSNumber numberWithInt:20], [NSNumber numberWithInt:40], nil];
	            lineSymbol = {
	  	    		  path: 'M 0,-2 0,8',
	  	    		  strokeOpacity: 1,
	  	    		  scale: 2,
	  	    		  strokeWeight: 4
	  	    		};
	        } else {
	        	neighborWeight = 15;
	            //aView.lineDashPattern = [[NSArray alloc] initWithObjects:[NSNumber numberWithInt:10], [NSNumber numberWithInt:40], [NSNumber numberWithInt:50],[NSNumber numberWithInt:40], nil];
	          lineSymbol = {
	    		  path: 'M 0,-2 0,2 M 0,6 0,8',
	    		  strokeOpacity: 1,
	    		  scale: 2
	    		};
	        }
	    }


		var neighborPath = new google.maps.Polyline({
			path: NeighborCoordinates,
			strokeColor: neighborColor,
			strokeOpacity: neighborOpacity,
			//strokeWeight: neighborWeight,
  		  icons: [{
  		    icon: lineSymbol,
  		    offset: '0',
  		    repeat: '40px'
  		  }],

		});


		neighborPath.setMap(map);
		neighborsArray.push(neighborPath);
	}

}



//Add a cell in the Google Map
function createCellOnMapFromJSON(cell) {
	var myLatlng = new google.maps.LatLng(cell.latitude, cell.longitude);

	var image;
	if (cell.techno == "LTE") {
		image = 'img/8_purple.png';
	} else if (cell.techno == "WCDMA") {
		image = 'img/8_teal.png';
	} else {
		image = 'img/8_yellow.png';
	}

	var marker = new google.maps.Marker({
		position: myLatlng,
		map: map,
		title:cell.cellName,
		icon: image
	});
	markersArray.push(marker);

	recordMarker(marker);
	cellIndexedByTelecomId[cell.telecomId] = cell;
	cellIndexedByName[cell.cellName] = cell;
}
var requestCounter;

// Index is the Period (15mn / hourly /..)

var allData = [];


function recordMarker(marker) {

	google.maps.event.addListener(marker, 'click', function() {
		if (undefined != currentInfoWindow) {
			currentInfoWindow.close();
		} 
		
		reverseGeoCode(marker.position, marker);			
	});
}

function manageData(theData, theCell, index) {
	requestCounter--;
	allData[index] = theData;
	if (requestCounter == 0) {
		var theCharts = new GenericCharts();
		theCharts.displayChart(allData, theCell);
		Ext.getBody().unmask();
	}
}
