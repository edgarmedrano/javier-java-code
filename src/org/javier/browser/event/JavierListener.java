/**
 * File:        JavierListener.java
 * Description: The listener interface for receiving Javier events.
 * Author:      Edgar Medrano Pérez
 *              edgarmedrano at gmail dot com
 * Created:     2007.04.15
 * Company:     JAVIER project
 *              http://javier.sourceforge.net
 * Notes:        
 */
package org.javier.browser.event;

import org.javier.browser.handlers.NetworkHandler;

/**
 * The listener interface for receiving Javier events.
 */
public interface JavierListener {
	
	/**
	 * Invoked when the load state changes. This allows a graphic browser 
	 * to represent the loading advance. 
	 * 
	 * @param readyState the new ready state
	 * 
	 * @see NetworkHandler#UNINITIALIZED
	 * @see NetworkHandler#OPEN
	 * @see NetworkHandler#SENT
	 * @see NetworkHandler#RECEIVING
	 * @see NetworkHandler#LOADED
	 */
	public void loadStateChanged(int readyState);
	
	/**
	 * Invoked when the URL changes. This notifies that a new document is
	 * loaded. In a graphic browser this allows to change the address bar
	 * to show the loaded document's URL.
	 * 
	 * @param url the new URL
	 */
	public void urlChanged(String url);
		
	/**
	 * Invoked when the last document's excecution ends.
	 * 
	 * @param endCode the end code
	 */
	public void excecutionEnded(int endCode);
	
}
