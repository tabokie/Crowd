package crowd;

import java.util.*;
import crowd.ui.WorkFlow;
import crowd.ui.ChatBox;
import javafx.application.Platform;

public class Command {
	public String raiser;
	public int operator;
	public Object[] operands;
	public static final int EMPTY = 0;
	public static final int RESET = 1;
	public static final int GROUP = 2;
	public static final int NODE = 3;
	public static final int LINK = 4; // between group
	public static final int SEND = 5; // can apply force
	public static final int STATUS = 6; // group progress
	public static final int COM = 7; // communicate through monitor
	public static final int REGISTER = 8; // communicate through monitor

	public void dispatch(App app) {
		final WorkFlow flow = app.getFlow();
		final ChatBox chatbox = app.getChatbox();
		switch (operator) {
			// case RESET: app.reset(); break;
			case GROUP:
			String[] tmp = new String[operands.length - 1];
			for(int i = 0; i < tmp.length; i++) tmp[i] = (String)operands[i+1];
			Platform.runLater(()->{
				flow.newGroup((String)operands[0], tmp);
			});
			break;
			case NODE:
			Platform.runLater(()->{
				flow.newNode((String)operands[0], (String)operands[1]);
			});
			break;
			case LINK:
			Platform.runLater(()->{
				flow.precedeGroup((String)operands[1], (String)operands[0]);
				if(operands.length > 2) {
					flow.setStroke((String)operands[1], (String)operands[0], (float)operands[2]);
				}
			});
			break;
			case SEND:
			Platform.runLater(()->{
				flow.connectNode((String)operands[0], (String)operands[1]);
			});
			break;
			case STATUS:
			Platform.runLater(()->{
				flow.setHalo((String)operands[0], (float)operands[1], (float)operands[2]);
			});
			break;
			case COM:
			app.output((String)operands[0], (String)operands[1]);
			break;
		}
	}
	public Command(String sender, int operator, Object... operands) {
		this.raiser = sender;
		this.operator = operator;
		this.operands = new Object[operands.length];
		for(int i = 0; i < operands.length; i++)
			this.operands[i] = operands[i];
	}
}