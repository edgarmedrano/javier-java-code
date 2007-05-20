/**
 * File:        AGIAppError.java
 * Description: AGI Application Error
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
 * AGI Application Error.
 */
public class AGIAppError extends AGIError {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 8898220688471945462L;

	/**
	 * The Constructor.
	 */
	public AGIAppError() {
		super();
	}

	/**
	 * The Constructor.
	 * 
	 * @param message the message
	 * @param number the number
	 */
	public AGIAppError(int number, String message) {
		super(number, message);
	}

	/**
	 * The Constructor.
	 * 
	 * @param number the number
	 */
	public AGIAppError(int number) {
		super(number);
	}

	/**
	 * The Constructor.
	 * 
	 * @param message the message
	 * @param cause the cause
	 */
	public AGIAppError(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * The Constructor.
	 * 
	 * @param message the message
	 */
	public AGIAppError(String message) {
		super(message);
	}

	/**
	 * The Constructor.
	 * 
	 * @param cause the cause
	 */
	public AGIAppError(Throwable cause) {
		super(cause);
	}
}
