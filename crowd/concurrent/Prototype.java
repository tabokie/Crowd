package crowd.concurrent;

// virtual node is initiated in a existing group
public interface Prototype {
	void receive(String thisNode, Simulator simulator, String fromNode, String message);
	void start(String thisNode, Simulator simulator);
}