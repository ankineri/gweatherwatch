using Toybox.WatchUi as Ui;
using Toybox.System as Sys;

class CurCityDrawable extends CustomTextDrawable {
	protected var phoneLocColor;

    function initialize(params) {
    	CustomTextDrawable.initialize(params);
    	if (params.hasKey(:phoneLocColor)) {
    		phoneLocColor = params.get(:phoneLocColor);
    	}
    	setText("?");
    }

    function draw(dc) {
    	var weather = LayoutContextExt.getWeather();
    	if (weather != null) {
    		setText(weather["city"]);
    		setColor(self.color);
    		if (phoneLocColor != null) {
	    		var phoneLoc = Persistent.Load(PersistKeys.LastPhoneLocation);
	    		if (phoneLoc != null) {
	    			var when = phoneLoc[2];
	    			if (Time.now().value() - when <= 60*60*4) {
	    				setColor(self.phoneLocColor);
	    			}
	    		}
    		}
    	}
    	CustomTextDrawable.draw(dc);
    }
}
