package crowd.concurrent;

public abstract class Event extends Thread implements Comparable<Event> {
	public final double timestamp;
	private Double threshold;
	Event(double t) {
		timestamp = t;
		threshold = new Double(t); // blocking in current task until the sub-events are clear
	}
	public int compareTo(Event rhs) {
		double diff = timestamp - rhs.timestamp;
		if(diff > 0 ) return 1;
		if(diff < 0 ) return -1;
		return 0;
	}
	public abstract void run() ;
	/**
	 * return the earlest sub-event timestamp which indicates 
	 * a possible discrete time jump for scheduler
	 */
	public double threshold(){ // read only
		synchronized (threshold) {
			return threshold.doubleValue();
		}
	}
}