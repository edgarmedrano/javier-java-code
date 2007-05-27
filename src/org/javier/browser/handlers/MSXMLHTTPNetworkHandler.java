/**
 * File:        MSXMLHTTPNetworkHandler.java
 * Description: A network handler that uses MSXMLHTTP
 * Author:      Edgar Medrano Pérez 
 *              edgarmedrano at gmail dot com
 * Created:     2007.04.21
 * Company:     JAVIER project
 *              http://javier.sourceforge.net
 * Notes:       WARNING!
 *              This class runs only on MS Windows, because
 *              it relies on MSXML.dll in the end 
 */

package org.javier.browser.handlers;

import static org.javier.jacob.OleAutomation.createActiveXObject;

import java.util.concurrent.TimeUnit;

import org.javier.browser.Document;
import org.javier.jacob.MSXML.DOMDocument;
import org.javier.jacob.MSXML.MSXMLHTTP;
import org.javier.jacob.MSXML.JavaNode;
import org.w3c.dom.Node;

/**
 * A network handler that uses {@link MSXMLHTTP}.
 * <p><strong>WARNING!</strong><br>
 * This class runs only on MS Windows, because it relies on MSXML.dll in 
 * the end.</p>
 * 
 * @author Edgar Medrano Pérez
 * @see MSXMLHTTP
 */
public class MSXMLHTTPNetworkHandler 
	extends AbstractNetworkHandler 
	implements Runnable {
	protected MSXMLHTTP xmlhttp;
	private DocType dt;
	//private Thread timer;
	private int timeout;
	
	public MSXMLHTTPNetworkHandler() {
		xmlhttp = (MSXMLHTTP) createActiveXObject(MSXMLHTTP.class); 
	}
	
	/**
	 * Use this to get the response text
	 * @return the response text
	 */
	public String getText() {
		return xmlhttp.getResponseText();
	}
	
	/**
	 * Use this to get the response XML
	 * @return the response XML
	 */
	public Node getXML() {
		DOMDocument doc = xmlhttp.responseXML();
		
		if(doc == null) {
			return null;
		}
		
		return new JavaNode(doc);
	}

	/**
	 * Loads the specified {@link Document} asynchronously.
	 * 
	 * @param docType the document type Xml/Text
	 * @param docRef  the document to be loaded 
	 * @return <code>true</code> on success. 
	 *         Actually it always return <code>true</code>.
	 */
	public boolean load(DocType docType, Document docRef) {
		String url = docRef.getUrl();
		String method = docRef.getMethod();
		String enctype = docRef.getEnctype();
		int maxAge = docRef.getMaxage();
		int maxStale = docRef.getMaxstale();
		String[] urlParts = url.split("\\?");
		timeout = docRef.getTimeout();
		dt = docType;
		
		
	    if (url.length() >= 2083) {
			method = "POST";
		}
		
		if(method.equalsIgnoreCase("POST")) {
			xmlhttp.open(method, urlParts[0], true);
			if(enctype.equals("")) {
				xmlhttp.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
			}
		} else {
			xmlhttp.open(method, url, true);		
		}
		
		if(!enctype.equals("")) {
			xmlhttp.setRequestHeader("Content-Type", enctype);
		} 
		
		if(maxAge == 0) {
			xmlhttp.setRequestHeader("Cache-Control","must-revalidate");
	    } else {
			xmlhttp.setRequestHeader("Cache-Control","max-age=" + maxAge);
		}

		if(maxStale != 0) {
			xmlhttp.setRequestHeader("Cache-Control","max-stale=" + maxStale);		
		}
		
		/*
		timer = new Thread(this);
		timer.start();
		*/
		
	    if (urlParts.length > 1) {
		   xmlhttp.send(urlParts[1]);
	    }else{
	       xmlhttp.send();
		} 
		
	    /*Running here instead of a thread*/
	    run();
	    
		return true;
	}

	/**
	 * Handles the response and the state changes
	 */
	protected void readyStateChange() {
		int readyState = xmlhttp.getReadyState(); 
		if (readyState == 4) {
			int status = xmlhttp.getStatus();
			if(status == 200 || status == -1) {
				if(dt == DocType.Xml) {
					fireLoaded(getXML()); 		     
				} else {
					fireLoaded(getText()); 	
				}
			} else {	
				fireLoaded("error.badfetch.http." + xmlhttp.getStatus());
			}
		} else {
			fireReadyStateChanged(xmlhttp.getReadyState());
		}
	}

	/**
	 * Polls the request state and report changes, this method calls
	 * readyStateChange
	 */
	public void run() {
		int readyState = xmlhttp.getReadyState(); 
		int lastReadyState;
		int elapsedTime = 0;
		
		while(readyState != 4) {
			lastReadyState = readyState;
			while(readyState == lastReadyState) {
				if(timeout > 0) {
					if(elapsedTime >= timeout) {
						xmlhttp.abort();
						fireLoaded("error.badfetch.http.timeout");					
						return;
					}
				}
				try {
					TimeUnit.MILLISECONDS.sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				elapsedTime += 10;
				readyState = xmlhttp.getReadyState();
			}
			
			if(readyState != 4) {
				readyStateChange();
			}
		}
		
		readyStateChange(); // process state 4 
		//timer = null;
	}

	/* (non-Javadoc)
	 * @see org.javier.browser.handlers.NetworkHandler#abort()
	 */
	public void abort() {
		xmlhttp.abort();
	}
}
