package crowd;

public interface Protocol {
	String makeMessage(String, String, String, String, String);
	String parseFromNodeId(String message);
	String parseFromGroupId(String message);
	String parseToNodeId(String message);
	String parseToGroupId(String message);
	String parseMessage(String message);
}