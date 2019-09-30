using Toybox.WatchUi as Ui;
using Toybox.System as Sys;

class CustomTextDrawable extends Ui.Text {

    //protected var myText, locX, locY;
    protected var maxLen;
	
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
    	if (params.hasKey(:font)) {
    		setFont(params.get(:font));
    	}
    	if (params.hasKey(:color)) {
    		setColor(params.get(:color));
    	}
    	if (params.hasKey(:maxlen)) {
    		self.maxLen = params.get(:maxlen);
    	}
    }
    
    function setText(text) {
    	if (self.maxLen != null) {
    		text = text.substring(0, self.maxLen);
    	}
    	Text.setText(text);
    }

    function draw(dc) {
    	Text.draw(dc);
    }
}
