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

## Crowd Simulator

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


### Preset Algorithm

Currently support algorithm:

* [ ] Lamport clock
* [ ] 2PC

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
