/*
 * Created on 06/12/2005 File JacobInvocation.java author: Maikon
 */
package proxies;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Date;

import org.javier.jacob.Dispatchable;
import org.javier.jacob.SAPI.ISpeechObjectTokens;

import annotations.OleInterface;
import annotations.OleMethod;
import annotations.OleProperty;

import com.jacob.com.Dispatch;
import com.jacob.com.DispatchEvents;
import com.jacob.com.Variant;

public class OleInvocationHandler implements InvocationHandler {

	private Dispatch activeX = null;

	private DispatchEvents dispEvents = null;

	public OleInvocationHandler(Class<?> clazz) {
		try {
			String ole = clazz.getAnnotation(OleInterface.class).name();
			activeX = new Dispatch(ole);

			Class<?> eventHandlerClass = clazz
					.getAnnotation(OleInterface.class).eventHandler();
			if (!eventHandlerClass.equals(Object.class)) {
				dispEvents = new DispatchEvents(activeX, eventHandlerClass
						.newInstance());
			}
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}

	}

	public OleInvocationHandler(Dispatch disp, Class<?> clazz) {
		try {
			activeX = disp;

			Class<?> eventHandlerClass = clazz
					.getAnnotation(OleInterface.class).eventHandler();
			if (!eventHandlerClass.equals(Object.class)) {
				dispEvents = new DispatchEvents(activeX, eventHandlerClass
						.newInstance());
			}
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		Object v = null;
		
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
		
		if (method.isAnnotationPresent(OleProperty.class)) {
			v = callPropety(method, args);
		} else if (method.isAnnotationPresent(OleMethod.class)) {
			v = callMethod(method, args);
		}
		return v;
	}

	private Object callMethod(Method method, Object[] args) throws Exception {
		Class<?> returnClazz = method.getReturnType();
		String name = method.getAnnotation(OleMethod.class).name();
		Object returnObject = null;

		if (null == name || "".equals(name)) {
			name = method.getName();
		}

		if (returnClazz.isAnnotationPresent(OleInterface.class)) {
			if (null != args) {
				returnObject = OleAutomationFactory.getActiveXComponet(Dispatch
						.callN(activeX, name, args).toDispatch(), returnClazz);
			} else {
				returnObject = OleAutomationFactory.getActiveXComponet(Dispatch
						.call(activeX, name).toDispatch(), returnClazz);
			}
		} else {
			if (null != args) {
				returnObject = variantReturn(Dispatch
						.callN(activeX, name, args), returnClazz);
			} else {
				returnObject = variantReturn(Dispatch.call(activeX, name),
						returnClazz);
			}
		}

		return returnObject;
	}

	private Object callPropety(Method method, Object[] args) throws Exception {
		String methodName = method.getName();
		String popertyName = method.getAnnotation(OleProperty.class).name();
		Class<?> returnClazz = method.getReturnType();
		Object returnObject = null;
		boolean blnSet = false;

		if (method.getName().equals("_GET_JACOB_DISPATCH_")) {
			return activeX;
		}

		if(returnClazz == null || returnClazz == Void.TYPE
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

		if (blnSet) {
			if (args[0] instanceof Dispatchable) {
				Dispatch.putRef(activeX, popertyName
					, ((Dispatchable) args[0])._GET_JACOB_DISPATCH_());
			} else {
				Dispatch.put(activeX, popertyName, args[0]);
			}
		} else {
			if (returnClazz.isAnnotationPresent(OleInterface.class)) {
				returnObject = OleAutomationFactory.getActiveXComponet(Dispatch
						.get(activeX, popertyName).toDispatch(), returnClazz);
			} else {
				returnObject = variantReturn(
						Dispatch.get(activeX, popertyName), returnClazz);
			}
		}
		return returnObject;
	}

	private Object variantReturn(Variant v, Class<?> returnClazz) throws Exception {
		if (v == null || v.isNull()) {
			return null;
		} else {
			if(returnClazz.isAssignableFrom(v.getClass())) {
				return v;
			} else {
				if (returnClazz.isPrimitive()) {
					if (returnClazz == Boolean.TYPE || returnClazz == Boolean.class) {
						v.changeType(Variant.VariantBoolean);
						return v.getBoolean();
					}
					if (returnClazz == Character.TYPE || returnClazz == Character.class) {
						v.changeType(Variant.VariantString);
						return v.getString().charAt(0);
					}
					if (returnClazz == Byte.TYPE || returnClazz == Byte.class) {
						v.changeType(Variant.VariantByte);
						return v.getByte();
					}
					if (returnClazz == Short.TYPE
							|| returnClazz == Short.class) {
						v.changeType(Variant.VariantShort);
						return v.getShort();
					}
					if (returnClazz == Integer.TYPE || returnClazz == Integer.class) {
						v.changeType(Variant.VariantInt);
						return v.getInt();
					}
					if (returnClazz == Long.TYPE || returnClazz == Long.class) {
						// Use Integer instead of Long it's safer
						v.changeType(Variant.VariantInt);
						return (long)v.getInt();
					}
					if (returnClazz == Float.TYPE || returnClazz == Float.class) {
						v.changeType(Variant.VariantFloat);
						return v.getFloat();
					}
					if (returnClazz == Double.TYPE || returnClazz == Double.class) {
						v.changeType(Variant.VariantDouble);
						return v.getDouble();
					}
				} else {
					if (returnClazz == String.class) {
						v.changeType(Variant.VariantString);
						return v.getString();
					}
					if (returnClazz == Date.class) {
						v.changeType(Variant.VariantDate);
						return v.getDate();
					}
				}
			}

			return v.toDispatch();
		}
	}
}
