package org.javier.jacob.MSXML;

import org.javier.jacob.Dispatchable;
import org.javier.jacob.OleInterface;
import org.javier.jacob.OleMethod;
import org.javier.jacob.OleProperty;

@OleInterface(name="Msxml2.IXMLDOMNodeList")
public interface IXMLDOMNodeList extends Dispatchable {
	/**
	 * Indicates the number of items in the collection. Read-only.
	 * @return
	 */
	@OleProperty long getLength();
	
	/**
	 * Allows random access to individual nodes within the collection. Read-only.
	 */ 
	@OleMethod IXMLDOMNode item(long index); 
}
