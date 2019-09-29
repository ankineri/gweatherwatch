using Toybox.WatchUi as Ui;
using Toybox.System as Sys;
using Toybox.Math;

class BatteryArcDrawable extends CustomArcDrawable {
	function initialize(params) {
		CustomArcDrawable.initialize(params);
		self.min = 0;
		self.max = 100;
	}

	function updateValue() {
		var charge = Sys.getSystemStats().battery;
		CustomArcDrawable.setFront(100 - Math.round(charge), 100);
	}
	
	function draw(dc) {
		updateValue();
		CustomArcDrawable.draw(dc);
	}
}