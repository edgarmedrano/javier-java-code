package org.javier.browser.handlers;

import java.io.Console;
import java.io.PrintWriter;

import org.javier.browser.handlers.ErrorHandler;

public class ConsoleErrorHandler implements ErrorHandler {
	PrintWriter pw;
	protected Console console = System.console();

	public ConsoleErrorHandler() {
		Console console = System.console();
		
		if(console != null) {
			pw = console.writer();
		} else {
			pw = new PrintWriter(System.out);
		}
		
	}
	
	public void writeln(String text) {
		pw.println(text);
	}
}
