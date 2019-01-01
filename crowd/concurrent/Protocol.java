package crowd.concurrent;

// new node is first identified as in the target's group
public interface Protocol { // message parser interface
	String stub(String msg);
}