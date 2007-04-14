package org.javier.browser.handlers;

import java.io.Console;

import org.javier.browser.handlers.InputHandler;

public class ConsoleInputHandler implements InputHandler {
	protected Console console = System.console();
	
	public String getInput(String text) {
		return console.readLine();
	}
}
