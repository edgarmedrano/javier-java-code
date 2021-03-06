/**
 * File:        EscapeUnescape.java
 * Description: JavaScript escape/unescape emulator 
 * Author:      Emu 
 * Created:     Before than 2004.02.16
 * Company:     http://dev.csdn.net/user/emu
 * Notes:       I had tried to do this with
 *  
 *                  URLEncoder.encode(String, "UTF-8");
 *  
 *              but it didn't work at all with Rhino, so i googled and found 
 *              this great piece of code at these two blogs:
 *              
 *              http://dev.csdn.net/article/13/13437.shtm
 *              http://www.blogjava.net/emu/articles/4773.html
 *              
 *              and it does pretty well
 *              
 *              Thanks to Emu
 */

package org.javier.util;

/**
 * Emulates the JavaScript escape and unescape functions.
 */
public class EscapeUnescape {
	
	/**
	 * returns the hexadecimal encoding of an argument in the ISO Latin 
	 * character set.
	 * 
	 * @param src the string to be encoded
	 * 
	 * @return the encoded string
	 */
	public static String escape(String src) {
		int i;
		char j;
		StringBuffer tmp = new StringBuffer();
		tmp.ensureCapacity(src.length() * 6);

		for (i = 0; i < src.length(); i++) {

			j = src.charAt(i);

			if (Character.isDigit(j) || Character.isLowerCase(j)
					|| Character.isUpperCase(j))
				tmp.append(j);
			else if (j < 256) {
				tmp.append("%");
				if (j < 16)
					tmp.append("0");
				tmp.append(Integer.toString(j, 16));
			} else {
				tmp.append("%u");
				tmp.append(Integer.toString(j, 16));
			}
		}
		return tmp.toString();
	}

	/**
	 * returns the ASCII string for the specified hexadecimal encoding value.
	 * 
	 * @param src the string to be decoded
	 * 
	 * @return the decoded string
	 */
	public static String unescape(String src) {
		StringBuffer tmp = new StringBuffer();
		tmp.ensureCapacity(src.length());
		int lastPos = 0, pos = 0;
		char ch;
		while (lastPos < src.length()) {
			pos = src.indexOf("%", lastPos);
			if (pos == lastPos) {
				if (src.charAt(pos + 1) == 'u') {
					ch = (char) Integer.parseInt(src
							.substring(pos + 2, pos + 6), 16);
					tmp.append(ch);
					lastPos = pos + 6;
				} else {
					ch = (char) Integer.parseInt(src
							.substring(pos + 1, pos + 3), 16);
					tmp.append(ch);
					lastPos = pos + 3;
				}
			} else {
				if (pos == -1) {
					tmp.append(src.substring(lastPos));
					lastPos = src.length();
				} else {
					tmp.append(src.substring(lastPos, pos));
					lastPos = pos;
				}
			}
		}
		return tmp.toString();
	}

	/**
	 * The main method.
	 * 
	 * @param args the commandline arguments
	 */
	public static void main(String[] args) {
		String tmp = "~!@#$%^&*()_+|\\=-,./?><;'][{}\"";
		System.out.println("testing escape : " + tmp);
		tmp = escape(tmp);
		System.out.println(tmp);
		System.out.println("testing unescape :" + tmp);
		System.out.println(unescape(tmp));
	}
}
