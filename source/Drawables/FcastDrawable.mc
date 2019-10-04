using Toybox.WatchUi as Ui;
using Toybox.System as Sys;

class FcastDrawable extends CustomTextDrawable {
	
    function initialize(params) {
    	CustomTextDrawable.initialize(params);
    	setText("?");
    }

	function makeFcastLine(weather) {
		var rv = "";
		var now = Sys.getClockTime();
		var off = now.timeZoneOffset;
		//Sys.println(weather);
		if ("false".equals(weather["fcast"]["success"])) {
			return "error";
		}
			
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
    		setText(makeFcastLine(weather["weather"]));
    	}
    	CustomTextDrawable.draw(dc);
    }
}
