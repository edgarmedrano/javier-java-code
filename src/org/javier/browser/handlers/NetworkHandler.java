/**
 * File:        NetworkHandler.java
 * Description: The Network Handler Interface
 * Author:      Edgar Medrano Pérez
 *              edgarmedrano at gmail dot com
 * Created:     2007.04.14
 * Company:     JAVIER project
 *              http://javier.sourceforge.net
 * Notes:        
 */
package org.javier.browser.handlers;

import org.javier.browser.Document;
import org.javier.browser.event.NetworkListener;

/**
 * The Network Handler Interface.
 */
public interface NetworkHandler {
	
	/** No instruction is being received. */
	static final int UNINITIALIZED = 1; 
	
	/** Connection to server is open. */
	static final int OPEN = 2; 
	
	/** Request is being sent. */
	static final int SENT = 3; 
	
	/** Receiving data. */
	static final int RECEIVING = 4; 
	
	/** Transmision is complete. */
	static final int LOADED = 5;
	
	/**
	 * The Enum DocType.
	 */
	static enum DocType { /** The Text. */
		 Text, /** The Xml. */
		 Xml }; 
	
	/**
	 * Adds the network listener.
	 * 
	 * @param l the listener
	 */
	public void addNetworkListener(NetworkListener l);
	
	/**
	 * Removes the network listener.
	 * 
	 * @param l the listener
	 */
	public void removeNetworkListener(NetworkListener l);
	
	/**
	 * Request an XML document.
	 * 
	 * @param docRef the document to load
	 * 
	 * @return true, if successfully loaded
	 */
	boolean loadXML(Document docRef);
	
	/**
	 * Request a text document.
	 * 
	 * @param docRef the document to load
	 * 
	 * @return true, if successfully loaded
	 */
	boolean loadText(Document docRef);
	
	/**
	 * Request an specified kind of document.
	 * 
	 * @param docRef the document to load
	 * @param docType the document type
	 * 
	 * @return true, if successfully loaded
	 */
	boolean load(DocType docType, Document docRef);
	
	/**
	 * Gets the XML.
	 * 
	 * @return the XML
	 */
	org.w3c.dom.Node getXML();
	
	/**
	 * Gets the text.
	 * 
	 * @return the text
	 */
	String getText();
	
	/**
	 * Abort.
	 */
	public void abort();
}
