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

public interface NetworkHandler {
	static final int UNINITIALIZED = 1; 
	static final int OPEN = 2; 
	static final int SENT = 3; 
	static final int RECEIVING = 4; 
	static final int LOADED = 5;
	
	static enum DocType { Text, Xml }; 
	public void addNetworkListener(NetworkListener l);
	public void removeNetworkListener(NetworkListener l);
	boolean loadXML(Document docRef);
	boolean loadText(Document docRef);
	boolean load(DocType docType, Document docRef);
	org.w3c.dom.Node getXML();
	String getText();
}
