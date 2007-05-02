/**
 * File:        OleInterface.java
 * Description: OleInterface annotation.
 * Author:      Maikon
 * Created:     2005.06.12
 * Company:     OleAutomation project
 *              http://sourceforge.net/projects/oleautomation/
 * Notes:        
 */
package org.javier.jacob;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotates an interface to link them to OleAutomation proxies.
 * @see OleAutomation 
 * @author Maikon
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface OleInterface {
	
	/**
	 * Class name, using the syntax library.object where library is the name 
	 * of the application (e.g., Word, Excel) or library containing the 
	 * object, and object is the type or class of the object to create.
	 * <p>Examples: Word.Application, Msxml2.DOMDocument, Sapi.SpVoice</p>
	 * 
	 * @return the class name
	 */
	public String name() default "";
    
    /**
	 * Event handler.
	 * 
	 * @return the event handler
	 */
    public Class<?> eventHandler() default Object.class;
}
