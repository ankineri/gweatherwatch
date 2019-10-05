using Toybox.WatchUi as Ui;
using Toybox.System as Sys;
using Toybox.Math;
using Toybox.ActivityMonitor;

class StepsArcDrawable extends CustomArcDrawable {
	protected var autoColor = false, reverse = false;
	function initialize(params) {
		CustomArcDrawable.initialize(params);
		if (params.hasKey(:autocolor)) {
			self.autoColor = params.get(:autocolor);
		}
		if (params.hasKey(:reverse)) {
			self.reverse = params.get(:reverse);
		}
		self.min = 0;
		self.max = 10000;
	}
	
	function doAutoColor(ratio) {
		if (ratio < 0.1) {
			self.colFront = Graphics.COLOR_WHITE;
			self.colBack = Graphics.COLOR_DK_RED;
		} else if (ratio < 0.7) {
			self.colFront = Graphics.COLOR_YELLOW;
			self.colBack = 0x777700;
		} else if (ratio < 1) {
			self.colFront = Graphics.COLOR_GREEN;
			self.colBack = Graphics.COLOR_WHITE;
		} else {
			self.colFront = 0x0099FF;
		}
	}
	
	function updateValue() {
		var activity = ActivityMonitor.getInfo();
		if (activity == null) {
			return;
		}
		
		var ratio = activity.steps / activity.stepGoal.toFloat();

		if (self.autoColor) {
			self.doAutoColor(ratio);
		}
		
		if (ratio > 1) {
			ratio = 1;
		}
		var progress = ratio * self.max;
		
		if (self.reverse) {
			CustomArcDrawable.setFront(self.max - progress, self.max);
		} else {
			CustomArcDrawable.setFront(0, progress);
		}
	}
	
	function draw(dc) {
		updateValue();
		CustomArcDrawable.draw(dc);
	}
}