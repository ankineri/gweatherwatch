using Toybox.WatchUi as Ui;
using Toybox.System as Sys;

class HeartRateRangeDrawable extends CustomTextDrawable {
	
    function initialize(params) {
    	CustomTextDrawable.initialize(params);
    	setText("?");
    }
    

    function draw(dc) {
    
    	var min = LayoutContext.get("HRMIN");
    	var max = LayoutContext.get("HRMAX");
    	if (min == null || max == null) {
    		min = "?";
    		max = "?";
    	}
    	setText(min + "-" + max);
		
    	CustomTextDrawable.draw(dc);
    }
}
