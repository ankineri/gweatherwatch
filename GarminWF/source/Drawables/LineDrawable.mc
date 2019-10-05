using Toybox.WatchUi as Ui;
using Toybox.System as Sys;

class LineDrawable extends Ui.Drawable {

    protected var x1, y1, x2, y2;
    protected var color;
	
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
    	self.x1 = self.getCoord(params, :x1, true, sett);
    	self.y1 = self.getCoord(params, :y1, false, sett);
    	self.x2 = self.getCoord(params, :x2, true, sett);
    	self.y2 = self.getCoord(params, :y2, false, sett);

    	if (params.hasKey(:color)) {
    		self.color = params.get(:color);
    	} else {
    		self.color = Graphics.COLOR_WHITE;
    	}
    	if (params.hasKey(:width)) {
    		self.width = params.get(:width);
    	} else {
    		self.width = 1;
    	}
    }

    function draw(dc) {
    	dc.setColor(self.color, Graphics.COLOR_BLACK);
    	dc.setPenWidth(self.width);
    	dc.drawLine(self.x1, self.y1, self.x2, self.y2);
    }
}
