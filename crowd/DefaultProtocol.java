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
      if(tokens[1].equals("group")) { // group name ...
        return new Command(tokens[0], Command.GROUP, 
        	(Object[])Arrays.copyOfRange(tokens, 2, tokens.length));
      }
      else if(tokens[1].equals("node")) {
        return new Command(tokens[0], Command.NODE, tokens[2], tokens[3]); // name group
      }
      else if(tokens[1].equals("link")) {
        if(tokens.length > 4) {
          return new Command(tokens[0], Command.LINK, tokens[2], tokens[3], Float.parseFloat(tokens[4])); // to from width
        }
        return new Command(tokens[0], Command.LINK, tokens[2], tokens[3]); // to from
      }
      else if(tokens[1].equals("send")) {
        if(tokens.length > 5) {
          return new Command(tokens[0], Command.SEND, tokens[2], tokens[3], tokens[4], tokens[5]); // from to message color
        }
        return new Command(tokens[0], Command.SEND, tokens[2], tokens[3], tokens[4]); // from to message
      }
      else if(tokens[1].equals("clear")) {
        return new Command(tokens[0], Command.RESET); // node name group
      }
      else if(tokens[1].equals("status")) {
        return new Command(tokens[0], Command.STATUS, tokens[2], Float.parseFloat(tokens[3]), Float.parseFloat(tokens[4]) ); // name, radius, progress
      }
      else if(tokens[1].equals("start")) { // name, type, group
        if(tokens.length >= 5)
          return new Command(tokens[0], Command.START, tokens[2], tokens[3], tokens[4]);
        else
          return new Command(tokens[0], Command.START, tokens[2], tokens[3], null); // auto grouping
      }
      else if(tokens[1].equals("shutdown")) { // node name
        return new Command(tokens[0], Command.SHUTDOWN, tokens[2]);
      }
      else if(tokens[1].equals("prototype")) {
        return new Command(tokens[0], Command.PROTOTYPE, tokens[2]);
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

