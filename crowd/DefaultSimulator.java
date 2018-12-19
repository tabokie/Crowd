package crowd;

public class DefaultSimulator implements Simulator {
	private Protocol protocol;
	public DefaultSimulator(Protocol p) {
		protocol = p;
	}
	public Protocol getProtocol() {
		return protocol;
	}
	public void setProtocol(Protocol p) {
		protocol = p;
	}
}