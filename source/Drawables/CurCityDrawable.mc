using Toybox.WatchUi as Ui;
using Toybox.System as Sys;

class CurCityDrawable extends CustomTextDrawable {
	
    function initialize(params) {
    	CustomTextDrawable.initialize(params);
    	setText("?");
    }

    function draw(dc) {
    	var weather = LayoutContextExt.getWeather();
    	if (weather != null) {
    		setText(weather["city"]);
    	}
    	CustomTextDrawable.draw(dc);
    }
}
