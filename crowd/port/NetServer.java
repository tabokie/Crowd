package crowd.port;

import java.net.*;
import java.util.*;
import java.util.concurrent.*;

import crowd.Buildable;
import crowd.App;

public class NetServer extends Buildable {
	private ServerSocket server;
	private Map<String, Socket> clients = new ConcurrentHashMap<String, Socket>();
	private Thread serverThread;
	public NetServer() {
		super();
	}
	public NetServer(App app) {
		super(app);
	}
	public void listen(int port) throws Exception {
		server = new ServerSocket(port);
		serverThread = new Thread(() -> {
			while(true) {
				try {
					Socket newClient = server.accept();
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	public App build() {
		serverThread.start();
		return parent;
	}
}