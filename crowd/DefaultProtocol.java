package crowd;

public class DefaultProtocol implements Protocol{
	public String makeMessage(String fromGroup, String fromNode, String toGroup, String toNode, String message) {
		return fromGroup + "-" + fromNode + "->" + toGroup + "-" + toNode + ":" + message;
	}
	public String parseFromNodeId(String message){
		int startIndex = message.indexOf("-");
		int endIndex = message.indexOf("->");
		return message.substring(startIndex + 1, endIndex - startIndex - 1);
	}
	public String parseFromGroupId(String message) {
		int endIndex = message.indexOf("-");
		return message.substring(0, endIndex);
	}
	public String parseToGroupId(String message) {
		int startIndex = message.indexOf("->");
		int endIndex = message.indexOf("-");
		return message.substring(startIndex + 2, endIndex - startIndex - 2);
	}
	public String parseToNodeId(String message) {
		int startIndex = message.indexOf("-");
		int endIndex = message.indexOf(":");
		return message.substring(startIndex + 1, endIndex - startIndex - 1)
	}
	public String parseMessage(String message){
		int index = message.indexOf(":");
		return message.substring(index + 1);
	}
}