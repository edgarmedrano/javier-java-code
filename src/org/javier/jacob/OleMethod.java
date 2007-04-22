/*
 * Created on 06/12/2005
 *
 * File OleMethod.java
 * author: Maikon
 */
package org.javier.jacob;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface OleMethod {
	public String name() default "";	
}
