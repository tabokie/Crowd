package crowd.ui;

import javafx.scene.Node;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.paint.LinearGradient;
import javafx.scene.shape.Rectangle;
import javafx.scene.paint.Stop;
import javafx.scene.paint.CycleMethod;
import javafx.beans.value.*;
import javafx.scene.paint.Color;
import javafx.geometry.Insets;

public class ChatBox {
	private ScrollPane pane;
	private VBox messageHolder;
	private int capacity = 0;
	public ChatBox() {
		build();
	}
	public Node getNode() {
		return pane;
	}
	private void build() {
		messageHolder = new VBox();
		messageHolder.setStyle("-fx-background-color: #ffffff;");
		messageHolder.setSpacing(5);
		messageHolder.setPadding(new Insets(20, 12, 3, 12));
		
		Rectangle mask = new Rectangle();
		mask.setX(0);
		mask.setY(0);
		mask.setFill(new LinearGradient(
			0,0,0,30,false,
			CycleMethod.NO_CYCLE, 
			new Stop(0.3, Color.TRANSPARENT), 
			new Stop(1, Color.BLACK)
		));

		pane = new ScrollPane();
		pane.setContent(messageHolder);
		// pane.setStyle("> .viewport{-fx-background-color: transparent;}");
		pane.setStyle("-fx-background: #ffffff;-fx-background-color: #ffffff;");
		pane.setFitToWidth(true);
		pane.setClip(mask);
		pane.setHbarPolicy(ScrollBarPolicy.NEVER);
    pane.setVbarPolicy(ScrollBarPolicy.NEVER);
    pane.setVvalue(pane.getVmax());
    pane.widthProperty().addListener(new ChangeListener() {
      @Override
      public void changed(ObservableValue obj, Object oldVal, Object newVal) {
        mask.setWidth((double) newVal);
      }
    });
    pane.heightProperty().addListener(new ChangeListener() {
      @Override
      public void changed(ObservableValue obj, Object oldVal, Object newVal) {
        mask.setHeight((double) newVal);
      }
    });
	}
	public void Retire(int count) {
		messageHolder.getChildren().remove(0, count);
		messageHolder.applyCss();
		messageHolder.layout();
    pane.setVvalue(pane.getVmax());
	}
	public void SetCapacity(int count) {
		capacity = count;
	}
	public void Retain(int capacity) { // make sure <= capacity
		int cur = messageHolder.getChildren().size();
		if(cur > capacity) {
			messageHolder.getChildren().remove(0, cur - capacity);
		}
	}
	public void Add(String message, boolean rightAligned) {
		if(capacity > 0) {
			Retain(capacity - 1);
		}
		Button btn = new Button(message);
		if(rightAligned) {
			btn.setStyle("-fx-background-color: linear-gradient(to right,#4fc1e9,#3bafda);"+
				"-fx-background-radius: 15;"+
	  		"-fx-border-radius: 15;");	
		} else {
			btn.setStyle("-fx-background-color: linear-gradient(to right,#f5f7fa,#e6e9ed);"+
				"-fx-background-radius: 15;"+
	  		"-fx-border-radius: 15;");	
		}
		messageHolder.getChildren().add(btn);
		messageHolder.applyCss();
		messageHolder.layout();
		if(rightAligned) {
			final double btnWidth = btn.getWidth() + 12 + 13; // + padding + MAGIC
			double leftMargin = messageHolder.getWidth() - btnWidth;
			messageHolder.setMargin(btn, new Insets(0,0,0, leftMargin));
			messageHolder.widthProperty().addListener(new ChangeListener() {
				@Override
				public void changed(ObservableValue obj, Object oldVal, Object newVal) {
					double leftMargin = (double)newVal - btnWidth;
					messageHolder.setMargin(btn, new Insets(0,0,0, leftMargin));
				}
			});
		}
		pane.layout();
    pane.setVvalue(pane.getVmax());
	}
	public void setWidth(double w) {
		pane.setMaxWidth(w);
		messageHolder.setMaxWidth(w);
	}
	public void setHeight(double h) {
		pane.setMaxHeight(h);
	}
}