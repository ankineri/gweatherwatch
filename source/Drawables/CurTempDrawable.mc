using Toybox.WatchUi as Ui;
using Toybox.System as Sys;

class CurTempDrawable extends CustomTextDrawable {
	
    function initialize(params) {
    	CustomTextDrawable.initialize(params);
    	setText("?");
    }

    function draw(dc) {
    	//Sys.println("Drawing temperature");
    	var weather = Persistent.Load(PersistKeys.Weather);
    	if (weather != null) {
    		setText(weather["weather"]["temp"]);
    		//Sys.println("Have temp: " + weather["weather"]["temp"]);
    	}
    	CustomTextDrawable.draw(dc);
    }
}
