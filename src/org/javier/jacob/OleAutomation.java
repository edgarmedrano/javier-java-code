/**
 * File:        OleAutomation.java
 * Description: OLE Automation implementation with Jacob + annotations
 * Author:      Edgar Medrano Pérez 
 *              edgarmedrano at gmail dot com
 * Created:     2007.04.13
 * Company:     JAVIER project
 *              http://javier.sourceforge.net
 * Notes:       This is based on a previous work named OleAutomation 
 *              by Maikon, but this is optimized for , check 
 *              http://sourceforge.net/projects/oleautomation/
 *              for further reference
 */
package org.javier.jacob;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Date;
import java.util.Hashtable;

import com.jacob.com.Dispatch;
import com.jacob.com.DispatchEvents;
import com.jacob.com.Variant;

/**
 * @author Edgar Medrano Pérez
 *
 */
public class OleAutomation implements InvocationHandler {
	private static class MethodName {
		public Class returnClazz;
		public Method method;
		public String name;
		public MethodName(Class<?> returnClazz
				, Method method
				, String name) {
			this.returnClazz = returnClazz;
			this.method = method;
			this.name = name;
		}
	}
	
	protected static Hashtable<Class<?>,Hashtable<Method,MethodName>> htClassImpl;
	protected static Method methodImpl;
	protected static Method getImpl; 
	protected static Method setImpl;
	protected static Method proxyMethodImpl;
	protected static Method proxyGetImpl;
	protected static Method proxySetImpl;
	protected static Method getJacobDispatchImpl;
	protected static Hashtable<Class<?>,Short> htVariantType;
	static {
		htClassImpl = new Hashtable<Class<?>,Hashtable<Method,MethodName>>();
		try {
			Class<?>[] args = new Class<?>[] { Class.class, String.class, Object[].class };
			methodImpl = OleAutomation.class.getMethod("invokeMethod", args);
			getImpl = OleAutomation.class.getMethod("invokeGet", args);
			setImpl = OleAutomation.class.getMethod("invokeSet", args);
			proxyMethodImpl = OleAutomation.class.getMethod("invokeProxyMethod", args);
			proxyGetImpl = OleAutomation.class.getMethod("invokeProxyGet", args);
			proxySetImpl = OleAutomation.class.getMethod("invokeProxySet", args);
			getJacobDispatchImpl = OleAutomation.class.getMethod("invokeGetDispatch", args);
		} catch (Exception e) {
			
		}
		
		htVariantType = new Hashtable<Class<?>, Short>();
		htVariantType.put(Boolean.TYPE, Variant.VariantBoolean);
		htVariantType.put(Boolean.class, Variant.VariantBoolean);
		htVariantType.put(Character.TYPE, Variant.VariantString);
		htVariantType.put(Character.class, Variant.VariantString);
		htVariantType.put(Byte.TYPE, Variant.VariantByte);
		htVariantType.put(Byte.class, Variant.VariantByte);
		htVariantType.put(Short.TYPE, Variant.VariantShort);
		htVariantType.put(Short.class, Variant.VariantShort);
		htVariantType.put(Integer.TYPE, Variant.VariantInt);
		htVariantType.put(Integer.class, Variant.VariantInt);
		htVariantType.put(Long.TYPE, Variant.VariantInt);
		htVariantType.put(Long.class, Variant.VariantInt);
		htVariantType.put(Float.TYPE, Variant.VariantFloat);
		htVariantType.put(Float.class, Variant.VariantFloat);
		htVariantType.put(Double.TYPE, Variant.VariantDouble);
		htVariantType.put(Double.class, Variant.VariantDouble);
		htVariantType.put(String.class, Variant.VariantString);
		htVariantType.put(Date.class, Variant.VariantDate);
	}
	
	public static Object createActiveXObject(Class<?> clazz) 
		throws IllegalArgumentException {
		return Proxy.newProxyInstance(clazz.getClassLoader()
				, new Class[]{ clazz }
				, new OleAutomation(clazz));
	}
	
