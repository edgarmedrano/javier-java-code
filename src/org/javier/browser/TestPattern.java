package org.javier.browser;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TestPattern {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Hashtable<String,Pattern> patterns = new Hashtable<String, Pattern>();
		/*
		patterns.put("xxx", Pattern.compile("\\d\\d\\d"));
		patterns.put("xx", Pattern.compile("\\d\\d"));
		patterns.put("x", Pattern.compile("\\d"));
		patterns.put("true", Pattern.compile("(1)"));
		patterns.put("false", Pattern.compile("(0)"));
		patterns.put("or", Pattern.compile("(1) o (2|3|4)"));
		patterns.put("and", Pattern.compile("(1) y (2|3|4)"));
		patterns.put("and/or", Pattern.compile("(1) y/o (2|3|4)"));
		patterns.put("if", Pattern.compile("(1) entonces (2|3|4)"));
		patterns.put("if-then", Pattern.compile("si (1) entonces (2|3|4)"));
		patterns.put("", Pattern.compile(".*"));
		*/
		patterns.put("", Pattern.compile("\\d*((\\.|\\*)\\d*)?"));
		String match = null;
		int length = 0;
		String string = "123.0hola";
		
		for(Enumeration<String> keys = patterns.keys();keys.hasMoreElements();) {
			String key = keys.nextElement();
			Matcher matcher = patterns.get(key).matcher(string);
			if(matcher.find() && matcher.groupCount() > 1) {
				if(matcher.group(1).length() > length) {
					match = key;
					length = matcher.group(1).length();
				}
			}
		} 
		
		if(match == null) {
			match = "";
		} else if(match.equals("")) {
			match = string;
		}
		
		System.out.println("match: " + match);
	}

}
