package crowd;

import javafx.scene.shape.*;
import javafx.scene.layout.Pane;

import javafx.animation.*;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import javafx.animation.KeyValue;

import java.util.*;

public class Node {
	private Vec2f position = new Vec2f(-1, -1);
	public Circle self = new Circle() ;
	private GroupNode parent;
	private Pane pane;
	private String id;
	private List<NodeLink> from = new ArrayList<NodeLink>();
	private List<NodeLink> to = new ArrayList<NodeLink>();

	Node(Pane p, GroupNode group, String name) {
		parent = group;
		pane = p;
		id = name;
    self.setCenterX(-1);
    self.setCenterY(-1);
    self.setRadius(2);
    self.setRadius(2);
    self.setFill(Color.DARKORANGE);
    self.setStrokeWidth(0);
    pane.getChildren().add(self);
		parent.addMember(this);
	}
	public String getId() {
		return id;
	}
	public float getCenterX() {
		return (float)self.getCenterX();
	}
	public float getCenterY() {
		return (float)self.getCenterY();
	}
	public float getSpeculativeCenterX() {
		return position.data[0];
	}
	public float getSpeculativeCenterY() {
		return position.data[1];
	}
	private boolean speculativeStateReady = false;
	public void speculateCenter(float x, float y) {
		if(getCenterX() < 0) {
			self.setCenterX(x);
			self.setCenterY(y);
		}
		else {
			for(NodeLink link: from) {
				link.speculateEnd(x, y, parent.getSpeculativeCenterX(), parent.getSpeculativeCenterY());
			}
			for(NodeLink link: to) {
				link.speculateStart(x, y, parent.getSpeculativeCenterX(), parent.getSpeculativeCenterY());
			}
			speculativeStateReady = true;
		}
		position.data[0] = x;
		position.data[1] = y;
	}
	public void exportSpeculativeState(List<KeyValue> appendList) {
		if(speculativeStateReady) {
			appendList.add(new KeyValue(self.centerXProperty(), getSpeculativeCenterX()));
			appendList.add(new KeyValue(self.centerYProperty(), getSpeculativeCenterY()));
		}
		for(NodeLink link: from) {
			link.exportSpeculativeState(appendList);
		}
		for(NodeLink link: to) {
			link.exportSpeculativeState(appendList);
		}
		speculativeStateReady = false;
	}
	public void fromLink(NodeLink link) {
		from.add(link);
		link.toNode = id;
		link.setEnd(getCenterX(), getCenterY(), parent.getCenterX(), parent.getCenterY());
		link.speculateEnd(getSpeculativeCenterX(), getSpeculativeCenterY(), parent.getSpeculativeCenterX(), parent.getSpeculativeCenterY());
	}
	public void toLink(NodeLink link) {
		to.add(link);
		link.fromNode = id;
		link.setStart(getCenterX(), getCenterY(), parent.getCenterX(), parent.getCenterY());
		link.speculateStart(getSpeculativeCenterX(), getSpeculativeCenterY(), parent.getSpeculativeCenterX(), parent.getSpeculativeCenterY());
	}
	public GroupNode getParent() {
		return parent;
	}
	public void setParent(GroupNode group) {
		parent = group;
		parent.addMember(this);
	} 
}