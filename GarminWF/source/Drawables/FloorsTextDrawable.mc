using Toybox.WatchUi as Ui;
using Toybox.System as Sys;
using Toybox.Lang as Lang;
using Toybox.Time;

class FloorsTextDrawable extends CustomTextDrawable {
	
    function initialize(params) {
    	CustomTextDrawable.initialize(params);
    	setText("?");
    }
    
    function draw(dc) {
		var activity = ActivityMonitor.getInfo();
		if (activity == null) {
			return;
		}
		setText(activity.floorsClimbed + "\n" + activity.floorsClimbedGoal);
    	CustomTextDrawable.draw(dc);
    }
}
