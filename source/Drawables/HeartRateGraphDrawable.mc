using Toybox.WatchUi as Ui;
using Toybox.System as Sys;
//using Toybox.SensorHistory;

class HeartRateGraphDrawable extends PlotDrawable {
	
	protected var lastMoment;
	
    function initialize(params) {
    	PlotDrawable.initialize(params);
    }
    
    function getIterator() {
	    // Check device for SensorHistory compatibility
	    if ((Toybox has :SensorHistory) && (Toybox.SensorHistory has :getHeartRateHistory)) {
	        return Toybox.SensorHistory.getHeartRateHistory({});
	    }
	    return null;
	}
	
	function updateData() {
		var iter = self.getIterator();
		var newLast = null;
		if (iter != null) {
			var piece = null;
			while (true) {
				var piece = iter.next();
				if (piece == null || self.lastMoment == piece.when) {
					break;
				}
				if (newLast == null) {
					newLast = piece.when;
				}
				self.addEntry(piece.data);
			}
			self.lastMoment = newLast;
		}
	}
				

    function draw(dc) {
    	updateData();
    	PlotDrawable.draw(dc);
    }
}
