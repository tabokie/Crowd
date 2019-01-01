package crowd.concurrent;

import java.util.concurrent.*;
import java.util.*;

import javafx.application.Platform;

import crowd.*;
import crowd.ui.*;

public class Simulator extends Thread {
	private static Protocol defaultProtocol = new DefaultProtocol();
	private Protocol protocol = null;
	private Map<String, Prototype> prototypes = new ConcurrentHashMap<String, Prototype>();
	private Map<String, Map<String, Object>> nodeState = new ConcurrentHashMap<String, Map<String, Object>>();
	private EventScheduler scheduler = new RealtimeEventScheduler();
	public Simulator (Protocol p) {
		this.protocol = p;
	}
	public Simulator() { }
	// Accessors
	public EventScheduler getScheduler() {
		return scheduler;
	}
	public void setProtocol(Protocol protocol) {
		this.protocol = protocol;
	}
	public Protocol getProtocol() { // node may leverage on central protocol
		if(protocol == null) return defaultProtocol;
		return protocol;
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

	public void addPrototype(String type, Prototype prototype) {
		prototypes.put(type, prototype);
	}
	public Map<String, Object> addNode(String name, String type) {
		Map<String, Object> datas = new ConcurrentHashMap<String, Object>();
		nodeState.put(name, datas);
		datas.put("type", type);
		if(Monitor.flow != null) {
			Platform.runLater(()->{
				Monitor.flow.newGroup(name);
			});
		}
		return datas;
	}
	public void startNode(String name) {
		String type = getData(name, "type");
		if(type == null) return ;
		Prototype node = prototypes.get(type);
		node.start(name, this);
		if(Monitor.flow != null) {
			Platform.runLater(()->{
				Monitor.flow.precedeGroup(name, getData(name, "target"));
			});
		}
	}
	@Override
	public void run() {
		scheduler.start();
		scheduler.close();
	}

	public void send(String fromNode, String toNode, String message) {
		String type = getData(toNode, "type");
		if(type == null) return ;
		Prototype node = prototypes.get(type);
		if(node == null) return;
		node.receive(toNode, this, fromNode, message);
		if(Monitor.flow != null) {
			Platform.runLater(()->{
				Monitor.flow.setStroke(fromNode, toNode, 0);
			});
		}
	}
}