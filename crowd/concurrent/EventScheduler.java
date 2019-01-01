package crowd.concurrent;

public interface EventScheduler {
	void enqueue(int time, ConsumeActor task);
	void enqueue(int time, Runnable task);
	void enqueue(Actor e);
	void close();
	void start();
}
