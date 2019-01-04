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

public class Container extends Buildable{
	private Pane pane;
	private static void handleCommand(WorkFlow flow, String command) {
    if(command != null && !command.isEmpty() && flow != null) {
      String[] tokens = command.split(" ");
      if(tokens[0].equals("gclaim")) { // group name ...
        flow.newGroup(tokens[1], Arrays.copyOfRange(tokens, 2, tokens.length));
      }
      else if(tokens[0].equals("nclaim")) {
        flow.newNode(tokens[1], tokens[2]); // node name group
      }
      else if(tokens[0].equals("glink")) {
        flow.precedeGroup(tokens[1], tokens[2]);
      }
      else if(tokens[0].equals("nlink")) {
        flow.connectNode(tokens[1], tokens[2]);
      }
      else if(tokens[0].equals("clear")) {
      	// not implemented
      	flow.clear();
      }
      else if(tokens[0].equals("check")) {
        flow.report();
      }
      else if(tokens[0].equals("gstr")) {
        flow.setStroke(tokens[1], tokens[2], Float.parseFloat(tokens[3]));
      }
      else if(tokens[0].equals("ghal")) {
        if(tokens.length >= 4) {
          flow.setHalo(tokens[1], Float.parseFloat(tokens[2]), Float.parseFloat(tokens[3]));
        }
      }
      else if(tokens[0].equals("start")) {
        flow.startConcurrentTest(Integer.parseInt(tokens[1]));
      }
    }
    return ;
  }
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
  private static HBox loadInputBar(WorkFlow flow) {
    HBox box = new HBox();

    Button submit = new Button("submit");
    submit.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
    TextField textField = new TextField();
    textField.setOnKeyPressed(new EventHandler<KeyEvent>() {
      @Override
      public void handle(KeyEvent event) {
        if(event.getCode() == KeyCode.ENTER) {
          handleCommand(flow, textField.getText());
        }
      }
    });
    submit.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent event) {
        handleCommand(flow, textField.getText());
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
  private static HBox loadInputBar(WorkFlow flow, ChatBox chatbox) {
    HBox box = new HBox();

    Button submit = new Button("submit");
    submit.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
    TextField textField = new TextField();
    textField.setOnKeyPressed(new EventHandler<KeyEvent>() {
      @Override
      public void handle(KeyEvent event) {
        if(event.getCode() == KeyCode.ENTER) {
          handleCommand(flow, textField.getText());
          chatbox.Add(textField.getText(), true);
        }
      }
    });
    submit.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent event) {
        handleCommand(flow, textField.getText());
        chatbox.Add(textField.getText(), true);
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
	public Container loadDefault() {
    Pane content = parent.getWorkflowPane();
    WorkFlow flow = parent.getFlow();

    BorderPane ret = new BorderPane();
    ret.setBackground(Background.EMPTY);

    ret.setTop(content);
    ret.setBottom(loadInputBar(flow));

    ret.heightProperty().addListener(new ChangeListener(){
      @Override
      public void changed(ObservableValue obj, Object oldVal, Object newVal) {
        flow.updateHeight((double)newVal - inputBarHeight);
      }
    });
    ret.widthProperty().addListener(new ChangeListener(){
      @Override
      public void changed(ObservableValue obj, Object oldVal, Object newVal) {
        flow.updateWidth((double)newVal);
      }
    });

    pane = ret;
    return this;
	}
  private static final float flowProportion = 0.8f;
  public Container loadChatbox() {
    Pane top = parent.getWorkflowPane();
    WorkFlow flow = parent.getFlow();
    ChatBox chatbox = parent.getChatbox();

    BorderPane ret = new BorderPane();
    ret.setBackground(Background.EMPTY);

    HBox inputbar = loadInputBar(flow, chatbox);
    VBox bottom = new VBox();
    bottom.getChildren().addAll(chatbox.getNode(), inputbar);
    bottom.setBackground(Background.EMPTY);
    
    ret.setTop(top);
    ret.setBottom(bottom);

    ret.heightProperty().addListener(new ChangeListener(){
      @Override
      public void changed(ObservableValue obj, Object oldVal, Object newVal) {
        flow.updateHeight(((double)newVal - inputBarHeight) * flowProportion);
        chatbox.setHeight(((double)newVal - inputBarHeight) * (1-flowProportion));
      }
    });
    ret.widthProperty().addListener(new ChangeListener(){
      @Override
      public void changed(ObservableValue obj, Object oldVal, Object newVal) {
        flow.updateWidth((double)newVal);
      }
    });

    pane = ret; // downgrade to pane
    return this;
  }
	public App build() {
		parent.setContainer(this);
		return parent;
	}
}