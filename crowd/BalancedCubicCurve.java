package crowd;

import javafx.scene.paint.*;
import javafx.animation.KeyValue;
import javafx.scene.shape.*;
import javafx.scene.layout.Pane;
import java.util.*;
import javafx.beans.value.*;
import javafx.beans.property.*;

public class BalancedCubicCurve implements BalancedCurve {
	private CubicCurve curve;
	BalancedCubicCurve(Pane pane) {
		curve = new CubicCurve();
		configureCurve(curve);
		pane.getChildren().add(curve);
	}
	public void setColor(Paint c) {
		curve.setStroke(c);
	}
	public ObjectProperty<Paint> colorProperty() {
		return curve.strokeProperty();
	}
	public void setWidth(double w) {
		curve.setStrokeWidth(w);
	}
	public DoubleProperty widthProperty() {
		return curve.strokeWidthProperty();
	}
	public Shape getRawRef() {
		return curve;
	}
 	private static void configureCurve(CubicCurve c) {
		c.setStrokeWidth(0);
		c.setFill(Color.TRANSPARENT);
		c.startXProperty().addListener(new ChangeListener(){
			@Override
			public void changed(ObservableValue obj, Object oldVal, Object newVal) {
				double deltaX = c.getEndX() - c.getStartX();
				c.setControlX1((double)newVal + deltaX / 2.0f);
				c.setControlX2(c.getEndX() - deltaX / 2.0f);
			}
		});
		c.startYProperty().addListener(new ChangeListener(){
			@Override
			public void changed(ObservableValue obj, Object oldVal, Object newVal) {
				c.setControlY1((double)newVal);
			}
		});
		c.endXProperty().addListener(new ChangeListener(){
			@Override
			public void changed(ObservableValue obj, Object oldVal, Object newVal) {
				double deltaX = c.getEndX() - c.getStartX();
				c.setControlX1(c.getStartX() + deltaX / 2.0f);
				c.setControlX2((double)newVal - deltaX / 2.0f);
			}
		});
		c.endYProperty().addListener(new ChangeListener(){
			@Override
			public void changed(ObservableValue obj, Object oldVal, Object newVal) {
				c.setControlY2((double)newVal);
			}
		});
	}
	public void setStart(double x, double y){
		curve.setStartX(x);
		curve.setStartY(y);
	}
	public double getStartX(){
		return curve.getStartX();
	}
	public double getStartY(){
		return curve.getStartY();
	}
	public void setEnd(double x, double y){
		curve.setEndX(x);
		curve.setEndY(y);
	}
	public double getEndX(){
		return curve.getEndX();
	}
	public double getEndY(){
		return curve.getEndY();
	}
	public DoubleProperty startXProperty(){
		return curve.startXProperty();
	}
	public DoubleProperty startYProperty(){
		return curve.startYProperty();
	}
	public DoubleProperty endXProperty(){
		return curve.endXProperty();
	}
	public DoubleProperty endYProperty(){
		return curve.endYProperty();
	}
	public BalancedCurve copy(Pane p) {
		BalancedCurve ret = new BalancedCubicCurve(p);
		ret.setStart(curve.getStartX(), curve.getStartY());
		ret.setEnd(curve.getEndX(), curve.getEndY());
		ret.setColor(curve.getStroke());
		ret.setWidth(curve.getStrokeWidth());
		return ret;
	}
}