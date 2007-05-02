/**
 * File:        DOMDocument.java
 * Description: DOMDocument from MSXML.dll
 * Author:      Edgar Medrano Pérez
 *              edgarmedrano at gmail dot com
 * Created:     2007.04.21
 * Company:     JAVIER project
 *              http://javier.sourceforge.net
 * Notes:        
 */
package org.javier.jacob.MSXML;

import org.javier.jacob.OleInterface;


/**
 * Represents the top level of the XML source. Includes members for 
 * retrieving and creating all other XML objects.
 */
@OleInterface(name="Msxml2.DOMDocument")
public interface DOMDocument extends IXMLDOMNode {
	
}
