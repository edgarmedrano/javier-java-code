/**
 * File:        AGIUsageError
 * Description: AGI Usage Error
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
 * AGI Usage Error.
 */
public class AGIUsageError extends AGIError {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 4340216602385076870L;

	/**
	 * The Constructor.
	 */
	public AGIUsageError() {
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
	public AGIUsageError(int number, String message) {
		super(number, message);
	}

	/**
	 * The Constructor.
	 * 
	 * @param number
	 *            the number
	 */
	public AGIUsageError(int number) {
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
	public AGIUsageError(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * The Constructor.
	 * 
	 * @param message
	 *            the message
	 */
	public AGIUsageError(String message) {
		super(message);
	}

	/**
	 * The Constructor.
	 * 
	 * @param cause
	 *            the cause
	 */
	public AGIUsageError(Throwable cause) {
		super(cause);
	}


}
