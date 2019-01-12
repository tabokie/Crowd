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
	private BalancedCurve rshadow;
	private Circle mask; // controlled by shadow and progress
	private Circle rmask;
	private AnimatedRadialGradient gradient; // controlled by shadow and progress
	private AnimatedRadialGradient rgradient;
	private DoubleProperty progress = new SimpleDoubleProperty(0);
	private DoubleProperty rprogress = new SimpleDoubleProperty(0);
	// for speculation
	private boolean startReady = false;
	private double startX = -1, startY = -1;
	private boolean endReady = false; 
	private double endX = -1, endY = -1;
	private boolean baseColorReady = false, rippleColorReady = false;
	private Color base = Color.DIMGRAY, ripple = Color.GREEN, rripple = Color.RED;
	private boolean baseWidthReady = false, rippleWidthReady = false;
	private double baseWidth = 1, rippleWidth = 3;
	private boolean progressReady = false;
	private boolean rprogressReady = false;
	private double progressValue = 0;
	private double rprogressValue = 0;
	RippleModifier(BalancedCurve prototype, Pane pane) {
		curve = prototype;
		shadow = curve.copy(pane); // bound to curve geometrily
		rshadow = curve.copy(pane);
		curve.setWidth(1);
		curve.setColor(base);
		shadow.setWidth(3);
		shadow.setColor(ripple);
		rshadow.setWidth(3);
		rshadow.setColor(rripple);
		mask = new Circle();
		rmask = new Circle();
		gradient = new AnimatedRadialGradient(mask, Color.BLACK);
		rgradient = new AnimatedRadialGradient(rmask, Color.BLACK);
		curve.startXProperty().addListener(new ChangeListener(){
			@Override
			public void changed(ObservableValue obj, Object oldVal, Object newVal) {
				double radius = Math.sqrt(Math.pow((double)newVal - curve.getEndX(), 2) + Math.pow(curve.getEndY() - curve.getStartY(), 2));
				mask.setCenterX((double)newVal);
				gradient.setCenterX((double)newVal);
				rmask.setRadius(getRProgress() * radius);
				rgradient.setRadius(radius);
			}
		}); // take over gradient's property
		curve.startYProperty().addListener(new ChangeListener(){
			@Override
			public void changed(ObservableValue obj, Object oldVal, Object newVal) {
				double radius = Math.sqrt(Math.pow((double)newVal - curve.getEndY(), 2) + Math.pow(curve.getEndX() - curve.getStartX(), 2));
				mask.setCenterY((double)newVal);
				gradient.setCenterY((double)newVal);
				rmask.setRadius(getRProgress() * radius);
				rgradient.setRadius(radius);
			}
		});
		curve.endXProperty().addListener(new ChangeListener(){
			@Override
			public void changed(ObservableValue obj, Object oldVal, Object newVal) {
				double radius = Math.sqrt(Math.pow((double)newVal - curve.getStartX(), 2) + Math.pow(curve.getEndY() - curve.getStartY(), 2));
				mask.setRadius(getProgress() * radius);
				gradient.setRadius(getProgress() * radius);
				rmask.setCenterX((double)newVal);
				rgradient.setCenterX((double)newVal);
			}
		});
		curve.endYProperty().addListener(new ChangeListener(){
			@Override
			public void changed(ObservableValue obj, Object oldVal, Object newVal) {
				double radius = Math.sqrt(Math.pow((double)newVal - curve.getStartY(), 2) + Math.pow(curve.getEndX() - curve.getStartX(), 2));
				mask.setRadius(getProgress() * radius);
				gradient.setRadius(getProgress() * radius);
				rmask.setCenterY((double)newVal);
				rgradient.setCenterY((double)newVal);
			}
		});
		progress.addListener(new ChangeListener(){
			@Override
			public void changed(ObservableValue obj, Object oldVal, Object newVal) {
				double radius = Math.sqrt(Math.pow(curve.getEndY() - curve.getStartY(), 2) + Math.pow(curve.getEndX() - curve.getStartX(), 2));
				mask.setRadius(getProgress() * radius);
				gradient.setRadius(getProgress() * radius);
			}
		});
		rprogress.addListener(new ChangeListener(){
			@Override
			public void changed(ObservableValue obj, Object oldVal, Object newVal) {
				double radius = Math.sqrt(Math.pow(curve.getEndY() - curve.getStartY(), 2) + Math.pow(curve.getEndX() - curve.getStartX(), 2));
				rmask.setRadius(getRProgress() * radius);
				rgradient.setRadius(getRProgress() * radius);
			}
		});
		shadow.getRawRef().setClip(mask);
		rshadow.getRawRef().setClip(rmask);
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
		// shadow.setStart(x, y);
		startReady = false;
	}
	public void setEnd(float x, float y) {
		curve.setEnd(x, y);
		// shadow.setEnd(x, y);
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
	public double getRProgress() {
		return rprogress.getValue();
	}
	public void setProgress(double x) {
		progress.setValue(x);
		progressReady = false;
	}
	public void setRProgress(double x) {
		rprogress.setValue(x);
		rprogressReady = false;
	}
	public void speculateProgress(double x) {
		progressValue = x;
		progressReady = true;
	}
	public void speculateRProgress(double x) {
		rprogressValue = x;
		rprogressReady = true;
	}
	public void startRipple(List<KeyValue> kvs) {
		if(progress.getValue() > 0.1 && progress.getValue() < 0.9) return ;
		progress.setValue(0);
		kvs.add(new KeyValue(progress, 2));
		progressReady = false;
	}
	public void rstartRipple(List<KeyValue> kvs) {
		if(rprogress.getValue() > 0.1 && rprogress.getValue() < 0.9) return ;
		rprogress.setValue(0);
		kvs.add(new KeyValue(rprogress, 2));
		rprogressReady = false;
	}
	public void exportSpeculativeState(List<KeyValue> kvs) {
		if(startReady) {
			kvs.add(new KeyValue(curve.startXProperty(), startX));
			kvs.add(new KeyValue(curve.startYProperty(), startY));
			// kvs.add(new KeyValue(shadow.startXProperty(), startX));
			// kvs.add(new KeyValue(shadow.startYProperty(), startY));
			startReady = false;
		}
		if(endReady) {
			kvs.add(new KeyValue(curve.endXProperty(), endX));
			kvs.add(new KeyValue(curve.endYProperty(), endY));
			// kvs.add(new KeyValue(shadow.endXProperty(), endX));
			// kvs.add(new KeyValue(shadow.endYProperty(), endY));
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
		if(rprogressReady) {
			kvs.add(new KeyValue(rprogress, rprogressValue));
			rprogressReady = false;
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