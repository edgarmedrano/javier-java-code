package org.javier.browser.handlers;

import java.util.Vector;

import org.w3c.dom.Node;

public abstract class AbstractNetworkHandler implements NetworkHandler {
	protected String text = "";
	protected Node xml = null;
	
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
	
	/**
	 * @see org.javier.browser.handlers.NetworkHandler#getText()
	 */
	public String getText() {
		return text;
	}
	
	/**
	 * @see org.javier.browser.handlers.NetworkHandler#getXML()
	 */
	public Node getXML() {
		return xml;
	}
}
