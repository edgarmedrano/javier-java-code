/**
 * File:        ConsoleInputHandler.java
 * Description: Console input handler
 * Author:      Edgar Medrano Pérez
 *              edgarmedrano at gmail dot com
 * Created:     2007.04.15
 * Company:     JAVIER project
 *              http://javier.sourceforge.net
 * Notes:        
 */
package org.javier.browser.handlers;

import java.io.BufferedReader;
import java.io.Console;
import java.io.IOException;
import java.io.InputStreamReader;

import org.javier.browser.handlers.InputHandler;

/**
 * Get input from console.
 */
public class ConsoleInputHandler implements InputHandler {
	protected Console console = System.console();
	protected BufferedReader br;
	
	public ConsoleInputHandler() {
		if(console == null) {
			br = new BufferedReader(new InputStreamReader(System.in));
		}
	}
	
	/**
	 * Gets the input.
	 * 
	 * @param text  the text
	 * 
	 * @return the input
	 */
	public String getInput(String text) throws IOException {
		return getInput(text,"");
	}

	/**
	 * Gets the input.
	 * 
	 * @param text  the text
	 * @param value the default value
	 * 
	 * @return the input
	 */
	public String getInput(String text, String value) throws IOException {
		return getInput(text, value, 0, 32767);		
	}

	/**
	 * Gets the input.
	 * 
	 * @param text  the text
	 * @param value the default value
	 * @param min   the minimum value's length
	 * @param max   the maximum value's length
	 * 
	 * @return the input
	 */
	public String getInput(String text, String value, int min, int max) throws IOException {
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
