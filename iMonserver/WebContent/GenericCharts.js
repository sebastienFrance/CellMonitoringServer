Ext.require('Ext.chart.*');
Ext.require(['Ext.Window', 'Ext.layout.container.Fit', 'Ext.fx.target.Sprite', 'Ext.window.MessageBox']);


var MONITORING_PERIOD = {
		QUARTER : {value: 0, name: "Last 6 hours, 15mn view",     code: "15mn"}, 
		HOURLY  : {value: 1, name: "Last 24 hours, hourly view",  code: "H"}, 
		DAILY   : {value: 2, name: "Last 7 days, daily view",     code: "D"},
		WEEKLY  : {value: 3, name: "Last 5 weeks, weekly view",   code: "W"},
		MONTHLY : {value: 4, name: "Last 6 months, monthly view", code: "M"}
};

var myData = [
              [0,'Last 6 hours, 15mn view'],
              [1,'Last 24 hours, hourly view'],
              [2,'Last 7 days, daily view'],
              [3,'Last 5 weeks, weekly view'],
              [4,'Last 6 months, monthly view']
              ];


function GenericCharts() {

	this._allKPIsAllPeriod = null;
	this._currentPeriodIndex = null;
	this._currentKPIIndex = 0;
	this._theWindow = null;
	this._theChart = null;
	this._theCell = null;
	this.displayChart = displayChart;
	this.loadKPIdata = loadKPIdata;
	this.getCurrentKPI = getCurrentKPI;
	this.getCurrentRelatedKPI = getCurrentRelatedKPI;
	this.getCurrentKPIName = getCurrentKPIName;
	this.getCurrentRelatedKPIName = getCurrentRelatedKPIName;
	this.getWindowTitle = getWindowTitle;
	this.loadNextKPIdata = loadNextKPIdata;
	this.loadPreviousKPIdata = loadPreviousKPIdata;
	this.buildStoreFromCurrentKPI = buildStoreFromCurrentKPI;
	this.getIndexOfKPI = getIndexOfKPI;
	this.createChart = createChart;
	this.removeSerie = removeSerie;
	this.addSerie = addSerie;
	this.updateChartContent = updateChartContent;
	this.ShallowCopy = ShallowCopy;
	

	function loadKPIdata() {
		var theStoreData = [];
		var theFullKPIsData = this._allKPIsAllPeriod[this._currentPeriodIndex];
		var j = theFullKPIsData[this._currentKPIIndex].KPIValues.length;

		var xColumnHeader;
		switch(this._currentPeriodIndex) {
		case MONITORING_PERIOD.QUARTER.value:
			xColumnHeader = MONITORING_PERIOD.QUARTER.code;
			break;
		case MONITORING_PERIOD.HOURLY.value:
			xColumnHeader = MONITORING_PERIOD.HOURLY.code;
			break;
		case MONITORING_PERIOD.DAILY.value:
			xColumnHeader = MONITORING_PERIOD.DAILY.code;
			break;
		case MONITORING_PERIOD.WEEKLY.value:
			xColumnHeader = MONITORING_PERIOD.WEEKLY.code;
			break;
		case MONITORING_PERIOD.MONTHLY.value:
			xColumnHeader = MONITORING_PERIOD.MONTHLY.code;
			break;
		default:
			xColumnHeader = MONITORING_PERIOD.HOURLY.code;
		}
		
		var relatedKPIIndex = -1;
		
		var relatedKPI = this.getCurrentRelatedKPI();
		if (relatedKPI != undefined) {
			relatedKPIIndex = this.getIndexOfKPI(relatedKPI.internalName);
		} 
		

		for (var i = 0; i < theFullKPIsData[this._currentKPIIndex].KPIValues.length; i++, j--) {
			if (relatedKPIIndex != -1) {
				theStoreData.push({
					name: xColumnHeader + '-' + j,
					data1: theFullKPIsData[this._currentKPIIndex].KPIValues[i],
		    	    data2: theFullKPIsData[relatedKPIIndex].KPIValues[i]
				});
			} else {
				theStoreData.push({
					name: xColumnHeader + '-' + j,
					data1: theFullKPIsData[this._currentKPIIndex].KPIValues[i]
				});
			}
		}

		return theStoreData;
	}

	function getCurrentKPI() {
		var theFullKPIsData = this._allKPIsAllPeriod[this._currentPeriodIndex];
		return getKPI(theFullKPIsData[this._currentKPIIndex].KPIName, this._theCell);
	}

	function getCurrentRelatedKPI() {
		var theFullKPIsData = this._allKPIsAllPeriod[this._currentPeriodIndex];
		
		// get the definition of the current KPI from the dictionary
		var theKPI = getKPI(theFullKPIsData[this._currentKPIIndex].KPIName, this._theCell);
		if (theKPI == undefined) {
			alert("Should never happen!" + theFullKPIsData[this._currentKPIIndex].KPIName + ' ' + this._theCell);
			return null;
		}
		if (theKPI.relatedKPI != undefined) {
			var theRelatedKPI = getKPI(theKPI.relatedKPI, this._theCell);
			return theRelatedKPI;
		} else {
			return null;
		}
	}

	function getCurrentKPIName() {
		var theKPI = this.getCurrentKPI();
		return theKPI.name;
	}

	function getCurrentRelatedKPIName() {
		var theKPI = this.getCurrentRelatedKPI();
		if (theKPI != undefined) {
			return theKPI.name;
		} else {
			return null;
		}
	}
	
	function getIndexOfKPI(KPIInternalName) {
		var theFullKPIsData = this._allKPIsAllPeriod[this._currentPeriodIndex];
		
		for (var i= 0 ; i < theFullKPIsData.length; i++) {
			if (theFullKPIsData[i].KPIName == KPIInternalName) {
				return i;
			}
		}
		return -1;
	}

	function getWindowTitle() {
		var windowTitle = '(' + this._theCell.techno + ' cell) ' + this._theCell.cellName + ' / ' + this.getCurrentKPIName();
		var relatedKPI = this.getCurrentRelatedKPIName();
		if (relatedKPI != undefined) {
			windowTitle += ' vs ' + relatedKPI;
		}
		return windowTitle;
	}

	function loadNextKPIdata() {
		var theFullKPIsData = this._allKPIsAllPeriod[this._currentPeriodIndex];
		this._currentKPIIndex++;
		if (this._currentKPIIndex >= theFullKPIsData.length) {
			this._currentKPIIndex = 0;
		}
		return this.loadKPIdata();
	}

	function loadPreviousKPIdata() {
		var theFullKPIsData = this._allKPIsAllPeriod[this._currentPeriodIndex];
		this._currentKPIIndex--;
		if (this._currentKPIIndex < 0) {
			this._currentKPIIndex = theFullKPIsData.length - 1;
		}
		return this.loadKPIdata();
	}

	function buildStoreFromCurrentKPI() {
		var theStoreData = this.loadKPIdata();

		var relatedKPIIndex = -1;
		
		var relatedKPI = this.getCurrentRelatedKPI();
		if (relatedKPI != undefined) {
			relatedKPIIndex = this.getIndexOfKPI(relatedKPI.internalName);
		} 

		var myStore = null;
		if (relatedKPIIndex != -1) {
			myStore = Ext.create('Ext.data.JsonStore', {
				fields: ['name', 'data1', 'data2'],
				data: theStoreData
			});
		} else {
			myStore = Ext.create('Ext.data.JsonStore', {
				fields: ['name', 'data1'],
				data: theStoreData
			});
		}

		return myStore;
	}

	function ShallowCopy(o) {
		  var copy = Object.create(o);
		  for (prop in o) {
		    if (o.hasOwnProperty(prop)) {
		      copy[prop] = o[prop];
		    }
		  }
		  return copy;
		}
	

	function displayChart(theData, theCell) {
		
		var windowId = 'windowCharts_' + theCell.cellName;
		var window = Ext.getCmp(windowId);
		if (window != undefined) {
			return;
		}
		
		// All KPIs index is the period (Last 6h / Last 24h...)
		this._allKPIsAllPeriod = this.ShallowCopy(theData);
		this._currentPeriodIndex = 1;

		this._theCell = this.ShallowCopy(theCell);
		this._currentKPIIndex = 0;

		var myInstance = this;

		var myStore = this.buildStoreFromCurrentKPI();
		this._theChart = this.createChart(myStore);

		var store = Ext.create('Ext.data.ArrayStore', {
			fields: ['PeriodId', 'PeriodLabel'],
			data : myData
		});
		var combo = Ext.create('Ext.form.field.ComboBox', {
			hideLabel: true,
			store: store,
			displayField: 'PeriodLabel',
			typeAhead: true,
			queryMode: 'local',
			triggerAction: 'all',
			emptyText: 'Select a Period...',
			selectOnFocus: true,
			width: 190,
			valueField : 'PeriodId',
			value:1,
			iconCls: 'no-icon',
			listeners:{
				//scope: yourScope,
				'select':  function(combo, records) 
				{ 
					myInstance._currentPeriodIndex = records[0].data.PeriodId;
					myStore.loadData(myInstance.loadKPIdata());
					myInstance._theWindow.setTitle(myInstance.getWindowTitle());
				}
			}
		});
		
		this._theWindow = Ext.create('Ext.Window', {
			id: 'windowCharts_' + this._theCell.cellName,
			width: 800,
			height: 600,
			minHeight: 400,
			minWidth: 550,
			hidden: false,
			maximizable: true,
			title: this.getWindowTitle(),
			renderTo: Ext.getBody(),
			layout: 'fit',
			tbar: [
			combo,     
			'->', {
				text: 'Previous KPI',
				handler: function() {
					var currentRelatedKPI = myInstance.getCurrentRelatedKPI();

					var newData = myInstance.loadPreviousKPIdata();
					myInstance.updateChartContent(currentRelatedKPI, newData, myStore);
					
					
					myInstance._theWindow.setTitle(myInstance.getWindowTitle());
				}
			}, {
				text: 'Next KPI',
				handler: function() {
					var currentRelatedKPI = myInstance.getCurrentRelatedKPI();
					var newData = myInstance.loadNextKPIdata();
					myInstance.updateChartContent(currentRelatedKPI, newData, myStore);
						
					myInstance._theWindow.setTitle(myInstance.getWindowTitle());
				}
			}


			],
			items: this._theChart    
		});
	}
	
	function updateChartContent(currentRelatedKPI, newData, myStore) {
		var newCurrentRelatedKPI = this.getCurrentRelatedKPI();

		if (newCurrentRelatedKPI != undefined) {
			var ba = this._theChart.axes.get('right');
			ba.setTitle(this.getCurrentRelatedKPIName());
		} else {
			var ba = this._theChart.axes.get('right');
			ba.setTitle(this.getCurrentKPIName());
		}

		var ba = this._theChart.axes.get('left');
		ba.setTitle(this.getCurrentKPIName());
		
		
		if (currentRelatedKPI != undefined) {

			if (newCurrentRelatedKPI == undefined) {
				this.removeSerie(this._theChart, 'serie1');							
			}
			myStore.loadData(newData);
		} else {
			if (newCurrentRelatedKPI != undefined) {
				// load data before to add a new serie
				myStore.loadData(newData);
				this.addSerie(this._theChart, 'serie1');
				// redraw the chart
				this._theChart.redraw();
			} else {
				myStore.loadData(newData);
			}
		}
	
	}
	
	
	function addSerie(chart, theSeriesId) {
		// define the new serie (do not use Ext.create())
		var myCurrentInstance = this;
		var serie = 
		{
				type: 'line',
				seriesId: theSeriesId,
				axis: 'right',
				smooth: true,
				xField: 'name',
				yField: 'data2', 
				highlight: {
					size: 7,
					radius: 7
				},
				markerConfig: {
					type: 'cross',
					size: 4,
					radius: 4,
					'stroke-width': 0
				},
				style: {
					stroke: '#777777',
					'stroke-width': 4,
					fill: '#80A080',
					opacity: 0.8
				},
				tips: {
					trackMouse: true,
					width: 140,
					height: 40,
					renderer: function(storeItem, item) {
						var myFloat = storeItem.get('data2');
						this.setTitle(myCurrentInstance.getCurrentRelatedKPIName() + ' '+ storeItem.get('name') + ': ' + myFloat.toFixed(2));
					}
				},
		};

		// add the serie to the chart (the variable 'chart' holds the complete chart component)
		chart.series.add(serie);

	}
	
    // removes the serie 'serieId' from the chart 'chart'
    //  parameters:    chart        the chart object
    //                seriesId    the ID of the serie
    function removeSerie(chart, seriesId)
    {
        // get the surface 
        var surface = chart.surface;
        
        // get the key of the serie
        for(var serieKey = 0; serieKey < chart.series.keys.length; serieKey++)
        {
            // check for the searched serie
            if(chart.series.keys[serieKey] == seriesId)
            {                
                // go through all the groups of the surface
                for(var groupKey = 0; groupKey < surface.groups.keys.length; groupKey++)
                {
                    // check if the group name contains the serie name
                    if(surface.groups.keys[groupKey].search(seriesId) == 0)
                    {
                        // destroy the group
                        surface.groups.items[groupKey].destroy();
                    }
                }                    


                // get the correct serie
                var serie = chart.series.items[serieKey];
                
                // remove the serie from the chart
                chart.series.remove(serie);


                // redraw the chart
                chart.redraw();    
            }
        }
   }
    
	function createChart(myStore) {
		var myInstance = this;

		var colors = ['url(#default)',
		              'url(#normal)',
		              'url(#low)',
		              'url(#medium)',
		              'url(#high)',
		              'url(#unknown)'];


		var baseColor = '#eee';

		Ext.define('Ext.chart.theme.Fancy', {
			extend: 'Ext.chart.theme.Base',

			constructor: function(config) {
				this.callParent([Ext.apply({
					axis: {
						fill: baseColor,
						stroke: baseColor
					},
					axisLabelLeft: {
						fill: baseColor
					},
					axisLabelBottom: {
						fill: baseColor
					},
					axisTitleLeft: {
						fill: baseColor
					},
					axisTitleBottom: {
						fill: baseColor
					},
					colors: colors
				}, config)]);
			}
		});
		
		
		var theNewChart = Ext.create('Ext.chart.Chart', {
			id: this._theCell.cellName,
			xtype: 'chart',
			style: 'background:#fff',
			theme: 'Fancy',
			animate: {
				easing: 'bounceOut',
				duration: 750
			},
			background: {
				fill: 'rgb(17, 17, 17)'
			},
			shadow: true,
			store: myStore,
			gradients: [
			            {
			            	'id': 'default',
			            	'angle': 0,
			            	stops: {
			            		0: {
			            			color: 'rgb(45, 117, 226)'
			            		},
			            		100: {
			            			color: 'rgb(14, 56, 117)'
			            		}
			            	}
			            },
			            {
			            	'id': 'normal',
			            	'angle': 0,
			            	stops: {
			            		0: {
			            			color: 'rgb(42, 216, 42)'
			            		},
			            		100: {
			            			color: 'rgb(20, 180, 20)'
			            		}
			            	}
			            },
			            {
			            	'id': 'low',
			            	'angle': 0,
			            	stops: {
			            		0: {
			            			color: 'rgb(255,215,0)'
			            		},
			            		100: {
			            			color: 'rgb(205,205,0)'
			            		}
			            	}
			            },
			            {
			            	'id': 'medium',
			            	'angle': 0,
			            	stops: {
			            		0: {
			            			color: 'rgb(255,193,37)'
			            		},
			            		100: {
			            			color: 'rgb(238,180,34)'
			            		}
			            	}
			            },
			            {
			            	'id': 'high',
			            	'angle': 0,
			            	stops: {
			            		0: {
			            			color: 'rgb(238,180,34)'
			            		},
			            		100: {
			            			color: 'rgb(139,54,38)'
			            		}
			            	}
			            },
			            {
			            	'id': 'unknown',
			            	'angle': 0,
			            	stops: {
			            		0: {
			            			color: 'rgb(255,228,225)'
			            		},
			            		100: {
			            			color: 'rgb(139,136,120)'
			            		}
			            	}
			            }],

			            axes: [{
			            	type: 'Numeric',
			            	position: 'left',
			            	fields: ['data1'],
			            	label: {
			            		renderer: Ext.util.Format.numberRenderer('0.00')
			            	},
			            	title: this.getCurrentKPIName(),
			            	grid: true
			            }, {
			            	type: 'Category',
			            	position: 'bottom',
			            	fields: ['name'],
			            	title: 'Period'
			            } , {
			            	type: 'Numeric',
			        		axesId: 'serie1',
			            	position: 'right',
			            	fields: ['data2'],
			            	label: {
			            		renderer: Ext.util.Format.numberRenderer('0.00')
			            	},
			            	title: this.getCurrentRelatedKPIName()
			            }],
			            series: [{
			            	type: 'column',
			            	axis: 'left',
			            	highlight: true,
			            	tips: {
			            		trackMouse: true,
			            		width: 140,
			            		height: 40,
			            		renderer: function(storeItem, item) {
			            			var myFloat = storeItem.get('data1');
			            			this.setTitle(myInstance.getCurrentKPIName() + ' '+ storeItem.get('name') + ': ' + myFloat.toFixed(2));
			            		}
			            	},
			            	renderer: function(sprite, storeItem, barAttr, i, store) {
			            		var theKPI = myInstance.getCurrentKPI();
			            		var KPIValue = storeItem.get('data1');
			            		if (theKPI.direction == "decrease") {
			            			if (KPIValue >= theKPI.low) {
			            				barAttr.fill = colors[1];
			            			} else if (KPIValue >= theKPI.medium) {
			            				barAttr.fill = colors[2];               			
			            			} else if (KPIValue >= theKPI.high) {
			            				barAttr.fill = colors[3];               			
			            			} else {
			            				barAttr.fill = colors[4];                			
			            			}
			            		} else if (theKPI.direction == "increase") {
			            			if (KPIValue <= theKPI.low) {
			            				barAttr.fill = colors[1];
			            			} else if (KPIValue <= theKPI.medium) {
			            				barAttr.fill = colors[2];               			
			            			} else if (KPIValue <= theKPI.high) {
			            				barAttr.fill = colors[3];               			
			            			} else {
			            				barAttr.fill = colors[4];                			
			            			}

			            		} else {
			            			barAttr.fill = colors[0];
			            		}
			            		return barAttr;
			            	},
			            	label: {
			            		display: 'insideEnd',
			            		'text-anchor': 'middle',
			            		field: 'data1',
			            		renderer: Ext.util.Format.numberRenderer('0.00'),
			            		orientation: 'vertical',
			            		color: '#f00'
			            	},
			            	xField: 'name',
			            	yField: 'data1'
			        	}]
		});
		
		var currentRelatedKPI = this.getCurrentRelatedKPI();
		if (currentRelatedKPI != undefined) {
			this.addSerie(theNewChart, 'serie1');
		}
		
		return theNewChart;
	}
}