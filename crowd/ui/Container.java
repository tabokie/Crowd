package crowd.ui;

import java.util.*;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.KeyCode;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
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
	private static Pane defaultLayout(Pane content, WorkFlow flow) {
		BorderPane ret = new BorderPane();
    ret.setBackground(Background.EMPTY);

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
    HBox box = new HBox();
    box.getChildren().addAll(submit, textField);
    box.setPadding(new Insets(7, 12, 7, 12));
    box.setSpacing(10);
    box.setAlignment(Pos.CENTER);
    box.setHgrow(textField, Priority.ALWAYS);
    submit.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent event) {
        handleCommand(flow, textField.getText());
      }
    });
    final double widgetHeight = 30;
    box.setMinHeight(widgetHeight);
    ret.setTop(content);
    ret.setBottom(box);

    ret.heightProperty().addListener(new ChangeListener(){
      @Override
      public void changed(ObservableValue obj, Object oldVal, Object newVal) {
        flow.updateHeight((double)newVal - widgetHeight);
      }
    });
    ret.widthProperty().addListener(new ChangeListener(){
      @Override
      public void changed(ObservableValue obj, Object oldVal, Object newVal) {
        flow.updateWidth((double)newVal);
      }
    });
    return ret;
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
	public Container loadDefault() {
		pane = defaultLayout(parent.getContentPane(), parent.getFlow());
		return this;
	}
	public App build() {
		parent.setContainer(this);
		return parent;
	}
}