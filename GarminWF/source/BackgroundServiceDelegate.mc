using Toybox.Background;
using Toybox.System as Sys;
using Toybox.Communications as Comm;
using Toybox.Application as App;
using Toybox.Time;

// The Service Delegate is the main entry point for background processes
// our onTemporalEvent() method will get run each time our periodic event
// is triggered by the system.
//
(:background)
class BackgroundServiceDelegate extends Sys.ServiceDelegate 
{
	hidden var _weatherInfo;
	hidden var _syncCounter = 0;
	hidden var _location;
	private var locationProvider;
	 
	function initialize() 
	{
		Sys.ServiceDelegate.initialize();
		locationProvider = new LocationProvider();
	}
	
	function loadGoogleData(loc) {
		var url = Lang.format("https://script.google.com/macros/s/AKfycbz8bWDUyTwvfKGar7P2YPEXznjC2G9vGo1XFQXyj46ocF_qGbQv/exec?lat=$1$&lon=$2$&system=metric", [
			loc[0],
			loc[1]]);  
			
		//Sys.println(" :: weather request " + url);

        var options = {
          :method => Comm.HTTP_REQUEST_METHOD_GET,
          :responseType => Comm.HTTP_RESPONSE_CONTENT_TYPE_JSON
        };

		_syncCounter = _syncCounter + 1;
    	Comm.makeWebRequest(url, {}, options, method(:OnReceiveWeather));
	
	}
	
    function onTemporalEvent() 
    {
    	//System.println("Have temporal event!");
    	var loc = locationProvider.getLocation();
    	/*var loc = Persistent.Load(PersistKeys.Location);
    	if (loc == null) {
    		loc = [55.676470, 37.445434];
    	}*/
    	loadGoogleData(loc);
    }
    
    function OnReceiveWeather(responseCode, data) {
    	//Sys.println("Have weather! Code: " + responseCode);
    	try
		{
			if (responseCode == 200)
			{
				Background.exit(data);
			}
		}
		catch(ex)
		{
			Sys.println("get weather error : " + ex.getErrorMessage());
		}
		
		Background.exit(null);
    }
}