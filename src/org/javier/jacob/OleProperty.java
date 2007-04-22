/*
 * Created on 06/12/2005
 *
 * File OleProperty.java
 * author: Maikon
 */
package org.javier.jacob;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface OleProperty {
	public String name() default "";
	public boolean set() default false;
	public boolean get() default false;
}
