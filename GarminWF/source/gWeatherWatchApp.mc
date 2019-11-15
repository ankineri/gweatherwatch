using Toybox.Background;
using Toybox.Time;
using Toybox.Application;
using Toybox.WatchUi;

class gWeatherWatchApp extends Application.AppBase {

    function initialize() {
        AppBase.initialize();
    }
    // onStart() is called on application start up
    function onStart(state) {

    }

    // onStop() is called when your application is exiting
    function onStop(state) {
    }

    // Return the initial view of your application here
    function getInitialView() {
    	InitBackgroundEvents();
        return [ new gWeatherWatchView() ];
    }

    // New app settings have been received so trigger a UI update
    function onSettingsChanged() {
        WatchUi.requestUpdate();
    }
    
    function getServiceDelegate()
    {
        return [new BackgroundServiceDelegate()];
    }
    
    function InitBackgroundEvents()
    {
    	var HOUR = new Toybox.Time.Duration(60 * 60);
		var lastTime = null; 
		if (lastTime == null) {
			var fromPersist = Persistent.Load(PersistKeys.LastBackgroundEvent);
			if (fromPersist != null) {
				lastTime = new Time.Moment(fromPersist);
			}
		}
		
		var scheduleAt = Time.now();
		if (lastTime != null) {
			var wantedAt = lastTime.add(HOUR);
			if (wantedAt.value() > scheduleAt.value()) {
				scheduleAt = wantedAt;
			}
		}
		
		var lastBkgnd = Background.getLastTemporalEventTime();
		if (lastBkgnd != null) {
			var allowedAt = lastBkgnd.add(new Toybox.Time.Duration(5 * 60));
			if (allowedAt.value() > scheduleAt.value()) {
				scheduleAt = allowedAt;
			}
		} 
		
		System.println("Last time weather was requested: " + (lastTime == null ? "NULL" : lastTime.value()));
		System.println("Requesting weather update at " + scheduleAt.value());
		Background.registerForTemporalEvent(scheduleAt);
    }
    
    function onBackgroundData(data) 
    {
    	//System.println("Have bckgnd data: " + data);
    	
    	if (data["loc"] != null) {
    		Persistent.Save(PersistKeys.LastPhoneLocation, data["loc"]);
    	}
        if (data["weather"] != null && "true".equals(data["weather"]["success"]))
        {
        	//System.println("UI got weather: " + data["weather"]);
        	Background.registerForTemporalEvent(new Toybox.Time.Duration(60 * 60));
        	Persistent.Save(PersistKeys.LastBackgroundEvent, Time.now().value());
        	Persistent.Save(PersistKeys.Weather, data["weather"]);
        	//System.println("Saved bckgnd data");
        	WatchUi.requestUpdate();
        } else {
        	// Request an update sooner if there is no weather data
        	System.println("Got no weather - scheduling in 10 minutes");
        	Background.registerForTemporalEvent(new Toybox.Time.Duration(10 * 60));
        }
    }    
}