package org.javier.browser.handlers;

import java.io.Console;
import java.io.PrintWriter;

public class ConsoleLogHandler implements LogHandler {
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
	
	public void writeln(String text) {
		pw.println(text);
	}
}
