/**
 * File:        JavaDocument.java
 * Description: DOMDocument wrapper implementing 
 *              org.w3c.dom.Document interface
 * Author:      Edgar Medrano Pérez
 *              edgarmedrano at gmail dot com
 * Created:     2007.04.22
 * Company:     JAVIER project
 *              http://javier.sourceforge.net
 * Notes:        
 */
package org.javier.jacob.MSXML;

import org.w3c.dom.Attr;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Comment;
import org.w3c.dom.DOMConfiguration;
import org.w3c.dom.DOMException;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.EntityReference;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;

/**
 * DOMDocument wrapper implementing {@link Document} interface.
 * <p><b>WARNING!</b><br>
 * No method is implemented, this class was created to provide a default
 * implementation for {@link JavaNode#getOwnerDocument()} method.
 * </p>
 * @see JavaNode#getOwnerDocument()
 */
public class JavaDocument 
	extends JavaNode 
	implements Document {

	public JavaDocument(DOMDocument node) {
		super(node);
	}

	public Node adoptNode(Node source) throws DOMException {
		// TODO Auto-generated method stub
		return null;
	}

	public Attr createAttribute(String name) throws DOMException {
		// TODO Auto-generated method stub
		return null;
	}

	public Attr createAttributeNS(String namespaceURI, String qualifiedName) throws DOMException {
		// TODO Auto-generated method stub
		return null;
	}

	public CDATASection createCDATASection(String data) throws DOMException {
		// TODO Auto-generated method stub
		return null;
	}

	public Comment createComment(String data) {
		// TODO Auto-generated method stub
		return null;
	}

	public DocumentFragment createDocumentFragment() {
		// TODO Auto-generated method stub
		return null;
	}

	public Element createElement(String tagName) throws DOMException {
		// TODO Auto-generated method stub
		return null;
	}

	public Element createElementNS(String namespaceURI, String qualifiedName) throws DOMException {
		// TODO Auto-generated method stub
		return null;
	}

	public EntityReference createEntityReference(String name) throws DOMException {
		// TODO Auto-generated method stub
		return null;
	}

	public ProcessingInstruction createProcessingInstruction(String target, String data) throws DOMException {
		// TODO Auto-generated method stub
		return null;
	}

	public Text createTextNode(String data) {
		// TODO Auto-generated method stub
		return null;
	}

	public DocumentType getDoctype() {
		// TODO Auto-generated method stub
		return null;
	}

	public Element getDocumentElement() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getDocumentURI() {
		// TODO Auto-generated method stub
		return null;
	}

	public DOMConfiguration getDomConfig() {
		// TODO Auto-generated method stub
		return null;
	}

	public Element getElementById(String elementId) {
		// TODO Auto-generated method stub
		return null;
	}

	public NodeList getElementsByTagName(String tagname) {
		// TODO Auto-generated method stub
		return null;
	}

	public NodeList getElementsByTagNameNS(String namespaceURI, String localName) {
		// TODO Auto-generated method stub
		return null;
	}

	public DOMImplementation getImplementation() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getInputEncoding() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean getStrictErrorChecking() {
		// TODO Auto-generated method stub
		return false;
	}

	public String getXmlEncoding() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean getXmlStandalone() {
		// TODO Auto-generated method stub
		return false;
	}

	public String getXmlVersion() {
		// TODO Auto-generated method stub
		return null;
	}

	public Node importNode(Node importedNode, boolean deep) throws DOMException {
		// TODO Auto-generated method stub
		return null;
	}

	public void normalizeDocument() {
		// TODO Auto-generated method stub
		
	}

	public Node renameNode(Node n, String namespaceURI, String qualifiedName) throws DOMException {
		// TODO Auto-generated method stub
		return null;
	}

	public void setDocumentURI(String documentURI) {
		// TODO Auto-generated method stub
		
	}

	public void setStrictErrorChecking(boolean strictErrorChecking) {
		// TODO Auto-generated method stub
		
	}

	public void setXmlStandalone(boolean xmlStandalone) throws DOMException {
		// TODO Auto-generated method stub
		
	}

	public void setXmlVersion(String xmlVersion) throws DOMException {
		// TODO Auto-generated method stub
		
	}
}
