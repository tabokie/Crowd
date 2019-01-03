package crowd.concurrent;

import java.util.concurrent.*;
import java.util.*;

import javafx.application.Platform;

// import crowd.util.Pair;
import javafx.util.Pair;
import crowd.App;
import crowd.Buildable;
import crowd.ui.*;

public class Simulator extends Buildable {
	// private static Protocol defaultProtocol = new DefaultProtocol();
	// private Protocol protocol = null;
	private Map<String, Prototype> prototypes = new ConcurrentHashMap<String, Prototype>();
	private Map<String, Map<String, Object>> nodeState = new ConcurrentHashMap<String, Map<String, Object>>();
	private EventScheduler scheduler = new RealtimeEventScheduler();
	public Simulator() {
		super();
	}
	public Simulator(App parent) {
		super(parent);
	}
	public App build() {
		scheduler.start();
		// scheduler.close();
		return parent;
	}
	// builder
	public Simulator addPrototype(String type, Prototype prototype) {
		prototypes.put(type, prototype);
		return this;
	}
	public Simulator setStartup(String... list) {
		for(int i = 0; i < list.length; i++) {
			String name = list[i];
			String type = getData(name, "type");
			if(type == null) return this;
			Prototype node = prototypes.get(type);
			node.start(name, this); // only push to queue
			if(parent.getFlow() != null) {
				Platform.runLater(()->{
					parent.getFlow().precedeGroup(name, getData(name, "target"));
				});
			}
		}
		return this;
	}
	public Simulator addNode(String name, String type, Pair<String, Object>... kvs) {
		Map<String, Object> state = addNode(name, type);
		for(int i = 0; i < kvs.length; i++) {
			state.put(kvs[i].getKey(), kvs[i].getValue());
		}
		return this;
	}
	// Accessors
	public EventScheduler getScheduler() {
		return scheduler;
	}
	public Map<String, Object> getDatas(String node) {
		Map<String, Object> datas = nodeState.get(node);
		if(datas == null) {
			datas = new ConcurrentHashMap<String, Object>();
			nodeState.put(node, datas);
		}
		return datas;
	}
	public <T> T getData(String node, String name) {
		Map<String, Object> datas = nodeState.get(node);
		if(datas != null) return (T)datas.get(name);
		return null;
	}
	public void setData(String node, String name, Object data) {
		Map<String, Object> datas = getDatas(node);
		datas.put(name, data);
	}
	private Map<String, Object> addNode(String name, String type) {
		Map<String, Object> datas = new ConcurrentHashMap<String, Object>();
		nodeState.put(name, datas);
		datas.put("type", type);
		if(parent.getFlow() != null) {
			Platform.runLater(()->{
				parent.getFlow().newGroup(name);
			});
		}
		return datas;
	}
	public void startNode(String name) {
		String type = getData(name, "type");
		if(type == null) return ;
		Prototype node = prototypes.get(type);
		node.start(name, this);
		if(parent.getFlow() != null) {
			Platform.runLater(()->{
				parent.getFlow().precedeGroup(name, getData(name, "target"));
			});
		}
	}
	public void send(String fromNode, String toNode, String message) {
		String type = getData(toNode, "type");
		if(type == null) return ;
		Prototype node = prototypes.get(type);
		if(node == null) return;
		node.receive(toNode, this, fromNode, message);
		if(parent.getFlow() != null) {
			Platform.runLater(()->{
				parent.getFlow().setStroke(fromNode, toNode, 0);
			});
		}
	}
}