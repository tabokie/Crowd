package crowd.ui;

import javafx.scene.shape.*;
import javafx.scene.layout.Pane;

import javafx.animation.*;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import javafx.animation.KeyValue;

import java.util.*;

public class ChildNode {
	private Vec2f position = new Vec2f(-1, -1);
	public Circle self = new Circle() ;
	private GroupNode parent;
	private Pane pane;
	private String id;
	private List<ChildLink> from = new ArrayList<ChildLink>();
	private List<ChildLink> to = new ArrayList<ChildLink>();

	ChildNode(Pane p, GroupNode group, String name) {
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
			for(ChildLink link: from) {
				link.speculateEnd(x, y, parent.getSpeculativeCenterX(), parent.getSpeculativeCenterY());
			}
			for(ChildLink link: to) {
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
		for(ChildLink link: from) {
			link.exportSpeculativeState(appendList);
		}
		for(ChildLink link: to) {
			link.exportSpeculativeState(appendList);
		}
		speculativeStateReady = false;
	}
	public void fromLink(ChildLink link) {
		from.add(link);
		link.toNode = id;
		link.setEnd(getCenterX(), getCenterY(), parent.getCenterX(), parent.getCenterY());
		link.speculateEnd(getSpeculativeCenterX(), getSpeculativeCenterY(), parent.getSpeculativeCenterX(), parent.getSpeculativeCenterY());
	}
	public void toLink(ChildLink link) {
		to.add(link);
		link.fromNode = id;
		link.setStart(getCenterX(), getCenterY(), parent.getCenterX(), parent.getCenterY());
		link.speculateStart(getSpeculativeCenterX(), getSpeculativeCenterY(), parent.getSpeculativeCenterX(), parent.getSpeculativeCenterY());
	}
	public ChildLink getOut(String id) {
		for(ChildLink li : to) {
			if(li.toNode.equals(id)){
				return li;
			}
		}
		return null;
	}
	public ChildLink getIn(String id) {
		for(ChildLink li : from) {
			if(li.fromNode.equals(id)){
				return li;
			}
		}
		return null;
	}
	public GroupNode getParent() {
		return parent;
	}
	public void setParent(GroupNode group) {
		parent = group;
		parent.addMember(this);
	} 
}