	public static Object createActiveXObject(Dispatch disp,Class<?> clazz)
		throws IllegalArgumentException {
		return Proxy.newProxyInstance(clazz.getClassLoader()
				, new Class[]{ clazz }
				, new OleAutomation(disp,clazz));
	}
	
	private static Hashtable<Method, MethodName> getClassImpl(Class<?> clazz) {
		Hashtable<Method, MethodName> impl = htClassImpl.get(clazz);
		
		if(impl == null) {
			impl = new Hashtable<Method,MethodName>();
			for(Method method: clazz.getMethods()) {
				if (method.isAnnotationPresent(OleProperty.class)) {
					impl.put(method,getPropertyImpl(method));
				} else if (method.isAnnotationPresent(OleMethod.class)) {
					impl.put(method,getMethodImpl(method));
				}
			}
			htClassImpl.put(clazz, impl);
		}
		return impl;
	}
	
	private static MethodName getMethodImpl(Method method) {
		Class<?> returnClazz = method.getReturnType();
		String name = method.getAnnotation(OleMethod.class).name();
		Method impl = null;
		
		if (null == name || "".equals(name)) {
			name = method.getName();
		}
		
		if (returnClazz.isAnnotationPresent(OleInterface.class)) {
			impl = proxyMethodImpl;
		} else {
			impl = methodImpl;
		}
		
		return new MethodName(returnClazz,impl, name);
	}
	
	private static MethodName getPropertyImpl(Method method) {
		String methodName = method.getName();
		String popertyName = method.getAnnotation(OleProperty.class).name();
		Class<?> args[] = method.getParameterTypes(); 
		Class<?> returnClazz = method.getReturnType();
		boolean blnSet = false;
		Method impl = null;

		if (method.getName().equals("_GET_JACOB_DISPATCH_")) {
			return new MethodName(Dispatch.class
					, getJacobDispatchImpl
					, "_GET_JACOB_DISPATCH_");
		}

		if(method.getAnnotation(OleProperty.class).set()) {
			blnSet = true;
		} else if(method.getAnnotation(OleProperty.class).get()) {
			blnSet = false;
		} else if(returnClazz == null || returnClazz == Void.TYPE
			|| returnClazz == Void.class) {
			blnSet = true;
		} else if(methodName.toLowerCase().startsWith("set") 
			&& args != null && args.length > 0) {
			blnSet = true;
		}
		
		if(popertyName == null || popertyName.equals("")) {
			if(methodName.toLowerCase().startsWith("set")
				|| methodName.toLowerCase().startsWith("get")) {			
				popertyName = methodName.substring(3);
			} else {
				popertyName = methodName;
			}
		}

		if(blnSet) {
			for(Class<?> inter:args[0].getInterfaces()) {
				if(inter == Dispatchable.class) {
					impl = proxySetImpl;
					break;
				}
			}
			
			if(impl == null) {
				impl = setImpl;
			}
		} else {
			if (returnClazz.isAnnotationPresent(OleInterface.class)) {
				impl = proxyGetImpl;
			} else {
				impl = getImpl;				
			}
		}
		
		return new MethodName(returnClazz, impl, popertyName);
	}
	private Dispatch activeX = null;
	@SuppressWarnings("unused")
	private DispatchEvents dispEvents = null;
	private Hashtable<Method, MethodName> htImpl = null;
	
	public OleAutomation(Class<?> clazz)
		throws IllegalArgumentException {
		this(new Dispatch(clazz.getAnnotation(OleInterface.class).name())
			, clazz);
	}
	
