package org.javier.browser.handlers;

import static org.javier.jacob.OleAutomation.createActiveXObject;

import java.util.concurrent.TimeUnit;

import org.javier.browser.Document;
import org.javier.jacob.MSXML.DOMDocument;
import org.javier.jacob.MSXML.MSXMLHTTP;
import org.javier.jacob.MSXML.JavaNode;
import org.w3c.dom.Node;

public class MSXMLHTTPNetworkHandler extends AbstractNetworkHandler implements Runnable {
	protected MSXMLHTTP xmlhttp;
	private DocType dt;
	private Node xml;
	private Thread timer;
	private int timeout;
	
	public MSXMLHTTPNetworkHandler() {
		xmlhttp = (MSXMLHTTP) createActiveXObject(MSXMLHTTP.class); 
	}
	
	public void readyStateChange() {
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
		
		timer = new Thread(this);
		timer.start();
		
	    if (urlParts.length > 1) {
		   xmlhttp.send(urlParts[1]);
	    }else{
	       xmlhttp.send();
		} 
		
		return true;
	}

	public String getText() {
		return xmlhttp.getResponseText();
	}

	public Node getXML() {
		if(xml == null) {
			DOMDocument doc = xmlhttp.responseXML();
			xml = new JavaNode(doc);
		}
		return xml;
	}

	public void run() {
		int readyState = xmlhttp.getReadyState(); 
		int lastReadyState = readyState;
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
			readyStateChange();
		}
	}
}
