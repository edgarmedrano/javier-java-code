/*
 * Created on 06/12/2005
 *
 * File JacobProxyFactory.java
 * author: Maikon
 */
package proxies;

import java.lang.reflect.Proxy;

import com.jacob.com.Dispatch;

public class OleAutomationFactory {
	
	public static Object getActiveXComponet(Class<?> clazz){		
		return Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz}, new OleInvocationHandler(clazz) );
	}
	
	public static Object getActiveXComponet(Dispatch disp,Class<?> clazz){		
		return Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz}, new OleInvocationHandler(disp,clazz) );
	}

}
