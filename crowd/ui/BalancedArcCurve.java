package crowd.ui;

import javafx.scene.paint.*;
import javafx.scene.shape.*;
import javafx.scene.layout.Pane;
import javafx.beans.value.*;
import javafx.animation.KeyValue;
import javafx.beans.property.*;
import java.util.*;

// delegated by member property
public class BalancedArcCurve implements BalancedCurve {
	public Arc curve;
	private DoubleProperty centerXProperty;
	private DoubleProperty centerYProperty;
	private DoubleProperty startXProperty;
	private DoubleProperty startYProperty;
	private DoubleProperty endXProperty;
	private DoubleProperty endYProperty;
	BalancedArcCurve(Pane pane, BalancedArcCurve rhs) {
		centerXProperty = rhs.centerXProperty;
		centerYProperty = rhs.centerYProperty;
		startXProperty = rhs.startXProperty();
		startYProperty = rhs.startYProperty();
		endXProperty = rhs.endXProperty();
		endYProperty = rhs.endYProperty();
		curve = new Arc();
		configureCurve(curve);
		startXProperty.addListener(new ChangeListener(){
			@Override
			public void changed(ObservableValue obj, Object oldVal, Object newVal) {
				solveNodeLink(curve, (double)newVal, startYProperty.getValue(), endXProperty.getValue(), endYProperty.getValue(), centerXProperty.getValue(), centerYProperty.getValue());
			}
		});
		startYProperty.addListener(new ChangeListener(){
			@Override
			public void changed(ObservableValue obj, Object oldVal, Object newVal) {
				solveNodeLink(curve, startXProperty.getValue(), (double)newVal, endXProperty.getValue(), endYProperty.getValue(), centerXProperty.getValue(), centerYProperty.getValue());
			}
		});
		endXProperty.addListener(new ChangeListener(){
			@Override
			public void changed(ObservableValue obj, Object oldVal, Object newVal) {
				solveNodeLink(curve, startXProperty.getValue(), startYProperty.getValue(), (double)newVal, endYProperty.getValue(), centerXProperty.getValue(), centerYProperty.getValue());
			}
		});
		endYProperty.addListener(new ChangeListener(){
			@Override
			public void changed(ObservableValue obj, Object oldVal, Object newVal) {
				solveNodeLink(curve, startXProperty.getValue(), startYProperty.getValue(), endXProperty.getValue(), (double)newVal, centerXProperty.getValue(), centerYProperty.getValue());
			}
		});
		pane.getChildren().add(curve);
	}
	BalancedArcCurve(Pane pane) {
		startXProperty = new SimpleDoubleProperty();
		startYProperty = new SimpleDoubleProperty();
		endXProperty = new SimpleDoubleProperty();
		endYProperty = new SimpleDoubleProperty();
		curve = new Arc();
		configureCurve(curve);
		startXProperty.addListener(new ChangeListener(){
			@Override
			public void changed(ObservableValue obj, Object oldVal, Object newVal) {
				solveNodeLink(curve, (double)newVal, startYProperty.getValue(), endXProperty.getValue(), endYProperty.getValue(), centerXProperty.getValue(), centerYProperty.getValue());
			}
		});
		startYProperty.addListener(new ChangeListener(){
			@Override
			public void changed(ObservableValue obj, Object oldVal, Object newVal) {
				solveNodeLink(curve, startXProperty.getValue(), (double)newVal, endXProperty.getValue(), endYProperty.getValue(), centerXProperty.getValue(), centerYProperty.getValue());
			}
		});
		endXProperty.addListener(new ChangeListener(){
			@Override
			public void changed(ObservableValue obj, Object oldVal, Object newVal) {
				solveNodeLink(curve, startXProperty.getValue(), startYProperty.getValue(), (double)newVal, endYProperty.getValue(), centerXProperty.getValue(), centerYProperty.getValue());
			}
		});
		endYProperty.addListener(new ChangeListener(){
			@Override
			public void changed(ObservableValue obj, Object oldVal, Object newVal) {
				solveNodeLink(curve, startXProperty.getValue(), startYProperty.getValue(), endXProperty.getValue(), (double)newVal, centerXProperty.getValue(), centerYProperty.getValue());
			}
		});
		pane.getChildren().add(curve);
	}
	public Shape getRawRef() {
		return curve;
	}
	public BalancedCurve copy(Pane p) {
		BalancedArcCurve ret = new BalancedArcCurve(p, this);
		ret.setStart(startXProperty.getValue(), startYProperty.getValue()); // refresh layout
		ret.setEnd(endXProperty.getValue(), endYProperty.getValue());
		ret.setColor(curve.getStroke());
		ret.setWidth(curve.getStrokeWidth());
		return ret;
	}
	public void setWidth(double w) {
		curve.setStrokeWidth(w);
	}
	public DoubleProperty widthProperty() {
		return curve.strokeWidthProperty(); 
	}
	public void setColor(Paint c) {
		curve.setStroke(c);
	}
	public ObjectProperty<Paint> colorProperty() {
		return curve.strokeProperty();
	}
	public void setCenterXProperty(DoubleProperty centerX) {
		centerXProperty = centerX;
		centerXProperty.addListener(new ChangeListener(){
			@Override
			public void changed(ObservableValue obj, Object oldVal, Object newVal) {
				solveNodeLink(curve, getStartX(), getStartY(), getEndX(), getEndY(), (double)newVal, centerYProperty.getValue());
			}
		});
	}
	public void setCenterYProperty(DoubleProperty centerY) {
		centerYProperty = centerY;
		centerYProperty.addListener(new ChangeListener(){
			@Override
			public void changed(ObservableValue obj, Object oldVal, Object newVal) {
				solveNodeLink(curve, getStartX(), getStartY(), getEndX(), getEndY(), centerXProperty.getValue(), (double)newVal);
			}
		});
	}
	private static void configureCurve(Arc c) {
		c.setStrokeWidth(0);
		c.setFill(Color.TRANSPARENT);
	}
	public void setStart(double x, double y){
		startXProperty.setValue(x);
		startYProperty.setValue(y);
	}
	public double getStartX(){
		return startXProperty.getValue();
	}
	public double getStartY(){
		return startYProperty.getValue();
	}
	public void setEnd(double x, double y){
		endXProperty.setValue(x);
		endYProperty.setValue(y);
	}
	public double getEndX(){
		return endXProperty.getValue();
	}
	public double getEndY(){
		return endYProperty.getValue();
	}
	public DoubleProperty startXProperty(){
		return startXProperty;
	}
	public DoubleProperty startYProperty(){
		return startYProperty;
	}
	public DoubleProperty endXProperty(){
		return endXProperty;
	}
	public DoubleProperty endYProperty(){
		return endYProperty;
	}
	static public void solveNodeLink(Arc c, double ax, double ay, double bx, double by, double cx, double cy) { // clockwise ?
		double denom = (cx-ax) * (by-ay) - (bx-ax) * (cy-ay);
		double w1 = ax * (cx-ax) + ay * (cy-ay);
		double w2 = cx * (bx-ax) + cy * (by-ay);
		double centerX = (w1 * (by-ay) - w2 * (cy-ay)) / denom;
		double centerY = (w2 * (cx-ax) - w1 * (bx-ax)) / denom;
		c.setCenterX(centerX);
		c.setCenterY(centerY);
		double oldRadius =  (Math.pow(cx-ax, 2) + Math.pow(cy-ay, 2));
		double newRadius =  ((Math.pow(cx-centerX, 2) + Math.pow(cy-centerY, 2)) - oldRadius);
		oldRadius =  Math.sqrt(oldRadius);
		newRadius =  Math.sqrt(newRadius);
		c.setRadiusX(newRadius);
		c.setRadiusY(newRadius);
		double start;
		if(Math.abs(ax-centerX) > 0.0001) start =  Math.atan(- (ay-centerY) / (ax-centerX)); // negate w.r.t. to coord
		else if(ay < centerX) start =  Math.PI / 2.0f;
		else start = - Math.PI / 2.0f;
		// if(ay > centerY) start +=   Math.PI; // center is above start point
		if(ax < centerX) start +=   Math.PI;
		// if(ay < centerY && ax < centerX && start > 0 || 
			// ay < centerY && ax > centerX && start < 0) start +=   Math.PI;
		c.setStartAngle(Math.toDegrees(start));
		double length =  Math.atan(oldRadius / newRadius) * 2;
		c.setLength(Math.toDegrees(length));
	}
	static public void solveNodeLink(Arc c, double ax, double ay, double bx, double by, double cx, double cy, List<KeyValue> kvs) {
		double denom = (cx-ax) * (by-ay) - (bx-ax) * (cy-ay);
		double w1 = ax * (cx-ax) + ay * (cy-ay);
		double w2 = cx * (bx-ax) + cy * (by-ay);
		double centerX = (w1 * (by-ay) - w2 * (cy-ay)) / denom;
		double centerY = (w2 * (cx-ax) - w1 * (bx-ax)) / denom;
		kvs.add(new KeyValue(c.centerXProperty(), centerX));
		kvs.add(new KeyValue(c.centerYProperty(), centerY));
		double oldRadius =  (Math.pow(cx-ax, 2) + Math.pow(cy-ay, 2));
		double newRadius =  ((Math.pow(cx-centerX, 2) + Math.pow(cy-centerY, 2)) - oldRadius);
		oldRadius =  Math.sqrt(oldRadius);
		newRadius =  Math.sqrt(newRadius);
		kvs.add(new KeyValue(c.radiusXProperty(), newRadius));
		kvs.add(new KeyValue(c.radiusYProperty(), newRadius));
		double start;
		if(Math.abs(ax-centerX) > 0.0001) start =  Math.atan(- (ay-centerY) / (ax-centerX)); // negate w.r.t. to coord
		else if(ay < centerX) start =  Math.PI / 2.0f;
		else start = - Math.PI / 2.0f;
		if(ax < centerX) start +=   Math.PI;
		kvs.add(new KeyValue(c.startAngleProperty(), Math.toDegrees(start)));
		double length =  Math.atan(oldRadius / newRadius) * 2;
		kvs.add(new KeyValue(c.lengthProperty(), Math.toDegrees(length)));
	}
}