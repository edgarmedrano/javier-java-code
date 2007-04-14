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

public final class FastConcatenation {
	private StringBuilder stb;
	
	public FastConcatenation() {
		stb = new StringBuilder();
	}
	
	public FastConcatenation(int capacity) {
		stb = new StringBuilder(capacity);
	}
	
	public FastConcatenation(String str) {
		this();
		stb.append(str);
	}
	
	public FastConcatenation push(Object ... args) {
		for(int i = 0; i < args.length; i++) {
			stb.append(args[i]);
		}
		return this;
	}

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
	
	public String replaceAll(String str1, String str2) {
		String strJoin = stb.toString();
		stb = new StringBuilder(strJoin);
		return strJoin.replaceAll(str1, str2);
	}
	
	public String replace(String str1, String str2) {
		String strJoin = stb.toString();
		stb = new StringBuilder(strJoin);
		return strJoin.replace(str1, str2);
	}
}