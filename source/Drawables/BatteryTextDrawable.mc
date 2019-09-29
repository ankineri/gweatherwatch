using Toybox.WatchUi as Ui;
using Toybox.System as Sys;

class BatteryTextDrawable extends CustomTextDrawable {
	
    function initialize(params) {
    	CustomTextDrawable.initialize(params);
    	setText("?");
    }
    

    function draw(dc) {
		
		var charge = Math.round(Sys.getSystemStats().battery);
		setText(charge.toNumber() + "%");
    	CustomTextDrawable.draw(dc);
    }
}
