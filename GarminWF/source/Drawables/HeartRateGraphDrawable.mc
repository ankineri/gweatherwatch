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
	    	var param = {};
	    	param.put(:period, self.width);
	        return Toybox.SensorHistory.getHeartRateHistory(param);
	    }
	    return null;
	}
	
	function updateData() {
		var iter = self.getIterator();
		var newLast = null;
		var tmpAr = new [self.plotData.capacity()];
		var count = 0;
		if (iter != null) {
			var piece = null;
			while (count < tmpAr.size()) {
				piece = iter.next();
				
				if (piece == null || (self.lastMoment != null && self.lastMoment.value == piece.when.value)) {
					//Sys.println("Terminating search: reached end or known");
					break;
				}
				if (newLast == null) {
					newLast = piece.when;
				}
				tmpAr[count] = piece.data;
				count++;
			}
			//Sys.println("Found " + count + " elements out of " + tmpAr.size());
			if (newLast != null) {
				self.lastMoment = newLast;
			}
			for (var i = count - 1; i >= 0; --i) {
				self.addEntry(tmpAr[i]);
			}
		}
	}
				

    function draw(dc) {
    	updateData();
    	PlotDrawable.draw(dc);
    	LayoutContext.set("HRMIN", self.min);
    	LayoutContext.set("HRMAX", self.max);    	
    }
}
