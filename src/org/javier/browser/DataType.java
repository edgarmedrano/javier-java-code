/**
 * File:        DataType.java
 * Description: Represent a VoiceXML field data type.
 * Author:      Edgar Medrano Pérez 
 *              edgarmedrano at gmail dot com
 * Created:     2007.06.10
 * Company:     JAVIER project
 *              http://javier.sourceforge.net
 * Notes:        
 */

package org.javier.browser;

import java.util.Enumeration;
import java.util.Formatter;
import java.util.Hashtable;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.javier.browser.Document.Tag;

/**
 * @author Propietario
 *
 */
public class DataType {
	protected static final Hashtable<String,DataType> datatypes = new Hashtable<String,DataType>();
	
	protected final Properties properties = new Properties();

	static public enum Type {
		Boolean, Date, Digits, Currency, Number, Phone, Time, Custom
	}
	
	/** Maps the tag names to {@link Tag} enum. */
	static protected final Hashtable<String, Type> htTypeEnum 
		= new Hashtable<String, Type>(Type.values().length);
	static {
		for(Type type: Type.values()) {
			htTypeEnum.put(type.toString().toLowerCase(),type);
		}
	}	
		
	protected Type type;
	protected final Hashtable<String,Pattern> patterns = new Hashtable<String, Pattern>();

	protected DataType(String type, String pattern) {
		int index;
		String[] args;
		
		index = type.indexOf('?');
		if(index >= 0) {
			args = type.substring(index).split(";");
			type = type.substring(0, index - 1);
			for(String arg : args) {
				index = arg.indexOf('=');
				if(index >= 0) {
					properties.setProperty(arg.substring(0, index - 1), arg.substring(index));
				} else {
					properties.setProperty(arg, "");
				}
			}
		}
		
		index = type.lastIndexOf('/');
		if(index >= 0) {
			type = type.substring(index);
		}
		
		this.type = htTypeEnum.get(type);
		
		if(this.type == null) {
			this.type = Type.Custom;
		}
		
		switch(this.type) {
			case Boolean:
				pattern = "true=1|yes;false=2|no";
				break;
			case Currency:
				pattern = "\\d*((\\.|\\*)\\d*)?";				
				break;
			case Date:
				pattern = "\\d{0,4}[- /.]?(0[1-9]|1[012])[- /.]?(0[1-9]|[12][0-9]|3[01])";								
				break;
			case Digits:
				pattern = "\\d{1,12}";												
				break;
			case Number:
				pattern = "\\d*((\\.|\\*)\\d*)?";				
				break;
			case Phone:
				pattern = "\\d{1,12}";								
				break;
			case Time:
				pattern = "\\d{1,12}";												
				break;
			default: //Custom
				
		}
		
		patterns.put("",Pattern.compile(pattern));
		
		if(!pattern.equals("")) {
			patterns.put("",Pattern.compile(".*"));
		}
	}
	public static DataType getType(String type) {
		return getType(type,"");
	}

	public static DataType getType(String type, String pattern) {
		DataType datatype = datatypes.get(type);
		
		if(datatype == null) {
			datatype = new DataType(type,pattern);
			datatypes.put(type, datatype);
		}
		
		return datatype;
	}
	
	/**
	 * Gets the property as a string.
	 * 
	 * @param name the name
	 * 
	 * @return the property
	 */
	public String getString(String name) {
		return properties.getProperty(name, "");
	}	
	
	/**
	 * Gets the property as an integer.
	 * 
	 * @param name
	 *            the name
	 * @param defaultValue
	 *            the default value
	 * 
	 * @return the property
	 */
	public int getInteger(String name) {
		return Integer.parseInt(properties.getProperty(name, "0"));
	}	
	
	/**
	 * Gets the property as a long.
	 * 
	 * @param name
	 *            the name
	 * @param defaultValue
	 *            the default value
	 * 
	 * @return the property
	 */
	public long getLong(String name) {
		return Long.parseLong(properties.getProperty(name, "0"));
	}	
	
	/**
	 * Gets the property as a float.
	 * 
	 * @param name
	 *            the name
	 * @param defaultValue
	 *            the default value
	 * 
	 * @return the property
	 */
	public float getFloat(String name) {
		return Float.parseFloat(properties.getProperty(name, "0"));
	}
	
	/**
	 * Parse.
	 * 
	 * @param value
	 *            the value
	 * 
	 * @return the string
	 */
	public String parse(String value) {
		String match = null;
		int length = 0;
		
		for(Enumeration<String> keys = patterns.keys();keys.hasMoreElements();) {
			String key = keys.nextElement();
			Matcher matcher = patterns.get(key).matcher(value);
			if(matcher.find()) {
				if(matcher.group(0).length() > length) {
					match = key;
					length = matcher.group(0).length();
				}
			}
		} 
		
		if(match == null) {
			match = "";
		} else if(match.equals("")) {
			match = value;
		}
		
		return match;
	}
}
