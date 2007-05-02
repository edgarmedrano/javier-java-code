/**
 * File:        Dispatchable.java
 * Description: Defines OleAutomation's dispatch getter
 * Author:      Edgar Medrano Pérez 
 *              edgarmedrano at gmail dot com
 * Created:     2007.04.17
 * Company:     JAVIER project
 *              http://javier.sourceforge.net
 * Notes:       
 */
package org.javier.jacob;

import com.jacob.com.Dispatch;
/**
 * OleAutomation needs this interface to be able to get the {@link Dispatch}
 * of a Proxy created by OleAutomation to use it while setting a property
 * or passing it as a parameter
 *   
 * @author Edgar Medrano Pérez
 * @see OleAutomation#invokeProxyGet(Class, String, Object[])
 * @see OleAutomation#invokeProxySet(Class, String, Object[])
 * @see OleAutomation#invokeProxyMethod(Class, String, Object[])
 */
public interface Dispatchable {
	@OleProperty Dispatch _GET_JACOB_DISPATCH_();
}
