/**
 * File:        ConsoleOutputHandler.java
 * Description: Console output handler
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

import org.javier.browser.event.OutputListener;

/**
 * Output prompts to console.
 */
public class ConsoleOutputHandler implements OutputListener {
	
	/** The pw. */
	protected PrintWriter pw;

	/**
	 * The Constructor.
	 */
	public ConsoleOutputHandler() {
		Console console = System.console();
		
		if(console != null) {
			pw = console.writer();
		} else {
			pw = new PrintWriter(System.out);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.javier.browser.event.OutputListener#addText(java.lang.String)
	 */
	public void addText(String text) {
		pw.println(text);
	}

	/* (non-Javadoc)
	 * @see org.javier.browser.event.OutputListener#clearText()
	 */
	public void clearText() {
		/*do nothing*/
	}

	/* (non-Javadoc)
	 * @see org.javier.browser.event.OutputListener#waitUntilDone()
	 */
	public void waitUntilDone() {
		/*do nothing*/
	}
}
