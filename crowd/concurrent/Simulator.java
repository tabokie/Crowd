package crowd.concurrent;

import java.util.concurrent.*;
import java.util.*;

import javafx.application.Platform;

import javafx.util.Pair;
import crowd.App;
import crowd.Buildable;
import crowd.Command;
import crowd.ui.*;
import crowd.util.JavaRuntime;
import crowd.port.OPort;

public class Simulator extends Buildable implements OPort {
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
	public boolean send(String target, Object message) {
		return false;
	}
	public boolean send(Object message) {
		if(!(message instanceof Command)) return false;
		Command cmd = (Command) message;
		switch(cmd.operator) {
			case Command.START: // name type group
			addNode((String)cmd.operands[0], (String)cmd.operands[1], (String)cmd.operands[2]);
			break;
			case Command.SHUTDOWN:
			shutdownNode((String)cmd.operands[0]);
			break;
			case Command.PROTOTYPE:
			addPrototype((String)cmd.operands[0], (Prototype) JavaRuntime.LoadObjectFromResource((String)cmd.operands[0]));
			break;
		}
		return false;
	}
	public void close() {
		scheduler.close();
	}
	public App build() {
		scheduler.start();
		parent.addOPort(this);
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
				final String fromGroup = getData(name, "group");
				final String[] toNode = getData(name, "target");
				for(String to: toNode) {
					Platform.runLater(()->{
						parent.getFlow().precedeGroup(fromGroup, getData(to, "group"));
					});	
				}
			}
		}
		return this;
	}
	private static int autoId = 0;
	private static String autoGroupId() {
		autoId ++;
		return "-auto-gen-" + String.valueOf(autoId) + "-";
	}
	public Simulator shutdownNode(String name) {
		nodeState.put(name, null);
		return this;
	}
	public Simulator addNode(String name, String type, String group, Pair<String, Object>... kvs) {
		if(group == null) {
			group = autoGroupId();
		}
		Map<String, Object> state = addNode(name, type, group);
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
	// runtime operation
	public void send(String fromNode, String toNode, String message) {
		String type = getData(toNode, "type");
		if(type == null) return ;
		parent.input(fromNode + ":" + message);
		Prototype node = prototypes.get(type);
		if(node == null) return;
		node.receive(toNode, this, fromNode, message); // simulate
		if(parent.getFlow() != null) {
			Platform.runLater(()->{
				parent.getFlow().connectNode(fromNode, toNode,null);
			});
		}
	}
	// private
	private Map<String, Object> addNode(String name, String type, String group) {
		Map<String, Object> datas = new ConcurrentHashMap<String, Object>();
		nodeState.put(name, datas);
		datas.put("type", type);
		datas.put("group", group);
		Prototype prototype = prototypes.get(type);
		if(prototype != null) {
			prototype.init(datas);
		}
		if(parent.getFlow() != null) {
			Platform.runLater(()->{
				parent.getFlow().newGroup(group);
				parent.getFlow().newNode(name, group);
			});
		}
		return datas;
	}


}