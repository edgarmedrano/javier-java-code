/**
 * File:        IXMLDOMNode.java
 * Description: IXMLDOMNode from MSXML.dll
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
 * Represents a single node in the document tree. IXMLDOMNode is the base 
 * interface for accessing data in the XML object model. This interface 
 * includes support for data types, namespaces, document type definitions 
 * (DTDs), and XML schemas.
 */
@OleInterface(name = "Msxml2.IXMLDOMNode")
public interface IXMLDOMNode extends Dispatchable {

	/**
	 * Contains the list of attributes for this node. Read-only.
	 * 
	 * @return the attributes
	 */
	@OleProperty
	IXMLDOMNamedNodeMap getAttributes();

	/**
	 * Contains a node list containing the children nodes. Read-only.
	 * 
	 * @return the child nodes
	 */
	@OleProperty
	IXMLDOMNodeList getChildNodes();

	
	/**
	 * Contains the first child of this node. Read-only.
	 * 
	 * @return the first child
	 */
	@OleProperty
	IXMLDOMNode getFirstChild(); 
	 
	/**
	 * Returns the last child node. Read-only.
	 * 
	 * @return the last child
	 */
	@OleProperty
	IXMLDOMNode getLastChild(); 
	 
	/**
	 * Contains the next sibling of this node in the parent's child list.
	 * Read-only.
	 * 
	 * @return the next sibling
	 */
	@OleProperty
	IXMLDOMNode getNextSibling(); 

	/**
	 * Returns the qualified name for attribute, document type, element, entity,
	 * or notation nodes. Returns a fixed string for all other node types.
	 * 
	 * @return the node name
	 */
	@OleProperty
	String getNodeName();

	/**
	 * Specifies the XML Document Object Model (DOM) node type, which determines
	 * valid values and whether the node can have child nodes. Read-only.
	 * 
	 * @return the node type
	 */
	@OleProperty
	short getNodeType();

	/**
	 * Contains the text associated with the node. Read/write.
	 * 
	 * @return the node value
	 */
	@OleProperty
	String getNodeValue();
	
	/**
	 * Returns the root of the document that contains the node. Read-only.
	 * 
	 * @return the owner document
	 */
	@OleProperty
	DOMDocument getOwnerDocument(); 
	 
	/**
	 * Contains the parent node. Read-only.
	 * 
	 * @return the parent node
	 */
	@OleProperty
	IXMLDOMNode getParentNode(); 
	 
	/**
	 * Contains the previous sibling of the node in the parent's child list.
	 * Read-only.
	 * 
	 * @return the previous sibling
	 */
	@OleProperty
	IXMLDOMNode getPreviousSibling(); 
	
	/**
	 * Appends a new child as the last child of the node.
	 * 
	 * @param newChild the new child node to be appended at the end of the 
	 *                 list of children belonging to this node. 
	 * 
	 * @return the new child node successfully appended to the list.
	 */
	@OleMethod
	IXMLDOMNode appendChild(IXMLDOMNode newChild); 
	 
	 
	/**
	 * Clones a new node.
	 * 
	 * @param deep A flag that indicates whether to recursively clone all 
	 *             nodes that are descendants of this node. If True, creates 
	 *             a clone of the complete tree below this node. If False, 
	 *             clones this node and its attributes only. 
	 * 
	 * @return the newly created clone node.
	 */
	@OleMethod
	IXMLDOMNode cloneNode(boolean deep); 
	 
	/**
	 * Provides a fast way to determine whether a node has children.
	 * 
	 * @return true, if has child nodes
	 */
	@OleMethod 
	boolean hasChildNodes(); 
	 
	 
	/**
	 * Inserts a child node to the left of the specified node or at the end of
	 * the list.
	 * 
	 * @param newChild the new node to be inserted.
	 * @param refChild the reference node; the newChild parameter is inserted 
	 *                 to the left of the refChild parameter. If Null, the 
	 *                 newChild parameter is inserted at the end of the child 
	 *                 list. 
	 * 
	 * @return the IXMLDOM node
	 */
	@OleMethod
	IXMLDOMNode insertBefore(IXMLDOMNode newChild,IXMLDOMNode refChild); 
	 
	/**
	 * Removes the specified child node from the list of children and returns
	 * it.
	 * 
	 * @param childNode the child node to be removed from the list of children 
	 *                  of this node.
	 * 
	 * @return the IXMLDOM node
	 */
	@OleMethod
	IXMLDOMNode removeChild(IXMLDOMNode childNode); 
	 
	/**
	 * Replaces the specified old child node with the supplied new child node.
	 * 
	 * @param newChild the new child that is to replace the old child. 
	 *                 If <code>null</code>, oldChild is removed without a 
	 *                 replacement
	 * @param oldChild the old child that is to be replaced by the new child.
	 * 
	 * @return the old child that is replaced.
	 */
	@OleMethod
	IXMLDOMNode replaceChild(IXMLDOMNode newChild, IXMLDOMNode oldChild); 
}
