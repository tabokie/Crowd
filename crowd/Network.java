package crowd;

// datagram interface for simulated network
public interface Network {	
	void broadcast(String message);
	void send(String target, String message);
}