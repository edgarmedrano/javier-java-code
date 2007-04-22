package org.javier.browser.handlers;

import java.util.Vector;

import org.javier.browser.Document;

public abstract class AbstractNetworkHandler implements NetworkHandler {
	
	private final Vector<NetworkListener> vecNetworkListeners 
	  = new  Vector<NetworkListener>();
	
	/**
	 * @see org.javier.browser.handlers.NetworkHandler#addNetworkListener(NetworkListener l)
	 */
	public void addNetworkListener(NetworkListener l) {
		vecNetworkListeners.add(l);
	}
	
	/**
	 * @see org.javier.browser.handlers.NetworkHandler#removeNetworkListener(NetworkListener l)
	 */
	public void removeNetworkListener(NetworkListener l) {
		vecNetworkListeners.add(l);
	}
	
	protected void fireReadyStateChanged(int readyState) {
		for(NetworkListener l: vecNetworkListeners) {
			l.readyStateChanged(readyState);
		}
	}
	
	protected void fireLoaded(Object result) {
		for(NetworkListener l: vecNetworkListeners) {
			l.requestCompleted(result);
		}
	}
		
	public boolean loadXML(Document docRef) {
		return load(DocType.Xml,docRef);
	}

	public boolean loadText(Document docRef) {
		return load(DocType.Text,docRef);
	}	
}
