/*
 * Created on 06/12/2005
 *
 * File OleInterface.java
 * author: Maikon
 */
package org.javier.jacob;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface OleInterface {
	public String name() default "";
    public Class<?> eventHandler() default Object.class;
}
