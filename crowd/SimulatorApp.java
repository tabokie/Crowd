package crowd;

import javafx.util.Pair;
import java.util.concurrent.atomic.*;

import crowd.concurrent.Simulator;
import crowd.concurrent.EchoPrototype;
import crowd.ui.Container;
import crowd.util.JavaRuntime;
import crowd.concurrent.Prototype;

public class SimulatorApp extends App {
	@Override
	public void init() throws Exception {
		this.setName("simulator");
		this.buildWorkflow().buildChatbox()
		.build(Container.class).loadCompact().build()
		.build(Simulator.class)
		.addPrototype("echo", (Prototype) JavaRuntime.LoadObjectFromResource("DynamicPrototype"))
		.addNode("left", "echo", null, new Pair("target", "right"))
		.addNode("right", "echo", null)
		.setStartup("left").build();
	}
	public static void main(String[] args) {
		launch(args);
	}
}