package crowd;

import javafx.animation.KeyValue;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.layout.Pane;

import java.util.*;

public class NodeLink {
	public String fromNode;
	public String toNode;
	public Arc curve;
	public Vec2f fromPoint = new Vec2f();
	public Vec2f toPoint = new Vec2f();
	NodeLink(Arc c) {
		curve = c;
	}
	public static NodeLink NewLink(Pane pane) {
		Arc curve = new Arc();
    curve.setStroke(Color.BLACK); 
    curve.setStrokeWidth(3); 
    curve.setFill(Color.TRANSPARENT);
    pane.getChildren().add(curve);
    return new NodeLink(curve);
	}
	public void updateEnd(float x, float y, float cx, float cy) {
		toPoint.data[0] = x;
		toPoint.data[1] = y;
		update(cx, cy);
	}
	public void updateStart(float x, float y, float cx, float cy) {
		fromPoint.data[0] = x; 
		fromPoint.data[1] = y;
		update(cx, cy);
	}
	public void update(float cx, float cy) {
		float denom = (cx-fromPoint.data[0]) * (toPoint.data[1]-fromPoint.data[1]) - (toPoint.data[0]-fromPoint.data[0]) * (cy-fromPoint.data[1]);
		float w1 = fromPoint.data[0] * (cx-fromPoint.data[0]) + fromPoint.data[1] * (cy-fromPoint.data[1]);
		float w2 = cx * (toPoint.data[0]-fromPoint.data[0]) + cy * (toPoint.data[1]-fromPoint.data[1]);
		float centerX = (w1 * (toPoint.data[1]-fromPoint.data[1]) - w2 * (cy-fromPoint.data[1])) / denom;
		float centerY = (w2 * (cx-fromPoint.data[0]) - w1 * (toPoint.data[0]-fromPoint.data[0])) / denom;
		curve.setCenterX(centerX);
		curve.setCenterY(centerY);
		float oldRadius = (float)(Math.pow(cx-fromPoint.data[0], 2) + Math.pow(cy-fromPoint.data[1], 2));
		float newRadius = (float)((Math.pow(cx-centerX, 2) + Math.pow(cy-centerY, 2)) - oldRadius);
		oldRadius = (float)Math.sqrt(oldRadius);
		newRadius = (float)Math.sqrt(newRadius);
		curve.setRadiusX(newRadius);
		curve.setRadiusY(newRadius);
		float start;
		if(Math.abs(fromPoint.data[0]-centerX) > 0.0001) start = (float)Math.atan(- (fromPoint.data[1]-centerY) / (fromPoint.data[0]-centerX)); // negate w.r.t. to coord
		else if(fromPoint.data[1] < centerX) start = (float)Math.PI / 2.0f;
		else start = -(float)Math.PI / 2.0f;
		// if(fromPoint.data[1] > centerY) start += (float) Math.PI; // center is above start point
		if(fromPoint.data[0] < centerX) start += (float) Math.PI;
		// if(fromPoint.data[1] < centerY && fromPoint.data[0] < centerX && start > 0 || 
			// fromPoint.data[1] < centerY && fromPoint.data[0] > centerX && start < 0) start += (float) Math.PI;
		curve.setStartAngle(Math.toDegrees(start));
		float length = (float)Math.atan(oldRadius / newRadius) * 2;
		curve.setLength(Math.toDegrees(length));
		System.out.println(curve.toString());
	}
	public void move(float cx, float cy, List<KeyValue> kvs) {
		float denom = (cx-fromPoint.data[0]) * (toPoint.data[1]-fromPoint.data[1]) - (toPoint.data[0]-fromPoint.data[0]) * (cy-fromPoint.data[1]);
		float w1 = fromPoint.data[0] * (cx-fromPoint.data[0]) + fromPoint.data[1] * (cy-fromPoint.data[1]);
		float w2 = cx * (toPoint.data[0]-fromPoint.data[0]) + cy * (toPoint.data[1]-fromPoint.data[1]);
		float centerX = (w1 * (toPoint.data[1]-fromPoint.data[1]) - w2 * (cy-fromPoint.data[1])) / denom;
		float centerY = (w2 * (cx-fromPoint.data[0]) - w1 * (toPoint.data[0]-fromPoint.data[0])) / denom;
		kvs.add(new KeyValue(curve.centerXProperty(), centerX));
		kvs.add(new KeyValue(curve.centerYProperty(), centerY));
		float oldRadius = (float)(Math.pow(cx-fromPoint.data[0], 2) + Math.pow(cy-fromPoint.data[1], 2));
		float newRadius = (float)((Math.pow(cx-centerX, 2) + Math.pow(cy-centerY, 2)) - oldRadius);
		oldRadius = (float)Math.sqrt(oldRadius);
		newRadius = (float)Math.sqrt(newRadius);
		kvs.add(new KeyValue(curve.radiusXProperty(), newRadius));
		kvs.add(new KeyValue(curve.radiusYProperty(), newRadius));
		float start;
		if(Math.abs(fromPoint.data[0]-centerX) > 0.0001) start = (float)Math.atan(- (fromPoint.data[1]-centerY) / (fromPoint.data[0]-centerX)); // negate w.r.t. to coord
		else if(fromPoint.data[1] < centerX) start = (float)Math.PI / 2.0f;
		else start = -(float)Math.PI / 2.0f;
		if(fromPoint.data[0] < centerX) start += (float) Math.PI;
		kvs.add(new KeyValue(curve.startAngleProperty(), Math.toDegrees(start)));
		float length = (float)Math.atan(oldRadius / newRadius) * 2;
		kvs.add(new KeyValue(curve.lengthProperty(), Math.toDegrees(length)));
	}
	public void moveEnd(float x, float y, float cx, float cy, List<KeyValue> kvs) {
		toPoint.data[0] = x;
		toPoint.data[1] = y;
		move(cx, cy, kvs);
	}
	public void moveStart(float x, float y, float cx, float cy, List<KeyValue> kvs) {
		fromPoint.data[0] = x; 
		fromPoint.data[1] = y;
		move(cx, cy, kvs);
	}
}