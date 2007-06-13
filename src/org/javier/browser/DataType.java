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
import java.util.Hashtable;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.javier.browser.Document.Tag;

/**
 * VoiceXML field data type.
 */
public class DataType {
	
	/** The Constant datatypes. */
	protected static final Hashtable<String,DataType> datatypes = new Hashtable<String,DataType>();
	
	/** The properties. */
	protected final Properties properties = new Properties();

	/**
	 * The Enum Type.
	 */
	static public enum Type {
		/** The Boolean. */
		Boolean, 
		/** The Date. */
		Date, 
		/** The Digits. */
		Digits, 
		/** The Currency. */
		Currency, 
		/** The Number. */
		Number, 
		/** The Phone. */
		Phone, 
		/** The Time. */
		Time, 
		/** The Custom. */
		Custom
	}
	
	/** Maps the tag names to {@link Tag} enum. */
	static protected final Hashtable<String, Type> htTypeEnum 
		= new Hashtable<String, Type>(Type.values().length);
	static {
		for(Type type: Type.values()) {
			htTypeEnum.put(type.toString().toLowerCase(),type);
		}
	}	
		
	/** The definition. */
	protected String definition;
	
	/** The type. */
	protected Type type;
	
	/** The patterns. */
	protected final Hashtable<String,Pattern> patterns = new Hashtable<String, Pattern>();

	/** The max. */
	protected int max;

	/** The min. */
	protected int min;

	/**
	 * The Constructor.
	 * 
	 * @param pattern
	 *            the pattern
	 * @param type
	 *            the type
	 */
	protected DataType(String type, String pattern) {
		int index;
		String[] args;
		
		index = type.indexOf('?');
		if(index >= 0) {
			args = type.substring(index + 1).split(";");
			type = type.substring(0, index);
			for(String arg : args) {
				index = arg.indexOf('=');
				if(index >= 0) {
					properties.setProperty(arg.substring(0, index), arg.substring(index + 1));
				} else {
					properties.setProperty(arg, "");
				}
			}
		}
		
		index = type.lastIndexOf('/');
		if(index >= 0) {
			type = type.substring(index + 1);
		}
		
		this.type = htTypeEnum.get(type);
		
		if(this.type == null) {
			this.type = Type.Custom;
		}

		if(pattern.equals("")) {
			switch(this.type) {
				case Boolean:
					min = getInteger("length",getString("minlength","1"));
					max = getInteger("length",getString("maxlength","3"));
					String y = getString("y","1|yes");
					String n = getString("n","2|no");
					pattern = "true=" + y + ";false=" + n;
					break;
				case Currency:
					min = getInteger("length",getString("minlength","1"));
					max = getInteger("length",getString("maxlength","12"));
					pattern = "\\d{" + min + "," + max + "}((\\.|\\*)\\d{0,2})?";				
					break;
				case Date:
					min = getInteger("length",getString("minlength","4"));
					max = getInteger("length",getString("maxlength","10"));
					pattern = "\\d{0,4}[- /.]?(0[1-9]|1[012])[- /.]?(0[1-9]|[12][0-9]|3[01])";								
					break;
				case Digits:
					min = getInteger("length",getString("minlength","1"));
					max = getInteger("length",getString("maxlength","255"));
					pattern = "\\d{" + min + "," + max + "}";												
					break;
				case Number:
					min = getInteger("length",getString("minlength","1"));
					max = getInteger("length",getString("maxlength","9"));
					pattern = "\\d*((\\.|\\*)\\d*)?";				
					break;
				case Phone:
					min = getInteger("length",getString("minlength","1"));
					max = getInteger("length",getString("maxlength","13"));
					pattern = "\\d{" + min + "," + max + "}";								
					break;
				case Time:
					min = getInteger("length",getString("minlength","1"));
					max = getInteger("length",getString("maxlength","6"));
					pattern = "\\d{" + min + "," + max + "}";								
					break;
			}
		} else {
			min = getInteger("length",getString("minlength","-1"));
		    max = getInteger("length",getString("maxlength","-1"));
			
		    if(min < 0) {
		    	min = parsePattern(pattern,0);
		    }
			
		    if(max < 0) {
		    	max = parsePattern(pattern,255);		    	
		    }
			
		}

    	definition = type + ";grammar=" + pattern;
    	
		args = pattern.split(";");
		for(String arg : args) {
			index = arg.indexOf('=');
			if(index >= 0) {
				patterns.put(arg.substring(0, index),Pattern.compile(arg.substring(index + 1)));
			} else {
				patterns.put("", Pattern.compile(arg));
			}
		}
	}
	
	/** The i. */
	private int i;
	
