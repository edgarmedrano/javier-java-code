package org.javier.browser.handlers;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Hashtable;

import org.javier.browser.Document;
import org.javier.browser.handlers.AbstractNetworkHandler;
import org.jdesktop.http.Method;
import org.jdesktop.http.async.XmlHttpRequest;
import org.jdesktop.http.async.AsyncHttpRequest.ReadyState;

public class XMLHTTPNetworkHandler 
	extends AbstractNetworkHandler 
	implements PropertyChangeListener {
	protected XmlHttpRequest xmlhttp = new XmlHttpRequest();
	private DocType dt;
	static protected final Hashtable<ReadyState,Integer> htReadyStateInt 
		= new Hashtable<ReadyState,Integer>(); 
	static {
		htReadyStateInt.put(ReadyState.UNINITIALIZED, UNINITIALIZED);
		htReadyStateInt.put(ReadyState.OPEN, OPEN);
		htReadyStateInt.put(ReadyState.SENT, SENT);
		htReadyStateInt.put(ReadyState.RECEIVING, RECEIVING);
		htReadyStateInt.put(ReadyState.LOADED, LOADED);
	}
	
	public XMLHTTPNetworkHandler() {
		xmlhttp.addReadyStateChangeListener(this);
	}

	public void propertyChange(PropertyChangeEvent arg0) {
		ReadyState readyState = xmlhttp.getReadyState(); 
		if (readyState == ReadyState.LOADED) {
			int status = xmlhttp.getStatus();
			if(status == 200 || status == -1) {
				if(dt == DocType.Xml) {
					fireLoaded(xmlhttp.getResponseXML()); 		     
				} else {
					fireLoaded(xmlhttp.getResponseText()); 		     						
				}
			} else {	
				fireLoaded("error.badfetch.http." + xmlhttp.getStatus());
			}
		} else {
			fireReadyStateChanged(htReadyStateInt.get(xmlhttp.getReadyState()));
		}
	}
	
	public boolean load(DocType docType, Document docRef) {
		String url = docRef.getUrl();
		String method = docRef.getMethod();
		String enctype = docRef.getEnctype();
		int maxAge = docRef.getMaxage();
		int maxStale = docRef.getMaxstale();
		String[] urlParts = url.split("\\?");
		dt = docType;
		
	    if (url.length() >= 2083) {
			method = "POST";
		}
		
		if(Method.valueOf(method) == Method.POST) {
			xmlhttp.open(Method.POST, urlParts[0], true);
			if(enctype.equals("")) {
				xmlhttp.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
			}
		} else {
			xmlhttp.open(Method.GET, url);		
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
		
	    if (urlParts.length > 1) {
		   xmlhttp.send(urlParts[1]);
	    }else{
	       xmlhttp.send();
		} 
		
		return true;
	}
	
	public boolean loadXML(Document docRef) {
		return load(DocType.Xml,docRef);
	}

	public boolean loadText(Document docRef) {
		return load(DocType.Text,docRef);
	}
}
