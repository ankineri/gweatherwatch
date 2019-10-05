using Toybox.System as Sys;

class CircularBuffer {
 	private var data;
 	private var front, back, _size;
 	private var min, max;
 	function initialize(bufsize) {
 		front = 0;
 		back = 0;
 		_size = 0;
 		data = new [bufsize];
 	}
 	
 	function normalize() {
 		if (front == data.size()) {
 			front = 0;
 		}
 		if (back == data.size()) {
 			back = 0;
 		}
 	}
 	
 	function pop() {
 		if (_size == 0) {
 			return null;
 		}
 		var old = data[front];
 		++front;
 		--_size;
 		normalize();
 		return old;
 	}
 	
 	function capacity() {
 		return data.size();
 	}
 	
 	function top() {
 		if (_size == 0) {
 			return null;
 		}
 		return data[front];
 	}
 	
 	function size() {
 		return _size;
 	}
 	
 	function push(val) {
 		if (_size == data.size()) {
 			pop();
 		}
 		data[back] = val;
 		++back;
 		++_size;
 		normalize();
 	}
 	function asArray() {
 		var rv = new [_size];
 		var idx = 0;
 		for (var i = front; idx != _size; ++i) {
 			if (i == data.size()) {
 				i = 0;
 			}
 			rv[idx] = data[i];
 			++idx;
 		}
 		return rv;
 	}
 }