package crowd;

import javafx.animation.KeyValue;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.layout.Pane;

import java.util.*;

public class GroupLink {
	public String fromGroup;
	public String toGroup;
	private Vec2f startPosition = new Vec2f();
	private Vec2f endPosition = new Vec2f();
	public CubicCurve curve;
	private final static float initialStrokeWidth = 0.5f;
	GroupLink(CubicCurve c) {
		curve = c;
	}
	public String toString() {
		return new String("[" 
			+ String.valueOf(curve.getControlX1()) + ", " 
			+ String.valueOf(curve.getControlY1()) + "; "
			+ String.valueOf(curve.getControlX2()) + ", " 
			+ String.valueOf(curve.getControlY2()) + "]");
	}
	public static GroupLink NewLink(Pane pane) {
		CubicCurve curve = new CubicCurve();
    curve.setStroke(Color.BLACK); 
    curve.setStrokeWidth(initialStrokeWidth);
    curve.setFill(Color.TRANSPARENT);
    pane.getChildren().add(curve);
    return new GroupLink(curve);
	}
	public void setEnd(float x, float y) {
		endPosition.data[0] = x;
		endPosition.data[1] = y;
		curve.setEndX(x);
		curve.setEndY(y);
		double deltaX = curve.getEndX() - curve.getStartX();
		curve.setControlX1(curve.getStartX() + deltaX / 2.0f);
		curve.setControlX2(x - deltaX / 2.0f);
		curve.setControlY2(y);
	}
	public void setStart(float x, float y) {
		startPosition.data[0] = x;
		startPosition.data[1] = y;
		curve.setStartX(x);
		curve.setStartY(y);
		double deltaX = curve.getEndX() - curve.getStartX();
		curve.setControlX1(x + deltaX / 2.0f);
		curve.setControlX2(curve.getEndX() - deltaX / 2.0f);
		curve.setControlY1(y);
	}
	public float getStartX() {
		return (float)curve.getStartX();
	}
	public float getStartY() {
		return (float)curve.getStartY();
	}
	public float getEndX() {
		return (float)curve.getEndX();
	}
	public float getEndY() {
		return (float)curve.getEndY();
	}
	public float getSpeculativeStartX() {
		return startPosition.data[0];
	}
	public float getSpeculativeStartY() {
		return startPosition.data[1];
	}
	public float getSpeculativeEndX() {
		return endPosition.data[0];
	}
	public float getSpeculativeEndY() {
		return endPosition.data[1];
	}
	private boolean speculativeStateReady = false;
	public void speculateEnd(float x, float y) {
		endPosition.data[0] = x;
		endPosition.data[1] = y;
		speculativeStateReady = true;
	}
	public void speculateStart(float x, float y) {
		startPosition.data[0] = x;
		startPosition.data[1] = y;
		speculativeStateReady = true;
	}
	public void exportSpeculativeState(List<KeyValue> kvs) {
		if(speculativeStateReady) {
			kvs.add(new KeyValue(curve.startXProperty(), getSpeculativeStartX()));
			kvs.add(new KeyValue(curve.startYProperty(), getSpeculativeStartY()));
			kvs.add(new KeyValue(curve.endXProperty(), getSpeculativeEndX()));
			kvs.add(new KeyValue(curve.endYProperty(), getSpeculativeEndY()));
			kvs.add(new KeyValue(curve.controlX1Property(), (getSpeculativeStartX() + getSpeculativeEndX()) / 2.0f));
			kvs.add(new KeyValue(curve.controlX2Property(), (getSpeculativeStartX() + getSpeculativeEndX()) / 2.0f));
			kvs.add(new KeyValue(curve.controlY1Property(), getSpeculativeStartY()));
			kvs.add(new KeyValue(curve.controlY2Property(), getSpeculativeEndY()));	
		}
		speculativeStateReady = false;
	}
}