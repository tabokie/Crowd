package crowd;

import java.util.*;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;

import javafx.util.Duration;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.animation.*;

public class GroupNode {
	// manager of nodes
	private List<Node> members = new ArrayList<Node>();
	private Vec2f position = new Vec2f(-1, -1);
	private Node center = null;
	// as member of GroupNode
	private Circle vcenter = new Circle();
	// public Circle halo = new Circle();
	public Halo halo ;
	private List<GroupLink> from = new ArrayList<GroupLink>();
	private List<GroupLink> to = new ArrayList<GroupLink>();
	private Pane pane;
	private String id;
	private int level = 0;

	GroupNode(Pane pa, String name, Vec2f p) {
		position.copy(p);
		pane = pa;
		id = name;
    vcenter.setCenterX(position.data[0]);
    vcenter.setCenterY(position.data[1]);
    vcenter.setRadius(3);
    vcenter.setFill(Color.TRANSPARENT);
    vcenter.setStrokeWidth(1);
    vcenter.setStroke(Color.RED);

    pane.getChildren().add(vcenter);
    halo = new Halo(pane);
	}
	GroupNode(Pane pa, String name) {
		pane = pa;
		id = name;
    vcenter.setCenterX(-1);
    vcenter.setCenterY(-1);
    vcenter.setRadius(3);
    vcenter.setFill(Color.TRANSPARENT);
    vcenter.setStrokeWidth(1);
    vcenter.setStroke(Color.RED);

    pane.getChildren().add(vcenter);
    halo = new Halo(pane);
	}
	GroupNode() { }
	public String toString() {
		String ret = new String(id + ": (" + String.valueOf(getCenterX()) + ", " + String.valueOf(getCenterY())
			+ ") -> ");
		for(GroupLink l : to ) {
			ret += l.toString();
		}
		return ret;
	}
	static GroupNode NewDummy() {
		GroupNode ret = new GroupNode();
		ret.pane = null;
		ret.id = "dummy";
		ret.vcenter.setCenterX(-1);
		ret.vcenter.setCenterY(-1);
		ret.halo = new Halo(null);
		return ret;
	}
	public void setLevel(int l){
		level = l;
	}
	public int getLevel() {
		return level;
	}
	public String getId() {
		return id;
	}
	public float getSpeculativeCenterX() {
		return position.data[0];
	}
	public float getSpeculativeCenterY() {
		return position.data[1];
	}
	public float getCenterX() {
		return (float)vcenter.getCenterX();
	}
	public float getCenterY() {
		return (float)vcenter.getCenterY();
	}
	public GroupLink getOut(String id) {
		for(GroupLink out : to ) {
			if(out.toGroup.equals(id)) {
				return out;
			}
		}
		return null;
	}
	public GroupLink getIn(String id) {
		for(GroupLink in : from ) {
			if(in.fromGroup.equals(id)) {
				return in;
			}
		}
		return null;
	}
	public void fromLink(GroupLink link) {
		link.toGroup = (id);
		link.setEnd(getCenterX(), getCenterY());
		from.add(link);
		link.speculateEnd(getSpeculativeCenterX(), getSpeculativeCenterY());
	}
	public void toLink(GroupLink link) {
		link.fromGroup = id;
		link.setStart(getCenterX(), getCenterY());
		to.add(link);
		link.speculateStart(getSpeculativeCenterX(), getSpeculativeCenterY());
	}
	public void addMember(Node node) {
		if(node != null) {
			members.add(node);
			speculateNodes();
		}
	}
	public void connectNode(Node a, Node b) { // from little end to higher end
		if(pane == null) return ;
		Node fromNode = null;
		Node toNode = null;
		int na = members.indexOf(a);
		int nb = members.indexOf(b);
		if(na < 0 || nb < 0) return ;
		if(nb-na > members.size() / 2.0f) na += members.size();
		if(na-nb > members.size() / 2.0f) nb += members.size();
		if(na < nb) {
			fromNode = a;
			toNode = b;
		}
		else {
			fromNode = b;
			toNode = a;
		}
		NodeLink link = NodeLink.NewLink(pane);
		fromNode.toLink(link);
		toNode.fromLink(link);
	}
	final float initialRadius = 10;
	public void speculateNodes() {
		float total = members.size();
		float radius = initialRadius + (float)Math.sqrt(total) * initialRadius * 0.5f;
		float cur = 0;
		for(Node node : members ) {
			float half = 1.0f / total * (float)Math.PI;
			node.speculateCenter(position.data[0] + radius * (float)Math.cos(cur + half), 
				position.data[1] + radius * (float)Math.sin(cur + half));
			cur += half * 2;
		}
	}
	public void speculateLinks() {
		float x = position.data[0];
		float y = position.data[1];
		if(getCenterX() < 0) {
			for(GroupLink link : from) {
				link.setEnd(x, y);
			}
			for(GroupLink link : to) {
				link.setStart(x, y);
			}
		}
		else {
			for(GroupLink link : from) {
				link.speculateEnd(x, y);
			}
			for(GroupLink link : to) {
				link.speculateStart(x, y);
			}
		}
	}
	private boolean speculativeStateReady = false;
	public void speculateCenter(float x, float y) {
		position.data[0] = x; // need to put in front
		position.data[1] = y;
		speculateLinks();
		speculateNodes();
		if(getCenterX() < 0) {
			vcenter.setCenterX(x);
			vcenter.setCenterY(y);
			halo.setCenterX(x);
			halo.setCenterY(y);
		}
		else {
			halo.speculateCenter(x, y);
			speculativeStateReady = true;
		}
		
	}
	public void exportSpeculativeState(List<KeyValue> kvs) {
		if(speculativeStateReady) {
			kvs.add(new KeyValue(vcenter.centerXProperty(), getSpeculativeCenterX()));
			kvs.add(new KeyValue(vcenter.centerYProperty(), getSpeculativeCenterY()));
			halo.exportSpeculativeState(kvs);
			// kvs.add(new KeyValue(halo.centerXProperty(), getSpeculativeCenterX()));
			// kvs.add(new KeyValue(halo.centerYProperty(), getSpeculativeCenterY()));
		}
		speculativeStateReady = false;
		for(GroupLink link : from) {
			link.exportSpeculativeState(kvs);
		}
		for(GroupLink link : to) {
			link.exportSpeculativeState(kvs);
		}
		for(Node node: members) {
			node.exportSpeculativeState(kvs);
		}
	}
	// public void setNodes() {
	// 	float total = members.size();
	// 	float radius = initialRadius + (float)Math.sqrt(total) * initialRadius * 0.5f;
	// 	float cur = 0;
	// 	for(Node node : members ) {
	// 		float half = 1.0f / total * (float)Math.PI;
	// 		node.setCenter(position.data[0] + radius * (float)Math.cos(cur + half), 
	// 			position.data[1] + radius * (float)Math.sin(cur + half));
	// 		cur += half * 2;
	// 	}
	// }
	// public void setLinks() {
	// 	float x = position.data[0];
	// 	float y = position.data[1];
	// 	for(GroupLink link : from) {
	// 		link.setEnd(x, y);
	// 	}
	// 	for(GroupLink link : to) {
	// 		link.setStart(x, y);
	// 	}
	// }
	// public void setCenter(float x, float y) {
	// 	position.data[0] = x;
	// 	position.data[1] = y;
	// 	speculativeStateReady = false;
	// 	vcenter.setCenterX(x);
	// 	vcenter.setCenterY(y);
	// 	setNodes();
	// 	setLinks();
	// }
}
