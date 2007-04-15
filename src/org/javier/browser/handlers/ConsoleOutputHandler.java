package org.javier.browser.handlers;

import java.io.Console;
import java.io.PrintWriter;

import org.javier.browser.OutputListener;

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
