using Toybox.WatchUi as Ui;
using Toybox.System as Sys;

class PlotDrawable extends Ui.Drawable {

    protected var height, width, fillColor, lineColor;
    protected var history;
    protected var min, max;
	
    function initialize(params) {
        Drawable.initialize(params);
    	var x = 0, y = 0;
    	//Sys.println(params);
    	var sett = Sys.getDeviceSettings();
    	if ("center".equals(params.get(:x))) {
    		x = sett.screenWidth / 2;
    	} else {
    		x = params.get(:x);
    	}
    	if ("center".equals(params.get(:y))) {
    		y = sett.screenHeight / 2;
    	} else {
    		y = params.get(:y);
    	}
    	setLocation(x, y);
    	
    	fillColor = params.fillColor;
    	lineColor = params.lineColor;
    }
    
    function addEntry(val) {
    	
    }

    function draw(dc) {
    	Drawable.draw(dc);
    }
}