	public OleAutomation(Dispatch disp, Class<?> clazz)
		throws IllegalArgumentException {
		try {
			if(disp == null) {
				throw new IllegalArgumentException("dispatch cann't be null");
			}
			
			activeX = disp;

			Class<?> eventHandlerClass 
				= clazz.getAnnotation(OleInterface.class).eventHandler();
			if (!eventHandlerClass.equals(Object.class)) {
				dispEvents = new DispatchEvents(activeX, eventHandlerClass
						.newInstance());
			}
			
			htImpl = getClassImpl(clazz);
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	public Object invoke(Object proxy, Method method, Object[] args)
		throws Throwable {
		MethodName mn = htImpl.get(method);
		
		/*Check parameters*/
		if(args != null) { 
			// Translate Longs to Integers it's safer
			for(int i = 0; i < args.length; i++) {
				if(args[i] != null && 
					(args[i].getClass() == Long.TYPE 
						|| args[i].getClass() == Long.class)) {
					args[i] = ((Long) args[i]).intValue();
				}
			}
		}
		
		return mn.method.invoke(this
			, new Object[] 
		        { mn.returnClazz
				, mn.name
				, args });
	}
	
	public Object invokeGet(Class<?> returnClazz, String name, Object args[]) 
		throws Exception {
		Variant result = Dispatch.get(activeX, name);
		return variantReturn(result, returnClazz);
	}
	
	public Dispatch invokeGetDispatch(Class<?> returnClazz, String name, Object args[]) {
		return activeX;
	}
	
	public Object invokeMethod(Class<?> returnClazz, String name, Object args[]) 
		throws Exception {
		Variant result;
		
		if (null != args) {
			result = Dispatch.callN(activeX, name, args);
		} else {
			result = Dispatch.call(activeX, name);
		}
		
		return variantReturn(result,returnClazz);
	}
	
	public Object invokeProxyGet(Class<?> returnClazz, String name, Object args[]) 
		throws Exception {
		Dispatch result = Dispatch.get(activeX, name).toDispatch();
		return OleAutomation.createActiveXObject(result, returnClazz);
	}
	
	public Object invokeProxyMethod(Class<?> returnClazz, String name, Object args[]) 
		throws Exception {
		Dispatch result;
		if (null != args) {
			result = Dispatch.callN(activeX, name, args).toDispatch(); 
		} else {
			result = Dispatch.call(activeX, name).toDispatch(); 
		}
		
		if(result.m_pDispatch == 0) {
			return null;
		}
		
		return OleAutomation.createActiveXObject(result, returnClazz);
	}
	
	public void invokeProxySet(Class<?> returnClazz, String name, Object args[]) 
		throws Exception {
		Dispatch result = ((Dispatchable) args[0])._GET_JACOB_DISPATCH_();
		Dispatch.putRef(activeX, name, result);
	}	

	public void invokeSet(Class<?> returnClazz, String name, Object args[]) 
		throws Exception {
		Dispatch.put(activeX, name, args[0]);
	}
	
	private Object variantReturn(Variant v, Class<?> returnClazz) 
		throws Exception {
		if (v == null || v.isNull()) {
			return null;
		} else {
			if(returnClazz.isAssignableFrom(v.getClass())) {
				return v;
			} else {
				Short variantType = htVariantType.get(returnClazz);
				
				if(variantType != null) {
					v.changeType(variantType.shortValue());
					switch(variantType.shortValue()) {
						case Variant.VariantBoolean: return v.getBoolean(); 
						case Variant.VariantString:
							if(returnClazz == Character.TYPE 
								|| returnClazz == Character.class) {
								return v.getString().charAt(0);
							}
							
							return v.getString(); 
						case Variant.VariantByte: return v.getByte(); 
						case Variant.VariantShort: return v.getShort(); 
						case Variant.VariantInt:
							if(returnClazz == Long.TYPE 
								|| returnClazz == Long.class) {
								return (long)v.getInt();
							}
							
							return v.getInt(); 
						case Variant.VariantFloat: return v.getFloat(); 
						case Variant.VariantDouble: return v.getDouble(); 
						case Variant.VariantDate: return v.getDate(); 
					
					}
				}
			}

			return v.toDispatch();
		}
	}
	

}
