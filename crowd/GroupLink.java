package crowd;

import javafx.animation.KeyValue;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.layout.Pane;
import javafx.scene.Group;
import java.util.*;
import javafx.scene.effect.BlendMode;

public class GroupLink {
	public String fromGroup;
	public String toGroup;
	private Vec2f startPosition = new Vec2f();
	private Vec2f endPosition = new Vec2f();
	public CubicCurve curve;
	public CubicCurve shadowCurve;
	public Circle shadowMask;
	// have tried blend(no isolation), mask with main curve(no repeat mask allowed)
	private final static float initialStrokeWidth = 0.5f;
	GroupLink(Pane p) {
		curve = new CubicCurve();
		curve.setStroke(Color.BLACK); 
    curve.setStrokeWidth(initialStrokeWidth);
    curve.setFill(Color.TRANSPARENT);
		shadowCurve = new CubicCurve();
		shadowCurve.setStrokeWidth(5);
		shadowCurve.setStroke(Color.RED);
		shadowCurve.setFill(Color.TRANSPARENT);
		shadowMask = new Circle();
		shadowMask.setRadius(300);	
		shadowCurve.setClip(shadowMask);
		p.getChildren().add(curve);
		p.getChildren().add(shadowCurve);
		// p.add(shadowMask);
	}
	public String toString() {
		return new String("[" 
			+ String.valueOf(curve.getControlX1()) + ", " 
			+ String.valueOf(curve.getControlY1()) + "; "
			+ String.valueOf(curve.getControlX2()) + ", " 
			+ String.valueOf(curve.getControlY2()) + "]");
	}
	public static GroupLink NewLink(Pane pane) {
		return new GroupLink(pane);
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

		shadowCurve.setEndX(x);
		shadowCurve.setEndY(y);
		shadowCurve.setControlX1(curve.getStartX() + deltaX / 2.0f);
		shadowCurve.setControlX2(x - deltaX / 2.0f);
		shadowCurve.setControlY2(y);
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

		shadowCurve.setStartX(x);
		shadowCurve.setStartY(y);
		shadowCurve.setControlX1(x + deltaX / 2.0f);
		shadowCurve.setControlX2(curve.getEndX() - deltaX / 2.0f);
		shadowCurve.setControlY1(y);

		shadowMask.setCenterX(x);
		shadowMask.setCenterY(y);
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

			kvs.add(new KeyValue(shadowCurve.startXProperty(), getSpeculativeStartX()));
			kvs.add(new KeyValue(shadowCurve.startYProperty(), getSpeculativeStartY()));
			kvs.add(new KeyValue(shadowCurve.endXProperty(), getSpeculativeEndX()));
			kvs.add(new KeyValue(shadowCurve.endYProperty(), getSpeculativeEndY()));
			kvs.add(new KeyValue(shadowCurve.controlX1Property(), (getSpeculativeStartX() + getSpeculativeEndX()) / 2.0f));
			kvs.add(new KeyValue(shadowCurve.controlX2Property(), (getSpeculativeStartX() + getSpeculativeEndX()) / 2.0f));
			kvs.add(new KeyValue(shadowCurve.controlY1Property(), getSpeculativeStartY()));
			kvs.add(new KeyValue(shadowCurve.controlY2Property(), getSpeculativeEndY()));	

			kvs.add(new KeyValue(shadowMask.centerXProperty(), getSpeculativeStartX()));
			kvs.add(new KeyValue(shadowMask.centerYProperty(), getSpeculativeStartY()));
		}
		speculativeStateReady = false;
	}
}