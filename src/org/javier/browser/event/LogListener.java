/**
 * File:        LogListener.java
 * Description: The listener interface for receiving log events.
 * Author:      Edgar Medrano Pérez
 *              edgarmedrano at gmail dot com
 * Created:     2007.04.15
 * Company:     JAVIER project
 *              http://javier.sourceforge.net
 * Notes:        
 */
package org.javier.browser.event;

/**
 * The listener interface for receiving log events.
 */
public interface LogListener {
	
	/** The NONE. */
	public final int NONE = 0;
	
	/** The ERROR level. */
	public final int ERROR = 1;
	
	/** The WARNING level. */
	public final int WARNING = 2;
	
	/** The COMMENT level. */
	public final int COMMENT = 3;
	
	/** The VERBOSE level. */
	public final int VERBOSE = 4;
	
	/**
	 * Invoked when a log message is reported.
	 * 
	 * @param level the level, one of: {@link #VERBOSE}, {@link #COMMENT}
	 *              , {@link #WARNING}, {@link #ERROR}, {@link #NONE}
	 * @param description the log message
	 */
	void logReported(String description, int level);

}
