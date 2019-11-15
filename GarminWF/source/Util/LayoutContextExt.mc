class LayoutContextExt {
	static function loadAndSetWeather() {
		var weather = Persistent.Load(PersistKeys.Weather);
    	if (weather != null) {
    		LayoutContext.set("WEATHER", weather["weather"]);
    		return weather["weather"];
    	}
    	return null;
    }
    
    static function getWeather() {
    	var rv = LayoutContext.get("WEATHER");
    	rv = rv != null ? rv : self.loadAndSetWeather();
		return rv;
	}
	
	static function reset() {
		LayoutContext.getInstance().reset();
	}
}
    	
    