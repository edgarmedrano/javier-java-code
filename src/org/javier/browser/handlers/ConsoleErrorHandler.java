package org.javier.browser.handlers;

import java.io.Console;
import java.io.PrintWriter;

import org.javier.browser.ErrorListener;

public class ConsoleErrorHandler implements ErrorListener {
	protected PrintWriter pw;

	public ConsoleErrorHandler() {
		Console console = System.console();
		
		if(console != null) {
			pw = console.writer();
		} else {
			pw = new PrintWriter(System.out);
		}
		
	}
	
	public void errorFound(String description) {
		pw.println(description);
	}
}
