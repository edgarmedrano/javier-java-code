/**
 * File:        JavaNodeList.java
 * Description: IXMLDOMNodeList wrapper implementing 
 *              org.w3c.dom.NodeList interface 
 * Author:      Edgar Medrano Pérez
 *              edgarmedrano at gmail dot com
 * Created:     2007.04.22
 * Company:     JAVIER project
 *              http://javier.sourceforge.net
 * Notes:        
 */
package org.javier.jacob.MSXML;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * IXMLDOMNodeList wrapper implementing {@link NodeList} interface
 */
public class JavaNodeList implements NodeList {

	/** The wrapped node list. */
	private IXMLDOMNodeList nodeList;

	/**
	 * Constructs the specified node list wrapper.
	 * 
	 * @param nodeList the node list to be wrapped
	 */
	public JavaNodeList(IXMLDOMNodeList nodeList) {
		this.nodeList = nodeList;
	}

	/* (non-Javadoc)
	 * @see org.w3c.dom.NodeList#getLength()
	 */
	public int getLength() {
		return (int) nodeList.getLength();
	}

	/* (non-Javadoc)
	 * @see org.w3c.dom.NodeList#item(int)
	 */
	public Node item(int index) {
		return new JavaNode(nodeList.item(index));
	}

}
