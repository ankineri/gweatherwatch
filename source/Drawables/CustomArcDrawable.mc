using Toybox.WatchUi as Ui;
using Toybox.System as Sys;
using Toybox.Graphics;
using Toybox.Math;

class CustomArcDrawable extends Ui.Drawable {

	protected var x, y, radius, angFrom, angTo, min, max;
	
	protected var arrowVal, frontLow, frontHigh;
	
	protected var colBack, colFront, colArrow, width;
	
    function initialize(params) {
    	Drawable.initialize(params);
    	self.x = 0;
    	self.y = 0;
    	var sett = Sys.getDeviceSettings();
    	if ("center".equals(params.get(:x))) {
    		self.x = sett.screenWidth / 2;
    	} else {
    		self.x = params.get(:x);
    	}
    	if ("center".equals(params.get(:y))) {
    		self.y = sett.screenHeight / 2;
    	} else {
    		self.y = params.get(:y);
    	}
    	
    	self.angFrom = params.get(:from);
    	self.angTo = params.get(:to);
    	
    	if ("full".equals(params.get(:r))) {
    		self.radius = (sett.screenHeight < sett.screenWidth ? sett.screenHeight : sett.screenWidth) / 2;
    	} else {
    		self.radius = params.get(:r);
    	}
    	
    	if (params.hasKey(:min)) {
    		self.min = params.get(:min);
    	} else {
    		self.min = 0;
    	}
    	
    	if (params.hasKey(:max)) {
    		self.max = params.get(:max);
    	} else {
    		self.max = 100;
    	}
    	
    	if (params.hasKey(:width)) {
    		self.width = params.get(:width);
    	} else {
    		self.width = 3;
    	}
    	
    	if (params.hasKey(:bgcolor)) {
    		self.colBack = params.get(:bgcolor);
    	} else {
    		self.colBack = Graphics.COLOR_DK_GRAY;
    	}
    	
    	if (params.hasKey(:color)) {
    		self.colFront = params.get(:color);
    	} else {
    		self.colFront = Graphics.COLOR_YELLOW;
    	}
    	
    	if (params.hasKey(:arrowcolor)) {
    		self.colArrow = params.get(:arrowcolor);
    	} else {
    		self.colArrow = Graphics.COLOR_GREEN;
    	}
    }
    
    function setColors(front, back) {
    	self.colFront = front;
    	self.colBack = back;
    } 
    
    function setValue(val) {
    	self.arrowVal = val;
    }
    
    function setFront(from, to) {
    	self.frontLow = from;
    	self.frontHigh = to;
    }
    
    function angle(val) {
    	var ratio = (val + 0.0 - self.min) / (self.max - self.min);
    	var angFromEffective = self.angFrom;
    	if (angFromEffective < self.angTo) {
    		angFromEffective += 360;
    	}
		var rv = Math.round(self.angFrom + (ratio * (self.angTo - angFromEffective)));
		
		return self.normalize(rv);
    }
    
    function normalize(ang) {
    	if (ang >= 360) {
    		ang -= 360;
    	}
    	if (ang < 0) {
    		ang += 360;
    	}
    	return ang;
    }

	function drawFront(dc) {
		//return;
		var from = self.angle(self.frontLow);
		var to = self.angle(self.frontHigh);
		if (from == to) {
			return;
		}
		//Sys.println("Highlight: from " + from + ", to " + to);
		dc.setColor(colFront, Graphics.COLOR_BLACK);
		dc.drawArc(self.x, self.y, self.radius, Graphics.ARC_CLOCKWISE, from, to);
	}
	
	function drawArrow(dc) {
		var pos = self.angle(self.arrowVal);
		dc.setColor(self.colArrow, Graphics.COLOR_BLACK);
		dc.setPenWidth(self.width + 8);
		dc.drawArc(self.x, self.y, self.radius, Graphics.ARC_CLOCKWISE, normalize(pos + 1), normalize(pos - 1));
	}
	
    function draw(dc) {
    	dc.setPenWidth(self.width);
    	dc.setColor(self.colBack, Graphics.COLOR_BLACK);
    	dc.drawArc(self.x, self.y, self.radius, Graphics.ARC_CLOCKWISE, self.angFrom, self.angTo);
    	if (self.frontLow != null) {
    		self.drawFront(dc);
    	}
    	if (self.arrowVal != null) {
    		self.drawArrow(dc);
    	}
    }
}
