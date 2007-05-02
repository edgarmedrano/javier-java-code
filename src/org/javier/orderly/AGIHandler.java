/**
 * File:        AGIHandler.java
 * Description: AGI Input/Output handler 
 * Author:      Edgar Medrano Pérez
 *              edgarmedrano at gmail dot com
 * Created:     2007.05.02
 * Company:     JAVIER project
 *              http://javier.sourceforge.net
 * Notes:        
 */
package org.javier.orderly;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

import org.javier.browser.Javier;
import org.javier.browser.event.ErrorListener;
import org.javier.browser.event.JavierListener;
import org.javier.browser.event.OutputListener;
import org.javier.browser.handlers.ConsoleErrorHandler;
import org.javier.browser.handlers.ConsoleInputHandler;
import org.javier.browser.handlers.ConsoleLogHandler;
import org.javier.browser.handlers.ConsoleOutputHandler;
import org.javier.browser.handlers.InputHandler;
import org.javier.browser.handlers.MSXMLHTTPNetworkHandler;
import org.javier.browser.handlers.SAPIOutputHandler;
import org.javier.browser.handlers.StreamLogHandler;

import com.orderlysoftware.orderlycalls.asterisk.agi.AGIConnection;
import com.orderlysoftware.orderlycalls.asterisk.agi.AGIReusableProcessor;
import com.orderlysoftware.orderlycalls.asterisk.agi.AGIServer;
import com.orderlysoftware.orderlycalls.asterisk.agi.AGISettings;
import com.orderlysoftware.orderlycalls.base.Settings;

/**
 * AGI Input/Output handler
 */
public class AGIHandler 
	implements AGIReusableProcessor,
	  JavierListener,
	  InputHandler,
	  OutputListener
	  {

	private AGIConnection agi;
	private Javier javier;
	private Method command;
	private Object commandArgs[];
	private Object commandResult; 

	/* (non-Javadoc)
	 * @see com.orderlysoftware.orderlycalls.asterisk.agi.AGIProcessor#processCall(com.orderlysoftware.orderlycalls.asterisk.agi.AGIConnection)
	 */
	public void processCall(AGIConnection agi) throws IOException {
		this.agi = agi;
		HashMap properties = agi.getAGIProperties();
		
		javier = new Javier(this,new MSXMLHTTPNetworkHandler());
		
		try {
			javier.addLogListener(new StreamLogHandler("Javier.log"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	
		javier.mainLoop("http://localhost/sictel.php");
		
		for(;;) {
			if(command != null) {
				try {
					commandResult = command.invoke(agi, commandArgs);
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
				while(commandResult != null) {
					agi.noop();
				}
			} else {
				if(command == null) {
					agi.exec("WAIT", "1");
				}
			}
		}
		
		
	}

	/**
	 * Excecution ended.
	 * 
	 * @param endCode
	 *            the end code
	 */
	public void excecutionEnded(int endCode) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * Load state changed.
	 * 
	 * @param readyState
	 *            the ready state
	 */
	public void loadStateChanged(int readyState) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * Url changed.
	 * 
	 * @param url
	 *            the url
	 */
	public void urlChanged(String url) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * Gets the input.
	 * 
	 * @param text
	 *            the text
	 * @param value
	 *            the value
	 * 
	 * @return the input
	 */
	public String getInput(String text, String value) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Adds the text.
	 * 
	 * @param text
	 *            the text
	 */
	public void addText(String text) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * Clear text.
	 */
	public void clearText() {
		// TODO Auto-generated method stub
		
	}

	/**
	 * Wait until done.
	 */
	public void waitUntilDone() {
		// TODO Auto-generated method stub
		
	}

}
