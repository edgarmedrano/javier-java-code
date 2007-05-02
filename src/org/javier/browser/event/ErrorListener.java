/**
 * File:        ErrorListener.java
 * Description: The listener interface for receiving error events.
 * Author:      Edgar Medrano Pérez
 *              edgarmedrano at gmail dot com
 * Created:     2007.04.15
 * Company:     JAVIER project
 *              http://javier.sourceforge.net
 * Notes:        
 */
package org.javier.browser.event;

/**
 * The listener interface for receiving error events.
 */
public interface ErrorListener {
	/**
	 * Invoked when the Document founds an error.
	 * 
	 * @param description the error's description
	 */
	void errorFound(String description);
}
