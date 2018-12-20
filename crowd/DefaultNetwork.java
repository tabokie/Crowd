package crowd;

public class DefaultNetwork implements Network {
	private Monitor monitor = new Monitor();
	private Map<String, Runner> runners = new HashMap<String, Runner>();
	public void send(String target, String message) {
		// monitor.flow.connectGroup();
	}
	public void broadcast(String message) {
		return ;
	}
}