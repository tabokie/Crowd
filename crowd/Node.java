package crowd;

import javafx.scene.shape.*;
import javafx.scene.layout.Pane;

import javafx.animation.*;
import javafx.util.Duration;
import javafx.animation.KeyValue;

import java.util.*;

public class Node {
	private Vec2f position = new Vec2f();
	public Ellipse self = null;
	private GroupNode parent;
	private Pane pane;
	private String id;
	private List<NodeLink> from = new ArrayList<NodeLink>();
	private List<NodeLink> to = new ArrayList<NodeLink>();

	Node(Pane p, GroupNode group, String name) {
		parent = group;
		pane = p;
		id = name;
		parent.addMember(this);
	}
	public String getId() {
		return id;
	}
	public void moveTo(Vec2f a, List<KeyValue> list) {
		moveTo(a.data[0], a.data[1], list);
	}
	public void moveTo(float x, float y, List<KeyValue> appendList) {
		if(self == null) {
			self = new Ellipse();
	    self.setCenterX(x);
	    self.setCenterY(y);
	    self.setRadiusX(2);
	    self.setRadiusY(2);
	    pane.getChildren().add(self);
		}
		else{
			appendList.add(new KeyValue(self.centerXProperty(), x));
			appendList.add(new KeyValue(self.centerYProperty(), y));
		}
		for(NodeLink link: from) {
			link.moveEnd(x, y, parent.getCenter().data[0], parent.getCenter().data[1], appendList);
		}
		for(NodeLink link: from) {
			link.moveStart(x, y, parent.getCenter().data[0], parent.getCenter().data[1], appendList);
		}
		position.data[0] = x;
		position.data[1] = y;
	}
	public void fromLink(NodeLink link) {
		from.add(link);
		link.toNode = id;
		link.updateEnd(position.data[0], position.data[1], parent.getCenter().data[0], parent.getCenter().data[1]);
	}
	public void toLink(NodeLink link) {
		to.add(link);
		link.fromNode = id;
		link.updateStart(position.data[0], position.data[1], parent.getCenter().data[0], parent.getCenter().data[1]);
	}
	public GroupNode getParent() {
		return parent;
	}
	public void setParent(GroupNode group) {
		parent = group;
		parent.addMember(this);
	} 
}