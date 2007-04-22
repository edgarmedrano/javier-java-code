package org.javier.jacob.MSXML;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class JavaNodeList implements NodeList {

	private IXMLDOMNodeList childNodes;

	public JavaNodeList(IXMLDOMNodeList childNodes) {
		this.childNodes = childNodes;
	}

	public int getLength() {
		return (int) childNodes.getLength();
	}

	public Node item(int index) {
		return new JavaNode(childNodes.item(index));
	}

}
