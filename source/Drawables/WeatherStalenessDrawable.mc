using Toybox.WatchUi as Ui;
using Toybox.System as Sys;
using Toybox.Time;
class WeatherStalenessDrawable extends CustomTextDrawable {
	private var shouldColor = false;
    function initialize(params) {
    	CustomTextDrawable.initialize(params);
    	if (params.hasKey(:colorback)) {
    		shouldColor = true;
    		setBackgroundColor(0x0);
    	}
    	setText("?");
    }

	function setBckgnd(minutes) {
		var color = 0x0;
		if (minutes <= 60) {
			color = 0x00AA00;
		} else if (minutes < 120) {
			color = 0xAAAA00;
		} else {
			color = 0xAA0000;
		}
		setBackgroundColor(color);
	}
		

	function makeFcastLine(weather) {
		var rv = "";
		var now = Sys.getClockTime();
		var off = now.timeZoneOffset;
		for (var i = 0; i < weather["fcast"]["data"].size(); ++i) {
			var entry = weather["fcast"]["data"][i];
			var time = entry["dt"] + off;
			time = time / 60 / 60;
			time = time % 24;
			var temp = entry["temp"];
			rv += time + ": " + temp + entry["symbol"] + ", ";
		}
		return rv.substring(0, rv.length() - 2);
	}

    function draw(dc) {
    	var weather = Persistent.Load(PersistKeys.Weather);
    	if (weather != null) {
    		var when = weather["weather"]["dt"];
    		var min = Time.now().value() - when;
    		min /= 60;
    		if (shouldColor) {
    			setBckgnd(min);
    		}
    		var label = min + "m";
    		if (min > 99) {
    			label = (min / 60) + "h";
    		}
    		setText(label);
    	}
    	CustomTextDrawable.draw(dc);
    }
}
