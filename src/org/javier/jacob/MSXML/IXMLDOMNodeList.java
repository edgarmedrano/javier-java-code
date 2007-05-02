/**
 * File:        IXMLDOMNodeList.java
 * Description: IXMLDOMNodeList from MSXML.dll 
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
import org.javier.jacob.OleMethod;
import org.javier.jacob.OleProperty;

/**
 * Enables iteration and indexed access operations on the live collection 
 * of IXMLDOMNode.
 */
@OleInterface(name="Msxml2.IXMLDOMNodeList")
public interface IXMLDOMNodeList extends Dispatchable {
	
	/**
	 * Indicates the number of items in the collection. Read-only.
	 * 
	 * @return the length
	 */
	@OleProperty long getLength();
	
	/**
	 * Allows random access to individual nodes within the collection.
	 * Read-only.
	 * 
	 * @param index the item within the collection. The first item is zero.
	 * 
	 * @return the node. <br>
	 *         <code>null</code> if the index is out of range.
	 */ 
	@OleMethod IXMLDOMNode item(long index); 
}
