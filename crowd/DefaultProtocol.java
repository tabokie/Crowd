package crowd;

public class DefaultProtocol implements Protocol{
	public String parseNodeId(String message){
		int index = message.indexOf(":");
		return message.substring(0, index);
	}
	public String parseGroupId(String message){
		return "000";
	}
	public String parseMessage(String message){
		int index = message.indexOf(":");
		return message.substring(index + 1);
	}
}