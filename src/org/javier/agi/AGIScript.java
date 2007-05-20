/**
 * File:        AGIScript.java
 * Description: Executes Asterisk Gateway Interface scripts 
 * Author:      Edgar Medrano Pérez
 *              edgarmedrano at gmail dot com
 * Created:     2007.05.19
 * Company:     JAVIER project
 *              http://javier.sourceforge.net
 * Notes:        
 */
package org.javier.agi;

/**
 * The Asterisk Gateway Interface script interface.
 */
public interface AGIScript {
	
	/**
	 * Execute the script.
	 * 
	 * @param agi the agi connection to send commands to Asterisk
	 */
	public void execute(AGIConnection agi);
}
