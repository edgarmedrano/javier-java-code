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

public class ConsoleOutputHandler implements OutputListener {
	protected PrintWriter pw;

	public ConsoleOutputHandler() {
		Console console = System.console();
		
		if(console != null) {
			pw = console.writer();
		} else {
			pw = new PrintWriter(System.out);
		}
	}
	
	public void addText(String text) {
		pw.println(text);
	}

	public void clearText() {
		/*do nothing*/
	}

	public void waitUntilDone() {
		/*do nothing*/
	}
}
