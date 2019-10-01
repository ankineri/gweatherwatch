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
		var lastTime = Background.getLastTemporalEventTime();
		if (lastTime != null) 
		{
    		var nextTime = lastTime.add(HOUR);
    		Background.registerForTemporalEvent(nextTime);
		} 
		else 
		{
    		Background.registerForTemporalEvent(Time.now());
		}
    }
    
    function onBackgroundData(data) 
    {
    	//System.println("Have bckgnd data: " + data);
    	Background.registerForTemporalEvent(new Toybox.Time.Duration(60 * 60));
        if (data != null)
        {
        	Persistent.Save(PersistKeys.Weather, data);
        	//System.println("Saved bckgnd data");
        	WatchUi.requestUpdate();
        }
    }    
}