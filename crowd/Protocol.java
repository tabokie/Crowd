package crowd;

public interface Protocol {
	String parseNodeId(String message);
	String parseGroupId(String message);
	String parseMessage(String message);
}