/**
 * File:        AbstractNetworkHandler.java
 * Description: Abstract network handler
 * Author:      Edgar Medrano Pérez
 *              edgarmedrano at gmail dot com
 * Created:     2007.04.14
 * Company:     JAVIER project
 *              http://javier.sourceforge.net
 * Notes:        
 */
package org.javier.browser.handlers;

import java.util.Vector;

import org.javier.browser.Document;
import org.javier.browser.event.NetworkListener;

/**
 * Abstract network handler.
 */
public abstract class AbstractNetworkHandler implements NetworkHandler {
	
	/** The network listeners. */
	private final Vector<NetworkListener> vecNetworkListeners 
	  = new  Vector<NetworkListener>();
	
	/* (non-Javadoc) 
	 * @see org.javier.browser.handlers.NetworkHandler#addNetworkListener(NetworkListener l)
	 */
	public void addNetworkListener(NetworkListener l) {
		vecNetworkListeners.add(l);
	}
	
	/* (non-Javadoc) 
	 * @see org.javier.browser.handlers.NetworkHandler#removeNetworkListener(NetworkListener l)
	 */
	public void removeNetworkListener(NetworkListener l) {
		vecNetworkListeners.add(l);
	}
	
	/**
	 * Fire ready state changed.
	 * 
	 * @param readyState the ready state
	 */
	protected void fireReadyStateChanged(int readyState) {
		for(NetworkListener l: vecNetworkListeners) {
			l.readyStateChanged(readyState);
		}
	}
	
	/**
	 * Fire loaded.
	 * 
	 * @param result the result
	 */
	protected void fireLoaded(Object result) {
		for(NetworkListener l: vecNetworkListeners) {
			l.requestCompleted(result);
		}
	}
		
	/* (non-Javadoc)
	 * @see org.javier.browser.handlers.NetworkHandler#loadXML(org.javier.browser.Document)
	 */
	public boolean loadXML(Document docRef) {
		return load(DocType.Xml,docRef);
	}

	/* (non-Javadoc)
	 * @see org.javier.browser.handlers.NetworkHandler#loadText(org.javier.browser.Document)
	 */
	public boolean loadText(Document docRef) {
		return load(DocType.Text,docRef);
	}	
}
