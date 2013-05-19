/**
 * File:        JabberHandler.java
 * Description: Jabber Input/Output handler 
 * Author:      Edgar Medrano Pérez
 *              edgarmedrano at gmail dot com
 * Created:     2013.05.18
 * Company:     JAVIER project
 *              http://javier.sourceforge.net
 * Notes:        
 */
package org.javier.jabber;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.javier.browser.Javier;
import org.javier.browser.event.JavierListener;
import org.javier.browser.event.OutputListener;
import org.javier.browser.handlers.InputHandler;
import org.javier.browser.handlers.MSXMLHTTPNetworkHandler;
import org.javier.browser.handlers.StreamLogHandler;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.util.StringUtils;

import com.jacob.com.ComThread;

/**
 * AGI Input/Output handler
 */
public class JabberHandler 
	implements MessageListener, 
	  PacketListener,
	  JavierListener,
	  InputHandler,
	  OutputListener,
	  Thread.UncaughtExceptionHandler
	  {

	private String buffer = "";
	private Javier javier;
	private String user;
	private JabberService server;
	
	static {
		ComThread.startMainSTA();		
	}
	

	public void execute(JabberService server, String user) {
		this.server = server;
		this.user = user;
		
	    String homeAddress = "http://localhost/javier/default.vxml";
	    String logFile = "Javier.log";
		
	    try {
		    Properties properties = new Properties();
		    File file = new File("JabberService.conf");
		    
	        properties.load(new FileInputStream(file));
	    	homeAddress = properties.getProperty("home_address", homeAddress);
	    	logFile = properties.getProperty("log_file", logFile);
	    } catch (IOException e) {
			e.printStackTrace();
	    }
		
		ComThread.InitMTA();		

		javier = new Javier(this,new MSXMLHTTPNetworkHandler());
		javier.addJavierListener(this);
		javier.addOutputListener(this);
		/*
		javier.setDebugEnabled(true);
		*/
		/*
		javier.addLogListener(new ConsoleLogHandler());
		*/
		try {
			javier.addLogListener(new StreamLogHandler(logFile));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}		
		
	    server.addPacketListener(this, new PacketFilter() {
				@Override
				public boolean accept(Packet packet) {
					if(packet instanceof Message) {
						Message message = (Message) packet;
						
						if(message.getType() == Message.Type.chat) {
							return StringUtils.parseName(message.getFrom()).equals(JabberHandler.this.user);
						}
					}
					
					return false;
				}
			});
	    
		try {
			javier.mainLoop(homeAddress);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		server.removePacketListener(this);
		ComThread.Release(); 				
	}

	/**
	 * Excecution ended.
	 * 
	 * @param endCode
	 *            the end code
	 */
	public void excecutionEnded(int endCode) {
	}

	/**
	 * Load state changed.
	 * 
	 * @param readyState
	 *            the ready state
	 */
	public void loadStateChanged(int readyState) {
		// do nothing
	}

	/**
	 * Url changed.
	 * 
	 * @param url
	 *            the url
	 */
	public void urlChanged(String url) {
		// do nothing
	}

	/**
	 * Gets the input.
	 * 
	 * @param text  the text
	 * 
	 * @return the input
	 */
	public String getInput(String text) throws IOException {
		return getInput(text,"");
	}

	/**
	 * Gets the input.
	 * 
	 * @param text  the text
	 * @param value the default value
	 * 
	 * @return the input
	 */
	public String getInput(String text, String value) throws IOException {
		return getInput(text, value, 0, 32767);		
	}

	/**
	 * Gets the input.
	 * 
	 * @param text  the text
	 * @param value the default value
	 * @param min   the minimum value's length
	 * @param max   the maximum value's length
	 * 
	 * @return the input
	 */
	public String getInput(String text, String value, int min, int max) throws IOException {
		String result = value;
		String timeout = javier.getProperty("timeout");
		long time = 0;
		long count = 0;
		
		if(timeout.indexOf("ms") >= 0) {
			timeout = timeout.replaceFirst("ms", "");
			time = Long.parseLong(timeout);
		} else {
			if(timeout.indexOf("s") > 0) {
				timeout = timeout.replaceFirst("s", "");
				time = Long.parseLong(timeout) * 1000;
			}
		}		
		
		try {
			while(buffer.length() == 0) {
				if(count >= time) {
					break;
				}
				Thread.sleep(1);
				count++;
			}
			
			result = buffer;
			buffer = "";
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}

	/**
	 * Adds the text.
	 * 
	 * @param text
	 *            the text
	 * @throws IOException 
	 */
	public void addText(String text) throws IOException {
		try {
			sendMessage(text, user);
		} catch (XMPPException e) {
			throw(new IOException(e.getMessage(),e.getCause()));
		}		
	}
	
	/**
	 * Clear text.
	 */
	public void clearText() {
		// do nothing
	}

	/**
	 * Wait until done.
	 */
	public void waitUntilDone() {
		// do nothing
	}

	public void uncaughtException(Thread t, Throwable e) {
		e.printStackTrace();	
	}

	@Override
	public void processPacket(Packet packet) {
		if(packet instanceof Message) {
			processMessage((Message) packet);
		}
	}

	private void processMessage(Message message) {
		buffer = message.getBody();
	}

	@Override
	public void processMessage(Chat chat, Message message) {}
	
    public void sendMessage(String message, String to) throws XMPPException {
	    Chat chat = server.createChat(to, this);
	    
	    chat.sendMessage(message);
    }

	public String getUser() {
		return user;
	}
	
}
