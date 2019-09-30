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
				min = self.asArray[0];
				max = self.asArray[0];
				for (var i = 0; i < self.asArray.size(); ++i) {
					if (self.asArray[i] < min) {
						min = self.asArray[i];
					}
					if (self.asArray[i] > max) {
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
}