/**
 * File:        IXMLDOMNamedNodeMap.java
 * Description: IXMLDOMNamedNodeMap from MSXML.dll 
 * Author:      Edgar Medrano Pérez
 *              edgarmedrano at gmail dot com
 * Created:     2007.04.22
 * Company:     JAVIER project
 *              http://javier.sourceforge.net
 * Notes:        
 */
package org.javier.jacob.MSXML;

import org.javier.jacob.Dispatchable;
import org.javier.jacob.OleInterface;
import org.javier.jacob.OleProperty;
import org.javier.jacob.OleMethod;

/**
 * Enables iteration and access, by name, to the collection of attributes. 
 * IXMLDOMNamedNodeMapincludes support for namespaces.
 */
@OleInterface(name="Msxml2.IXMLDOMNamedNodeMap")
public interface IXMLDOMNamedNodeMap extends Dispatchable {
	
	/**
	 * Indicates the number of items in the collection. Read-only.
	 * 
	 * @return the length
	 */
	@OleProperty long getLength();
	
	/**
	 * Retrieves the attribute with the specified name.
	 * 
	 * @param name the name of the attribute.
	 * 
	 * @return An object. Returns an IXMLDOMNode object for the specified attribute. 
	 *         Returns Nothing if the attribute node is not in this collection.
	 */
	@OleMethod IXMLDOMNode getNamedItem(String name);
	
	/**
	 * Returns the attribute with the specified namespace and attribute name.
	 * 
	 * @param baseName     The string specifying the base name of the 
	 *                     attribute, without namespace qualification.
	 * @param namespaceURI The string specifying the namespace prefix that 
	 *                     qualifies the attribute name.
	 *                      
	 * @return the qualified item
	 */
	@OleMethod IXMLDOMNode getQualifiedItem(String baseName, String namespaceURI);
	
	/**
	 * Allows random access to individual nodes within the collection.
	 * Read-only.
	 * 
	 * @param index An index of the item within the collection. 
	 *              The first item is zero. 
	 * 
	 * @return the node. <br>
	 *         <code>null</code> if the index is out of range.
	 */ 
	@OleMethod IXMLDOMNode item(long index); 

	/**
	 * Removes an attribute from the collection.
	 * 
	 * @param name The string specifying the name of the attribute to remove 
	 *             from the collection. 
	 * 
	 * @return the node removed from the collection. <br>
	 *         <code>null</code> if the named node is not an attribute.

	 */
	@OleMethod IXMLDOMNode removeNamedItem(String name); 
	 
	/**
	 * Removes the attribute with the specified namespace and attribute name.
	 * 
	 * @param baseName     The string specifying the base name of the 
	 *                     attribute, without namespace qualification.
	 * @param namespaceURI The string specifying the namespace prefix that 
	 *                     qualifies the attribute name. 
	 * 
	 * @return the attribute node removed, or Null if no node was removed.
	 */
	@OleMethod IXMLDOMNode removeQualifiedItem(String baseName, String namespaceURI); 
	 
	/**
	 * Adds the supplied node to the collection.
	 * 
	 * @param newItem The object containing the attribute to be added to 
	 *                the collection.
	 * 
	 * @return the attribute successfully added to the collection.
	 */
	@OleMethod IXMLDOMNode setNamedItem(IXMLDOMNode newItem); 
}
