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

public class ConsoleLogHandler implements LogListener {
	PrintWriter pw;
	protected Console console = System.console();

	public ConsoleLogHandler() {
		Console console = System.console();
		
		if(console != null) {
			pw = console.writer();
		} else {
			pw = new PrintWriter(System.out);
		}
	}

	public void logReported(String description, int level) {
		pw.println(description);
	}
}
