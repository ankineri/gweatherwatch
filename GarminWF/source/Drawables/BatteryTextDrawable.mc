using Toybox.WatchUi as Ui;
using Toybox.System as Sys;

class BatteryTextDrawable extends CustomTextDrawable {
	
    function initialize(params) {
    	CustomTextDrawable.initialize(params);
    	setText("?");
    }
    

    function draw(dc) {
		
		var charge = Math.round(Sys.getSystemStats().battery).toNumber();
		if (charge == 100) {
			setText("100");
		} else {
			setText(charge + "%");
		}
    	CustomTextDrawable.draw(dc);
    }
}
