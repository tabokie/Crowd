package crowd.port;

import java.net.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.io.*;
import java.net.Socket;

import crowd.Buildable;
import crowd.App;
import crowd.Command;

public class NetServer extends Buildable implements OPort {
	private ServerSocket server;
	private AtomicBoolean close = new AtomicBoolean(false);
	private class ClientMeta {
		public Socket socket;
		public String name;
		public ObjectOutputStream os = null;
		ClientMeta(Socket socket) {
			this.socket = socket;
			try {
				os = new ObjectOutputStream(socket.getOutputStream());
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
	// IP:NAME to socket 
	private Map<String, ClientMeta> clients = new ConcurrentHashMap<String, ClientMeta>();
	private Thread listenerThread = null;
	private ExecutorService threadPool = Executors.newFixedThreadPool(3);
	public NetServer() {
		super();
	}
	public NetServer(App app) {
		super(app);
	}
	public NetServer listen(int port) {
		try {
			server = new ServerSocket(port);
		} catch(Exception e) {
			e.printStackTrace();
			return null; // failure return nullpointer
		}
		listenerThread = new Thread(()->{
			while(!close.get()) {
				try {
					final Socket newClient = server.accept();
					threadPool.execute(()->{
						serve(newClient);
					});
				}
				catch(Exception e) {
					e.printStackTrace();
					break;
				}
			}
		});
		return this;
	}
	public void close() {
		close.set(true);
	}
	private void serve(Socket client) {
		ObjectInputStream is = null;
		String name = null;
		try {
			is = new ObjectInputStream(client.getInputStream());
			String firstStrike = (String) is.readObject(); // exception if closed
			Command cmd = parent.getProtocol().parse(firstStrike);
			name = cmd.raiser;
			clients.put(name, new ClientMeta(client));
			parent.input(firstStrike);
			while(client.isConnected()) {
				String message = (String) is.readObject();
				parent.input(message);
			}
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if(is != null) is.close();
				if(name != null) {
					ClientMeta meta = clients.get(name);
					clients.remove(name);
					if(meta != null && meta.os != null) {
						meta.os.close();
					}		
				}
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
	public App build() {
		parent.addOPort(this);
		if(listenerThread != null) {
			listenerThread.start();
		}
		return parent;
	}
	public boolean send(String target, String message) {
		ClientMeta ret = clients.get(target);
		if(ret == null) return false;
		try {
			ret.os.writeObject(parent.getName() + ":" + message);
		} catch(Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
}