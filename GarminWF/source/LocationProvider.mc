using Toybox.Activity;
using Toybox.Position;
using Toybox.System as Sys;
using Toybox.Application as App;
using Toybox.Time;

(:background)
class LocationProvider {
	private var lastLat, lastLon;
	public function getLocation() {
		var activityInfo = Activity.getActivityInfo(); 
		var activityLocation = activityInfo.currentLocation;
		var phoneLocation = Persistent.Load(PersistKeys.LastPhoneLocation);
		if (phoneLocation == null && activityLocation == null) {
			Sys.println("Returning default location!");
			return [50.063506, 14.445937];
		}
		
		
		var actAt = 0;
		if (activityInfo.startTime != null) {
			actAt = activityInfo.startTime.value();
		}
		
		var phoneAt = 0;
		if (phoneLocation != null) {
			phoneAt = phoneLocation[2];
		} 
		
		if (phoneLocation == null || phoneAt < actAt) {
			Sys.println("Returning activity location: " + activityLocation);
			return activityLocation.toDegrees();
		}
		
		if (activityLocation == null || phoneAt >= actAt) {
			Sys.println("Returning phone location: " + phoneLocation);
			return [phoneLocation[0], phoneLocation[1]];
		}
			
	}
}