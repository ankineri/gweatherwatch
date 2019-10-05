using Toybox.WatchUi as Ui;
using Toybox.System as Sys;

class PositionableDrawable extends Ui.Drawable {

    protected var locX, locY;
	
	function getCoord(params, val, horiz, sett) {
		var x = params.get(val);
		if ("center".equals(x)) {
    		x = (horiz ? sett.screenWidth : sett.screenHeight) / 2;
    	} else if ("max".equals(x)) {
    		x = (horiz ? sett.screenWidth : sett.screenHeight);
    	}
    	
    	return x;
    }
	
    function initialize(params) {
        Drawable.initialize(params);
    	var sett = Sys.getDeviceSettings();
		locX = getCoord(params, :x, true, sett);
		locY = getCoord(params, :y, false, sett);
    }

    function draw(dc) {
    	Drawable.draw(dc);
    }
}
