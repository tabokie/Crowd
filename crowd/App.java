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
import javafx.application.Platform;

import crowd.ui.*;
import crowd.concurrent.*;
import crowd.util.*;
import crowd.port.*;

public class App extends Application {
	private String name = "";
	private WorkFlow flow = null;
	private ChatBox chatbox = null;
	private Container container;
	private Scene scene;
	private final static Protocol defaultProtocol = new DefaultProtocol();
	private Protocol protocol = null;
	private List<OPort> oports = new ArrayList<OPort>();
	public App() { }
	// accessors
	public String getName() {
		return name;
	}
	public void setName(String n) {
		name = n;
	}
	public WorkFlow getFlow() {
		return flow;
	}
	public ChatBox getChatbox() {
		return chatbox;
	}
	public Protocol getProtocol() {
		if(protocol == null) return defaultProtocol;
		return protocol;
	}
	// builders
	public App buildWorkflow() {
		flow = new WorkFlow(new Vec2f(0,0), new Vec2f(800, 400));
		return this;
	}
	public App buildChatbox() {
		chatbox = new ChatBox();
		return this;
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
	public <T extends Buildable> T build(Class<T> cls) {
		try {
			T instance = cls.newInstance();
			instance.bind(this);	
			return instance;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	public void addOPort(OPort port) {
		oports.add(port);
	}
	// runtime
	@Override
  public void start(Stage primaryStage) {
  	if(scene == null) {
  		scene = new Scene(flow.getPane(), 800, 400);
  	}
    primaryStage.setTitle("Crowd");
    primaryStage.setScene(scene);
    primaryStage.show(); // block here
  }
  @Override
  public void stop() {
  	for(OPort port: oports) {
  		port.close();
  	}
  }
  @Override
  public void init() throws Exception {
  	setName("app");
  	this.buildWorkflow().buildChatbox()
  	.build(Container.class).loadCompact().build();
  }
  // input from user or network
  public void input(String message) {
  	if(protocol == null) protocol = defaultProtocol;
  	Command cmd = protocol.parse(message);
  	Platform.runLater(()->{
			if(cmd == null ) {
	  		chatbox.Add("an invalid command", true);
	  	} else if(cmd.raiser.length() == 0) {
	  		chatbox.Add("user" + message, true);
	  	} else {
	  		chatbox.Add(message, false);
	  	}
  	});
  	
  	if(cmd != null)
  		input(cmd);
  }
  public void input(Command cmd) {
  	cmd.dispatch(this);
  }
  public void output(String target, Object message) {
  	for(OPort port : oports) {
  		if(target == null) port.send(message);
  		else port.send(target, message);
  	}
  }

  // entry
  public static void main(String[] args) {
  	launch(args);
  }
}