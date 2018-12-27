package crowd.concurrent;

// no time constraint granted for those operation without time tag
public class Actor extends Thread implements Comparable<Actor> {
	public final int timestamp;
	private Integer threshold;
	private Boolean finished = new Boolean(false);
	private ConsumeActor target = null;
	// private StateType state;
	// private final String name;
	public Actor(int t, Runnable target) {
		super(target);
		timestamp = t;
		threshold = new Integer(t); // blocking in current task until the sub-Actors are clear
	}
	public Actor(int t, ConsumeActor target) {
		super();
		timestamp = t;
		this.target = target;
		threshold = new Integer(t); // blocking in current task until the sub-Actors are clear
	}
	private DiscreteEventScheduler scheduler;
	public void start(DiscreteEventScheduler scheduler) {
		this.scheduler = scheduler;
		start();
	}
	public int compareTo(Actor rhs) {
		return timestamp - rhs.timestamp;
	}
	@Override
	public String toString() {
		return "Actor[" + String.valueOf(timestamp) + "]";
	}
	public void act(int time, Runnable event) {
		setThreshold(time + timestamp); // meaning locally, all event <= time is finished.
		if(scheduler != null) {
			// scheduler.enqueue(time, event); // shouldn't use global view here
			scheduler.enqueue(new Actor(time + timestamp, event));
		}
		else {
			event.run();
		}
	}
	public void act(int time, ConsumeActor event) {
		setThreshold(time + timestamp);
		if(scheduler != null) {
			// scheduler.enqueue(time, event);
			scheduler.enqueue(new Actor(time + timestamp, event));
		}
		else {
			event.run(this);
		}
	}
	@Override
	public void run() {
		super.run();
		if(target != null) {
			target.run(this);
		}
		setFinished(true);
	}
	public void setFinished(boolean finished) {
		synchronized (this.finished) {
			this.finished = finished;
		}
	}
	public boolean getFinished() {
		synchronized (finished) {
			return finished.booleanValue();
		}
	}
	public void setThreshold(int t) {
		synchronized (threshold) {
			threshold = t;
		}
	}
	/**
	 * return the earlest sub-Actor timestamp which indicates 
	 * a possible discrete time jump for scheduler
	 */
	public int getThreshold(){ // read only
		synchronized (threshold) {
			return threshold.intValue();
		}
	}
}