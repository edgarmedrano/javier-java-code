/**
 * File:        JavaNamedNodeMap.java
 * Description: IXMLDOMNamedNodeMap wrapper implementing 
 *              org.w3c.dom.NamedNodeMap interface 
 * Author:      Edgar Medrano Pérez
 *              edgarmedrano at gmail dot com
 * Created:     2007.04.22
 * Company:     JAVIER project
 *              http://javier.sourceforge.net
 * Notes:        
 */
package org.javier.jacob.MSXML;

import org.w3c.dom.DOMException;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * IXMLDOMNamedNodeMap wrapper implementing {@link NamedNodeMap} interface
 */
public class JavaNamedNodeMap 
	implements NamedNodeMap {

	/** The wrapped attributes. */
	private IXMLDOMNamedNodeMap attributes;

	/**
	 * Constructs the specified attributes wrapper.
	 * 
	 * @param attributes the attributes to be wrapped
	 */
	public JavaNamedNodeMap(IXMLDOMNamedNodeMap attributes) {
		this.attributes = attributes;
	}

	/* (non-Javadoc)
	 * @see org.w3c.dom.NamedNodeMap#getLength()
	 */
	public int getLength() {
		return (int) attributes.getLength();
	}

	/* (non-Javadoc)
	 * @see org.w3c.dom.NamedNodeMap#getNamedItem(java.lang.String)
	 */
	public Node getNamedItem(String name) {
		IXMLDOMNode result = attributes.getNamedItem(name);
		
		if(result == null) {
			return null;
		}

		return new JavaNode(result);
	}

	/* (non-Javadoc)
	 * @see org.w3c.dom.NamedNodeMap#getNamedItemNS(java.lang.String, java.lang.String)
	 */
	public Node getNamedItemNS(String namespaceURI, String localName)
			throws DOMException {
		IXMLDOMNode result = attributes.getQualifiedItem(localName, namespaceURI);
		
		if(result == null) {
			return null;
		}

		return new JavaNode(result);
	}

	/* (non-Javadoc)
	 * @see org.w3c.dom.NamedNodeMap#item(int)
	 */
	public Node item(int index) {
		IXMLDOMNode result = attributes.item(index);
		
		if(result == null) {
			return null;
		}

		return new JavaNode(result);
	}

	/* (non-Javadoc)
	 * @see org.w3c.dom.NamedNodeMap#removeNamedItem(java.lang.String)
	 */
	public Node removeNamedItem(String name) throws DOMException {
		IXMLDOMNode result = attributes.removeNamedItem(name);
		
		if(result == null) {
			return null;
		}

		return new JavaNode(result);
	}

	/* (non-Javadoc)
	 * @see org.w3c.dom.NamedNodeMap#removeNamedItemNS(java.lang.String, java.lang.String)
	 */
	public Node removeNamedItemNS(String namespaceURI, String localName)
			throws DOMException {
		return new JavaNode(attributes.removeQualifiedItem(localName, namespaceURI));
	}

	/* (non-Javadoc)
	 * @see org.w3c.dom.NamedNodeMap#setNamedItem(org.w3c.dom.Node)
	 */
	public Node setNamedItem(Node arg) throws DOMException {
		throw new DOMException(DOMException.NOT_SUPPORTED_ERR,"This method isn't implemmented");
	}

	/* (non-Javadoc)
	 * @see org.w3c.dom.NamedNodeMap#setNamedItemNS(org.w3c.dom.Node)
	 */
	public Node setNamedItemNS(Node arg) throws DOMException {
		throw new DOMException(DOMException.NOT_SUPPORTED_ERR,"This method isn't implemmented");
	}

}
