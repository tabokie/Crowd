package crowd.concurrent;

import java.util.Map;

public class TwoPhasePrototype implements Prototype {
	public void init(Map<String, Object> datas) {
		datas.put("lock", new Object());
		datas.put("leader", new Boolean(false));
	}
	private boolean inMember(String[] members, String member) {
		for(String m: members ) {
			if(member.equals(m)) return true;
		}
		return false;
	}
	public void receive(String thisNode, Simulator simulator, String fromNode, String message) {
		Object lock = simulator.getData(thisNode, "lock");
		synchronized(lock) { // refuse to use atomic
			if(!simulator.<Boolean>getData(thisNode, "leader")) { // simple echo
				simulator.getScheduler().enqueue(1, (Actor actor)-> {
					simulator.send(thisNode, fromNode, message);
				});
				return ;
			}
			final String[] members = simulator.getData(thisNode, "members");
			if(members == null) { // no cluster
				simulator.getScheduler().enqueue(1, (Actor actor) -> {
					simulator.send(thisNode, fromNode, "ok");
				});
			} else if(inMember(members, fromNode)){
				int phase = simulator.<Integer>getData(thisNode, "phase");
				int response = simulator.<Integer>getData(thisNode, "response");
				int seq = simulator.<Integer>getData(thisNode, "seq"); // nearest active transaction
				if(phase == 0) return; // not in transaction
				else if(phase >= 1) { // in prepare phrase
					int inSeq = Integer.parseInt(message);
					if(inSeq == seq) {
						response ++;
						if(response == members.length) {
							simulator.setData(thisNode, "response", new Integer(0)); // reset
							if(phase == 1) {
								simulator.setData(thisNode, "phase", new Integer(2)); // commit phase
								final String seqStr = String.valueOf(seq);
								simulator.getScheduler().enqueue(1, (Actor actor) -> {
									for(String m: members) {
										simulator.send(thisNode, m, seqStr);
									}
								});
							} else if(phase == 2) {
								simulator.setData(thisNode, "phase", new Integer(0)); // wait for new transaction
								simulator.getScheduler().enqueue(1, (Actor actor) -> {
									simulator.send(thisNode, simulator.<String>getData(thisNode, "request"), "success");
								});
							}
						} else {
							simulator.setData(thisNode, "response", new Integer(response));
						}
					} else if(inSeq < 0) { // failed
						simulator.setData(thisNode, "phase", new Integer(0));
						simulator.getScheduler().enqueue(1, (Actor actor) -> {
							simulator.send(thisNode, simulator.<String>getData(thisNode, "request"), "failed");
						});
					}
				}
			} else {
				int phase = simulator.<Integer>getData(thisNode, "phase");
				if(phase != 0) {
					simulator.getScheduler().enqueue(1, (Actor actor) -> {
						simulator.send(thisNode, fromNode, "failed");
					});
				} else { // a new task, enter prepare phrase
					final int seq = simulator.<Integer>getData(thisNode, "seq") + 1; // nearest active transaction
					simulator.setData(thisNode, "request", fromNode);
					simulator.setData(thisNode, "seq", new Integer(seq));
					simulator.setData(thisNode, "phase", new Integer(1));
					simulator.setData(thisNode, "response", new Integer(0));
					final String seqStr = String.valueOf(seq);
					simulator.getScheduler().enqueue(1, (Actor actor) -> {
						for(String m: members) {
							simulator.send(thisNode, m, seqStr);
						}
					});
				}
			}
		}
	}
	public void start(String thisNode, Simulator simulator) {
		Object lock = simulator.getData(thisNode, "lock");
		synchronized(lock) {
			if(!simulator.<Boolean>getData(thisNode, "leader")) return ; // only leader can
			System.out.println("leader start running");
			simulator.getScheduler().enqueue(1, (Actor actor) -> {

			});
			return;	
		}
	}

}