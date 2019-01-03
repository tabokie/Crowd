package crowd.ui;

import java.util.*;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.KeyCode;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.layout.*;
import javafx.geometry.*;
import javafx.stage.Stage;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.layout.Background;
import javafx.beans.value.*;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.paint.CycleMethod;

import crowd.App;
import crowd.Buildable;

public class Container extends Buildable{
	private Pane pane;
  private List<String> css = new ArrayList<String>();
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
  private static Rectangle mask;
  private static ScrollPane loadMessageHolder() {
    VBox messages = new VBox();
    messages.setStyle("-fx-background-color: #ffffff;");
    // messages.setBackground(Background.EMPTY);
    for(int i = 0; i < 5; i ++) {
      Button message = new Button("this is message #" + String.valueOf(i));
      message.getStyleClass().add("message");
      messages.getChildren().add(message);
      messages.setMargin(message, new Insets(0, 0, 0, 200));
    }
    messages.setSpacing(5);
    messages.setPadding(new Insets(20, 12, 20, 12));

    ScrollPane holder = new ScrollPane();
    holder.setBackground(Background.EMPTY);
    holder.setFitToWidth(true);
    // holder.getViewport().setBackground(Background.EMPTY);
    mask = new Rectangle();
    // relative position
    mask.setX(0);
    mask.setY(0);
    mask.setWidth(100);
    mask.setHeight(100);
    mask.setFill(new LinearGradient(0,0,0,30, false, CycleMethod.NO_CYCLE, new Stop(0.3, Color.TRANSPARENT), new Stop(1, Color.BLACK)));
    holder.setClip(mask);
    holder.setContent(messages);
    holder.setHbarPolicy(ScrollBarPolicy.NEVER);
    holder.setVbarPolicy(ScrollBarPolicy.NEVER);
    holder.widthProperty().addListener(new ChangeListener() {
      @Override
      public void changed(ObservableValue obj, Object oldVal, Object newVal) {
        mask.setWidth((double) newVal);
      }
    });
    holder.heightProperty().addListener(new ChangeListener() {
      @Override
      public void changed(ObservableValue obj, Object oldVal, Object newVal) {
        mask.setHeight((double) newVal);
      }
    });

    return holder;
  }
	public Container loadDefault() {
    Pane content = parent.getContentPane();
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
    Pane content = parent.getContentPane();
    WorkFlow flow = parent.getFlow();

    BorderPane ret = new BorderPane();
    ret.setBackground(Background.EMPTY);

    HBox inputbar = loadInputBar(flow);
    ScrollPane holder = loadMessageHolder();
    VBox chatbox = new VBox();
    chatbox.getChildren().addAll(holder, inputbar);
    chatbox.setBackground(Background.EMPTY);
    
    ret.setTop(content);
    ret.setBottom(chatbox);

    ret.heightProperty().addListener(new ChangeListener(){
      @Override
      public void changed(ObservableValue obj, Object oldVal, Object newVal) {
        flow.updateHeight(((double)newVal - inputBarHeight) * flowProportion);
        holder.setMaxHeight(((double)newVal - inputBarHeight) * (1-flowProportion));
        // mask.setY(((double)newVal - inputBarHeight) * flowProportion);
      }
    });
    ret.widthProperty().addListener(new ChangeListener(){
      @Override
      public void changed(ObservableValue obj, Object oldVal, Object newVal) {
        flow.updateWidth((double)newVal);
      }
    });

    css.add("chatbox.css");

    pane = ret; // downgrade to pane
    return this;
  }
	public App build() {
		parent.setContainer(this);
    for(String file: css) {
      parent.addCss(file);
    }
		return parent;
	}
}