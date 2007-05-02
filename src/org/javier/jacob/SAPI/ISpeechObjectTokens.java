/**
 * File:        ISpeechObjectTokens.java
 * Description: ISpeechObjectTokens from MS SAPI
 * Author:      Edgar Medrano Pérez
 *              edgarmedrano at gmail dot com
 * Created:     2007.04.21
 * Company:     JAVIER project
 *              http://javier.sourceforge.net
 * Notes:        
 */
package org.javier.jacob.SAPI;

import org.javier.jacob.Dispatchable;
import org.javier.jacob.OleInterface;
import org.javier.jacob.OleMethod;
import org.javier.jacob.OleProperty;

/**
 * The ISpeechObjectTokens automation interface represents a collection of 
 * SpObjectToken objects.
 */
@OleInterface(name="Sapi.ISpeechObjectTokens")
public interface ISpeechObjectTokens extends Dispatchable {
	
	/**
	 * The number of objects in the collection.
	 * 
	 * @return the number of objects in the collection.
	 */
	@OleProperty long Count();
	
	/**
	 * The Item method returns a member of the ISpeechObjectTokens 
	 * collection by its index. 
	 * 
	 * @param index Specifies the Index.
	 * 
	 * @return The Item method returns an {@link SpObjectToken} variable.
	 */
	@OleMethod SpObjectToken Item(long index);
}
