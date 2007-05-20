/**
 * File:        AGISIGHUPHangup.java
 * Description: AGI SIGHUP Hangup
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
 * AGI SIGHUP Hangup.
 */
public class AGISIGHUPHangup extends AGIHangup {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -6010900494003045894L;

	/**
	 * The Constructor.
	 */
	public AGISIGHUPHangup() {
		super();
	}

	/**
	 * The Constructor.
	 * 
	 * @param message
	 *            the message
	 * @param number
	 *            the number
	 */
	public AGISIGHUPHangup(int number, String message) {
		super(number, message);
	}

	/**
	 * The Constructor.
	 * 
	 * @param number
	 *            the number
	 */
	public AGISIGHUPHangup(int number) {
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
	public AGISIGHUPHangup(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * The Constructor.
	 * 
	 * @param message
	 *            the message
	 */
	public AGISIGHUPHangup(String message) {
		super(message);
	}

	/**
	 * The Constructor.
	 * 
	 * @param cause
	 *            the cause
	 */
	public AGISIGHUPHangup(Throwable cause) {
		super(cause);
	}
}
