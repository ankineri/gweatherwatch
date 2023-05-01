using Toybox.WatchUi as Ui;
using Toybox.System as Sys;
using Toybox.Graphics;

class BatteryImageDrawable extends PositionableDrawable {
	protected var bmps = new [4];
	protected var xalign;
    function initialize(params) {
    	PositionableDrawable.initialize(params);
		
		self.bmps[0] = Ui.loadResource(Rez.Drawables.b0);
		self.bmps[1] = Ui.loadResource(Rez.Drawables.b1);
		self.bmps[2] = Ui.loadResource(Rez.Drawables.b2);
		self.bmps[3] = Ui.loadResource(Rez.Drawables.b3);
		
		if (params.hasKey(:xalign)) {
			self.xalign = params.get(:xalign);
		}
		if ("center".equals(self.xalign)) {
			locX -= width / 2;
			// locX -= self.getWidth() / 2;
		}
    }
    
    function chargeToBmp(chg) {
    	if (chg >= 75) {
    		return 3;
    	}
    	if (chg >= 50) {
    		return 2;
    	}
    	if (chg >= 25) {
    		return 1;
    	}
    	return 0;
    }

    function draw(dc) {
    
    	var charge = Sys.getSystemStats().battery;
    	var bmpIdx = self.chargeToBmp(charge);
    
		dc.drawBitmap(locX, locY, self.bmps[bmpIdx]);
    }
}
