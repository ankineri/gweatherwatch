using Toybox.Activity;
using Toybox.Position;
using Toybox.System as Sys;
using Toybox.Application as App;
(:background)
class LocationProvider {
	private var lastLat, lastLon;
	public function getLocation() {
		var activityLocation = Activity.getActivityInfo().currentLocation;
		if (activityLocation == null) {
			Sys.println("Returning default location!");
			return [50.063506, 14.445937];
		}
		Sys.println("Got location: " + activityLocation);
		return activityLocation.toDegrees();
	}
}