package crowd.concurrent;

import java.util.concurrent.atomic.*;
import java.util.Map;

public class EchoPrototype implements Prototype {
	public void init(Map<String, Object> datas) {
		datas.put("count", new AtomicInteger(0));
	}
	public void receive(String thisNode, Simulator simulator, String fromNode, String message) {
		System.out.println(thisNode + " receives: " + message);
		simulator.getScheduler().enqueue(3, (Actor actor) -> {
			AtomicInteger countRef = simulator.getData(thisNode, "count");
			final int count = countRef.getAndIncrement();
			simulator.send(thisNode, fromNode, "hello from " + thisNode); // send back
			actor.act(5, ()->{ // wait for 5 second and check
				if( simulator.<AtomicInteger>getData(thisNode, "count").get() <= count) {
					System.out.println("Oops, didn't get response after 5 seconds");
				}
				else {
					System.out.println("Got response after 5 seconds");
				}
			});
		});
	}
	public void start(String thisNode, Simulator simulator) {
		simulator.getScheduler().enqueue(0, (Actor actor) -> {
			AtomicInteger countRef = simulator.getData(thisNode, "count");
			countRef.set(0);
			simulator.send(thisNode, simulator.getData(thisNode, "target"), "hello from " + thisNode);
			actor.act(5, ()->{ // wait for 5 second and check
				if( simulator.<AtomicInteger>getData(thisNode, "count").get() == 0) {
					System.out.println("Oops, didn't get response after 5 seconds");
				}
				else {
					System.out.println("Got response after 5 seconds");
				}
			});
		});
	}

}