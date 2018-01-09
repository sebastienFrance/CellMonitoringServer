Ext.require('Ext.container.Viewport');
Ext.Loader.setConfig({enabled: true});
Ext.Loader.setPath('Ext.ux', 'ux');
Ext.require([
    'Ext.window.*'
  //  'Ext.ux.GMapPanel'
]);
Ext.application({
    name: 'HelloExt',
    launch: function() {

    function onItemSearchLocation(item, pressed){
		var ddd = Ext.getCmp('locationTextField');
		codeAddress(ddd.getValue());
    }
    
    function onItemAbout(item, pressed){
    	getAbout();
    }

    
    
    function codeAddress(address) {
        //var address = document.getElementById('address').value;
        geocoder.geocode( { 'address': address}, function(results, status) {
          if (status == google.maps.GeocoderStatus.OK) {
            map.setCenter(results[0].geometry.location);
            getCellsAround(map, results[0].geometry.location);
          } else {
            alert('Geocode was not successful for the following reason: ' + status);
          }
        });
      }
    
    var myDataOriginal = [
                  [0,'Loading zones...'],
                  ];
    var store = Ext.create('Ext.data.ArrayStore', {
    	fields: ['id', 'name'],
    	data : myDataOriginal
    });


	window.combo = Ext.create('Ext.form.field.ComboBox', {
		id:'comboZones',
		hideLabel: true,
		store: store,
		displayField: 'name',
		typeAhead: true,
		queryMode: 'local',
		triggerAction: 'all',
		emptyText: 'Select a Zone...',
		selectOnFocus: true,
		width: 190,
		valueField : 'id',
		//value:0,
		iconCls: 'no-icon',
		listeners:{
			//scope: yourScope,
			'select':  function(combo, records) 
			{ 
				getCellOfZones(records[0].data.name);
				//myInstance._currentPeriodIndex = records[0].data.PeriodId;
				//myStore.loadData(myInstance.loadKPIdata());
				//myInstance._theWindow.setTitle(myInstance.getWindowTitle());
			}
		}
	});

	

	
 var filterPanel = Ext.create('Ext.panel.Panel', {
    	    bodyPadding: 5,  // Don't want content to crunch against the borders
    	    layout: 'fit',
    	    title: 'iMonitoring',
    	    collapsible: true,
    	    html: ' <div id="map_canvas" style="width: 100%; height: 100%;"></div>',
    	    tbar: {
                id: 'panelWithToolbars_tbar',
              items: [
              {
                  xtype    : 'textfield',
                  id	   : 'locationTextField',	
                  name     : 'field1',
                  emptyText: 'Location',
                  listeners:{  
                      scope:this,  
                      specialkey: function(f,e){  
                          if(e.getKey()==e.ENTER){  
                      		var theLocation = Ext.getCmp('locationTextField');
                    		codeAddress(theLocation.getValue());
                          }  
                      }
                  }
              },
              {
                  // xtype: 'button', // default for Toolbars
                  text: 'Search',
                  handler: onItemSearchLocation
             },
             {
                 // xtype: 'button', // default for Toolbars
                 text: 'Cells',
              	    disabled: true,
             },
             {
                 // xtype: 'button', // default for Toolbars
                 text: 'Dashboard',
              	    disabled: true,
             },
             {
                 // xtype: 'button', // default for Toolbars
                 text: 'Me',
              	    disabled: true,
             },
             {
                 // xtype: 'button', // default for Toolbars
                 text: 'Bookmarks',
              	 disabled: true,
             },
             combo,
             {
                 // xtype: 'button', // default for Toolbars
                 text: 'About',
                 handler: onItemAbout
             },
            '->',
             {
                 // xtype: 'button', // default for Toolbars
                 text: 'Options',
              	    disabled: true,
             }

          ] }

    	});
 
 var webBody = new Ext.Viewport({
		id:'bgBody',
		margins: '5 5 5 0',
		layout:'fit',
		items: filterPanel
	});


    }
});

