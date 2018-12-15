package crowd;

import javafx.animation.KeyValue;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.layout.Pane;

import java.util.*;

public class GroupLink {
	public String fromGroup;
	public String toGroup;
	public CubicCurve curve;
	GroupLink(CubicCurve c) {
		curve = c;
	}
	public static GroupLink NewLink(Pane pane) {
		CubicCurve curve = new CubicCurve();
    curve.setStroke(Color.BLACK); 
    curve.setStrokeWidth(3); 
    curve.setFill(Color.TRANSPARENT);
    pane.getChildren().add(curve);
    return new GroupLink(curve);
	}
	public void updateEnd(float x, float y) {
		curve.setEndX(x);
		curve.setEndY(y);
		double deltaX = curve.getEndX() - curve.getStartX();
		curve.setControlX1(curve.getStartX() + deltaX / 2.0f);
		curve.setControlX2(x - deltaX / 2.0f);
		curve.setControlY2(y);
	}
	public void updateStart(float x, float y) {
		curve.setStartX(x);
		curve.setStartY(y);
		double deltaX = curve.getEndX() - curve.getStartX();
		curve.setControlX1(x + deltaX / 2.0f);
		curve.setControlX2(curve.getEndX() - deltaX / 2.0f);
		curve.setControlY1(y);
	}
	public void moveEnd(float x, float y, List<KeyValue> kvs) {
		kvs.add(new KeyValue(curve.endXProperty(), x));
		kvs.add(new KeyValue(curve.endYProperty(), y));
		kvs.add(new KeyValue(curve.controlX1Property(), curve.getStartX() + (curve.getEndX() - curve.getStartX()) / 2.0f));
		kvs.add(new KeyValue(curve.controlX2Property(), x - (curve.getEndX() - curve.getStartX()) / 2.0f));
		kvs.add(new KeyValue(curve.controlY2Property(), y));
	}
	public void moveStart(float x, float y, List<KeyValue> kvs) {
		kvs.add(new KeyValue(curve.startXProperty(), x));
		kvs.add(new KeyValue(curve.startYProperty(), y));
		kvs.add(new KeyValue(curve.controlX1Property(), x + (curve.getEndX() - curve.getStartX()) / 2.0f));
		kvs.add(new KeyValue(curve.controlX2Property(), curve.getEndX() - (curve.getEndX() - curve.getStartX()) / 2.0f));
		kvs.add(new KeyValue(curve.controlY1Property(), y));
	}
}