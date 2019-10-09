using Toybox.WatchUi as Ui;
using Toybox.System as Sys;
using Toybox.Sensor;

class PressureDrawable extends CustomTextDrawable {
	
    function initialize(params) {
    	CustomTextDrawable.initialize(params);
    	setText("?");
    }
    
    function getIterator() {
	    // Check device for SensorHistory compatibility
	    if ((Toybox has :SensorHistory) && (Toybox.SensorHistory has :getPressureHistory)) {
	    	var param = {};
	    	param.put(:period, 1);
	        return Toybox.SensorHistory.getPressureHistory(param);
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
				val = x.data / 133.322;
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
