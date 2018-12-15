package crowd;

import javafx.scene.layout.Pane;
import java.util.*;

public class WorkFlow {
	private List<List<GroupNode>> flow = new ArrayList<List<GroupNode>>();
	private Map<String, Node> nodes = new HashMap<String, Node>(); // map from id to node, id can be assigned from int, or ip addr
	private Map<String, GroupNode> groups = new HashMap<String, GroupNode>();
	private Vec2f origin = new Vec2f();
	private Vec2f canvas = new Vec2f();
	private Pane pane;
	WorkFlow(Pane p, Vec2f o, Vec2f size) {
		pane = p;
		origin.copy(o);
		canvas.copy(size);
	}
	public void setup(String newGroup, String[] precedentGroup) {
		GroupNode group = groups.get(newGroup);
		if(group == null) {
			group = new GroupNode(pane, newGroup);
			groups.put(newGroup,group);
			insertGroup(group, precedentGroup);
			updateLayout();
		}
		if(precedentGroup != null) {
			for(int i = 0; i < precedentGroup.length; i++) {
				connectGroup(groups.get(precedentGroup[i]), group);
				// GroupNode from = groups.get(precedentGroup[i]);
				// if(from != null){
				// 	GroupLink link = GroupLink.NewLink(pane);
				// 	group.fromLink(link);
				// 	from.toLink(link);
				// }
			}
		}
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
	public void claim(String nodeId, String groupIdentifier) {
		GroupNode group = groups.get(groupIdentifier);
		if(group == null)return;
		Node node = nodes.get(nodeId);
		if(node == null) {
			node = new Node(pane, group, nodeId);
			nodes.put(nodeId, node);
		}
		else {
			node.setParent(group);
		}
	}
	public void connectGroup(GroupNode fromNode, GroupNode toNode) {
		if(fromNode == null || toNode == null) return;  
		if(fromNode.getLevel() == toNode.getLevel()) { // remove to
			List<GroupNode> curLevel = flow.get(fromNode.getLevel());
			if(curLevel == null) return ;
			curLevel.remove(toNode);
			if(fromNode.getLevel() + 1 >= flow.size()) {
				flow.add(new ArrayList<GroupNode>());
			}
			flow.get(fromNode.getLevel() + 1).add(toNode);
			// updateLayout();
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
	public void connectGroup(String fromId, String toId) {
		GroupNode fromNode = groups.get(fromId);
		GroupNode toNode = groups.get(toId);
		connectGroup(fromNode, toNode);
	}
	public void connectNode(String fromId, String toId) {
		Node fromNode = nodes.get(fromId);
		Node toNode = nodes.get(toId);
		if(fromNode == null || toNode == null ) return ;
		if(fromNode.getParent() == toNode.getParent() ) {
			fromNode.getParent().connectNode(fromNode, toNode);
		}
		else {
			connectGroup(fromNode.getParent().getId(), toNode.getParent().getId());
		}
	}
	public void updateLayout() {
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
				node.moveLayout(x, y + delta*0.5f + delta * j);
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