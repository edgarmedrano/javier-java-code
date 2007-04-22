package org.javier.jacob.MSXML;

import org.javier.jacob.Dispatchable;
import org.javier.jacob.OleInterface;
import org.javier.jacob.OleMethod;
import org.javier.jacob.OleProperty;

import com.jacob.com.Dispatch;


@OleInterface(name="Msxml2.XMLHTTP")
public interface MSXMLHTTP extends Dispatchable {
	/**
	 * Specifies the event handler to be called when the readyState property changes. Read/write. 
	 */
	@OleProperty void setOnReadyStateChange(Dispatch object);
	 
	/**
	 * Represents the state of the request. Read-only. 
	 */
	@OleProperty int getReadyState();
	 
	/**
	 * Represents only one of several forms in which the HTTP response can be returned. Read-only.
	 */
	@OleProperty String getResponseBody(); 
	
	/**
	 * Represents only one of several forms in which the HTTP response can be returned. Read-only.
	 */
	@OleProperty Dispatch getResponseStream(); 
	
	/**
	 * Represents the response entity body as a string. Read-only.
	 */
	@OleProperty String getResponseText(); 
	 
	/**
	 * Represents the parsed response entity body. Read-only.
	 */ 
	@OleProperty DOMDocument responseXML(); 
	 
	/**
	 * Represents the HTTP status code returned by a request. Read-only.
	 */
	@OleProperty int getStatus(); 
	 
	/**
	 * Represents the HTTP response line status. Read-only.
	 */ 
	@OleProperty String getStatusText(); 
	 
	/**
	 * Cancels the current HTTP request.
	 */
	@OleMethod void abort(); 
	 
	/**
	 * Retrieves the values of all the HTTP headers.
	 */ 
	@OleMethod String getAllResponseHeaders(); 
	 
	/**
	 * Retrieves the value of an HTTP header from the response body.
	 */ 
	@OleMethod String getResponseHeader(String bstrHeader); 
	 
	/**
	 * Initializes an MSXML2.XMLHTTP request, and specifies the method, URL, and authentication information for the request.
	 */ 
	@OleMethod void open(String bstrMethod
			, String bstrUrl
			, boolean varAsync
			, String bstrUser
			, String bstrPassword); 
	@OleMethod void open(String bstrMethod
			, String bstrUrl
			, boolean varAsync
			, String bstrUser); 
	@OleMethod void open(String bstrMethod
			, String bstrUrl
			, boolean varAsync); 
	@OleMethod void open(String bstrMethod
			, String bstrUrl); 
	 
	/**
	 * Sends an HTTP request to the server and receives a response.
	 */
	@OleMethod void send(String body); 
	@OleMethod void send(); 
	 
	/**
	 * Specifies the name of an HTTP header.
	 */ 
	@OleMethod void setRequestHeader(String bstrHeader, String bstrValue); 
}
