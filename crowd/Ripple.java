package crowd;

import javafx.scene.shape.*;
import javafx.beans.property.*;
import javafx.beans.value.*;
import javafx.animation.KeyValue;
import java.util.*;
import javafx.scene.paint.Color;

// Ripple must be attach to a designated path 
// which is already included in the context
public class Ripple {
	public CubicCurve path;
	private DoubleProperty startX, startY;
	private DoubleProperty endX, endY;

	private DoubleProperty progress = new SimpleDoubleProperty(0);
	private Circle mask;
	private AnimatedRadialGradient gradient;
	// bad ctor due to no common interface for function with start/end
	Ripple(CubicCurve path) { // the full display of designated path
		this.path = path;
		// the path is managed by ripple now
		mask = new Circle();
		mask.setRadius(0);
		gradient = new AnimatedRadialGradient(mask, Color.RED);
		startX = path.startXProperty();
		startY = path.startYProperty();
		endX = path.endXProperty();
		endY = path.endYProperty();
		startX.addListener(new ChangeListener(){
			@Override
			public void changed(ObservableValue obj, Object oldVal, Object newVal) {
				mask.setCenterX((double)newVal);
				gradient.setCenterX((double)newVal);
			}
		}); // take over gradient's property
		startY.addListener(new ChangeListener(){
			@Override
			public void changed(ObservableValue obj, Object oldVal, Object newVal) {
				mask.setCenterY((double)newVal);
				gradient.setCenterY((double)newVal);
			}
		});
		endX.addListener(new ChangeListener(){
			@Override
			public void changed(ObservableValue obj, Object oldVal, Object newVal) {
				double radius = Math.sqrt(Math.pow((double)newVal - (double)oldVal, 2) + Math.pow(path.getEndY() - path.getStartY(), 2));
				mask.setRadius(radius);
				gradient.setRadius(getProgress() * radius);
			}
		});
		endY.addListener(new ChangeListener(){
			@Override
			public void changed(ObservableValue obj, Object oldVal, Object newVal) {
				double radius = Math.sqrt(Math.pow((double)newVal - (double)oldVal, 2) + Math.pow(path.getEndX() - path.getStartX(), 2));
				mask.setRadius(getProgress() * radius);
				gradient.setRadius(getProgress() * radius);
			}
		});
		progress.addListener(new ChangeListener(){
			@Override
			public void changed(ObservableValue obj, Object oldVal, Object newVal) {
				double radius = Math.sqrt(Math.pow(path.getEndY() - path.getStartY(), 2) + Math.pow(path.getEndX() - path.getStartX(), 2));
				mask.setRadius(getProgress() * radius);
				gradient.setRadius(getProgress() * radius);
			}
		});
		path.setClip(mask);

	}
	public void start(List<KeyValue> kvs) {
		progress.setValue(0);
		kvs.add(new KeyValue(progress, 2));
	}
	public void setProgress(double p) {
		progress.setValue(p);
	}
	public double getProgress() {
		return progress.getValue();
	} 
	public DoubleProperty progressProperty() {
		return progress;
	}
	public DoubleProperty startXProperty() {
		return startX;
	}
	public DoubleProperty startYProperty() {
		return startY;
	}
	public DoubleProperty endXProperty() {
		return endX;
	}
	public DoubleProperty endYProperty() {
		return endY;
	}
}