	/**
	 * Parses the pattern.
	 * 
	 * @param weight
	 *            the weight
	 * @param pattern
	 *            the pattern
	 * 
	 * @return the int
	 */
	private int parsePattern(String pattern, int weight) {
		i = 0;
		return parsePattern(pattern, ' ', weight);
	}
	
	/**
	 * Parses the pattern.
	 * 
	 * @param weight
	 *            the weight
	 * @param pattern
	 *            the pattern
	 * @param parent
	 *            the parent
	 * 
	 * @return the int
	 */
	private int parsePattern(String pattern, char parent, int weight) {
		int length = 0;
		int last = 0;
		
		for(; i < pattern.length(); i++) {
			switch(pattern.charAt(i)) {
				case '\\':
					i++;
					length += last;
					last = 1;
					break;
				case '|':
					i++;
					if(parent == '|') {
						return length + last;
					} else {
						int aux = length + last;
						last = 0;
						length = parsePattern(pattern, '|', weight);
						if(weight == 0) {
							if(aux < length) {
								length = aux;
							} 								
						} else {
							if(aux > length) {
								length = aux;
							} 								
						}
					}
					break;
				case ')':
					i++;
					return length + last;
				case ']':
					i++;
					return 1;
				case '[':
					last = parsePattern(pattern, '[', weight);
				case '{':
					int index = pattern.indexOf("}", i);
					if(index >= 0) {
						String range = pattern.substring(i, index);
						i = index;
						index = range.indexOf(',');
						if(index >= 0) {
							if(weight == 0) {
								last *= Integer.parseInt(range.split(",")[0]);
							} else {
								last *= Integer.parseInt(range.split(",")[1]);								
							}
						} else {
							last *= Integer.parseInt(range);
						}
					}
					break;
				case '(':
					last = parsePattern(pattern, '(', weight);
					break;
				case '*':
					last *= weight;
					break;
				case '+':
					last *= weight > 0 ? weight : 1;
					break;
				case '?':
					last *= weight > 0 ? 1 : 0;
					break;
				default:
					length += last;
					last = 1;
					break;
			}
		}
		length += last;
		
		return length;
	}

	/**
	 * Gets the type.
	 * 
	 * @param type
	 *            the type
	 * 
	 * @return the type
	 */
	public static DataType getType(String type) {
		return getType(type,"");
	}

	/**
	 * Gets the type.
	 * 
	 * @param pattern
	 *            the pattern
	 * @param type
	 *            the type
	 * 
	 * @return the type
	 */
	public static DataType getType(String type, String pattern) {
		DataType datatype = datatypes.get(type + ";" + pattern);
		
		if(datatype == null) {
			datatype = new DataType(type,pattern);
			datatypes.put(type + ";" + pattern, datatype);
		}
		
		return datatype;
	}
	
	/**
	 * Gets the property as a string.
	 * 
	 * @param name
	 *            the name
	 * @param defaultValue
	 *            the default value to return if property is not defined
	 * 
	 * @return the property
	 */
	public String getString(String name, String defaultValue) {
		return properties.getProperty(name, defaultValue);
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
	public int getInteger(String name, String defaultValue) {
		return Integer.parseInt(properties.getProperty(name, defaultValue));
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
	public long getLong(String name, String defaultValue) {
		return Long.parseLong(properties.getProperty(name, defaultValue));
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
	public float getFloat(String name, String defaultValue) {
		return Float.parseFloat(properties.getProperty(name, defaultValue));
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
		String match = "";
		int length = 0;
		
		for(Enumeration<String> keys = patterns.keys();keys.hasMoreElements();) {
			String key = keys.nextElement();
			Matcher matcher = patterns.get(key).matcher(value);
			if(matcher.find()) {
				int groupLength = matcher.group(0).length();
				if(groupLength > length && groupLength >= min && groupLength <= max) {
					match = key.equals("") ? matcher.group(0) : key;
					length = matcher.group(0).length();
				}
			}
		} 
				
		return match;
	}

	/**
	 * Gets the max.
	 * 
	 * @return the max
	 */
	public int getMax() {
		return max;
	}

	/**
	 * Gets the min.
	 * 
	 * @return the min
	 */
	public int getMin() {
		return min;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return definition;
	}

	/**
	 * The main method.
	 * 
	 * @param args
	 *            the args
	 */
	public static void main(String[] args) {
		DataType test[] = {
				/*DataType.getType("boolean")
				,DataType.getType("date")
				,DataType.getType("digits")
				,DataType.getType("currency")
				,DataType.getType("number")
				,DataType.getType("phone")
				,DataType.getType("time")
				,DataType.getType("digits?length=1","1|2|8|9")
				,*/ DataType.getType("digits?length=1")
				};
		for(DataType dt: test) {
			System.out.printf("type: %s, min: %d, max: %d\n", dt, dt.getMin(), dt.getMax());
		}
	}
}
