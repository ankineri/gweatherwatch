using Toybox.System as Sys;
using Toybox.WatchUi as Ui;
using Toybox.Graphics;
class FcastRectsDrawable extends PositionableDrawable {
	protected var font;
	protected var values;
	protected var hWidth, tWidth;
	function initialize(param) {
		PositionableDrawable.initialize(param);
		self.font = param.get(:font);
		self.hWidth = param.get(:hwidth);
		self.tWidth = param.get(:twidth);
	}
	
	function makeCell(entry, offset) {
		var time = entry["dt"] + offset;
		time = time / 60 / 60;
		time = time % 24;
		var temp = entry["temp"];
		return [time + "", temp, self.cellColor(entry["symbol"])];
	}
	
	function cellColor(symbol) {
		switch (symbol) {
			case "":
				return [Graphics.COLOR_WHITE, Graphics.COLOR_BLACK];
			case "!":
				return [Graphics.COLOR_WHITE, Graphics.COLOR_DK_BLUE];
			case "*":
				return [Graphics.COLOR_BLACK, Graphics.COLOR_WHITE];
			case "*!":
			case "!*":
				return [Graphics.COLOR_WHITE, Graphics.COLOR_PURPLE];
		}
		return [Graphics.COLOR_WHITE, Graphics.COLOR_BLACK];
	}
	
	function drawCell(cell, posX, posY, dc) {
		var curX = posX;
		var hourSize = dc.getTextDimensions(cell[0], self.font);
		var tempSize = dc.getTextDimensions(cell[1], self.font);
		
		var height = hourSize[1];
		if (height < tempSize[1]) {
			height = tempSize[1];
		}
		
		height += 2;
		
		var fullWidth = self.hWidth + self.tWidth;
		
		dc.setColor(Graphics.COLOR_DK_GRAY, cell[2][1]);
		
		dc.fillRectangle(posX, posY, self.hWidth + 1, height);
		
		dc.setColor(cell[2][1], cell[2][1]);
		
		dc.fillRectangle(posX + self.hWidth + 1, posY, self.tWidth - 1, height);
				
		dc.setColor(Graphics.COLOR_WHITE, Graphics.COLOR_TRANSPARENT);
		
		dc.drawText(posX + self.hWidth / 2 + 1, posY, self.font, cell[0], Graphics.TEXT_JUSTIFY_CENTER);
		curX = posX + self.hWidth + 1;
		dc.setColor(Graphics.COLOR_YELLOW, Graphics.COLOR_TRANSPARENT);
		dc.setPenWidth(1);
		dc.drawLine(curX, posY, curX, posY + height);

		dc.setColor(cell[2][0], Graphics.COLOR_TRANSPARENT);
		
		dc.drawText(posX + self.hWidth + self.tWidth / 2, posY, self.font, cell[1], Graphics.TEXT_JUSTIFY_CENTER);
		dc.setColor(Graphics.COLOR_GREEN, Graphics.COLOR_TRANSPARENT);
		dc.setPenWidth(2);
		dc.drawRectangle(posX, posY, fullWidth, height);
		return posX + fullWidth;
	}
	
	function drawAll(dc) {
		var posX = self.locX;
		for (var i = 0; i < values.size(); ++i) {
			posX = drawCell(values[i], posX, locY, dc);
		}
	}
	
	function prepareValues() {
		self.values = new [0];
		var now = Sys.getClockTime();
		var off = now.timeZoneOffset;		
		var weather = LayoutContextExt.getWeather();
		if (weather == null || !"true".equals(weather["fcast"]["success"])) {
			return;
		}
		self.values = new [3];
		var fcast = weather["fcast"]["data"];
		
		for (var i = 0; i < fcast.size(); ++i) {
			values[i] = makeCell(fcast[i], off);
		}
	}
	
	function draw(dc) {
		self.prepareValues();
		self.drawAll(dc);
	}


}