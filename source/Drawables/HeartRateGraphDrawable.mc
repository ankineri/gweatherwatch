/*using Toybox.WatchUi as Ui;
using Toybox.System as Sys;
using Toybox.Sensor;

class HeartRateGraphDrawable extends Drawable {
	
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
		
		// Print out the next entry in the iterator
		if (sensorIter != null) {
			while (true) {
				var piece = sensorIter.next();
				if (piece == null) {
					break;
				}
				System.println(piece);
			}
		}
    	CustomTextDrawable.draw(dc);
    }
}
*/