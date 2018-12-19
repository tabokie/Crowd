package crowd;

public interface Simulator {
	// void send(String target, String message);
	Protocol getProtocol();
	void setProtocol(Protocol p);
}