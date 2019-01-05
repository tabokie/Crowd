package crowd.port;

public interface OPort {
	boolean send(String target, String message);
}