using Toybox.Communications;
using Toybox.System as Sys;
using Toybox.Time;

(:background)
class PhoneConnectivity {
	var lastLat, lastLng, when;
	class Listener extends Communications.ConnectionListener {
		function onComplete() {
		}
		
		function onError() {
		}
	}

	function phoneMessageCallback(msg) {
		Sys.println("Have message: " + msg.data);
		if (msg != null && msg.data != null) {
			lastLat = msg.data[0];
			lastLng = msg.data[1];
			when = Time.now();
		}
		//StringUtil.spl
	   	//message = msg.data;
	}
	
	function request() {
		Communications.transmit("GIMME LOC", {}, new Listener());
	}
	
	function init() {
		Communications.registerForPhoneAppMessages(method(:phoneMessageCallback));
	}
}
	