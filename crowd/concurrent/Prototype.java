package crowd.concurrent;

import java.util.Map;

// virtual node is initiated in a existing group
public interface Prototype {
	void init(Map<String, Object> data);
	void receive(String thisNode, Simulator simulator, String fromNode, String message);
	void start(String thisNode, Simulator simulator);
}