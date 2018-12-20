package crowd;

public class EchoRunner implements Runner {
	private int echoCount = 0;
	private String lastEcho = "";
	private String groupId;
	private String nodeId;
	EchoRunner(String group, String node) {
		groupId = group;
		nodeId = node;
	}
	public String report(Network network){
		return String.valueOf(echoCount) + ":" + lastEcho;
	}
	public void respond(Network network, Protocol protocol, String message){
		network.broadcast(protocol.makeMessage(groupId, nodeId, "0", "0", protocol.parseMessage(message)));
	}
	public void online(Network network, Protocol protocol){
		network.broadcast(protocol.makeMessage(groupId, nodeId, "0", "0", ""));
	}
}