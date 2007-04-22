package org.javier.jacob.MSXML;

import org.w3c.dom.DOMException;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class JavaNamedNodeMap implements NamedNodeMap {

	private IXMLDOMNamedNodeMap attributes;

	public JavaNamedNodeMap(IXMLDOMNamedNodeMap attributes) {
		this.attributes = attributes;
	}

	public int getLength() {
		return (int) attributes.getLength();
	}

	public Node getNamedItem(String name) {
		IXMLDOMNode result = attributes.getNamedItem(name);
		
		if(result == null) {
			return null;
		}

		return new JavaNode(result);
	}

	public Node getNamedItemNS(String namespaceURI, String localName)
			throws DOMException {
		IXMLDOMNode result = attributes.getQualifiedItem(localName, namespaceURI);
		
		if(result == null) {
			return null;
		}

		return new JavaNode(result);
	}

	public Node item(int index) {
		IXMLDOMNode result = attributes.item(index);
		
		if(result == null) {
			return null;
		}

		return new JavaNode(result);
	}

	public Node removeNamedItem(String name) throws DOMException {
		IXMLDOMNode result = attributes.removeNamedItem(name);
		
		if(result == null) {
			return null;
		}

		return new JavaNode(result);
	}

	public Node removeNamedItemNS(String namespaceURI, String localName)
			throws DOMException {
		return new JavaNode(attributes.removeQualifiedItem(localName, namespaceURI));
	}

	public Node setNamedItem(Node arg) throws DOMException {
		throw new DOMException(DOMException.NOT_SUPPORTED_ERR,"This method isn't implemmented");
	}

	public Node setNamedItemNS(Node arg) throws DOMException {
		throw new DOMException(DOMException.NOT_SUPPORTED_ERR,"This method isn't implemmented");
	}

}
