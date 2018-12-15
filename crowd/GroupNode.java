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
	private Vec2f centerPosition = new Vec2f();
	private Node center = null;
	// as member of GroupNode
	private boolean coldInstalled = true;
	private Ellipse vcenter;
	private List<GroupLink> from = new ArrayList<GroupLink>();
	private List<GroupLink> to = new ArrayList<GroupLink>();
	private Pane pane;
	private String id;
	private int level = 0;

	GroupNode(Pane pa, String name, Vec2f p) {
		centerPosition.copy(p);
		pane = pa;
		id = name;
		vcenter = new Ellipse();
    vcenter.setCenterX(centerPosition.data[0]);
    vcenter.setCenterY(centerPosition.data[1]);
    vcenter.setRadiusX(3);
    vcenter.setRadiusY(3);
    vcenter.setFill(Color.WHITE);
    vcenter.setStrokeWidth(1);
    vcenter.setStroke(Color.RED);
    pane.getChildren().add(vcenter);
	}
	GroupNode(Pane pa, String name) {
		pane = pa;
		id = name;
		vcenter = new Ellipse();
    vcenter.setCenterX(0);
    vcenter.setCenterY(0);
    vcenter.setRadiusX(3);
    vcenter.setRadiusY(3);
    vcenter.setFill(Color.WHITE);
    vcenter.setStrokeWidth(1);
    vcenter.setStroke(Color.RED);
    pane.getChildren().add(vcenter);
	}
	GroupNode() { }
	static GroupNode NewDummy() {
		GroupNode ret = new GroupNode();
		ret.pane = null;
		ret.id = "dummy";
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
	public Vec2f getCenter() {
		return centerPosition;
	}
	public void fromLink(GroupLink link) {
		link.toGroup = (id);
		link.updateEnd(centerPosition.data[0], centerPosition.data[1]);
		from.add(link);
	}
	public void toLink(GroupLink link) {
		link.fromGroup = id;
		link.updateStart(centerPosition.data[0], centerPosition.data[1]);
		to.add(link);
	}
	public void addMember(Node node) {
		if(node != null) {
			members.add(node);
			expandLayout();
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
	final float initialRadius = 30;
	private float radius = 30;
	public void shrinkLayout() { // clockwise
		final Timeline timeline = new Timeline();
		List<KeyValue> kvs = new ArrayList<KeyValue>();
		float total = members.size();
		radius = initialRadius + (float)Math.sqrt(total) * initialRadius * 0.5f;
		float cur = 0;
		for(Node node : members ) {
			float half = 1.0f / total * (float)Math.PI;
			node.moveTo(centerPosition.data[0] + radius * (float)Math.cos(cur + half), 
				centerPosition.data[1] + radius * (float)Math.sin(cur + half), kvs);
			cur += half * 2;
		}
		KeyFrame frame = new KeyFrame(Duration.millis(2000), "moveMe", null, kvs);
		timeline.getKeyFrames().add(frame);	
		timeline.play();
	}
	public void expandLayout() { // anti-clockwise
		final Timeline timeline = new Timeline();
		List<KeyValue> kvs = new ArrayList<KeyValue>();
		float total = members.size();
		radius = initialRadius + (float)Math.sqrt(total) * initialRadius * 0.5f;
		float cur = 0;
		for(Node node : members ) {
			float half = 1.0f / total * (float)Math.PI;
			node.moveTo(centerPosition.data[0] + radius * (float)Math.cos(cur + half), 
				centerPosition.data[1] + radius * (float)Math.sin(cur + half), 
				kvs);
			cur += half * 2;
		}
		KeyFrame frame = new KeyFrame(Duration.millis(2000), "moveMe", null, kvs);
		timeline.getKeyFrames().add(frame);	
		timeline.play();
	}
	public void moveLayout(float x, float y) { // move with center, use lineTo transition
		final Timeline timeline = new Timeline();
		List<KeyValue> kvs = new ArrayList<KeyValue>();
		// EventHandler onFinished = new EventHandler<ActionEvent>() {
		// 	public void handle(ActionEvent e) {
		// 		System.out.println("event terminated");
		// 	}
		// };
		if(coldInstalled) {
			if(vcenter != null) {
				vcenter.setCenterX(x);
				vcenter.setCenterY(y);
			}
			for(GroupLink link : from) {
				link.updateEnd(x, y);
			}
			for(GroupLink link : to) {
				link.updateStart(x, y);
			}
			coldInstalled = false;
		}
		else {
			if(vcenter != null){
				kvs.add(new KeyValue(vcenter.centerXProperty(), x));
				kvs.add(new KeyValue(vcenter.centerYProperty(), y));
			}
			for(GroupLink link : from) {
				link.moveEnd(x, y, kvs);
			}
			for(GroupLink link : to) {
				link.moveStart(x, y, kvs);
			}
		}
		centerPosition.data[0] = x;
		centerPosition.data[1] = y;

		float total = members.size();
		radius = initialRadius + (float)Math.sqrt(total) * initialRadius * 0.5f;
		float cur = 0;
		for(Node node : members ) {
			float half = 1.0f / total * (float)Math.PI;
			node.moveTo(x + radius * (float)Math.cos(cur + half), 
				y + radius * (float)Math.sin(cur + half), kvs);
			cur += half * 2;
		}
		KeyFrame frame = new KeyFrame(Duration.millis(2000), "moveMe", null, kvs);
		timeline.getKeyFrames().add(frame);	
		timeline.play();
	}
}
