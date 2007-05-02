/**
 * File:        OleMethod.java
 * Description: OleMethod annotation
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
 * Annotates ActiveX's methods to link them to OleAutomation
 * proxy's method implementations.
 * @see OleAutomation 
 * @author Maikon
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface OleMethod {
	
	/**
	 * The method's name.
	 * 
	 * @return the method's name
	 */
	public String name() default "";	
}
