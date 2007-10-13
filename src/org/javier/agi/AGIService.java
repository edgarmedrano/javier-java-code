/**
 * File:        AGIService.java
 * Description: FastAGI Server
 * Author:      Edgar Medrano Pérez
 *              edgarmedrano at gmail dot com
 * Created:     2007.05.18
 * Company:     JAVIER project
 *              http://javier.sourceforge.net
 * Notes:        
 */
package org.javier.agi;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.javier.util.NullOutputStream;

/**
 * FastAGI Server.
 */
public class AGIService {
	
	/** The server socket. */
	protected final ServerSocket serverSocket;
	
	/** The pool. */
	protected final ExecutorService pool;
	
	/** The handler constructor. */
	protected Constructor<?> handlerConstructor;

	/**
	 * The main method.
	 * 
	 * @param args the args
	 */
	public static void main(String args[]) {
		String handlerClass = AGIScript.class.getName();
		String bindAddress = "";
		int port = 4573;
		int poolSize = 24;
		
	    try {
		    Properties properties = new Properties();
		    
	        properties.load(new FileInputStream("AGIService.properties"));
		    handlerClass = properties.getProperty("handler_class", handlerClass);
	    	bindAddress = properties.getProperty("bind_address", bindAddress);
		    
		    if(properties.containsKey("port")) {
				port = Integer.parseInt(properties.getProperty("port"));
		    }
		    
		    if(properties.containsKey("pool_size")) {
				poolSize = Integer.parseInt(properties.getProperty("poolSize", String.valueOf(poolSize)));
		    }
	    } catch (IOException e) {
	    	
	    }
		
		try {
			new AGIService(handlerClass,port,poolSize,bindAddress).run();
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
		
		/** The socket. */
		protected Socket socket;

		/**
		 * The Constructor.
		 * 
		 * @param socket
		 *            the socket
		 * @throws IOException 
		 * 
		 * @throws IllegalArgumentException
		 *             the illegal argument exception
		 * @throws InvocationTargetException
		 *             the invocation target exception
		 * @throws IllegalAccessException
		 *             the illegal access exception
		 * @throws InstantiationException
		 *             the instantiation exception
		 * @throws IOException 
		 */
		public Handler(Socket socket) throws IOException {
			this.socket = socket;
		}

		/* (non-Javadoc)
		 * @see java.lang.Runnable#run()
		 */
		public void run() {
			try {
				AGIConnection agi = new AGIConnection(socket.getInputStream()
						,socket.getOutputStream()
						,new NullOutputStream());
				
				try {
					AGIScript script = (AGIScript) handlerConstructor.newInstance();
					script.execute(agi);
				} catch (IllegalArgumentException e1) {
					e1.printStackTrace();
				} catch (InstantiationException e1) {
					e1.printStackTrace();
				} catch (IllegalAccessException e1) {
					e1.printStackTrace();
				} catch (InvocationTargetException e1) {
					e1.printStackTrace();
				}
				
				agi.close();
			} catch (AGIException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			/*Try to free resources*/
			socket = null;
			System.gc();
		}
	}

	/**
	 * The Constructor.
	 * 
	 * @param port
	 *            the port
	 * @param handlerClass
	 *            the handler class
	 * @param poolSize
	 *            the pool size
	 * @param bindAddress 
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
	public AGIService(String handlerClass, int port, int poolSize, String bindAddress)
			throws IOException, ClassNotFoundException, SecurityException,
			NoSuchMethodException {
		this(Class.forName(handlerClass).getConstructor(new Class<?>[] {}), port, poolSize, bindAddress);
	}

	/**
	 * The Constructor.
	 * 
	 * @param port
	 *            the port
	 * @param handlerConstructor
	 *            the handler constructor
	 * @param poolSize
	 *            the pool size
	 * @param bindAddress 
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
	public AGIService(Constructor<?> handlerConstructor, int port, int poolSize, String bindAddress)
			throws IOException, ClassNotFoundException, SecurityException,
			NoSuchMethodException {
		this.handlerConstructor = handlerConstructor;
		Class<?> clazz = handlerConstructor.getDeclaringClass();
		boolean implementsScript = false; 
		
		for(Class<?> implInt : clazz.getInterfaces()) {
			if(implInt.equals(AGIScript.class)) {
				implementsScript = true;
				break;
			}
		}
		
		if(!implementsScript) {
			throw(new ClassNotFoundException(clazz.getName() + " doesn't implement " + AGIScript.class.getName() + " interface"));
		}

		if(bindAddress != null && !bindAddress.equals("")) {
			serverSocket = new ServerSocket(port,0, InetAddress.getByName(bindAddress));
		} else {
			serverSocket = new ServerSocket(port);
		}
		
		pool = Executors.newFixedThreadPool(poolSize);
	}

	/**
	 * Run.
	 */
	public void run() { // run the service
		try {
			for (;;) {
				try {
					pool.execute(new Handler(serverSocket.accept()));
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				}
			}
		} catch (IOException ex) {
			shutdown();
		}
	}

	/**
	 * Shutdown.
	 */
	public void shutdown() {
		pool.shutdown(); // Disable new tasks from being submitted
		try {
			// Wait a while for existing tasks to terminate
			if (!pool.awaitTermination(60, TimeUnit.SECONDS)) {
				pool.shutdownNow(); // Cancel currently executing tasks
				// Wait a while for tasks to respond to being cancelled
				if (!pool.awaitTermination(60, TimeUnit.SECONDS))
					System.err.println("Pool did not terminate");
			}
		} catch (InterruptedException ie) {
			// (Re-)Cancel if current thread also interrupted
			pool.shutdownNow();
			// Preserve interrupt status
			Thread.currentThread().interrupt();
		}
	}
}
