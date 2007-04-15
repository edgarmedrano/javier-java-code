package org.javier.browser.handlers;

import java.io.BufferedReader;
import java.io.Console;
import java.io.IOException;
import java.io.InputStreamReader;

import org.javier.browser.handlers.InputHandler;

public class ConsoleInputHandler implements InputHandler {
	protected Console console = System.console();
	protected BufferedReader br;
	
	public ConsoleInputHandler() {
		if(console == null) {
			br = new BufferedReader(new InputStreamReader(System.in));
		}
	}
	
	public String getInput(String text,String value) {
		String result = "";
		
		if(console != null) {
			result = console.readLine();
		} else {
			try {
				result =  br.readLine();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return result;
	}
}
