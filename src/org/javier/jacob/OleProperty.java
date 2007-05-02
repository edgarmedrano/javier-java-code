/**
 * File:        OleProperty.java
 * Description: OleProperty annotation.
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
 * Annotates ActiveX's getter and/or setter methods to link them to 
 * OleAutomation proxy's getter and/or setter implementations.
 * @see OleAutomation 
 * @author Maikon
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface OleProperty {
	
	/**
	 * The property's name.
	 * 
	 * @return the string
	 */
	public String name() default "";
	
	/**
	 * Indicates if the annotated method is a setter.
	 * 
	 * @return <code>true</code>, if it's setter. 
	 *         <br>Default value is <code>false</code>.
	 */
	public boolean set() default false;
	
	/**
	 * Indicates if the annotated method is a getter.
	 * 
	 * @return <code>true</code>, if it's getter
	 *         <br>Default value is <code>false</code>.
	 */
	public boolean get() default false;
}
