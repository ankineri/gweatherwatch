using Toybox.WatchUi as Ui;
using Toybox.System as Sys;
using Toybox.Sensor;

class HeartRateDrawable extends CustomTextDrawable {
	
    function initialize(params) {
    	CustomTextDrawable.initialize(params);
    	setText("?");
    }
    
    function getIterator() {
	    // Check device for SensorHistory compatibility
	    if ((Toybox has :SensorHistory) && (Toybox.SensorHistory has :getHeartRateHistory)) {
	        return Toybox.SensorHistory.getHeartRateHistory({});
	    }
	    return null;
	}

    function draw(dc) {
		var sensorIter = getIterator();
		
		if (sensorIter != null) {
			var val = null;
			while (val == null) {
				var x = sensorIter.next();
				if (x == null) {
					break;
				}
				val = x.data;
			}
			if (val == null) {
				setText("N/A"); 
			} else {
		    	setText(val + "");
		    }
		}
		
    	CustomTextDrawable.draw(dc);
    }
}
