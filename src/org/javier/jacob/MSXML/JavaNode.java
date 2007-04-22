package org.javier.jacob.MSXML;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.UserDataHandler;

public class JavaNode implements Node {
	protected IXMLDOMNode node;

	public JavaNode(IXMLDOMNode node) {
		this.node = node;
	}

	public Node appendChild(Node newChild) throws DOMException {
		throw new DOMException(DOMException.NOT_SUPPORTED_ERR,"This method isn't implemmented");
	}

	public Node cloneNode(boolean deep) {
		IXMLDOMNode result = node.cloneNode(deep);

		if(result == null) {
			return null;
		}
		
		return new JavaNode(result);
	}

	public short compareDocumentPosition(Node other) throws DOMException {
		throw new DOMException(DOMException.NOT_SUPPORTED_ERR,"This method isn't implemmented");
	}

	public NamedNodeMap getAttributes() {
		IXMLDOMNamedNodeMap result = node.getAttributes();
		
		if(result == null) {
			return null;
		}

		return new JavaNamedNodeMap(result);
	}

	public String getBaseURI() {
		throw new DOMException(DOMException.NOT_SUPPORTED_ERR,"This method isn't implemmented");
	}

	public NodeList getChildNodes() {
		IXMLDOMNodeList result = node.getChildNodes();
		
		if(result == null) {
			return null;
		}

		return new JavaNodeList(result);
	}

	public Object getFeature(String feature, String version) {
		throw new DOMException(DOMException.NOT_SUPPORTED_ERR,"This method isn't implemmented");
	}

	public Node getFirstChild() {
		IXMLDOMNode result = node.getFirstChild();
		
		if(result == null) {
			return null;
		}

		return new JavaNode(result);
	}

	public Node getLastChild() {
		IXMLDOMNode result = node.getLastChild();
		
		if(result == null) {
			return null;
		}

		return new JavaNode(result);
	}

	public String getLocalName() {
		throw new DOMException(DOMException.NOT_SUPPORTED_ERR,"This method isn't implemmented");
	}

	public String getNamespaceURI() {
		throw new DOMException(DOMException.NOT_SUPPORTED_ERR,"This method isn't implemmented");
	}

	public Node getNextSibling() {
		IXMLDOMNode result = node.getNextSibling();
		
		if(result == null) {
			return null;
		}

		return new JavaNode(result);
	}

	public String getNodeName() {
		return node.getNodeName();
	}

	public short getNodeType() {
		return node.getNodeType();
	}

	public String getNodeValue() throws DOMException {
		return node.getNodeValue();
	}

	public Document getOwnerDocument() {
		DOMDocument result = node.getOwnerDocument();
		
		if(result == null) {
			return null;
		}

		return new JavaDocument(result);
	}

	public Node getParentNode() {
		IXMLDOMNode result = node.getParentNode();
		
		if(result == null) {
			return null;
		}

		return new JavaNode(result);
	}

	public String getPrefix() {
		throw new DOMException(DOMException.NOT_SUPPORTED_ERR,"This method isn't implemmented");
	}

	public Node getPreviousSibling() {
		IXMLDOMNode result = node.getPreviousSibling();
		
		if(result == null) {
			return null;
		}

		return new JavaNode(result);
	}

	public String getTextContent() throws DOMException {
		throw new DOMException(DOMException.NOT_SUPPORTED_ERR,"This method isn't implemmented");
	}

	public Object getUserData(String key) {
		throw new DOMException(DOMException.NOT_SUPPORTED_ERR,"This method isn't implemmented");
	}

	public boolean hasAttributes() {
		return (node.getAttributes().getLength() > 0);
	}

	public boolean hasChildNodes() {
		return node.hasChildNodes();
	}

	public Node insertBefore(Node newChild, Node refChild) throws DOMException {
		throw new DOMException(DOMException.NOT_SUPPORTED_ERR,"This method isn't implemmented");
	}

	public boolean isDefaultNamespace(String namespaceURI) {
		throw new DOMException(DOMException.NOT_SUPPORTED_ERR,"This method isn't implemmented");
	}

	public boolean isEqualNode(Node arg) {
		throw new DOMException(DOMException.NOT_SUPPORTED_ERR,"This method isn't implemmented");
	}

	public boolean isSameNode(Node other) {
		throw new DOMException(DOMException.NOT_SUPPORTED_ERR,"This method isn't implemmented");
	}

	public boolean isSupported(String feature, String version) {
		throw new DOMException(DOMException.NOT_SUPPORTED_ERR,"This method isn't implemmented");
	}

	public String lookupNamespaceURI(String prefix) {
		throw new DOMException(DOMException.NOT_SUPPORTED_ERR,"This method isn't implemmented");
	}

	public String lookupPrefix(String namespaceURI) {
		throw new DOMException(DOMException.NOT_SUPPORTED_ERR,"This method isn't implemmented");
	}

	public void normalize() {
		throw new DOMException(DOMException.NOT_SUPPORTED_ERR,"This method isn't implemmented");
	}

	public Node removeChild(Node oldChild) throws DOMException {
		throw new DOMException(DOMException.NOT_SUPPORTED_ERR,"This method isn't implemmented");
	}

	public Node replaceChild(Node newChild, Node oldChild) throws DOMException {
		throw new DOMException(DOMException.NOT_SUPPORTED_ERR,"This method isn't implemmented");
	}

	public void setNodeValue(String nodeValue) throws DOMException {
		throw new DOMException(DOMException.NOT_SUPPORTED_ERR,"This method isn't implemmented");
	}

	public void setPrefix(String prefix) throws DOMException {
		throw new DOMException(DOMException.NOT_SUPPORTED_ERR,"This method isn't implemmented");
	}

	public void setTextContent(String textContent) throws DOMException {
		throw new DOMException(DOMException.NOT_SUPPORTED_ERR,"This method isn't implemmented");
	}

	public Object setUserData(String key, Object data, UserDataHandler handler) {
		throw new DOMException(DOMException.NOT_SUPPORTED_ERR,"This method isn't implemmented");
	}

}
