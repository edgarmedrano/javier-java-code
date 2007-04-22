package org.javier.jacob.MSXML;

import org.javier.jacob.Dispatchable;
import org.javier.jacob.OleInterface;
import org.javier.jacob.OleMethod;
import org.javier.jacob.OleProperty;

@OleInterface(name = "Msxml2.IXMLDOMNode")
public interface IXMLDOMNode extends Dispatchable {

	/**
	 * Contains the list of attributes for this node. Read-only.
	 * 
	 * @return
	 */
	@OleProperty
	IXMLDOMNamedNodeMap getAttributes();

	/**
	 * Contains a node list containing the children nodes. Read-only.
	 * 
	 * @return
	 */
	@OleProperty
	IXMLDOMNodeList getChildNodes();

	
	/**
	 * Contains the first child of this node. Read-only.
	 * 
	 * @return
	 */
	@OleProperty
	IXMLDOMNode getFirstChild(); 
	 
	/**
	 * Returns the last child node. Read-only.
	 * 
	 * @return
	 */
	@OleProperty
	IXMLDOMNode getLastChild(); 
	 
	/**
	 * Contains the next sibling of this node in the parent's child list. Read-only.
	 * 
	 * @return
	 */
	@OleProperty
	IXMLDOMNode getNextSibling(); 

	/**
	 * Returns the qualified name for attribute, document type, element, entity,
	 * or notation nodes. Returns a fixed string for all other node types.
	 * 
	 * @return
	 */
	@OleProperty
	String getNodeName();

	/**
	 * Specifies the XML Document Object Model (DOM) node type, which determines
	 * valid values and whether the node can have child nodes. Read-only.
	 * 
	 * @return
	 */
	@OleProperty
	short getNodeType();

	/**
	 * Contains the text associated with the node. Read/write.
	 * 
	 * @return
	 */
	@OleProperty
	String getNodeValue();
	
	/**
	 * Returns the root of the document that contains the node. Read-only.
	 * 
	 * @return
	 */
	@OleProperty
	DOMDocument getOwnerDocument(); 
	 
	/**
	 * Contains the parent node. Read-only.
	 * 
	 * @return
	 */
	@OleProperty
	IXMLDOMNode getParentNode(); 
	 
	/**
	 * Contains the previous sibling of the node in the parent's child list. Read-only.
	 * 
	 * @return
	 */
	@OleProperty
	IXMLDOMNode getPreviousSibling(); 
	
	/**
	 * Appends a new child as the last child of the node. 
	 *
	 */
	@OleMethod
	IXMLDOMNode appendChild(IXMLDOMNode newChild); 
	 
	 
	/**
	 * Clones a new node. 
	 *
	 */
	@OleMethod
	IXMLDOMNode cloneNode(boolean deep); 
	 
	/**
	 * Provides a fast way to determine whether a node has children. 
	 */
	@OleMethod 
	boolean hasChildNodes(); 
	 
	 
	/**
	 * Inserts a child node to the left of the specified node or at the end of the list. 
	 *
	 */
	@OleMethod
	IXMLDOMNode insertBefore(IXMLDOMNode newNode,IXMLDOMNode refChild); 
	 
	/**
	 * Removes the specified child node from the list of children and returns it. 
	 *
	 */
	@OleMethod
	IXMLDOMNode removeChild(IXMLDOMNode childNode); 
	 //
	 
	/**
	 * Replaces the specified old child node with the supplied new child node. 
	 *
	 */
	@OleMethod
	IXMLDOMNode replaceChild(IXMLDOMNode newChild, IXMLDOMNode oldChild); 
}
