package crowd;

import java.util.*;

public class DefaultProtocol implements Protocol {
	// public static final String UI_PREFIX = "ui"; // user:
	// public static final String PORT_PREFIX = "pr"; // node:
	// public static final String SIMULATOR_PREFIX = "sm"; // node:
	public Command parse(String input) {
		if(input != null && !input.isEmpty()) {
			input = input.replaceFirst(":", " "); // raiser
      String[] tokens = input.split(" ");
      if(tokens[1].equals("g")) { // group name ...
        return new Command(tokens[0], Command.GROUP, 
        	(Object[])Arrays.copyOfRange(tokens, 2, tokens.length));
      }
      else if(tokens[1].equals("n")) {
        return new Command(tokens[0], Command.NODE, tokens[2], tokens[3]); // node name group
      }
      else if(tokens[1].equals("glink")) {
        return new Command(tokens[0], Command.LINK, tokens[2], tokens[3]); // node name group
      }
      else if(tokens[1].equals("nlink")) {
        return new Command(tokens[0], Command.SEND, tokens[2], tokens[3]); // node name group
      }
      else if(tokens[1].equals("clear")) {
        return new Command(tokens[0], Command.RESET); // node name group
      }
      else if(tokens[1].equals("server")) {
      	return new Command(tokens[0], Command.REGISTER, tokens[2], tokens[3]); // name + ip:port
      }
      else if(tokens[1].equals("relay")) {
      	return new Command(tokens[0], Command.COM, tokens[2], tokens[3]);
      }
      else {
      	return new Command(tokens[0], Command.EMPTY, input.substring(input.indexOf(" ") + 1));
      }
    }
    return null;
	}
	public String pickle(Command cmd) {
		return "user:wowo";
	}
}