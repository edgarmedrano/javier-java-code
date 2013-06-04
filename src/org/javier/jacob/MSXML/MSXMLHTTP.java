/**
 * File:        MSXMLHTTP.java
 * Description: XMLHTTP from MSXML.dll 
 * Author:      Edgar Medrano Pérez
 *              edgarmedrano at gmail dot com
 * Created:     2007.04.21
 * Company:     JAVIER project
 *              http://javier.sourceforge.net
 * Notes:        
 */
package org.javier.jacob.MSXML;

import org.javier.jacob.Dispatchable;
import org.javier.jacob.OleInterface;
import org.javier.jacob.OleMethod;
import org.javier.jacob.OleProperty;

import com.jacob.com.Dispatch;

/**
 * Provides client-side protocol support for communication with HTTP servers.
 * 
 * @author Edgar Medrano Pérez
 */
@OleInterface(name="Msxml2.XMLHTTP")
public interface MSXMLHTTP extends Dispatchable {
	
	/**
	 * Specifies the event handler to be called when the readyState property
	 * changes. Read/write.
	 * 
	 * @param object the ready state change event handler
	 */
	@OleProperty void setOnReadyStateChange(Dispatch object);
	 
	/**
	 * Represents the state of the request. Read-only.
	 * 
	 * @return the ready state
	 */
	@OleProperty int getReadyState();
	 
	/**
	 * Represents only one of several forms in which the HTTP response can be
	 * returned. Read-only.
	 * 
	 * @return the response body
	 */
	@OleProperty String getResponseBody(); 
	
	/**
	 * Represents only one of several forms in which the HTTP response can be
	 * returned. Read-only.
	 * 
	 * @return the response stream
	 */
	@OleProperty Dispatch getResponseStream(); 
	
	/**
	 * Represents the response entity body as a string. Read-only.
	 * 
	 * @return the response text
	 */
	@OleProperty String getResponseText(); 
	 
	/**
	 * Represents the parsed response entity body. Read-only.
	 * 
	 * @return the DOM document
	 */ 
	@OleProperty DOMDocument responseXML(); 
	 
	/**
	 * Represents the HTTP status code returned by a request. Read-only.
	 * 
	 * @return the status
	 */
	@OleProperty int getStatus(); 
	 
	/**
	 * Represents the HTTP response line status. Read-only.
	 * 
	 * @return the status text
	 */ 
	@OleProperty String getStatusText(); 
	 
	/**
	 * Gets the number of milliseconds that the browser is to wait for a server response. Default value is 0.
	 * 
	 * @return the timeout in milliseconds
	 * @since Windows Internet Explorer 8
	 * @throws Exception If there's no Windows Internet Explorer 8 or better installed/supported.
	 */
	@OleProperty int getTimeout();
	
	/**
	 * Sets number of milliseconds that the browser is to wait for a server response. Default value is 0.
	 * @param timeout The timeout in milliseconds
	 * @since Windows Internet Explorer 8
	 * @throws Exception If there's no Windows Internet Explorer 8 or better installed/supported.
	 */
	@OleProperty void setTimeout(int timeout); 
	
	/**
	 * Cancels the current HTTP request.
	 */
	@OleMethod void abort(); 
	 
	/**
	 * Retrieves the values of all the HTTP headers.
	 * 
	 * @return the all response headers
	 */ 
	@OleMethod String getAllResponseHeaders(); 
	 
	/**
	 * Retrieves the value of an HTTP header from the response body.
	 * 
	 * @param bstrHeader A string containing the case-insensitive header name.
	 * 
	 * @return the resulting header information.
	 */ 
	@OleMethod String getResponseHeader(String bstrHeader); 
	 
