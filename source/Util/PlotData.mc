using Toybox.Math;
class PlotData {
	private var buffer, asArray, valid, min, max;
	function initialize(size) {
		self.buffer = new CircularBuffer(size);
		self.asArray = [];
		self.valid = true;
	}
	function add(val) {
		self.buffer.push(val);
		self.valid = false;
	}
	
	function reloadIfNeeded() {
		if (!self.valid) {
			self.asArray = self.buffer.asArray();
			if (self.asArray.size()) {
				min = null;
				max = null;
				for (var i = 0; i < self.asArray.size(); ++i) {
					if (self.asArray[i] == null) {
						continue;
					}
					if (min == null || self.asArray[i] < min) {
						min = self.asArray[i];
					}
					if (max == null || self.asArray[i] > max) {
						max = self.asArray[i];
					}
				}
			}
			self.valid = true;
		}
	}					
	
	function getAll() {
		self.reloadIfNeeded();
		return self.asArray;
	}
	
	function getMin() {
		self.reloadIfNeeded();
		return self.min;
	}
	function getMax() {
		self.reloadIfNeeded();
		return self.max;
	}
	function capacity() {
		return self.buffer.capacity();
	}
}