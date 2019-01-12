package crowd;

import javafx.util.Pair;
import java.util.concurrent.atomic.*;

import crowd.concurrent.Simulator;
import crowd.concurrent.EchoPrototype;
import crowd.concurrent.TwoPhasePrototype;
import crowd.ui.Container;
import crowd.util.JavaRuntime;
import crowd.concurrent.Prototype;

public class SimulatorApp extends App {
	@Override
	public void init() throws Exception {
		this.setName("simulator");
		/* // echo system
		this.buildWorkflow().buildChatbox()
		.build(Container.class).loadCompact().build()
		.build(Simulator.class)
		.addPrototype("echo", (Prototype) JavaRuntime.LoadObjectFromResource("DynamicPrototype"))
		.addNode("left", "echo", "1", new Pair("target", "right"))
		.addNode("right", "echo", "1")
		.setStartup("left").build();
		*/
		this.buildWorkflow().buildChatbox()
		.build(Container.class).loadCompact().build()
		.build(Simulator.class)
		//
		.addPrototype("2pc", new TwoPhasePrototype())
		.addPrototype("echo", new EchoPrototype())
		.addNode("2.1", "2pc", "2", 
			new Pair("leader", new Boolean(true)), 
			new Pair("members", new String[]{"2.2", "2.3", "2.4", "2.5"}), 
			new Pair("phase", new Integer(0)),
			new Pair("seq", new Integer(0)),
			new Pair("response", new Integer(0)),
			new Pair("request", "null"))
		.addNode("2.2", "2pc", "2")
		.addNode("2.3", "2pc", "2")
		.addNode("2.4", "2pc", "2")
		.addNode("2.5", "2pc", "2")
		.addNode("3.1", "2pc", "3", 
			new Pair("leader", new Boolean(true)), 
			new Pair("members", new String[]{"3.2", "3.3", "3.4", "3.5", "3.6", "3.7"}), 
			new Pair("phase", new Integer(0)),
			new Pair("seq", new Integer(0)),
			new Pair("response", new Integer(0)),
			new Pair("request", "null"))
		.addNode("3.2", "2pc", "3")
		.addNode("3.3", "2pc", "3")
		.addNode("3.4", "2pc", "3")
		.addNode("3.5", "2pc", "3")
		.addNode("3.6", "2pc", "3")
		.addNode("3.7", "2pc", "3")
		.addNode("1", "echo", null, new Pair("target", new String[]{"2.1", "3.1"}))
		.setStartup("1").build();
	}
	public static void main(String[] args) {
		launch(args);
	}
}