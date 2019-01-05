package crowd.ui;

import java.util.*;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.KeyCode;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.geometry.*;
import javafx.stage.Stage;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.layout.Background;
import javafx.beans.value.*;

import crowd.App;
import crowd.Buildable;
import crowd.DefaultProtocol;

public class Container extends Buildable{
	private Pane pane;
	public Container(App parent) {
		super(parent);
	}
	public Container() {
		super();
	}
	public Pane getPane() {
		return pane;
	}

  private static final float inputBarHeight = 30;
  private static HBox newInputBar(App app) {
    HBox box = new HBox();

    Button submit = new Button("submit");
    submit.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
    TextField textField = new TextField();
    textField.setOnKeyPressed(new EventHandler<KeyEvent>() {
      @Override
      public void handle(KeyEvent event) {
        if(event.getCode() == KeyCode.ENTER) {
          app.input(":" + textField.getText());
        }
      }
    });
    submit.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent event) {
        app.input(":" + textField.getText());
      }
    });

    box.getChildren().addAll(submit, textField);
    box.setPadding(new Insets(7, 12, 7, 12));
    box.setSpacing(10);
    box.setAlignment(Pos.CENTER);
    box.setHgrow(textField, Priority.ALWAYS);
    box.setMinHeight(inputBarHeight);

    return box;
  }
  private static final float flowProportion = 0.8f;
  // always load input bar
  public Container loadCompact() {
    WorkFlow flow = parent.getFlow();
    ChatBox chatbox = parent.getChatbox();

    BorderPane ret = new BorderPane();
    ret.setBackground(Background.EMPTY);
    if(flow != null) {
      ret.setTop(flow.getPane());
      final double zoom = (chatbox == null) ? 1 : flowProportion;
      ret.heightProperty().addListener(new ChangeListener() {
        @Override
        public void changed(ObservableValue obj, Object oldVal, Object newVal) {
          flow.updateHeight(((double)newVal - inputBarHeight) * zoom);
        }
      });
      ret.widthProperty().addListener(new ChangeListener(){
        @Override
        public void changed(ObservableValue obj, Object oldVal, Object newVal) {
          flow.updateWidth((double)newVal);
        }
      });
    }
    HBox inputbar = newInputBar(parent);
    if(chatbox != null) {
      VBox bottom = new VBox();
      bottom.getChildren().addAll(chatbox.getNode(), inputbar);
      bottom.setBackground(Background.EMPTY);
      ret.setBottom(bottom);
      final double zoom = (flow == null) ? 1 : (1-flowProportion);
      ret.heightProperty().addListener(new ChangeListener() {
        @Override 
        public void changed(ObservableValue obj, Object oldVal, Object newVal) {
          chatbox.setHeight(((double)newVal - inputBarHeight) * zoom);
        }
      }); 
    } else {
      ret.setBottom(inputbar);
    }

    pane = ret;
    return this;
  }
	public App build() {
		parent.setContainer(this);
		return parent;
	}
}