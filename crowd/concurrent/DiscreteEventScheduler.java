package crowd.concurrent;

import java.util.concurrent.atomic.*;

public class DiscreteEventScheduler extends Thread implements EventScheduler{
	private ConcurrentPriorityQueue<Actor> actorQueue = new ConcurrentPriorityQueue<Actor>();
	private volatile int timestamp = 0; // can only be modified by scheduler thread
	private final int BUFFER_SIZE = 10;
	private Actor[] actorBuffer = new Actor[BUFFER_SIZE];
	private int size = 0;
	private AtomicBoolean closed = new AtomicBoolean(false);
	public void enqueue(int time, ConsumeActor task) {
		Actor e = new Actor(time + timestamp, task);
		actorQueue.insert(e);
	}
	public void enqueue(int time, Runnable task) {
		Actor e = new Actor(time + timestamp, task);
		actorQueue.insert(e);
	}
	public void enqueue(Actor e) { // shouldn't call from outside, only by actor
		actorQueue.insert(e);
	}
	public String toString() {
		return actorQueue.toString();
	}
	public int getTime() {
		return timestamp;
	}
	public void close() {
		closed.set(true);
		try {
			join();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		while(!closed.get() || actorQueue.readMin() != null || findUnfinished() >= 0) {
			// update buffer
			int minThreshold = -1;
			int idx = -1;
			for(int i = 0; i < size; i ++) {
				if(actorBuffer[i] == null) continue;
				int t = actorBuffer[i].getThreshold();
				if(t <= timestamp && actorBuffer[i].getFinished()) { // meaning local job all submitted 
					actorBuffer[i] = null;
					continue;
				}
				if(minThreshold < 0 || minThreshold > t) {
					idx = i;
					minThreshold = t;
				}
			}
			if(minThreshold > timestamp)timestamp = minThreshold;
			while(true) {
				Actor actor = actorQueue.readMin();
				if(actor != null && actor.timestamp <= timestamp) {
					// actor allowed to schedule
					int index = findEmpty();
					if(index < 0) break;
					actor = actorQueue.deleteMin(); // at most previous time since no one can delete it
					actorBuffer[index] = actor;
					actor.start(this);
				}
				else break; // Actorual break after consume the queue
			}
		}
	}
	private int findUnfinished() {
		for(int i = 0; i < size; i ++ ){
			if(actorBuffer[i] != null) return i;
		}
		return  -1;
	}
	private int findEmpty() {
		for(int i = 0; i < size; i ++ ){
			if(actorBuffer[i] == null) return i;
		}
		if(size + 1 < BUFFER_SIZE) { // expand
			size ++;
			return size-1;
		}
		return  -1;
	}

}
