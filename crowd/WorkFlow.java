package crowd;

import javafx.scene.layout.Pane;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import java.util.*;

public class WorkFlow {
	private List<List<GroupNode>> flow = new ArrayList<List<GroupNode>>();
	private Map<String, Node> nodes = new Hashtable<String, Node>(); // thread safe
	private Map<String, GroupNode> groups = new Hashtable<String, GroupNode>();
	private Vec2f origin = new Vec2f();
	private Vec2f canvas = new Vec2f();
	private Pane pane;
	private final static float minMarginOfWidth = 0.07f;
	private final static float minHeaderOfWidth = 0.05f;
	WorkFlow(Pane p, Vec2f o, Vec2f size) {
		pane = p;
		origin.copy(o);
		canvas.copy(size);
	}
	public void startConcurrentTest(int baseSize) {
		for(int i = 0; i < 100; i ++) {
			final int tmp = i;
			Thread work = new Thread(() -> {
				this.newGroup("2." + String.valueOf(tmp), new String[]{"1." + String.valueOf(tmp % baseSize)});
			});
			work.start();
		}
		for(int i = 0; i < 100; i ++) {
			final int tmp = i;
			Thread work = new Thread(() -> {
				this.newNode("n." + String.valueOf(tmp) , "2." + String.valueOf(tmp));
			});
			work.start();
		}
	}
	public void report() {
		int level = 1;
		for(List<GroupNode> list: flow) {
			System.out.println("level " + String.valueOf(level));
			for(GroupNode group: list) {
				System.out.println(group.toString());
			}
			level ++;
		}
		return ;
	}
	// public interface will update layout at the end
	public String newGroup(String name) {
		return newGroup(name, null);
	}
	public String newGroup(String name, String[] requisite) {
		GroupNode group = groups.get(name);
		if(group != null) {
			return new String("find duplicated group named " + name);
		}
		group = new GroupNode(pane, name);
		groups.put(name, group);
		insertGroup(group, requisite);
		if(requisite != null) {
			for(int i = 0; i < requisite.length; i++) {
				precedeGroup(group, groups.get(requisite[i]));
			}
		}
		updateLayout();
		nextFrame(2000);
		return null;
	}
	public String newNode(String name, String belongToGroup) {
		GroupNode group = groups.get(belongToGroup);
		if(group == null) return new String("can't find group named " + belongToGroup );
		Node node = nodes.get(name);
		if(node != null) return new String("find duplicated node named " + name);
		node = new Node(pane, group, name);
		nodes.put(name, node);
		nextFrame(group, 2000);
		return null;
	}
	public String precedeGroup(String fromName, String toName) {
		GroupNode fromNode = groups.get(fromName);
		GroupNode toNode = groups.get(toName);
		precedeGroup(fromNode, toNode);
		nextFrame(2000);
		return null;
	}
	public String connectNode(String fromName, String toName) {
		Node fromNode = nodes.get(fromName);
		Node toNode = nodes.get(toName);
		if(fromNode == null || toNode == null ) return new String("can't find node to connect");
		if(fromNode.getParent() == toNode.getParent() ) {
			// fromNode.getParent().connectNode(fromNode, toNode);
			// nextFrame(fromNode.getParent(), 2000);
			NodeLink link = null;
			List<KeyValue> kvs = new ArrayList<KeyValue>();
			if((link = fromNode.getOut(toName)) != null) {
				link.start(Color.GREEN, kvs);
			}
			else if((link = toNode.getOut(fromName)) != null) {
				link.start(Color.RED, kvs);
			}
			else {
				fromNode.getParent().connectNode(fromNode, toNode, kvs);
			}
			nextFrame(kvs, 2000);
		}
		else {
			precedeGroup(fromNode.getParent().getId(), toNode.getParent().getId());
			nextFrame(2000);
		}
		return null;
	}
	public String setStroke(String groupId, String target, float width) {
		GroupNode groupNode = groups.get(groupId);
		if(groupNode == null) return new String("can't find group named " + groupId);
		GroupLink li = groupNode.getOut(target);
		if(li == null) return new String("can't find route from " + groupId + " to " + target);
		List<KeyValue> kvs = new ArrayList<KeyValue>();
		// kvs.add(new KeyValue(li.curve.strokeWidthProperty(), width ));
		// li.ripple.start(kvs);
		li.curve.startRipple(kvs);
		nextFrame(kvs, 2000);
		return null;
	}
	public String setHalo(String groupId, float radius, float progress) {
		GroupNode groupNode = groups.get(groupId);
		if(groupNode == null) return new String("can't find group named " + groupId);
		// System.out.println("try to set halo of " + groupId + " with " + radius);
		List<KeyValue> kvs = new ArrayList<KeyValue>();
		groupNode.halo.speculateRadius(radius);
		groupNode.halo.speculateProgress(progress);
		groupNode.halo.exportSpeculativeState(kvs);
		nextFrame(kvs, 2000);
		return null;
	}
	private void nextFrame(KeyFrame frame, float millis) {
		final Timeline timeline =  new Timeline();
		timeline.getKeyFrames().add(frame);
		timeline.play();
	}
	private void nextFrame(List<KeyValue> kvs, float millis) {
		final Timeline timeline =  new Timeline();
		KeyFrame frame = new KeyFrame(Duration.millis(millis), "", null, kvs);
		timeline.getKeyFrames().add(frame);
		timeline.play();
	}
	private void nextFrame(KeyValue kv, float millis) {
		final Timeline timeline =  new Timeline();
		KeyFrame frame = new KeyFrame(Duration.millis(millis), kv);
		timeline.getKeyFrames().add(frame);
		timeline.play();
	}
	private void nextFrame(float millis) {
		List<KeyValue> kvs = new ArrayList<KeyValue>();
		for(List<GroupNode> list : flow) {
			for(GroupNode group : list) {
				group.exportSpeculativeState(kvs);
			}
		}
		nextFrame(kvs, millis);
	}
	private void nextFrame(GroupNode group, float millis) {
		List<KeyValue> kvs = new ArrayList<KeyValue>();
		group.exportSpeculativeState(kvs);
		nextFrame(kvs, millis);
	}
	private synchronized void precedeGroup(GroupNode fromNode, GroupNode toNode) {
		if(fromNode == null || toNode == null) return;  
		if(fromNode.getLevel() == toNode.getLevel()) { // remove to
			List<GroupNode> curLevel = flow.get(fromNode.getLevel());
			if(curLevel == null) return ;
			curLevel.remove(toNode);
			if(fromNode.getLevel() + 1 >= flow.size()) {
				flow.add(new ArrayList<GroupNode>());
			}
			flow.get(fromNode.getLevel() + 1).add(toNode);
		}
		else if(fromNode.getLevel() > toNode.getLevel()) {
			GroupNode tmp = fromNode;
			fromNode = toNode;
			toNode = tmp;
		}
		// setup dummy nodes
		for(int level = fromNode.getLevel() + 1; level < toNode.getLevel(); level ++) {
			GroupNode dummy = GroupNode.NewDummy();
			flow.get(level).add(dummy);
			GroupLink dummyLink = GroupLink.NewLink(pane);
			fromNode.toLink(dummyLink);
			dummy.fromLink(dummyLink);
			fromNode = dummy;
		}
		updateLayout();
		GroupLink link = GroupLink.NewLink(pane);
		fromNode.toLink(link);
		toNode.fromLink(link);
	}
	private synchronized void insertGroup(GroupNode group, String[] precedentGroup) {
		int min = 0;
		if(precedentGroup != null) {
			for(int i = 0; i < precedentGroup.length; i++) {
				GroupNode preGroup = groups.get(precedentGroup[i]);
				if(preGroup != null && preGroup.getLevel() >= min) {
					min = preGroup.getLevel() + 1;
				}
			}	
		}
		while(min >= flow.size()) {
			flow.add(new ArrayList<GroupNode>());
		}
		flow.get(min).add(group);
		group.setLevel(min);
		return ;
	}
	// MUST operate on a consistent view
	private synchronized void updateLayout() {
		if(groups.size() == 0) return ;
		float margin = (canvas.data[0] / flow.size()) / 2.0f;
		if(margin < minMarginOfWidth * canvas.data[0]) {
			margin = minMarginOfWidth * canvas.data[0];
		}
		float interval = (canvas.data[0] - margin * 2) / (flow.size() - 1);
		if(flow.size() == 1) interval = 0;
		int maxHeight = 0;
		for(List<GroupNode> li : flow) {
			if(maxHeight < li.size()) maxHeight = li.size();
		}
		if(maxHeight == 0) return;
		// previously use uniform interval for every lane
		// float header = (canvas.data[1] / maxHeight) / 2.0f; 
		float header = minHeaderOfWidth * canvas.data[1];
		for(int i = 0; i < flow.size(); i++) {
			List<GroupNode> cur = flow.get(i);
			float hinterval = (canvas.data[1] - header * 2) / cur.size();
			if(cur.size() == 1) hinterval = canvas.data[1] - header * 2;
			float x = origin.data[0] + margin + interval * i;
			// float y = origin.data[1] + (canvas.data[1] - hinterval * cur.size() + hinterval) / 2.0f;
			float y = origin.data[1] + header + hinterval * 0.5f;
			for(GroupNode node: cur) {
				node.setLevel(i);
				node.speculateCenter(x, y);
				y += hinterval;
			}
		}
	}
}