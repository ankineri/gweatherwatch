using Toybox.Application as App;

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
		App.getApp().setProperty(key, value);
	}
	public static function Load(key) {
		try {
			return App.getApp().getProperty(key);
		} catch (ex) {
			return null;
		}
	}
}