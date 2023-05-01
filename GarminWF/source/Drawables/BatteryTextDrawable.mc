using Toybox.WatchUi as Ui;
using Toybox.System as Sys;

class BatteryTextDrawable extends CustomTextDrawable {
	private var short = false;
    function initialize(params) {
    	CustomTextDrawable.initialize(params);
		if (params.get(:short)) {
			self.short = true;
		}
    	setText("?");
    }
    

    function draw(dc) {
		var charge = Math.round(Sys.getSystemStats().battery).toNumber();
		var text = charge + "%";
		if (Sys.getSystemStats().charging) {
			text = "+" + text;
		}
		if (self.short) {
			if (charge == 100) {
				text = "100";
			} else {
				text = charge + "%";
			}
		}
		setText(text);
    	CustomTextDrawable.draw(dc);
    }
}
