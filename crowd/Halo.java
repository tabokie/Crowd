package crowd;

import javafx.animation.KeyValue;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.layout.Pane;

import java.util.*;

public class Halo {
	private Arc progress = new Arc();
	private Circle halo = new Circle();
	private Vec2f position = new Vec2f();
	private float radius = 0;
	private float progressPercent = 0;
	private Pane pane;
	private boolean sizeSpeculativeStateReady = false;
	private boolean positiionSpeculativeStateReady = false;
	Halo(Pane p) {
		progress.setStrokeWidth(4);
		progress.setFill(Color.TRANSPARENT);
		progress.setStroke(Color.DEEPPINK);
		progress.setStartAngle(0);
		progress.setLength(0);

		halo.setFill(Color.CORAL);
		halo.setRadius(0);
    halo.setStrokeWidth(0);

    pane = p;
    if(pane != null) {
	    pane.getChildren().add(progress);
	    pane.getChildren().add(halo);	
    }
	}

	public void speculateProgress(float percentage) {
		this.progressPercent = percentage;
		sizeSpeculativeStateReady = true;
	}
	public void speculateRadius(float radius) {
		this.radius = radius;
		sizeSpeculativeStateReady = true;
	}
	public void speculateCenter(float x, float y) {
		position.data[0] = x;
		position.data[1] = y;
		positiionSpeculativeStateReady = true;
	}
	public void exportSpeculativeState(List<KeyValue> kvs) {
		if(sizeSpeculativeStateReady) {
			kvs.add(new KeyValue(progress.lengthProperty(), progressPercent * 360));
			kvs.add(new KeyValue(progress.radiusXProperty(), radius));
			kvs.add(new KeyValue(progress.radiusYProperty(), radius));
			kvs.add(new KeyValue(halo.radiusProperty(), radius));
		}
		if(positiionSpeculativeStateReady) {
			kvs.add(new KeyValue(progress.centerXProperty(), position.data[0]));
			kvs.add(new KeyValue(progress.centerYProperty(), position.data[1]));
			kvs.add(new KeyValue(halo.centerXProperty(), position.data[0]));
			kvs.add(new KeyValue(halo.centerYProperty(), position.data[1]));
		}
		sizeSpeculativeStateReady = false;
		positiionSpeculativeStateReady = false;
	}
	public void setCenterX(float x) {
		progress.setCenterX(x);
		halo.setCenterX(x);
	}
	public void setCenterY(float y) {
		progress.setCenterY(y);
		halo.setCenterY(y);
	}
}