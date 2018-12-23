package crowd;

import javafx.scene.shape.*;
import javafx.scene.paint.*;
import javafx.scene.layout.Pane;
import javafx.beans.property.*;

public interface BalancedCurve {
	void setStart(double x, double y);
	double getStartX();
	double getStartY();
	void setEnd(double x, double y);
	double getEndX();
	double getEndY();
	DoubleProperty startXProperty();
	DoubleProperty startYProperty();
	DoubleProperty endXProperty();
	DoubleProperty endYProperty();
	void setColor(Paint c);
	ObjectProperty<Paint> colorProperty();
	void setWidth(double w);
	DoubleProperty widthProperty();
	BalancedCurve copy(Pane p);
	Shape getRawRef();
}