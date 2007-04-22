package org.javier.jacob.MSXML;

import org.javier.jacob.Dispatchable;
import org.javier.jacob.OleInterface;
import org.javier.jacob.OleProperty;
import org.javier.jacob.OleMethod;

@OleInterface(name="Msxml2.IXMLDOMNamedNodeMap")
public interface IXMLDOMNamedNodeMap extends Dispatchable {
	/**
	 * Indicates the number of items in the collection. Read-only.
	 * @return
	 */
	@OleProperty long getLength();
	
	/**
	 * Retrieves the attribute with the specified name.
	 */
	@OleMethod IXMLDOMNode getNamedItem(String name);
	
	/**
	 * Returns the attribute with the specified namespace and attribute name.
	 */
	@OleMethod IXMLDOMNode getQualifiedItem(String baseName, String namespaceURI);
	
	/**
	 * Allows random access to individual nodes within the collection. Read-only.
	 */ 
	@OleMethod IXMLDOMNode item(long index); 

	/**
	 * Removes an attribute from the collection. 
	 */
	@OleMethod IXMLDOMNode removeNamedItem(String name); 
	 
	/**
	 * Removes the attribute with the specified namespace and attribute name. 
	 */
	@OleMethod IXMLDOMNode removeQualifiedItem(String baseName, String namespaceURI); 
	 
	/**
	 * Adds the supplied node to the collection. 
	 */
	@OleMethod IXMLDOMNode setNamedItem(IXMLDOMNode newItem); 
}
