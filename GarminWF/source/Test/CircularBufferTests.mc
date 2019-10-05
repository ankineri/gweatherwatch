using Toybox.Test;
(:test)
function testEmpty(logger) {
	var buf = new CircularBuffer(1);
	Test.assert(buf.top() == null);
	Test.assert(buf.pop() == null);
	return true;
}

(:test)
function testNormalFlow(logger) {
	var buf = new CircularBuffer(3);
	buf.push(7);
	Test.assert(buf.top() == 7);
	Test.assert(buf.size() == 1);
	Test.assert(buf.asArray()[0] == 7);
	buf.push(42);
	
	var ar = buf.asArray();
	Test.assert(ar.size() == 2);
	Test.assert(ar[0] == 7);
	Test.assert(ar[1] == 42);
	
	Test.assert(buf.top() == 7);
	Test.assert(buf.pop() == 7);
	Test.assert(buf.top() == 42);

	buf.push(777);
	buf.push(13);
	ar = buf.asArray();
	Test.assert(ar.size() == 3);
	Test.assert(ar[0] == 42);
	Test.assert(ar[1] == 777);
	Test.assert(ar[2] == 13);	
	Test.assert(buf.size() == 3);
	buf.push(12);
	
	ar = buf.asArray();
	Test.assert(ar.size() == 3);
	Test.assert(ar[0] == 777);
	Test.assert(ar[1] == 13);
	Test.assert(ar[2] == 12);	
	
	Test.assert(buf.size() == 3);
	Test.assert(buf.top() == 777);
	Test.assert(buf.pop() == 777);
	Test.assert(buf.pop() == 13);
	Test.assert(buf.size() == 1);
	Test.assert(buf.asArray()[0] == 12);
	Test.assert(buf.pop() == 12);
	Test.assert(buf.asArray().size() == 0);
	Test.assert(buf.pop() == null);
	return true;
}
	