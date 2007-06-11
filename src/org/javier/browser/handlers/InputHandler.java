/**
 * File:        InputHandler.java
 * Description: The Input Handler Interface
 * Author:      Edgar Medrano Pérez
 *              edgarmedrano at gmail dot com
 * Created:     2007.04.15
 * Company:     JAVIER project
 *              http://javier.sourceforge.net
 * Notes:        
 */
package org.javier.browser.handlers;

import java.io.IOException;

public interface InputHandler {

	String getInput(String text, String value, String type, String slot, boolean modal) 
	   throws IOException;

}
