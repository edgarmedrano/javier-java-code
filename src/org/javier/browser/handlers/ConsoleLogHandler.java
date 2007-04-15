package org.javier.browser.handlers;

import java.io.Console;
import java.io.PrintWriter;

import org.javier.browser.LogListener;

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
