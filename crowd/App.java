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
import crowd.concurrent.*;

public class App extends Application {
	private WorkFlow flow;
	private Pane contentPane;
	private Container container;
	private Scene scene;
	public Pane getContentPane() {
		return contentPane;
	}
	public WorkFlow getFlow() {
		return flow;
	}
	public App() {
		contentPane = new Pane();
		contentPane.setMaxSize(1920, 1080);
		flow = new WorkFlow(contentPane, new Vec2f(0,0), new Vec2f(800, 400));
		createContainer().loadDefault().build();
	}
	public Container createContainer() {
		container = new Container(this);
		return container;
	}
	public void setContainer(Container c) {
		container = c;
	}
	public Simulator createSimulator() {
		return new Simulator(this);
	}
	@Override
  public void start(Stage primaryStage) {
  	if(container != null) {
	    scene = new Scene(container.getPane(),800,400);
  	}
  	else {
  		scene = new Scene(contentPane, 800, 400);
  	}
    primaryStage.setTitle("Crowd");
    primaryStage.setScene(scene);
    primaryStage.show();
  }
  @Override
  public void init() {
  	createContainer().loadDefault().build();
  }
  public static void main(String[] args) {
  	launch(args);
  }
}