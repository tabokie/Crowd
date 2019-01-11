package crowd.port;

public interface OPort {
	boolean send(String target, Object message);
	boolean send(Object message); // broadcase if supported
	void close();
}