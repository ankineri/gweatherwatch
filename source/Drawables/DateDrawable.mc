using Toybox.WatchUi as Ui;
using Toybox.System as Sys;
using Toybox.Lang as Lang;
using Toybox.Time;
using Time.Gregorian;
class DateDrawable extends CustomTextDrawable {
	
    function initialize(params) {
    	CustomTextDrawable.initialize(params);
    	setText("?");
    }
    
    function dow(value) {
    	switch (value) {
    	case 1:
    		return "Sun";
    	case 2:
    		return "Mon";
    	case 3:
    		return "Tue";
    	case 4:
    		return "Wed";
    	case 5:
    		return "Thu";
    	case 6:
    		return "Fri";
    	case 7:
    		return "Sat";
    	}
    	return "Wtf";
    }

    function draw(dc) {
	    var date = Gregorian.info(Time.now(), Time.FORMAT_SHORT);
		var str = Lang.format("$1$.$2$.$3$ $4$", [
			date.day.format("%02d"),
			date.month.format("%02d"),
			date.year,
			dow(date.day_of_week)
		]);
		setText(str);
    	CustomTextDrawable.draw(dc);
    }
}
