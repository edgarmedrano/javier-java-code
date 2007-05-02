/**
 * File:        JavaNode.java
 * Description: IXMLDOMNode wrapper implementing 
 *              org.w3c.dom.Node interface 
 * Author:      Edgar Medrano Pérez
 *              edgarmedrano at gmail dot com
 * Created:     2007.04.22
 * Company:     JAVIER project
 *              http://javier.sourceforge.net
 * Notes:        
 */
package org.javier.jacob.MSXML;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.UserDataHandler;

/**
 * IXMLDOMNode wrapper implementing {@link Node} interface
 */
public class JavaNode implements Node {
	
	/** The wrapped node. */
	protected IXMLDOMNode node;

	/**
	 * Constructs the specified node wrapper. 
	 * 
	 * @param node the node to be wrapped
	 */
	public JavaNode(IXMLDOMNode node) {
		this.node = node;
	}

	/* (non-Javadoc)
	 * @see org.w3c.dom.Node#appendChild(org.w3c.dom.Node)
	 */
	public Node appendChild(Node newChild) throws DOMException {
		throw new DOMException(DOMException.NOT_SUPPORTED_ERR,"This method isn't implemmented");
	}

	/* (non-Javadoc)
	 * @see org.w3c.dom.Node#cloneNode(boolean)
	 */
	public Node cloneNode(boolean deep) {
		IXMLDOMNode result = node.cloneNode(deep);

		if(result == null) {
			return null;
		}
		
		return new JavaNode(result);
	}

	/* (non-Javadoc)
	 * @see org.w3c.dom.Node#compareDocumentPosition(org.w3c.dom.Node)
	 */
	public short compareDocumentPosition(Node other) throws DOMException {
		throw new DOMException(DOMException.NOT_SUPPORTED_ERR,"This method isn't implemmented");
	}

	/* (non-Javadoc)
	 * @see org.w3c.dom.Node#getAttributes()
	 */
	public NamedNodeMap getAttributes() {
		IXMLDOMNamedNodeMap result = node.getAttributes();
		
		if(result == null) {
			return null;
		}

		return new JavaNamedNodeMap(result);
	}

	/* (non-Javadoc)
	 * @see org.w3c.dom.Node#getBaseURI()
	 */
	public String getBaseURI() {
		throw new DOMException(DOMException.NOT_SUPPORTED_ERR,"This method isn't implemmented");
	}

	/* (non-Javadoc)
	 * @see org.w3c.dom.Node#getChildNodes()
	 */
	public NodeList getChildNodes() {
		IXMLDOMNodeList result = node.getChildNodes();
		
		if(result == null) {
			return null;
		}

		return new JavaNodeList(result);
	}

	/* (non-Javadoc)
	 * @see org.w3c.dom.Node#getFeature(java.lang.String, java.lang.String)
	 */
	public Object getFeature(String feature, String version) {
		throw new DOMException(DOMException.NOT_SUPPORTED_ERR,"This method isn't implemmented");
	}

	/* (non-Javadoc)
	 * @see org.w3c.dom.Node#getFirstChild()
	 */
	public Node getFirstChild() {
		IXMLDOMNode result = node.getFirstChild();
		
		if(result == null) {
			return null;
		}

		return new JavaNode(result);
	}

	/* (non-Javadoc)
	 * @see org.w3c.dom.Node#getLastChild()
	 */
	public Node getLastChild() {
		IXMLDOMNode result = node.getLastChild();
		
		if(result == null) {
			return null;
		}

		return new JavaNode(result);
	}

	/* (non-Javadoc)
	 * @see org.w3c.dom.Node#getLocalName()
	 */
	public String getLocalName() {
		throw new DOMException(DOMException.NOT_SUPPORTED_ERR,"This method isn't implemmented");
	}

	/* (non-Javadoc)
	 * @see org.w3c.dom.Node#getNamespaceURI()
	 */
	public String getNamespaceURI() {
		throw new DOMException(DOMException.NOT_SUPPORTED_ERR,"This method isn't implemmented");
	}

	/* (non-Javadoc)
	 * @see org.w3c.dom.Node#getNextSibling()
	 */
	public Node getNextSibling() {
		IXMLDOMNode result = node.getNextSibling();
		
		if(result == null) {
			return null;
		}

		return new JavaNode(result);
	}

	/* (non-Javadoc)
	 * @see org.w3c.dom.Node#getNodeName()
	 */
	public String getNodeName() {
		return node.getNodeName();
	}

	/* (non-Javadoc)
	 * @see org.w3c.dom.Node#getNodeType()
	 */
	public short getNodeType() {
		return node.getNodeType();
	}

	/* (non-Javadoc)
	 * @see org.w3c.dom.Node#getNodeValue()
	 */
	public String getNodeValue() throws DOMException {
		return node.getNodeValue();
	}

	/* (non-Javadoc)
	 * @see org.w3c.dom.Node#getOwnerDocument()
	 */
	public Document getOwnerDocument() {
		DOMDocument result = node.getOwnerDocument();
		
		if(result == null) {
			return null;
		}

		return new JavaDocument(result);
	}

	/* (non-Javadoc)
	 * @see org.w3c.dom.Node#getParentNode()
	 */
	public Node getParentNode() {
		IXMLDOMNode result = node.getParentNode();
		
		if(result == null) {
			return null;
		}

		return new JavaNode(result);
	}

	/* (non-Javadoc)
	 * @see org.w3c.dom.Node#getPrefix()
	 */
	public String getPrefix() {
		throw new DOMException(DOMException.NOT_SUPPORTED_ERR,"This method isn't implemmented");
	}

