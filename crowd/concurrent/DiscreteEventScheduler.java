package crowd.concurrent;

public class DiscreteEventScheduler {
	private ConcurrentPriorityQueue<Event> eventQueue = new ConcurrentPriorityQueue<Event>();
	private double timestamp = 0;
	private final int BUFFER_SIZE = 10;
	private Event[] eventBuffer = new Event[BUFFER_SIZE];
	private int size = 0;
	public void serve() {
		while(true) {
			// update buffer
			double minThreshold = -1;
			for(int i = 0; i < size; i ++) {
				if(minThreshold < 0 && minThreshold > eventBuffer[i].timestamp) minThreshold = eventBuffer[i].timestamp;
			}
			if(minThreshold > 0)timestamp = minThreshold;
			while(true) {
				Event event = eventQueue.readMin();
				if(event.timestamp < timestamp) {
					// event allowed to schedule
					int index = findEmpty();
					if(index < 0) break;
					event = eventQueue.deleteMin(); // at most previous time since no one can delete it
					eventBuffer[index] = event;
					event.start();
				}
				else break;
			}
		}
	}
	private int findEmpty() {
		for(int i = 0; i < size; i ++ ){
			if(eventBuffer[i] == null) return i;
		}
		if(size <= BUFFER_SIZE) { // expand
			size ++;
			return size-1;
		}
		return  -1;
	}
}