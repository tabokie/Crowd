package crowd.ui;

import javafx.beans.property.*;
import javafx.beans.value.*;
import javafx.scene.shape.*;

import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;

// gradient is independent from target
public class AnimatedRadialGradient {
	private Shape target;
	private DoubleProperty radiusProperty = new SimpleDoubleProperty(0);
	private DoubleProperty centerXProperty = new SimpleDoubleProperty(0);
	private DoubleProperty centerYProperty = new SimpleDoubleProperty(0);
	// private DoubleProperty widthProperty = new SimpleDoubleProperty();
	private double x = 0, y = 0;
	private double radius = 0;
	private Color color;
	// default color as black
	AnimatedRadialGradient(Shape target, Color color) {
		this.target = target;
		this.color = color;
		// this.x = x;
		// this.y = y;
		radiusProperty.addListener(new ChangeListener(){
			@Override
			public void changed(ObservableValue obj, Object oldVal, Object newVal) {
				setRadius((double)newVal);
			}
		});
		centerXProperty.addListener(new ChangeListener(){
			@Override
			public void changed(ObservableValue obj, Object oldVal, Object newVal) {
				setCenterX((double)newVal);
			}
		});
		centerYProperty.addListener(new ChangeListener(){
			@Override
			public void changed(ObservableValue obj, Object oldVal, Object newVal) {
				setCenterY((double)newVal);
			}
		});
	}
	private void updateFill() {
		target.setFill(new RadialGradient(0, 0, getCenterX(), getCenterY(), getRadius(), false,
			CycleMethod.NO_CYCLE, new Stop(0.8, Color.TRANSPARENT), new Stop(1, color)));
		return ;
	}
	public void setRadius(double r) {
		radius = r;
		updateFill();
	}
	public double getRadius() {
		return radius;
	}
	public void setCenterX(double x) {
		this.x = x;
		updateFill();
	}
	public double getCenterX() {
		return x;
	}
	public void setCenterY(double y) {
		this.y = y;
		updateFill();
	}
	public double getCenterY() {
		return y;
	}
	public DoubleProperty radiusProperty() {
		return radiusProperty;
	}
	public DoubleProperty centerXProperty() {
		return centerXProperty;
	}
	public DoubleProperty centerYProperty() {
		return centerYProperty;
	}
	public void setColor(Color c) {
		color = c;
	}
}