using Toybox.System as Sys;
class LayoutContext {
	public var data;
	function initialize() {
		self.reset();
	}
	
	function reset() {
		self.data = {};
	}
	
	static function get(key) {
		var instance = getInstance();
		if (!instance.data.hasKey(key)) {
			return null;
		}
		return getInstance().data[key];
	}
	
	static function set(key, value) {
		var instance = getInstance();
		//Sys.println(instance.data);
		instance.data.put(key, value);
	}
	
	static var _instance = null;
	static function getInstance() {
		if (_instance == null) {
			_instance = new LayoutContext();
		}
		return _instance;
	}
} 