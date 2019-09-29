using Toybox.WatchUi as Ui;
using Toybox.System as Sys;
using Toybox.Math;

class BatteryArcDrawable extends CustomArcDrawable {
	protected var autoColor = false;
	function initialize(params) {
		CustomArcDrawable.initialize(params);
		if (params.hasKey(:autocolor)) {
			self.autoColor = params.get(:autocolor);
		}
		self.min = 0;
		self.max = 100;
	}
	
	function doAutoColor(batt) {
		if (batt < 15) {
			self.colFront = Graphics.COLOR_RED;
			self.colBack = Graphics.COLOR_DK_RED;
		} else if (batt < 30) {
			self.colFront = Graphics.COLOR_YELLOW;
			self.colBack = 0x777700;
		} else {
			self.colFront = Graphics.COLOR_GREEN;
			self.colBack = Graphics.COLOR_DK_GREEN;
		}
	}

	function updateValue() {
		var charge = Math.round(Sys.getSystemStats().battery);
		if (self.autoColor) {
			self.doAutoColor(charge);
		}
		CustomArcDrawable.setFront(100 - charge, 100);
	}
	
	function draw(dc) {
		updateValue();
		CustomArcDrawable.draw(dc);
	}
}