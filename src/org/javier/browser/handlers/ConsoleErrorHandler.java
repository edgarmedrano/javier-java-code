/**
 * File:        ConsoleErrorHandler.java
 * Description: Console error handler
 * Author:      Edgar Medrano Pérez
 *              edgarmedrano at gmail dot com
 * Created:     2007.04.15
 * Company:     JAVIER project
 *              http://javier.sourceforge.net
 * Notes:        
 */
package org.javier.browser.handlers;

import java.io.Console;
import java.io.PrintWriter;

import org.javier.browser.event.ErrorListener;

/**
 * Output error messages to console.
 */
public class ConsoleErrorHandler implements ErrorListener {
	
	/** The pw. */
	protected PrintWriter pw;

	/**
	 * The Constructor.
	 */
	public ConsoleErrorHandler() {
		Console console = System.console();
		
		if(console != null) {
			pw = console.writer();
		} else {
			pw = new PrintWriter(System.out);
		}
		
	}
	
	/* (non-Javadoc)
	 * @see org.javier.browser.event.ErrorListener#errorFound(java.lang.String)
	 */
	public void errorFound(String description) {
		pw.println(description);
	}
}
