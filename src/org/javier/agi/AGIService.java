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

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * FastAGI Server.
 */
public class AGIService {
	
	/** The server socket. */
	protected final ServerSocket serverSocket;
	
	/** The pool. */
	protected final ExecutorService pool;
	
	/** The handler constructor. */
	protected Constructor handlerConstructor;

	/**
	 * The main method.
	 * 
	 * @param args the args
	 */
	public static void main(String args[]) {
		String handlerClass;
		int port;
		int poolSize;
		
		handlerClass = args.length > 0 ? args[0] : AGIScript.class.getName();
		port = args.length > 1 ? Integer.valueOf(args[1]) : 4573;
		poolSize = args.length > 2 ? Integer.valueOf(args[2]) : 24;
		
		try {
			new AGIService(handlerClass,port,poolSize).run();
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

		/** The script. */
		protected AGIScript script;

		private AGIConnection agi;

		/**
		 * The Constructor.
		 * 
		 * @param socket
		 *            the socket
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
		public Handler(Socket socket) throws IllegalArgumentException,
				InstantiationException, IllegalAccessException,
				InvocationTargetException, IOException {
			this.socket = socket;
			try {
				agi = new AGIConnection(socket.getInputStream(),socket.getOutputStream(), System.err);
			} catch (AGIException e) {
				socket.close();
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			script = (AGIScript) handlerConstructor.newInstance();
		}

		/* (non-Javadoc)
		 * @see java.lang.Runnable#run()
		 */
		public void run() {
			script.execute(agi);
			agi.close();
			try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			/**/
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
	public AGIService(String handlerClass, int port, int poolSize)
			throws IOException, ClassNotFoundException, SecurityException,
			NoSuchMethodException {
		this(Class.forName(handlerClass).getConstructor(new Class<?>[] {}), port, poolSize);
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
	public AGIService(Constructor handlerConstructor, int port, int poolSize)
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
		
		serverSocket = new ServerSocket(port);
		pool = Executors.newFixedThreadPool(poolSize);
	}

	/**
	 * Run.
	 */
	public void run() { // run the service
		try {
			for (;;) {
				try {
					System.out.println("ACCEPT");
					new Thread(new Handler(serverSocket.accept())).start();
					//pool.execute(new Handler(serverSocket.accept()));
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (InstantiationException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
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
	protected void shutdown() {
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
