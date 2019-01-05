package crowd;

import javafx.util.Pair;
import java.util.concurrent.atomic.*;

import crowd.concurrent.EchoPrototype;
import crowd.ui.Container;
import crowd.port.NetClient;

public class ClientApp extends App {
	@Override
	public void init() {
		this.setName("client");
		this.buildChatbox()
		.build(Container.class).loadCompact().build()
		.build(NetClient.class).register("server", "127.0.0.1", 4008).build();
	}
	public static void main(String[] args) {
		launch(args);
	}
}
