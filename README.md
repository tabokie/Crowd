# Crowd

Crowd is a system monitor with Java UI widget designed to better illustrate `replication`, `workflow`, `shared data` and other de-centralized system feature. It provides series of duplex ports through which working status can be transferred from extensive systems, including multi-pass worker, multi-process application, network cluster.

Also, for educational cause, it carries a compact distributed system simulator which demonstrates the monitor without connecting to large-scale network cluster.

This project is currently under development.

## Crowd UI

Mostly inspired by [Ambrose](https://github.com/twitter/ambrose), Crowd UI is implemented with JavaFX, and provides following widget in a windows application:

* Workflow keeps track of overall working dependency between nodes and report the current progress.

* Replicated Node contains several real nodes to compose a crash-tolerant super node, which then be treated as single node in Workflow.

Here is a screenshot of alpha version UI:

![alpha screenshot](docs/img/screenshot.png)

### Create Your Own

Other than standalone UI widget, user can build up customized application in one line, (due to JavaFX restriction, dynamic building is not yet supported).

Overwrite the init method in App template and launch your app:

```Java
package crowd;

public class MyApp extends App {
  @Override
  public void init() throws Exception {
    this.buildWorkflow().buildChatbox()
    .createContainer().loadChatbox().build()
    .createSimulator()
    .addPrototype("mytype", (Prototype) JavaRuntime.LoadObjectFromResource("MyPrototype"))
    .addNode("home", "mytype", new Pair("days", new AtomicInteger(0)), new Pair("name", "China"))
    .addNode("school", "mytype", new Pair("name", "ZJU"))
    .setStartup("home")
    .build();
  }
  public static void main(String[] args) {
    launch(args);
  }
}
```

## Crowd Simulator

Hands on distributed system with minimum coding required.

### Dynamic Loading

Crowd simulator decouples the necessary logic for system simulation from implementations. User are allowed to input logical script during runtime and Crowd will automatically handle the deployment and simulation of virtual nodes.

During runtime, script is dynamicly compiled and instantiated into new nodes. For now Crowd simply leverage JVM dynamic loading. 

### Discrete Time Event Simulating

To constraint the simulating threads to a controllable limit, simulator employs a main scheduler that takes over all discrete time events and schedules them onto fixed amount of sub-thread workers. Both real-time and speculative simulation driver are for option while running.

To ease the pain of user-end event design, Crowd introduces Actor to balance the use of local time and global time.

Here is a use case, notice the relative timing order between event.

```Java
// this happens in a local thread while global time is T
scheduler.enqueue(new Actor(0, (Actor actor) -> {
  actor.act(1, ()->{
    System.out.println("T+1 seconds from now");
  });
  actor.act(2, (Actor actor) -> {
    System.out.println("T+2 seconds from now");
    actor.act(3, ()->{
      System.out.println("T+2+3 seconds from now");
    });
  });
}));
```

### Simulate Algorithm Design

So far users can use Java script to load custom algorithm for distributed system simulation. Implement respective interface and instantiate real nodes during runtime.

Here is a example where a simple echo server is implemented:

```Java
public class MyPrototype implements Prototype {
  // called upon message
  public void receive(String thisNode, Simulator simulator, String fromNode, String message) {
    simulator.getScheduler().enqueue(
      3, // response at 3-rd second
      (Actor actor) -> {
        AtomicInteger countRef = simulator.getData(thisNode, "count"); // you can fetch local data via simulator
        final int count = countRef.getAndIncrement(); // incr once receive message
        simulator.send(thisNode, fromNode, "hello from " + thisNode); // send back
        actor.act(5, ()->{ // wait for 5 second and check
          if( simulator.<AtomicInteger>getData(thisNode, "count").get() <= count) {
            System.out.println("Oops, didn't get response in 5 seconds");
          }
          else {
            System.out.println("Got response within 5 seconds");
          }
        });
      }
    );
  }
  // called upon instantiation
  public void start(String thisNode, Simulator simulator) {
    simulator.getScheduler().enqueue(
      0, 
      (Actor actor) -> {
        AtomicInteger countRef = simulator.getData(thisNode, "count");
        countRef.set(0); // reset count
        simulator.send(thisNode, simulator.getData(thisNode, "target"), "hello from " + thisNode); // first message
        actor.act(5, ()->{ // wait for 5 second and check
          if( simulator.<AtomicInteger>getData(thisNode, "count").get() == 0) {
            System.out.println("Oops, didn't get response after 5 seconds");
          }
          else {
            System.out.println("Got response after 5 seconds");
          }
        });
      }
    );
  }
}
```

### Preset Algorithm

Currently support algorithm:

* [ ] Lamport clock
* [x] Echo Server
* [x] 2PC

Setup cluster in several lines:

```Java
this.buildWorkflow().buildChatbox()
.build(Container.class).loadCompact().build()
.build(Simulator.class)
// add preset protocol, or load dynamicly
.addPrototype("2pc", new TwoPhasePrototype())
.addPrototype("echo", new EchoPrototype())
// .addPrototype("dynamic", (Prototype) JavaRuntime.LoadObjectFromResource("DynamicPrototype"))
// setup leader state
.addNode("2.1", "2pc", "2", 
  new Pair("leader", new Boolean(true)), 
  new Pair("members", new String[]{"2.2", "2.3", "2.4", "2.5"}), 
  new Pair("phase", new Integer(0)),
  new Pair("seq", new Integer(0)),
  new Pair("response", new Integer(0)),
  new Pair("request", "null"))
// setup standby state
.addNode("2.2", "2pc", "2")
.addNode("2.3", "2pc", "2")
.addNode("2.4", "2pc", "2")
.addNode("2.5", "2pc", "2")
// setup leader state
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
// setup requester: echo server
.addNode("1", "echo", null, new Pair("target", new String[]{"2.1", "3.1"}))
// set triggering machine
.setStartup("1").build();
```

And a complete cluster is simulated as:

![consensus](docs/img/2pc-consensus.png)

![response](docs/img/2pc-response.png)

## Crowd Port

Apart from simulation on single machine, Crowd is designed to fit the needs of various parallel task, including processes, network neighbors.

Solutions include network communication, shared memory.

## Inspiration

Concurrent data structure:

* **[priority queue](./docs/concurrent-priority-queue-design.md)**: used for discrete event scheduling

Distributed simulator project:

* [fitzroy](https://github.com/jtfmumm/fitzroy): Distributed system simulator and distributed algorithm scratchpad

* [shadow](https://github.com/shadow/shadow): Shadow is a unique discrete-event network simulator that runs real applications like Tor, and distributed systems of thousands of nodes on a single machine.

* [Graphite](https://github.com/mit-carbon/Graphite): A parallel, distributed simulator for multicores.

* [primesim](https://github.com/PrincetonUniversity/primesim): A parallel and distributed simulator for thousand-core chips

Workflow / Dataflow visualization:

* [VWorkflows](https://github.com/miho/VWorkflows): Interactive flow/graph visualization for building domain specific visual programming environments.

* [Ambrose](https://github.com/twitter/ambrose): Twitter Ambrose is a platform for visualization and real-time monitoring of MapReduce data workflows.

Consensus protocol:

* [parsec](https://github.com/maidsafe/parsec): Protocol for asynchronous, reliable, secure and efficient consensus.
