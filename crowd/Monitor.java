package crowd;

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
import crowd.ui.*;

// 2-dimentional workflow
// distributed replication using round visualization

public class Monitor extends Application {
  BorderPane pane = null;
  Pane contentPane = null;
  WorkFlow flow = null;
  private void handleCommand(String command) {
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
        contentPane.getChildren().clear();
        flow = new WorkFlow(contentPane, new Vec2f(0,0), new Vec2f(800,400));
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

  public void start(Stage primaryStage) {
    pane = new BorderPane();
    pane.setBackground(Background.EMPTY);
    contentPane = new Pane();
    contentPane.setMaxSize(1920, 1080);
    // contentPane.setPrefSize(800, 400); // is auto updated by children's position

    flow = new WorkFlow(contentPane, new Vec2f(0, 0), new Vec2f(800, 400));
    flow.newGroup("1.0",null);
    flow.newGroup("1.1",null);

    Button submit = new Button("submit");
    submit.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
    TextField textField = new TextField();
    textField.setOnKeyPressed(new EventHandler<KeyEvent>() {
      @Override
      public void handle(KeyEvent event) {
        if(event.getCode() == KeyCode.ENTER) {
          handleCommand(textField.getText());
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
        handleCommand(textField.getText());
      }
    });
    final double widgetHeight = 30;
    box.setMinHeight(widgetHeight);
    pane.setTop(contentPane);
    pane.setBottom(box);

    pane.heightProperty().addListener(new ChangeListener(){
      @Override
      public void changed(ObservableValue obj, Object oldVal, Object newVal) {
        flow.updateHeight((double)newVal - widgetHeight);
      }
    });
    pane.widthProperty().addListener(new ChangeListener(){
      @Override
      public void changed(ObservableValue obj, Object oldVal, Object newVal) {
        flow.updateWidth((double)newVal);
      }
    });


    Scene scene =new Scene(pane,800,400);
    primaryStage.setTitle("Crowd");
    primaryStage.setScene(scene);
    primaryStage.show();
  }
  public static void main(String[] args) {
    launch(args);
  }
}

