/**
 * File:        JabberService.java
 * Description: Jabber interface
 * Author:      Edgar Medrano Pérez
 *              edgarmedrano at gmail dot com
 * Created:     2013.05.18
 * Company:     JAVIER project
 *              http://javier.sourceforge.net
 * Notes:        
 */
package org.javier.jabber;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.util.StringUtils;

/**
 * FastAGI Server.
 */
public class JabberService implements Runnable, MessageListener, PacketListener {
    XMPPConnection connection;
	
	/** The pool. */
	protected final ExecutorService pool;
	
	private String bindAddress;

	private int port;

	private Hashtable<String, Handler> htHandlers = new Hashtable<String, JabberService.Handler>();

	private String password;

	private String user;

	/**
	 * The main method.
	 * 
	 * @param args the args
	 */
	public static void main(String args[]) {
		String bindAddress = "localhost";
		int port = 5222;
		String user = "";
		String password = "";
		int poolSize = 24;
		
	    try {
		    Properties properties = new Properties();
		    
	        properties.load(new FileInputStream("JabberService.conf"));
	    	bindAddress = properties.getProperty("server_address", bindAddress);
		    
		    if(properties.containsKey("port")) {
				port = Integer.parseInt(properties.getProperty("port"));
		    }
		    
		    if(properties.containsKey("pool_size")) {
				poolSize = Integer.parseInt(properties.getProperty("poolSize", String.valueOf(poolSize)));
		    }
		    
		    if(properties.containsKey("user")) {
				user = properties.getProperty("user", user);
		    }
		    
		    if(properties.containsKey("password")) {
				password = properties.getProperty("password", password);
		    }
	    } catch (IOException e) {
	    	
	    }
	    
		try {
			new JabberService(port,poolSize,bindAddress,user,password).run();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * The Class Handler.
	 */
	protected class Handler implements Runnable {
		
		/** The user. */
		protected String user;

		/**
		 * The Constructor.
		 * 
		 * @param socket
		 *            the socket
		 */
		public Handler(String user) {
			this.user = user;
		}

		/* (non-Javadoc)
		 * @see java.lang.Runnable#run()
		 */
		public void run() {				
			JabberHandler jh = new JabberHandler();
			jh.execute(JabberService.this, user);
		}
	}

	/**
	 * The Constructor.
	 * 
	 * @param port
	 *            the port
	 * @param poolSize
	 *            the pool size
	 * @param bindAddress 
	 * @param password 
	 * @param user 
	 * 
	 * @throws SecurityException
	 *             the security exception
	 * @throws ClassNotFoundException
	 *             the class not found exception
	 * @throws IOException
	 *             the IO exception
	 * @throws NoSuchMethodException
	 *             the no such method exception
	 */
	public JabberService(int port, int poolSize, String bindAddress, String user, String password)
			throws IOException, ClassNotFoundException, SecurityException,
			NoSuchMethodException {
		this.port = port;
		this.bindAddress = bindAddress; 
		this.user = user;
		this.password = password;
		
		pool = Executors.newFixedThreadPool(poolSize);
	}

	/**
	 * Run.
	 */
	public void run() { // run the service
		// turn on the enhanced debugger
	    //XMPPConnection.DEBUG_ENABLED = true;
	 
	    // Enter your login information here
	    try {
			login(user, password);
			
		    for(;;) {
		    	try {
		    		Thread.sleep(1000);
		    	} catch (InterruptedException e) {
					e.printStackTrace();
					break;
				}
		    }
		 
			shutdown();
			
		    disconnect();
			
		} catch (XMPPException e1) {
			e1.printStackTrace();
		}	 
	}

    public void login(String userName, String password) throws XMPPException {
	    ConnectionConfiguration config = new ConnectionConfiguration(bindAddress,port, bindAddress);
	    connection = new XMPPConnection(config);
	    connection.connect();
	    connection.login(userName, password);
	    connection.addPacketListener(this, new PacketFilter() {
				@Override
				public boolean accept(Packet packet) {
					return (packet instanceof Message);
				}
			});
    }
    
    public void disconnect() {
    	connection.disconnect();
    }
	
	
	/**
	 * Shutdown.
	 */
	public int shutdown() {
		pool.shutdown(); // Disable new tasks from being submitted
		try {
			// Wait a while for existing tasks to terminate
			if (!pool.awaitTermination(60, TimeUnit.SECONDS)) {
				pool.shutdownNow(); // Cancel currently executing tasks
				// Wait a while for tasks to respond to being cancelled
				if (!pool.awaitTermination(60, TimeUnit.SECONDS)) {
					System.err.println("Pool did not terminate");
					return 1;
				}
			}
		} catch (InterruptedException ie) {
			// (Re-)Cancel if current thread also interrupted
			pool.shutdownNow();
			// Preserve interrupt status
			Thread.currentThread().interrupt();
		}
		return 0;
	}

	@Override
	public void processPacket(Packet packet) {
		if(packet instanceof Message) {
			Message message = (Message) packet;
			
			if(message.getType() == Message.Type.chat) {
				String user = StringUtils.parseName(message.getFrom());
				
				Handler handler = htHandlers.get(user);
				if(handler == null) {
					handler = new Handler(user);
					htHandlers.put(user,handler);
					try {
						pool.execute(handler);
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					}				
				}
			}
		}
	}

	@Override
	public void processMessage(Chat chat, Message message) { }

	public void addPacketListener(JabberHandler jabberHandler,
			PacketFilter packetFilter) {
		connection.addPacketListener(jabberHandler, packetFilter);
		
	}

	public void removePacketListener(JabberHandler jabberHandler) {
		connection.removePacketListener(jabberHandler);
		htHandlers.remove(jabberHandler.getUser());
	}

	public Chat createChat(String to, JabberHandler jabberHandler) {
		return connection.getChatManager().createChat(to, jabberHandler);
	}
}
