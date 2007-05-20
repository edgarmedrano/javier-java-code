/**
 * File:        AGIException
 * Description: AGI Exception
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
 * AGI Exception.
 */
public class AGIException extends Exception {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1224321232503638083L;
	
	/** The number. */
	private int number;

	/**
	 * The Constructor.
	 */
	public AGIException() {
	}

	/**
	 * The Constructor.
	 * 
	 * @param message
	 *            the message
	 */
	public AGIException(String message) {
		super(message);
	}

	/**
	 * The Constructor.
	 * 
	 * @param cause
	 *            the cause
	 */
	public AGIException(Throwable cause) {
		super(cause);
	}

	/**
	 * The Constructor.
	 * 
	 * @param message
	 *            the message
	 * @param cause
	 *            the cause
	 */
	public AGIException(String message, Throwable cause) {
		super(message, cause);
	}
	
	/**
	 * The Constructor.
	 * 
	 * @param number
	 *            the number
	 */
	public AGIException(int number) {
		this();
		this.number = number;
	}
	
	/**
	 * The Constructor.
	 * 
	 * @param message
	 *            the message
	 * @param number
	 *            the number
	 */
	public AGIException(int number, String message) {
		this(message);
		this.number = number;
	}

	/**
	 * Gets the number.
	 * 
	 * @return the number
	 */
	public int getNumber() {
		return number;
	}
}
