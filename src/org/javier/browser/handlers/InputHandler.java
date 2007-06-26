/**
 * File:        InputHandler.java
 * Description: The Input Handler Interface
 * Author:      Edgar Medrano Pérez
 *              edgarmedrano at gmail dot com
 * Created:     2007.04.15
 * Company:     JAVIER project
 *              http://javier.sourceforge.net
 * Notes:        
 */
package org.javier.browser.handlers;

import java.io.IOException;

/**
 * The Input Handler Interface.
 */
public interface InputHandler {

	/**
	 * Gets the input.
	 * 
	 * @param text
	 *            the text
	 * 
	 * @return the input
	 * 
	 * @throws IOException
	 *             the IO exception
	 */
	public String getInput(String text) throws IOException;

	/**
	 * Gets the input.
	 * 
	 * @param text
	 *            the text
	 * @param value
	 *            the default value
	 * 
	 * @return the input
	 * 
	 * @throws IOException
	 *             the IO exception
	 */
	public String getInput(String text, String value) throws IOException;

	/**
	 * Gets the input.
	 * 
	 * @param min
	 *            the minimum value's length
	 * @param text
	 *            the text
	 * @param max
	 *            the maximum value's length
	 * @param value
	 *            the default value
	 * 
	 * @return the input
	 * 
	 * @throws IOException
	 *             the IO exception
	 */
	public String getInput(String text, String value, int min, int max) throws IOException;
}
