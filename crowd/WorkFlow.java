package crowd;

import javafx.scene.layout.Pane;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.util.Duration;
import java.util.*;

// 1. out of bound
// 2. node link disappear after transform
// 3. glink initial control point too scary
public class WorkFlow {
	private List<List<GroupNode>> flow = new ArrayList<List<GroupNode>>();
	private Map<String, Node> nodes = new HashMap<String, Node>();
	private Map<String, GroupNode> groups = new HashMap<String, GroupNode>();
	private Vec2f origin = new Vec2f();
	private Vec2f canvas = new Vec2f();
	private Pane pane;
	WorkFlow(Pane p, Vec2f o, Vec2f size) {
		pane = p;
		origin.copy(o);
		canvas.copy(size);
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
	public String claimGroup(String newGroup, String[] precedentGroup) {
		GroupNode group = groups.get(newGroup);
		if(group != null) {
			return new String("find duplicated group named " + newGroup);
		}
		group = new GroupNode(pane, newGroup);
		groups.put(newGroup, group);
		insertGroup(group, precedentGroup);
		updateLayout();
		if(precedentGroup != null) {
			for(int i = 0; i < precedentGroup.length; i++) {
				connectGroup(groups.get(precedentGroup[i]), group);
			}
		}
		nextFrame();
		return null;
	}
	public String claimNode(String nodeId, String groupIdentifier) {
		GroupNode group = groups.get(groupIdentifier);
		if(group == null) return new String("can't find group named " + groupIdentifier );
		Node node = nodes.get(nodeId);
		if(node != null) return new String("find duplicated node named " + nodeId);
		node = new Node(pane, group, nodeId);
		nodes.put(nodeId, node);
		nextFrame();
		return null;
	}
	public String connectGroup(String fromId, String toId) {
		GroupNode fromNode = groups.get(fromId);
		GroupNode toNode = groups.get(toId);
		connectGroup(fromNode, toNode);
		nextFrame();
		return null;
	}
	public String connectNode(String fromId, String toId) {
		Node fromNode = nodes.get(fromId);
		Node toNode = nodes.get(toId);
		if(fromNode == null || toNode == null ) return new String("can't find node to connect");
		if(fromNode.getParent() == toNode.getParent() ) {
			fromNode.getParent().connectNode(fromNode, toNode);
		}
		else {
			connectGroup(fromNode.getParent().getId(), toNode.getParent().getId());
		}
		nextFrame();
		return null;
	}
	public void nextFrame() {
		// updateLayout();
		final Timeline timeline =  new Timeline();
		List<KeyValue> kvs = new ArrayList<KeyValue>();
		for(List<GroupNode> list : flow) {
			for(GroupNode group : list) {
				group.exportSpeculativeState(kvs);
			}
		}
		KeyFrame frame = new KeyFrame(Duration.millis(2000), "moveMe", null, kvs);
		timeline.getKeyFrames().add(frame);
		timeline.play();
	}
	// 
	private void connectGroup(GroupNode fromNode, GroupNode toNode) {
		if(fromNode == null || toNode == null) return;  
		if(fromNode.getLevel() == toNode.getLevel()) { // remove to
			List<GroupNode> curLevel = flow.get(fromNode.getLevel());
			if(curLevel == null) return ;
			curLevel.remove(toNode);
			if(fromNode.getLevel() + 1 >= flow.size()) {
				flow.add(new ArrayList<GroupNode>());
			}
			flow.get(fromNode.getLevel() + 1).add(toNode);
		updateLayout();
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


	private void insertGroup(GroupNode group, String[] precedentGroup) {
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
	private void updateLayout() {
		float x = origin.data[0] + canvas.data[0] / flow.size() / 2.0f;
		float w = canvas.data[0] * (1.0f - 1.0f / flow.size());
		float y = origin.data[1] + canvas.data[1] * 0.1f;
		float h = canvas.data[1] * 0.8f;
		boolean first = true;
		int groupCount = groups.size();
		for(int i = 0; i < flow.size(); i++) {
			List<GroupNode> cur = flow.get(i);
			float delta = h / (float)cur.size(); 
			int j = 0;
			for(GroupNode node: cur) {
				node.setLevel(i);
				node.speculateCenter(x, y + delta*0.5f + delta * j);
				j ++;
			}
			List<GroupNode> next = null;
			if(i+1 < flow.size()) next = flow.get(i+1);
			else break;
			if(first) {
				x += (cur.size() / (float)groupCount + next.size() / (float)groupCount / 2.0f) * w;
			}
			else{
				x += (cur.size() / (float)groupCount / 2.0f + next.size() / (float)groupCount / 2.0f) * w;
			}
			if(first)first = false;
		}
	}
}