Ext.require('Ext.chart.*');
Ext.require(['Ext.Window', 'Ext.layout.container.Fit', 'Ext.fx.target.Sprite', 'Ext.window.MessageBox']);

var _theWindow;


function buildMsg(theData) {
	var theMessage  = '<p><b>LTE Statistics</b> <br>'; 
	theMessage = theMessage + 'Cells: ' + theData.LTECellCount + '<br>' + 'Neighbors: ' + theData.LTENeighborCount + '<br><br>';
	theMessage = theMessage + '<b>WCDMA Statistics</b> <br>';
	theMessage = theMessage + 'Cells: ' + theData.WCDMACellCount + '<br>' + 'Neighbors: ' + theData.LTENeighborCount + '<br><br>';
	theMessage = theMessage + '<b>GSM Statistics</b> <br>';
	theMessage = theMessage + 'Cells: ' + theData.GSMCellCount + '<br>' + 'Neighbors: ' + theData.GSMNeighborCount + '<br><br>';
	theMessage = theMessage + 'Code and Design by <b>Sebastien Brugalieres</b></p>';
	return theMessage;
}

function displayAbout(theData) {

	var message = buildMsg(theData);
	
	
	Ext.Msg.show({
	     title:'About',
	     msg: message, 
	     buttons: Ext.Msg.OK,
	     icon: Ext.Msg.INFO
	});

}