using Toybox.WatchUi as Ui;
using Toybox.System as Sys;
using Toybox.Graphics;

class CurrentIconDrawable extends PositionableDrawable {
	protected var cloudyBmp, partlyBmp, rainBmp, snowBmp, sunnyBmp, thunderBmp, unknownBmp;
    function initialize(params) {
    	PositionableDrawable.initialize(params);
		self.cloudyBmp = Ui.loadResource(Rez.Drawables.cloudy);
		self.partlyBmp = Ui.loadResource(Rez.Drawables.partly);
		self.rainBmp = Ui.loadResource(Rez.Drawables.rain);
		self.snowBmp = Ui.loadResource(Rez.Drawables.snow);
		self.sunnyBmp = Ui.loadResource(Rez.Drawables.sunny);
		self.thunderBmp = Ui.loadResource(Rez.Drawables.thunder);
		self.unknownBmp = Ui.loadResource(Rez.Drawables.unknown);
    }
    
    function getBmpByConditions(cond) {
    	switch (cond) {
	    	case "clear":
			    return self.sunnyBmp;    
			case "1clouds":
			    return self.partlyBmp;    
			case "2clouds":
			    return self.cloudyBmp;    
			case "3clouds":
			    return self.cloudyBmp;    
			case "shower":
			    return self.rainBmp;    
			case "rain":
			    return self.rainBmp;    
			case "thunderstorm":
			    return self.thunderBmp;    
			case "snow":
			    return self.snowBmp;    
			case "mist":
			    return self.partlyBmp;
			default:
			    return self.unknownBmp;
		}
	}

    function draw(dc) {
    	var weather = LayoutContextExt.getWeather();
    	var conds = "unk";
    	if (weather != null) {
	    	conds = weather["conditions"];
	    }
		dc.drawBitmap(locX, locY, self.getBmpByConditions(conds));
    }
}