	/**
	 * Initializes an MSXML2.XMLHTTP request, and specifies the method, URL, 
	 * and authentication information for the request.
	 * 
	 * @param bstrMethod   The HTTP method used to open the connection, such 
	 *                     as GET, POST, PUT, or PROPFIND. 
	 *                     For IXMLHTTPRequest, this parameter is not 
	 *                     case-sensitive. 
	 * @param bstrUrl      The requested URL. This can be either an absolute 
	 *                     URL, such as "http://Myserver/Mypath/Myfile.asp", 
	 *                     or a relative URL, such as "../MyPath/MyFile.asp". 
	 * @param varAsync     Indicates whether the call is asynchronous. 
	 *                     The default is True (the call returns immediately). 
	 *                     If set to True, attach an onreadystatechange 
	 *                     property callback so that you can tell when the 
	 *                     send call has completed. 
	 * @param bstrUser     The name of the user for authentication. If this 
	 *                     parameter is Null ("") or missing and the site 
	 *                     requires authentication, the component displays a 
	 *                     logon window. 
	 * @param bstrPassword The password for authentication. This parameter is
	 *                     ignored if the user parameter is Null ("") or 
	 *                     missing. 
	 */ 
	@OleMethod void open(String bstrMethod
			, String bstrUrl
			, boolean varAsync
			, String bstrUser
			, String bstrPassword); 
	
	/**
	 * Initializes an MSXML2.XMLHTTP request, and specifies the method, URL, 
	 * and authentication information for the request.
	 * 
	 * @param bstrMethod   The HTTP method used to open the connection, such 
	 *                     as GET, POST, PUT, or PROPFIND. 
	 *                     For IXMLHTTPRequest, this parameter is not 
	 *                     case-sensitive. 
	 * @param bstrUrl      The requested URL. This can be either an absolute 
	 *                     URL, such as "http://Myserver/Mypath/Myfile.asp", 
	 *                     or a relative URL, such as "../MyPath/MyFile.asp". 
	 * @param varAsync     Indicates whether the call is asynchronous. 
	 *                     The default is True (the call returns immediately). 
	 *                     If set to True, attach an onreadystatechange 
	 *                     property callback so that you can tell when the 
	 *                     send call has completed. 
	 * @param bstrUser     The name of the user for authentication. If this 
	 *                     parameter is Null ("") or missing and the site 
	 *                     requires authentication, the component displays a 
	 *                     logon window. 
	 */
	@OleMethod void open(String bstrMethod
			, String bstrUrl
			, boolean varAsync
			, String bstrUser); 
	
	/**
	 * Initializes an MSXML2.XMLHTTP request, and specifies the method, URL, 
	 * and authentication information for the request.
	 * 
	 * @param bstrMethod   The HTTP method used to open the connection, such 
	 *                     as GET, POST, PUT, or PROPFIND. 
	 *                     For IXMLHTTPRequest, this parameter is not 
	 *                     case-sensitive. 
	 * @param bstrUrl      The requested URL. This can be either an absolute 
	 *                     URL, such as "http://Myserver/Mypath/Myfile.asp", 
	 *                     or a relative URL, such as "../MyPath/MyFile.asp". 
	 * @param varAsync     Indicates whether the call is asynchronous. 
	 *                     The default is True (the call returns immediately). 
	 *                     If set to True, attach an onreadystatechange 
	 *                     property callback so that you can tell when the 
	 *                     send call has completed. 
	 */
	@OleMethod void open(String bstrMethod
			, String bstrUrl
			, boolean varAsync); 
	
	/**
	 * Initializes an MSXML2.XMLHTTP request, and specifies the method, URL, 
	 * and authentication information for the request.
	 * 
	 * @param bstrMethod   The HTTP method used to open the connection, such 
	 *                     as GET, POST, PUT, or PROPFIND. 
	 *                     For IXMLHTTPRequest, this parameter is not 
	 *                     case-sensitive. 
	 * @param bstrUrl      The requested URL. This can be either an absolute 
	 *                     URL, such as "http://Myserver/Mypath/Myfile.asp", 
	 *                     or a relative URL, such as "../MyPath/MyFile.asp". 
	 */
	@OleMethod void open(String bstrMethod
			, String bstrUrl); 
	 
	/**
	 * Sends an HTTP request to the server and receives a response.
	 * 
	 * @param body The body of the message being sent with the request.
	 */
	@OleMethod void send(String body); 
	
	/**
	 * Sends an HTTP request to the server and receives a response.
	 */
	@OleMethod void send(); 
	 
	/**
	 * Specifies the name of an HTTP header.
	 * 
	 * @param bstrHeader A header name to set; for example, "depth". 
	 *                   This parameter should not contain a colon and 
	 *                   should be the actual text of the HTTP header. 
	 * @param bstrValue  The value of the header; for example, "infinity". 
	 */ 
	@OleMethod void setRequestHeader(String bstrHeader, String bstrValue); 
}
