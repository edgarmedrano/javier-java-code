/**
 * File:        AGIHangup.java
 * Description: AGI Hangup Error
 * Author:      Edgar Medrano Pérez
 *              edgarmedrano at gmail dot com
 * Created:     2007.05.19
 * Company:     JAVIER project
 *              http://javier.sourceforge.net
 * Notes:       The classes included in this package implement AGI 
 *              communication and are based in agi.py implementation
 *              from http://sourceforge.net/projects/pyst/ 
 */
package org.javier.agi;

/**
 * AGI Hangup.
 */
public class AGIHangup extends AGIError {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -4875997724294625702L;

	/**
	 * The Constructor.
	 */
	public AGIHangup() {
	}

	/**
	 * The Constructor.
	 * 
	 * @param message
	 *            the message
	 * @param number
	 *            the number
	 */
	public AGIHangup(int number, String message) {
		super(number, message);
	}

	/**
	 * The Constructor.
	 * 
	 * @param number
	 *            the number
	 */
	public AGIHangup(int number) {
		super(number);
	}

	/**
	 * The Constructor.
	 * 
	 * @param message
	 *            the message
	 * @param cause
	 *            the cause
	 */
	public AGIHangup(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * The Constructor.
	 * 
	 * @param message
	 *            the message
	 */
	public AGIHangup(String message) {
		super(message);
	}

	/**
	 * The Constructor.
	 * 
	 * @param cause
	 *            the cause
	 */
	public AGIHangup(Throwable cause) {
		super(cause);
	}
}
