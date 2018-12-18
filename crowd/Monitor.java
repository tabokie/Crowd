package crowd;

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

import java.util.*;

// 2-dimentional workflow
// distributed replication using round visualization

public class Monitor extends Application {
  Pane pane = null;
  Pane contentPane = null;
  WorkFlow flow = null;
  private void handleCommand(String command) {
    if(command != null && !command.isEmpty() && flow != null) {
      String[] tokens = command.split(" ");
      if(tokens[0].equals("gclaim")) { // group name ...
        flow.claimGroup(tokens[1], Arrays.copyOfRange(tokens, 2, tokens.length));
      }
      else if(tokens[0].equals("nclaim")) {
        flow.claimNode(tokens[1], tokens[2]); // node name group
      }
      else if(tokens[0].equals("glink")) {
        flow.connectGroup(tokens[1], tokens[2]);
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
        // flow.setHalo(tokens[1], Float.parseFloat(tokens[2]));
      }
    }
    return ;
  }

  public void start(Stage primaryStage) {
    pane = new Pane();
    contentPane = new Pane();

    flow = new WorkFlow(contentPane, new Vec2f(0, 0), new Vec2f(800, 400));
    flow.claimGroup("leader", null);
    flow.claimGroup("follower1", new String[]{"leader"});
    flow.claimGroup("follower2", new String[]{"leader"});

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
    pane.getChildren().add(contentPane);
    HBox box = new HBox();
    box.getChildren().addAll(submit, textField);
    box.setSpacing(10);
    submit.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent event) {
        handleCommand(textField.getText());
      }
    });
    pane.getChildren().add(box);

    Scene scene =new Scene(pane,800,400);
    primaryStage.setTitle("CubicBezierCurve");
    primaryStage.setScene(scene);
    primaryStage.show();
  }
  public static void main(String[] args) {
    launch(args);
  }
}

