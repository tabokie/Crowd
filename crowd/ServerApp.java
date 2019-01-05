package crowd;

import javafx.util.Pair;
import java.util.concurrent.atomic.*;

import crowd.concurrent.EchoPrototype;
import crowd.ui.Container;
import crowd.port.NetServer;

public class ServerApp extends App {
	@Override
	public void init() {
		this.setName("server");
		this.buildChatbox()
		.build(Container.class).loadCompact().build()
		.build(NetServer.class).listen(4008).build();
	}
	public static void main(String[] args) {
		launch(args);
	}
}
