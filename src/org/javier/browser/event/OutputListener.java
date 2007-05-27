/**
 * File:        OutputListener.java
 * Description: The listener interface for receiving output events.
 * Author:      Edgar Medrano Pérez
 *              edgarmedrano at gmail dot com
 * Created:     2007.04.15
 * Company:     JAVIER project
 *              http://javier.sourceforge.net
 * Notes:        
 */
package org.javier.browser.event;

import java.io.IOException;

/**
 * The listener interface for receiving output events.
 */
public interface OutputListener {

	/**
	 * Invoked to alert the output to wait until all output is done.
	 */
	void waitUntilDone() throws IOException;

	/**
	 * Invoked to alert the output that there is a text to output.
	 * 
	 * @param text the text to output
	 */
	void addText(String text) throws IOException;

	/**
	 * Invoked to alert the output that it needs to clear text.
	 */
	void clearText() throws IOException;
}
