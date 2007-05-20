/**
 * File:        ConsoleLogHandler.java
 * Description: Console log handler
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

import org.javier.browser.event.LogListener;

/**
 * Log messages to console.
 */
public class ConsoleLogHandler implements LogListener {
	
	/** The pw. */
	PrintWriter pw;
	
	/** The console. */
	protected Console console = System.console();

	/**
	 * The Constructor.
	 */
	public ConsoleLogHandler() {
		Console console = System.console();
		
		if(console != null) {
			pw = console.writer();
		} else {
			pw = new PrintWriter(System.out);
		}
	}

	/* (non-Javadoc)
	 * @see org.javier.browser.event.LogListener#logReported(java.lang.String, int)
	 */
	public void logReported(String description, int level) {
		pw.println(description);
	}
}
