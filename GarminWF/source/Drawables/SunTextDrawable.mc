using Toybox.WatchUi as Ui;
using Toybox.System as Sys;
using Toybox.Time;
using Toybox.Time.Gregorian;

class SunTextDrawable extends CustomTextDrawable {
	protected var sunrise = false;
    function initialize(params) {
    	CustomTextDrawable.initialize(params);
    	if (params.hasKey(:sunrise)) {
    		sunrise = params.get(:sunrise);
    	}
    	setText("?");
    }
    
    function getIterator() {
	    // Check device for SensorHistory compatibility
	    if ((Toybox has :SensorHistory) && (Toybox.SensorHistory has :getHeartRateHistory)) {
	        return Toybox.SensorHistory.getHeartRateHistory({});
	    }
	    return null;
	}
	
	function setVal(val) {
		var info = Gregorian.info(new Time.Moment(val), Toybox.Time.FORMAT_SHORT);
		var str = info.hour.format("%02d") + ":" + info.min.format("%02d");
		self.setText(str);
	}

    function draw(dc) {
    	var weather = Persistent.Load(PersistKeys.Weather);
    	if (weather != null) {
    		var data = weather["weather"];
    		setVal(sunrise ? data["sunrise"] : data["sunset"]);
    	}
    	CustomTextDrawable.draw(dc);
    }
}
