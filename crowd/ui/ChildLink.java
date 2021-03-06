package crowd.ui;

import javafx.animation.KeyValue;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.layout.Pane;

import java.util.*;

public class ChildLink {
	public String fromNode;
	public String toNode;
	public Arc curve;
	public Vec2f fromPoint = new Vec2f();
	public Vec2f toPoint = new Vec2f();
	public Vec2f centerPoint = new Vec2f();
	private boolean speculativeStateReady = false;
	private final static float initialStrokeWidth = 1f;
	ChildLink(Arc c) {
		curve = c;
	}
	public static ChildLink NewLink(Pane pane) {
		Arc curve = new Arc();
    // curve.setStroke(Color.DIMGRAY); 
    // curve.setStrokeWidth(initialStrokeWidth); 
    curve.setStrokeWidth(0);
    curve.setFill(Color.TRANSPARENT);
    pane.getChildren().add(curve);
    return new ChildLink(curve);
	}
	public float getStartX() {
		float x = (float)curve.getCenterX();
		float start = (float)curve.getStartAngle();
		float r = (float)curve.getRadiusX();
		return (float)Math.cos(Math.toRadians(start)) * r + x;
	}
	public float getStartY() {
		float y = (float)curve.getCenterY();
		float start = (float)curve.getStartAngle();
		float r = (float)curve.getRadiusX();
		return y - (float)Math.sin(Math.toRadians(start)) * r;
	}
	public float getEndX() {
		float x = (float)curve.getCenterX();
		float start = (float)curve.getStartAngle() + (float)curve.getLength();
		float r = (float)curve.getRadiusX();
		return (float)Math.cos(Math.toRadians(start)) * r + x;
	}
	public float getEndY() {
		float y = (float)curve.getCenterY();
		float start = (float)curve.getStartAngle() + (float)curve.getLength();
		float r = (float)curve.getRadiusX();
		return y - (float)Math.sin(Math.toRadians(start)) * r;
	}
	private float timeToLive = -1;
	public void setEnd(float x, float y, float cx, float cy) {
		solveChildLink(curve, getStartX(), getStartY(), x, y, cx, cy);
		toPoint.data[0] = x;
		toPoint.data[1] = y;
		centerPoint.data[0] = cx;
		centerPoint.data[1] = cy;
		timeToLive = 2000;
	}
	public void setStart(float x, float y, float cx, float cy) {
		solveChildLink(curve, x, y, getEndX(), getEndY(), cx, cy);
		fromPoint.data[0] = x;
		fromPoint.data[1] = y;
		centerPoint.data[0] = cx;
		centerPoint.data[1] = cy;
		timeToLive = 2000;
	}
	public void speculateStart(float x, float y, float cx, float cy) {
		fromPoint.data[0] = x;
		fromPoint.data[1] = y;
		centerPoint.data[0] = cx;
		centerPoint.data[1] = cy;
		speculativeStateReady = true;
	} 
	public void speculateEnd(float x, float y, float cx, float cy) {
		toPoint.data[0] = x;
		toPoint.data[1] = y;
		centerPoint.data[0] = cx;
		centerPoint.data[1] = cy;
		speculativeStateReady = true;
	}
	public float getSpeculativeStartX() {
		return fromPoint.data[0];
	}
	public float getSpeculativeStartY() {
		return fromPoint.data[1];
	}
	public float getSpeculativeEndX() {
		return toPoint.data[0];
	}
	public float getSpeculativeEndY() {
		return toPoint.data[1];
	}
	public float getSpeculativeCenterX() {
		return centerPoint.data[0];
	}
	public float getSpeculativeCenterY() {
		return centerPoint.data[1];
	}
	public void exportSpeculativeState(List<KeyValue> kvs) {
		if(speculativeStateReady){
			solveChildLink(curve, 
				getSpeculativeStartX(), getSpeculativeStartY(), 
				getSpeculativeEndX(), getSpeculativeEndY(), 
				getSpeculativeCenterX(), getSpeculativeCenterY(),
				kvs);	
		}
		// if(timeToLive > 0) {
		// 	kvs.add(new KeyValue(curve.strokeWidthProperty(), 0));
		// 	timeToLive = -1;
		// }
		speculativeStateReady = false;
	}
	public void start(Color color, List<KeyValue> kvs) {
		if(speculativeStateReady){ // fix position
			solveChildLink(curve, 
				getSpeculativeStartX(), getSpeculativeStartY(), 
				getSpeculativeEndX(), getSpeculativeEndY(), 
				getSpeculativeCenterX(), getSpeculativeCenterY(),
				kvs);	
		}
		speculativeStateReady = false;
		// reset stroke
		curve.setStroke(color);
		curve.setStrokeWidth(initialStrokeWidth);
		kvs.add(new KeyValue(curve.strokeWidthProperty(), 0));
	}
	static public void solveChildLink(Arc c, float ax, float ay, float bx, float by, float cx, float cy) { // clockwise ?
		float denom;
		do{ // (bx-ax, by-ay)
			denom = (cx-ax) * (by-ay) - (bx-ax) * (cy-ay);
			if(Math.abs(denom) < 0.005f) {
				cx += Math.abs(by-ay) * 0.05f;
				cy += Math.abs(bx-ax) * 0.05f;
			} else break;
		}while(true);
		float w1 = ax * (cx-ax) + ay * (cy-ay);
		float w2 = cx * (bx-ax) + cy * (by-ay);
		float centerX = (w1 * (by-ay) - w2 * (cy-ay)) / denom;
		float centerY = (w2 * (cx-ax) - w1 * (bx-ax)) / denom;
		c.setCenterX(centerX);
		c.setCenterY(centerY);
		float oldRadius = (float)(Math.pow(cx-ax, 2) + Math.pow(cy-ay, 2));
		float newRadius = (float)((Math.pow(cx-centerX, 2) + Math.pow(cy-centerY, 2)) - oldRadius);
		oldRadius = (float)Math.sqrt(oldRadius);
		newRadius = (float)Math.sqrt(newRadius);
		c.setRadiusX(newRadius);
		c.setRadiusY(newRadius);
		float start;
		if(Math.abs(ax-centerX) > 0.0001) start = (float)Math.atan(- (ay-centerY) / (ax-centerX)); // negate w.r.t. to coord
		else if(ay < centerX) start = (float)Math.PI / 2.0f;
		else start = -(float)Math.PI / 2.0f;
		// if(ay > centerY) start += (float) Math.PI; // center is above start point
		if(ax < centerX) start += (float) Math.PI;
		// if(ay < centerY && ax < centerX && start > 0 || 
			// ay < centerY && ax > centerX && start < 0) start += (float) Math.PI;
		c.setStartAngle(Math.toDegrees(start));
		float length = (float)Math.atan(oldRadius / newRadius) * 2;
		c.setLength(Math.toDegrees(length));
	}
	static public void solveChildLink(Arc c, float ax, float ay, float bx, float by, float cx, float cy, List<KeyValue> kvs) {
		float denom = (cx-ax) * (by-ay) - (bx-ax) * (cy-ay);
		do{ // (bx-ax, by-ay)
			denom = (cx-ax) * (by-ay) - (bx-ax) * (cy-ay);
			if(Math.abs(denom) < 0.005f) {
				cx += Math.abs(by-ay) * 0.05f;
				cy += Math.abs(bx-ax) * 0.05f;
			} else break;
		}while(true);
		float w1 = ax * (cx-ax) + ay * (cy-ay);
		float w2 = cx * (bx-ax) + cy * (by-ay);
		float centerX = (w1 * (by-ay) - w2 * (cy-ay)) / denom;
		float centerY = (w2 * (cx-ax) - w1 * (bx-ax)) / denom;
		kvs.add(new KeyValue(c.centerXProperty(), centerX));
		kvs.add(new KeyValue(c.centerYProperty(), centerY));
		float oldRadius = (float)(Math.pow(cx-ax, 2) + Math.pow(cy-ay, 2));
		float newRadius = (float)((Math.pow(cx-centerX, 2) + Math.pow(cy-centerY, 2)) - oldRadius);
		oldRadius = (float)Math.sqrt(oldRadius);
		newRadius = (float)Math.sqrt(newRadius);
		kvs.add(new KeyValue(c.radiusXProperty(), newRadius));
		kvs.add(new KeyValue(c.radiusYProperty(), newRadius));
		float start;
		if(Math.abs(ax-centerX) > 0.0001) start = (float)Math.atan(- (ay-centerY) / (ax-centerX)); // negate w.r.t. to coord
		else if(ay < centerX) start = (float)Math.PI / 2.0f;
		else start = -(float)Math.PI / 2.0f;
		if(ax < centerX) start += (float) Math.PI;
		kvs.add(new KeyValue(c.startAngleProperty(), Math.toDegrees(start)));
		float length = (float)Math.atan(oldRadius / newRadius) * 2;
		kvs.add(new KeyValue(c.lengthProperty(), Math.toDegrees(length)));
	}
}