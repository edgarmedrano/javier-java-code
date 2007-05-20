/**
 * File:        AGISIGPIPEHangup.java
 * Description: AGI SIGPIPE Hangup
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
 * AGI SIGPIPE Hangup.
 */
public class AGISIGPIPEHangup extends AGIHangup {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -6572970161482849541L;

	/**
	 * The Constructor.
	 */
	public AGISIGPIPEHangup() {
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
	public AGISIGPIPEHangup(int number, String message) {
		super(number, message);
	}

	/**
	 * The Constructor.
	 * 
	 * @param number
	 *            the number
	 */
	public AGISIGPIPEHangup(int number) {
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
	public AGISIGPIPEHangup(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * The Constructor.
	 * 
	 * @param message
	 *            the message
	 */
	public AGISIGPIPEHangup(String message) {
		super(message);
	}

	/**
	 * The Constructor.
	 * 
	 * @param cause
	 *            the cause
	 */
	public AGISIGPIPEHangup(Throwable cause) {
		super(cause);
	}
}
