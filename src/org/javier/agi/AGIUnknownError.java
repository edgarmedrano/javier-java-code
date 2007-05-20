/**
 * File:        AGIUnknownError.java
 * Description: AGI Unknown Error
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
 * AGI Unknown Error.
 */
public class AGIUnknownError extends AGIError {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 2871418845154441918L;

	/**
	 * The Constructor.
	 */
	public AGIUnknownError() {
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
	public AGIUnknownError(int number, String message) {
		super(number, message);
	}

	/**
	 * The Constructor.
	 * 
	 * @param number
	 *            the number
	 */
	public AGIUnknownError(int number) {
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
	public AGIUnknownError(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * The Constructor.
	 * 
	 * @param message
	 *            the message
	 */
	public AGIUnknownError(String message) {
		super(message);
	}

	/**
	 * The Constructor.
	 * 
	 * @param cause
	 *            the cause
	 */
	public AGIUnknownError(Throwable cause) {
		super(cause);
	}
}
