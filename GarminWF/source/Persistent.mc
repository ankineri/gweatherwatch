using Toybox.Application as App;
using Toybox.Application.Storage as Storage;

(:background)
class PersistKeys {
	public static const Weather = 1;
	public static const Location = 2;
	public static const LastBackgroundEvent = 3;
	public static const LastPhoneLocation = 4;
}

(:background)
class Persistent {
	public static function Save(key, value) {
		Storage.setValue(key, value);
		// App.getApp().setProperty(key, value);
	}
	public static function Load(key) {
		try {
			return Storage.getValue(key);
			// return App.getApp().getProperty(key);
		} catch (ex) {
			return null;
		}
	}
}