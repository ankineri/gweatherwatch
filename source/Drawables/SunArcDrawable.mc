using Toybox.WatchUi as Ui;
using Toybox.System as Sys;
using Toybox.Time.Gregorian;
using Toybox.Time;

class SunArcDrawable extends CustomArcDrawable {
	function setSunData(data) {
		var sunrise = new Time.Moment(data["sunrise"]);
		var sunset = new Time.Moment(data["sunset"]);
		var low = self.minutesInDay(sunrise);
		var high = self.minutesInDay(sunset);
		setFront(low, high);
	} 
	function loadSunData() {
		var weather = Persistent.Load(PersistKeys.Weather);
    	if (weather != null) {
    		setSunData(weather["weather"]);
    	}
	}
	function initialize(params) {
		CustomArcDrawable.initialize(params);
		self.min = 0;
		self.max = 24 * 60;
	}
	function minutesInDay(moment) {
		var info = Gregorian.info(moment, Toybox.Time.FORMAT_SHORT);
		var intoDay = info.hour * 60 + info.min;
		return intoDay;
	}
	function updateValue() {
		var intoDay = minutesInDay(Time.now());
		CustomArcDrawable.setValue(intoDay);
	}
	
	function draw(dc) {
		updateValue();
		loadSunData();
		CustomArcDrawable.draw(dc);
	}
}