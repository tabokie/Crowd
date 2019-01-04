package crowd;

import java.util.*;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.KeyCode;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
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
import crowd.util.*;

public class App extends Application {
	private WorkFlow flow = null;
	private ChatBox chatbox = null;
	private Container container;
	private Scene scene;
	public Pane getWorkflowPane() {
		return flow.getPane();
	}
	public Node getChatboxNode() {
		return chatbox.getNode();
	}
	public WorkFlow getFlow() {
		return flow;
	}
	public ChatBox getChatbox() {
		return chatbox;
	}
	public App() { }
	public App buildWorkflow() {
		flow = new WorkFlow(new Vec2f(0,0), new Vec2f(800, 400));
		return this;
	}
	public App buildChatbox() {
		chatbox = new ChatBox();
		return this;
	}
	public Container createContainer() {
		container = new Container(this);
		return container;
	}
	public void setContainer(Container c) {
		container = c; // overwrite
		scene = new Scene(container.getPane(), 800, 400);
	}
	public void addCss(String filename) {
		if(scene != null) {
			String path = ResourceManager.getUnixFullPath(filename);
			scene.getStylesheets().add(path);
		}
	}
	public Simulator createSimulator() {
		return new Simulator(this);
	}
	public <T extends Buildable> T createBuildable(Class<T> cls) throws Exception{
		T instance = cls.newInstance();
		instance.bind(this);
		return instance;
	}
	@Override
  public void start(Stage primaryStage) {
  	if(scene == null) {
  		scene = new Scene(flow.getPane(), 800, 400);
  	}
    primaryStage.setTitle("Crowd");
    primaryStage.setScene(scene);
    primaryStage.show();
  }
  @Override
  public void init() throws Exception {
  	buildWorkflow().buildChatbox().createBuildable(Container.class).loadChatbox().build();
  }
  public static void main(String[] args) {
  	launch(args);
  }
}