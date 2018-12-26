package crowd.ui;

import javafx.scene.shape.*;
import javafx.beans.property.*;
import javafx.beans.value.*;
import javafx.animation.KeyValue;
import java.util.*;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

public class RippleModifier{
	private BalancedCurve curve;
	private BalancedCurve shadow;
	private Circle mask; // controlled by shadow and progress
	private AnimatedRadialGradient gradient; // controlled by shadow and progress
	private DoubleProperty progress = new SimpleDoubleProperty(0);
	// for speculation
	private boolean startReady = false;
	private double startX = -1, startY = -1;
	private boolean endReady = false; 
	private double endX = -1, endY = -1;
	private boolean baseColorReady = false, rippleColorReady = false;
	private Color base = Color.BLACK, ripple = Color.RED;
	private boolean baseWidthReady = false, rippleWidthReady = false;
	private double baseWidth = 1, rippleWidth = 3;
	private boolean progressReady = false;
	private double progressValue = 0;
	RippleModifier(BalancedCurve prototype, Pane pane) {
		curve = prototype;
		shadow = curve.copy(pane);
		curve.setWidth(1);
		curve.setColor(Color.BLACK);
		shadow.setWidth(3);
		shadow.setColor(Color.RED);
		mask = new Circle();
		gradient = new AnimatedRadialGradient(mask, Color.BLACK);
		shadow.startXProperty().addListener(new ChangeListener(){
			@Override
			public void changed(ObservableValue obj, Object oldVal, Object newVal) {
				mask.setCenterX((double)newVal);
				gradient.setCenterX((double)newVal);
			}
		}); // take over gradient's property
		shadow.startYProperty().addListener(new ChangeListener(){
			@Override
			public void changed(ObservableValue obj, Object oldVal, Object newVal) {
				mask.setCenterY((double)newVal);
				gradient.setCenterY((double)newVal);
			}
		});
		shadow.endXProperty().addListener(new ChangeListener(){
			@Override
			public void changed(ObservableValue obj, Object oldVal, Object newVal) {
				double radius = Math.sqrt(Math.pow((double)newVal - (double)oldVal, 2) + Math.pow(shadow.getEndY() - shadow.getStartY(), 2));
				mask.setRadius(radius);
				gradient.setRadius(getProgress() * radius);
			}
		});
		shadow.endYProperty().addListener(new ChangeListener(){
			@Override
			public void changed(ObservableValue obj, Object oldVal, Object newVal) {
				double radius = Math.sqrt(Math.pow((double)newVal - (double)oldVal, 2) + Math.pow(shadow.getEndX() - shadow.getStartX(), 2));
				mask.setRadius(getProgress() * radius);
				gradient.setRadius(getProgress() * radius);
			}
		});
		progress.addListener(new ChangeListener(){
			@Override
			public void changed(ObservableValue obj, Object oldVal, Object newVal) {
				double radius = Math.sqrt(Math.pow(shadow.getEndY() - shadow.getStartY(), 2) + Math.pow(shadow.getEndX() - shadow.getStartX(), 2));
				mask.setRadius(getProgress() * radius);
				gradient.setRadius(getProgress() * radius);
			}
		});
		shadow.getRawRef().setClip(mask);
		// pane.getChildren().add(mask); // can;t add
	}
	public void setRippleColor(Color c) {
		shadow.setColor(c);
		rippleColorReady = false;
	}
	public void setBaseColor(Color c){
		curve.setColor(c);
		baseColorReady = false;
	}
	public void setRippleWidth(double w) {
		shadow.setWidth(w);
		rippleWidthReady = false;
	}
	public void setBaseWidth(double w) {
		curve.setWidth(w);
		baseWidthReady = false;
	}
	public void speculateRippleColor(Color c) {
		ripple = c;
		rippleColorReady = true;
	}
	public void speculateBaseColor(Color c) {
		base = c;
		baseColorReady = true;
	}
	public void speculateRippleWidth(double w) {
		rippleWidth = w;
		rippleWidthReady = true;
	}
	public void speculateBaseWidth(double w) {
		baseWidth = w;
		baseWidthReady = true;
	}
	public double getStartX() {
		return curve.getStartX();
	}
	public double getStartY() {
		return curve.getStartY();
	}
	public double getEndX(){
		return curve.getEndX();
	}
	public double getEndY() {
		return curve.getEndY();
	}
 	public void setStart(float x, float y) {
		curve.setStart(x, y);
		shadow.setStart(x, y);
		startReady = false;
	}
	public void setEnd(float x, float y) {
		curve.setEnd(x, y);
		shadow.setEnd(x, y);
		startReady = false;
	}
	public void speculateStart(float x, float y) {
		startX = x;
		startY = y;
		startReady = true;
	}
	public void speculateEnd(float x, float y) {
		endX = x;
		endY = y;
		endReady = true;
	}
	public double getProgress() {
		return progress.getValue();
	}
	public void setProgress(double x) {
		progress.setValue(x);
		progressReady = false;
	}
	public void speculateProgress(double x) {
		progressValue = x;
		progressReady = true;
	}
	public void startRipple(List<KeyValue> kvs) {
		progress.setValue(0);
		kvs.add(new KeyValue(progress, 2));
		progressReady = false;
	}
	public void exportSpeculativeState(List<KeyValue> kvs) {
		if(startReady) {
			kvs.add(new KeyValue(curve.startXProperty(), startX));
			kvs.add(new KeyValue(curve.startYProperty(), startY));
			kvs.add(new KeyValue(shadow.startXProperty(), startX));
			kvs.add(new KeyValue(shadow.startYProperty(), startY));
			startReady = false;
		}
		if(endReady) {
			kvs.add(new KeyValue(curve.endXProperty(), endX));
			kvs.add(new KeyValue(curve.endYProperty(), endY));
			kvs.add(new KeyValue(shadow.endXProperty(), endX));
			kvs.add(new KeyValue(shadow.endYProperty(), endY));
			endReady = false;
		}
		if(baseColorReady) {
			kvs.add(new KeyValue(curve.colorProperty(), base));
			baseColorReady = false;
		}
		if(baseWidthReady) {
			kvs.add(new KeyValue(curve.widthProperty(), baseWidth));
			baseWidthReady = false;
		}
		if(rippleColorReady) {
			kvs.add(new KeyValue(shadow.colorProperty(), ripple));
			rippleColorReady = false;
		}
		if(rippleWidthReady) {
			kvs.add(new KeyValue(shadow.widthProperty(), rippleWidth));
			rippleWidthReady = false;
		}
		if(progressReady) {
			kvs.add(new KeyValue(progress, progressValue));
			progressReady = false;
		}
	}
	public static RippleModifier NewArcCurve(Pane pane) {
		BalancedCurve c = new BalancedArcCurve(pane);
		return new RippleModifier(c, pane);
	}
	public static RippleModifier NewCubicCurve(Pane pane) {
		BalancedCurve c = new BalancedCubicCurve(pane);
		return new RippleModifier(c, pane);
	}
} 