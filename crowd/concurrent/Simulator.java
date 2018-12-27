package crowd.concurrent;

import java.util.concurrent.*;
import java.util.*;

public class Simulator {
	private static Protocol defaultProtocol = new DefaultProtocol();
	private Protocol protocol = null;
	private Map<String, Prototype> prototypes = new ConcurrentHashMap<String, Prototype>();
	private Map<String, Map<String, Object>> nodeState = new ConcurrentHashMap<String, Map<String, Object>>();
	private DiscreteEventScheduler scheduler = new DiscreteEventScheduler();
	public Simulator (Protocol p) {
		this.protocol = p;
	}
	public void setProtocol(Protocol protocol) {
		this.protocol = protocol;
	}
	public Protocol getProtocol() { // node may leverage on central protocol
		if(protocol == null) return defaultProtocol;
		return protocol;
	}
	public Object getData(String node, String name) {
		Map<String, Object> datas = nodeState.get(node);
		if(datas != null) return datas.get(name);
		return null;
	}
	public Map<String, Object> getDatas(String node) {
		Map<String, Object> datas = nodeState.get(node);
		if(datas == null) {
			datas = new ConcurrentHashMap<String, Object>();
			nodeState.put(node, datas);
		}
		return datas;
	}
	public void setData(String node, String name, Object data) {
		Map<String, Object> datas = getDatas(node);
		// assert(datas != null)
		datas.put(name, data);
	}
	public void send(String fromNode, String toNode, String message) {
		Object data = getData(toNode, "type");
		if(data == null) return;
		String type = (String)data;
		Prototype node = prototypes.get(type);
		if(node == null) return;
		node.receive(this, fromNode, message);
	}
}