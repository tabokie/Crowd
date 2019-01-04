package crowd;

import javafx.util.Pair;
import java.util.concurrent.atomic.*;

import crowd.concurrent.EchoPrototype;
import crowd.util.JavaRuntime;
import crowd.concurrent.Prototype;

public class SimulatorApp extends App {
	@Override
	public void init() throws Exception {
		this.buildWorkflow()
		.createContainer().loadDefault().build()
		.createSimulator()
		.addPrototype("echo", (Prototype) JavaRuntime.LoadObjectFromResource("DynamicPrototype"))
		.addNode("left", "echo", new Pair("target", "right"), new Pair("count", new AtomicInteger(0)))
		.addNode("right", "echo", new Pair("count", new AtomicInteger(0)))
		.setStartup("left").build();
	}
	public static void main(String[] args) {
		launch(args);
	}
}