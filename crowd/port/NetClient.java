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

public class NetClient extends Buildable implements OPort {
	private Map<String, ServerMeta> connected = new ConcurrentHashMap<String, ServerMeta>();
	private ExecutorService threadPool = Executors.newFixedThreadPool(1);
	private AtomicBoolean close = new AtomicBoolean(false);
	private class ServerMeta {
		public Socket socket;
		public ObjectOutputStream os = null;
		ServerMeta(Socket s) {
			socket = s;
			try {
				os = new ObjectOutputStream(s.getOutputStream());
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
	public NetClient() {
		super();
	}
	public NetClient(App app) {
		super(app);
	}
	public App build() {
		parent.addOPort(this);
		return parent;
	}
	public void close() {
		close.set(true);
	}
	public NetClient register(String name, String ip, int port) {
		try {
			final Socket newSocket;
			newSocket = new Socket(ip, port);
			threadPool.execute(()->{
				receive(name, newSocket);
			});
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return this;
	}
	private void receive(String name, Socket socket) {
		ServerMeta meta = null;
		ObjectInputStream is = null;
		try {
			meta = new ServerMeta(socket);
			connected.put(name, meta);
			is = new ObjectInputStream(socket.getInputStream()); // will block until server send?
			while(socket.isConnected() && !close.get()) {
				String message = (String) is.readObject();
				parent.input(message);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
				connected.remove(name);
				if(meta != null && meta.os != null) {
					meta.os.close();
				}
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
	public boolean send(String target, String message) {
		ServerMeta meta = connected.get(target);
		if(meta == null) {
			return false;
		}
		try {
			meta.os.writeObject(parent.getName() + ":" + message);
		} catch(Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
}