using Toybox.WatchUi as Ui;
using Toybox.System as Sys;
using Toybox.Graphics;

class StaticIconDrawable extends PositionableDrawable {
	protected var bmp;
	protected var xalign;
    function initialize(params) {
    	PositionableDrawable.initialize(params);
		self.bmp = Ui.loadResource(params.get(:resource));
		if (params.hasKey(:xalign)) {
			self.xalign = params.get(:xalign);
		}
		if ("center".equals(self.xalign)) {
			locX -= self.bmp.getWidth() / 2;
		}
    }

    function draw(dc) {
		dc.drawBitmap(locX, locY, self.bmp);
    }
}
