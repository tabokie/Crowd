package crowd.ui;

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
	// have tried blend(no isolation), mask with main curve(no repeat mask allowed)
	public RippleModifier curve;
	private final static float initialStrokeWidth = 0.5f;
	GroupLink(Pane p) {
		curve = RippleModifier.NewCubicCurve(p);
	}
	public static GroupLink NewLink(Pane pane) {
		return new GroupLink(pane);
	}
	public void setEnd(float x, float y) {
		curve.setEnd(x, y);
	}
	public void setStart(float x, float y) {
		curve.setStart(x, y);
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
	public void speculateEnd(float x, float y) {
		curve.speculateEnd(x, y);
	}
	public void speculateStart(float x, float y) {
		curve.speculateStart(x, y);
	}
	public void exportSpeculativeState(List<KeyValue> kvs) {
		curve.exportSpeculativeState(kvs);
	}
}