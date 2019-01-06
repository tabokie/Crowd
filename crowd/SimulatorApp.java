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
		.addNode("left", "echo", new Pair("target", "right"), new Pair("count", new AtomicInteger(0)))
		.addNode("right", "echo", new Pair("count", new AtomicInteger(0)))
		.setStartup("left").build();
	}
	public static void main(String[] args) {
		launch(args);
	}
}