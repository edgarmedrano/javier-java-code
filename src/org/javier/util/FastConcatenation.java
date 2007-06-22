/**
 * File:        FastConcatenation.java
 * Description: Fast string concatenation object
 * Author:      Edgar Medrano Pérez
 *              edgarmedrano at gmail dot com
 * Created:     2007.04.14
 * Company:     JAVIER project
 *              http://javier.sourceforge.net
 * Notes:        
 */
package org.javier.util;

/**
 * Fast string concatenation object. 
 * <p>This class relies in {@link StringBuilder} so it's not 
 * concurrent safe.</p>
 * 
 * @author Edgar Medrano Pérez
 * @see StringBuilder
 */
public final class FastConcatenation {
	
	/** The resulting string is stored here. */
	private StringBuilder stb;
	
	/**
	 * Constructs a FastConcatenation object with no characters in it and 
	 * an initial capacity of {@literal 16} characters.
	 */
	public FastConcatenation() {
		stb = new StringBuilder();
	}
	
	/**
	 * Constructs a FastConcatenation object with no characters in it and 
	 * an initial capacity specified by the capacity argument.
	 * 
	 * @param capacity the initial capacity.
	 */
	public FastConcatenation(int capacity) {
		stb = new StringBuilder(capacity);
	}
	
	/**
	 * Constructs a FastConcatenation object initialized to the contents 
	 * of the specified string. The initial capacity of the string builder 
	 * is {@literal 16} plus the length of the string argument.
	 * 
	 * @param str the initial contents of the buffer.
	 */
	public FastConcatenation(String str) {
		this();
		stb.append(str);
	}
	
	/**
	 * Appends the string representation of the specified arguments 
	 * to the concatenated string.
	 * 
	 * @param args the objects to be added
	 * 
	 * @return the fast concatenation
	 */
	public FastConcatenation push(Object ... args) {
		for(int i = 0; i < args.length; i++) {
			stb.append(args[i]);
		}
		return this;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		String strJoin;
		
		if(stb.length() == 0) {
			return "";
		}
		
		if(stb.length() == 1) {
			return stb.toString();
		}
		
		strJoin = stb.toString();
		stb = new StringBuilder();
		stb.append(strJoin);
		
		return strJoin;
	}
	
	/**
	 * Invokes {@link String#replaceAll(String, String)}.
	 * 
	 * @param regex       the regular expression to which this string 
	 *                    is to be matched
	 * @param replacement the string to be substituted for each match
	 * 
	 * @return The resulting string
	 */
	public String replaceAll(String regex, String replacement) {
		String strJoin = stb.toString();
		stb = new StringBuilder(strJoin);
		return strJoin.replaceAll(regex, replacement);
	}

	/**
	 * Gets the length of current concatenation
	 * 
	 * @return the length of current concatenation
	 */
	public int length() {
		return stb.length();
	}
}