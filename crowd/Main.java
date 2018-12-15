package crowd;

import javafx.application.Application;
import javafx.event.ActionEvent;
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

public class Main extends Application {
  public void start(Stage primaryStage) {
    Pane pane = new Pane();


    WorkFlow flow = new WorkFlow(pane, new Vec2f(0, 0), new Vec2f(800, 400));
    flow.setup("leader", null);
    flow.setup("follower1", new String[]{"leader"});
    flow.setup("follower2", new String[]{"leader"});

    Button submit = new Button("submit");
    submit.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
    TextField textField = new TextField();
    HBox box = new HBox();
    box.getChildren().addAll(submit, textField);
    box.setSpacing(10);
    submit.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent event) {
        if(textField.getText() != null && !textField.getText().isEmpty() ) {
          String[] tokens = textField.getText().split(" ");
          if(tokens[0].equals("group")) { // group name ...
            flow.setup(tokens[1], Arrays.copyOfRange(tokens, 2, tokens.length));
          }
          else if(tokens[0].equals("node")) {
            flow.claim(tokens[1], tokens[2]); // node name group
          }
          else if(tokens[0].equals("glink")) {
            flow.connectGroup(tokens[1], tokens[2]);
          }
          else if(tokens[0].equals("nlink")) {
            flow.connectNode(tokens[1], tokens[2]);
          }
        }
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

