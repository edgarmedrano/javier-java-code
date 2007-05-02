/**
 * File:        DocumentListener.java
 * Description: The listener interface for receiving Document events.
 * Author:      Edgar Medrano Pérez
 *              edgarmedrano at gmail dot com
 * Created:     2007.04.15
 * Company:     JAVIER project
 *              http://javier.sourceforge.net
 * Notes:        
 */
package org.javier.browser.event;

import org.javier.browser.Document.State;

/**
 * The listener interface for receiving Document events.
 */
public interface DocumentListener {
	
	/**
	 * Invoked when the Document founds an error.
	 * 
	 * @param description the error's description
	 */
	public void errorFound(String description);

	/**
	 * Invoked when the Document founds a warning.
	 * 
	 * @param description the warning
	 */
	public void warningFound(String description);

	/**
	 * Invoked when the Document founds a comment.
	 * 
	 * @param description the comment
	 */
	public void commentFound(String description);
	
	/**
	 * Invoked when the Document founds a verbose comment.
	 * 
	 * @param description the verbose comment
	 */
	public void verboseFound(String description);

	/**
	 * Invoked when the Document's state change.
	 * 
	 * @param state the new state
	 * @see State
	 */
	public void stateChanged(State state);
}
