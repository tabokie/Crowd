package crowd;

public interface Protocol {
	Command parse(String input);
	String pickle(Command cmd);
}