	/* (non-Javadoc)
	 * @see org.w3c.dom.Node#getPreviousSibling()
	 */
	public Node getPreviousSibling() {
		IXMLDOMNode result = node.getPreviousSibling();
		
		if(result == null) {
			return null;
		}

		return new JavaNode(result);
	}

	/* (non-Javadoc)
	 * @see org.w3c.dom.Node#getTextContent()
	 */
	public String getTextContent() throws DOMException {
		throw new DOMException(DOMException.NOT_SUPPORTED_ERR,"This method isn't implemmented");
	}

	/* (non-Javadoc)
	 * @see org.w3c.dom.Node#getUserData(java.lang.String)
	 */
	public Object getUserData(String key) {
		throw new DOMException(DOMException.NOT_SUPPORTED_ERR,"This method isn't implemmented");
	}

	/* (non-Javadoc)
	 * @see org.w3c.dom.Node#hasAttributes()
	 */
	public boolean hasAttributes() {
		return (node.getAttributes().getLength() > 0);
	}

	/* (non-Javadoc)
	 * @see org.w3c.dom.Node#hasChildNodes()
	 */
	public boolean hasChildNodes() {
		return node.hasChildNodes();
	}

	/* (non-Javadoc)
	 * @see org.w3c.dom.Node#insertBefore(org.w3c.dom.Node, org.w3c.dom.Node)
	 */
	public Node insertBefore(Node newChild, Node refChild) throws DOMException {
		throw new DOMException(DOMException.NOT_SUPPORTED_ERR,"This method isn't implemmented");
	}

	/* (non-Javadoc)
	 * @see org.w3c.dom.Node#isDefaultNamespace(java.lang.String)
	 */
	public boolean isDefaultNamespace(String namespaceURI) {
		throw new DOMException(DOMException.NOT_SUPPORTED_ERR,"This method isn't implemmented");
	}

	/* (non-Javadoc)
	 * @see org.w3c.dom.Node#isEqualNode(org.w3c.dom.Node)
	 */
	public boolean isEqualNode(Node arg) {
		throw new DOMException(DOMException.NOT_SUPPORTED_ERR,"This method isn't implemmented");
	}

	/* (non-Javadoc)
	 * @see org.w3c.dom.Node#isSameNode(org.w3c.dom.Node)
	 */
	public boolean isSameNode(Node other) {
		throw new DOMException(DOMException.NOT_SUPPORTED_ERR,"This method isn't implemmented");
	}

	/* (non-Javadoc)
	 * @see org.w3c.dom.Node#isSupported(java.lang.String, java.lang.String)
	 */
	public boolean isSupported(String feature, String version) {
		throw new DOMException(DOMException.NOT_SUPPORTED_ERR,"This method isn't implemmented");
	}

	/* (non-Javadoc)
	 * @see org.w3c.dom.Node#lookupNamespaceURI(java.lang.String)
	 */
	public String lookupNamespaceURI(String prefix) {
		throw new DOMException(DOMException.NOT_SUPPORTED_ERR,"This method isn't implemmented");
	}

	/* (non-Javadoc)
	 * @see org.w3c.dom.Node#lookupPrefix(java.lang.String)
	 */
	public String lookupPrefix(String namespaceURI) {
		throw new DOMException(DOMException.NOT_SUPPORTED_ERR,"This method isn't implemmented");
	}

	/* (non-Javadoc)
	 * @see org.w3c.dom.Node#normalize()
	 */
	public void normalize() {
		throw new DOMException(DOMException.NOT_SUPPORTED_ERR,"This method isn't implemmented");
	}

	/* (non-Javadoc)
	 * @see org.w3c.dom.Node#removeChild(org.w3c.dom.Node)
	 */
	public Node removeChild(Node oldChild) throws DOMException {
		throw new DOMException(DOMException.NOT_SUPPORTED_ERR,"This method isn't implemmented");
	}

	/* (non-Javadoc)
	 * @see org.w3c.dom.Node#replaceChild(org.w3c.dom.Node, org.w3c.dom.Node)
	 */
	public Node replaceChild(Node newChild, Node oldChild) throws DOMException {
		throw new DOMException(DOMException.NOT_SUPPORTED_ERR,"This method isn't implemmented");
	}

	/* (non-Javadoc)
	 * @see org.w3c.dom.Node#setNodeValue(java.lang.String)
	 */
	public void setNodeValue(String nodeValue) throws DOMException {
		throw new DOMException(DOMException.NOT_SUPPORTED_ERR,"This method isn't implemmented");
	}

	/* (non-Javadoc)
	 * @see org.w3c.dom.Node#setPrefix(java.lang.String)
	 */
	public void setPrefix(String prefix) throws DOMException {
		throw new DOMException(DOMException.NOT_SUPPORTED_ERR,"This method isn't implemmented");
	}

	/* (non-Javadoc)
	 * @see org.w3c.dom.Node#setTextContent(java.lang.String)
	 */
	public void setTextContent(String textContent) throws DOMException {
		throw new DOMException(DOMException.NOT_SUPPORTED_ERR,"This method isn't implemmented");
	}

	/* (non-Javadoc)
	 * @see org.w3c.dom.Node#setUserData(java.lang.String, java.lang.Object, org.w3c.dom.UserDataHandler)
	 */
	public Object setUserData(String key, Object data, UserDataHandler handler) {
		throw new DOMException(DOMException.NOT_SUPPORTED_ERR,"This method isn't implemmented");
	}
}
