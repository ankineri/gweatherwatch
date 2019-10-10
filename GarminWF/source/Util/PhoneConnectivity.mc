using Toybox.Communications;
using Toybox.System as Sys;

(:background)
class PhoneConnectivity {

	class Listener extends Communications.ConnectionListener {
		function onComplete() {
		}
		
		function onError() {
		}
	}

	function phoneMessageCallback(msg) {
		Sys.println("Have messeg: " + msg.data);
	   	//message = msg.data;
	}
	
	function request() {
		Communications.transmit("GIMME LOC", {}, new Listener());
	}
	
	function init() {
		Communications.registerForPhoneAppMessages(method(:phoneMessageCallback));
	}
}
	