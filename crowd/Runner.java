package crowd;

public interface Runner {
	String report(Network network); // direct return serialized message
	void respond(Network network,Protocol protocol, String message);
	void online(Network network, Protocol protocol);
}