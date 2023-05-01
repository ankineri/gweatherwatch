using Toybox.WatchUi as Ui;
using Toybox.System as Sys;

class CustomTextDrawable extends Ui.Text {

    //protected var myText, locX, locY;
    protected var maxLen, color;
	protected var maxWidth;
	protected var maxWidthSatFor;
	protected var font, text;
    function initialize(params) {
        Text.initialize(params);
    	Text.setText("?");
    	var x = 0, y = 0;
    	//Sys.println(params);
    	var sett = Sys.getDeviceSettings();
    	if ("center".equals(params.get(:x))) {
    		x = sett.screenWidth / 2;
    	} else {
    		x = params.get(:x);
    	}
    	if ("center".equals(params.get(:y))) {
    		y = sett.screenHeight / 2;
    	} else {
    		y = params.get(:y);
    	}
    	setLocation(x, y);
    	
    	if (params.hasKey(:justification)) {
    		setJustification(params.get(:justification));
    	}
		if (params.get(:customFont)) {
			var fontId = params.get(:font);
			self.font = Ui.loadResource(fontId);
		} else {
			self.font = params.get(:font);
		}
		setFont(self.font);
    	if (params.hasKey(:color)) {
    		self.color = params.get(:color);
    		setColor(self.color);
    	}
    	if (params.hasKey(:maxlen)) {
    		self.maxLen = params.get(:maxlen);
    	}
    	if (params.hasKey(:maxwidth)) {
    		self.maxWidth = params.get(:maxwidth);
    		self.maxWidthSatFor = "";
    	}
    }
    
    function setText(txt) {
    	if (self.maxLen != null) {
    		txt = txt.substring(0, self.maxLen);
    	}
    	Text.setText(txt);
    	self.text = txt;
    }
    
    function isMaxWidthSatisfied() {
    	if (self.maxWidth == null) {
    		return true;
    	}
    	if (self.maxWidthSatFor == null) {
    		return false;
    	}
    	return self.text.substring(0, self.maxWidthSatFor.length()) == self.maxWidthSatFor;
    }

    function draw(dc) {
    	if (!isMaxWidthSatisfied()) {
    		while (self.text.length() > 0 && dc.getTextWidthInPixels(self.text, self.font) > self.maxWidth) {
    			self.text = self.text.substring(0, self.text.length() - 1);
    		}
    		self.maxWidthSatFor = self.text;
    		Text.setText(self.text);
    	}
    	Text.draw(dc);
    }
}
