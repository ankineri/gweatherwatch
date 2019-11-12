using Toybox.Math;
class PlotData {
	private var buffer, asArrayBuf, valid, min, max;
	function initialize(size) {
		self.buffer = new CircularBuffer(size);
		self.asArrayBuf = [];
		self.valid = true;
	}
	function add(val) {
		self.buffer.push(val);
		self.valid = false;
	}
	
	function reloadIfNeeded() {
		if (!self.valid) {
			self.asArrayBuf = self.buffer.asArray();
			if (self.asArrayBuf.size()) {
				min = null;
				max = null;
				for (var i = 0; i < self.asArrayBuf.size(); ++i) {
					if (self.asArrayBuf[i] == null) {
						continue;
					}
					if (min == null || self.asArrayBuf[i] < min) {
						min = self.asArrayBuf[i];
					}
					if (max == null || self.asArrayBuf[i] > max) {
						max = self.asArrayBuf[i];
					}
				}
			}
			self.valid = true;
		}
	}					
	
	function getAll() {
		self.reloadIfNeeded();
		return self.asArrayBuf;
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