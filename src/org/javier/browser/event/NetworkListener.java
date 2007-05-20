/**
 * File:        NetworkListener.java
 * Description: Network Listener
 * Author:      Edgar Medrano Pérez
 *              edgarmedrano at gmail dot com
 * Created:     2007.05.01
 * Company:     JAVIER project
 *              http://javier.sourceforge.net
 * Notes:        
 */
package org.javier.browser.event;

/**
 * Network Listener.
 */
public interface NetworkListener {

	/**
	 * Ready state changed.
	 * 
	 * @param readyState
	 *            the ready state
	 */
	public void readyStateChanged(int readyState);

	/**
	 * Request completed.
	 * 
	 * @param result
	 *            the result
	 */
	public void requestCompleted(Object result);

}
