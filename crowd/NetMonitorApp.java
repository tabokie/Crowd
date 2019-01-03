package crowd;

import javafx.util.Pair;
import java.util.concurrent.atomic.*;

import crowd.concurrent.EchoPrototype;

public class NetMonitorApp extends App {
	@Override
	public void init() {
		this.createContainer().loadDefault().build()
		.createSimulator()
		.addPrototype("echo", new EchoPrototype())
		.addNode("left", "echo", new Pair("target", "right"), new Pair("count", new AtomicInteger(0)))
		.addNode("right", "echo", new Pair("count", new AtomicInteger(0)))
		.setStartup("left").build();
	}
	public static void main(String[] args) {
		launch(args);
	}
}
