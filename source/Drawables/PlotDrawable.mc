using Toybox.WatchUi as Ui;
using Toybox.System as Sys;
using Toybox.Math;

class PlotDrawable extends PositionableDrawable {

    protected var height, width, fillColor, lineColor, backColor;
    protected var history;
    protected var min, max;
    protected var plotData;
	
    function initialize(params) {
        PositionableDrawable.initialize(params);
    	
    	self.fillColor = params.get(:fillcolor);
    	self.lineColor = params.get(:linecolor);
    	self.backColor = params.get(:bkcolor);
    	self.height = params.get(:height);
    	self.width = params.get(:width);
    	var numEntries = params.get(:width);
    	self.plotData = new PlotData(numEntries);
    }
    
    function addEntry(val) {
    	self.plotData.add(val);
    }
    
    function getHeights() {
    	var min = self.plotData.getMin();
    	var max = self.plotData.getMax();
    	var alldata = self.plotData.getAll();
    	//Sys.println("Max: " + max + ", min: " + min);
    	//Sys.println(alldata);
    	if (alldata.size() == 0) {
    		return alldata;
    	}
    	if (min == max) {
    		max = min + 1;
    	}
    	var rv = new [alldata.size()];
    	for (var i = 0; i < rv.size(); ++i) {
    		rv[i] = (Math.round((alldata[i] + 0.0 - min) / (max - min) * self.height)).toNumber();
    	}
    	return rv;
    }

    function draw(dc) {
    	dc.setColor(self.backColor, Graphics.COLOR_BLACK);
    	//dc.fillRectangle(self.locX, self.locY, self.width, self.height);
    	var heights = self.getHeights();
    	var bottom = self.locY + self.height;
    	dc.setColor(self.fillColor, Graphics.COLOR_BLACK);
    	dc.setPenWidth(1);
    	//Sys.println("Heights: " + heights);
    	for (var i = 0; i < heights.size(); ++i) {
    		dc.drawLine(i + self.locX, bottom, i + self.locX, bottom - heights[i]);
    	}
    	dc.setColor(self.lineColor, Graphics.COLOR_BLACK);
    	for (var i = 0; i < heights.size(); ++i) {
    		dc.drawPoint(i + self.locX, bottom - heights[i]);
    	}
    	self.plotData.add(Math.rand() % 500);
    }
}
