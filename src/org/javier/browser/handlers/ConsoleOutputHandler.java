package org.javier.browser.handlers;

import java.io.Console;
import java.io.PrintWriter;

import org.javier.browser.handlers.OutputHandler;

public class ConsoleOutputHandler implements OutputHandler {
	PrintWriter pw;
	protected Console console = System.console();

	public ConsoleOutputHandler(String voice) {
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
