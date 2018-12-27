package crowd.concurrent;

public interface Prototype {
	void receive(Simulator simulator, String fromNode, String message);
}