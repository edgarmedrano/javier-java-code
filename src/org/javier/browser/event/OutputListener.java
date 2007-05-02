/**
 * File:        OutputListener.java
 * Description: The listener interface for receiving output events.
 * Author:      Edgar Medrano P�rez
 *              edgarmedrano at gmail dot com
 * Created:     2007.04.15
 * Company:     JAVIER project
 *              http://javier.sourceforge.net
 * Notes:        
 */
package org.javier.browser.event;

/**
 * The listener interface for receiving output events.
 */
public interface OutputListener {

	/**
	 * Invoked to alert the output to wait until all output is done.
	 */
	void waitUntilDone();

	/**
	 * Invoked to alert the output that there is a text to output.
	 * 
	 * @param text the text to output
	 */
	void addText(String text);

	/**
	 * Invoked to alert the output that it needs to clear text.
	 */
	void clearText();
}
