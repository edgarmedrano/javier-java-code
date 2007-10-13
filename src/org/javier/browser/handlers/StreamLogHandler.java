/**
 * File:        StreamLogHandler.java
 * Description: A log handler that uses a Stream to log
 * Author:      Edgar Medrano Pérez
 *              edgarmedrano at gmail dot com
 * Created:     2007.04.15
 * Company:     JAVIER project
 *              http://javier.sourceforge.net
 * Notes:         
 */
package org.javier.browser.handlers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;

import org.javier.browser.event.LogListener;

/**
 * A log handler that uses a Stream to log.
 */
public class StreamLogHandler implements LogListener {
	
	/** The pw. */
	protected PrintWriter pw;
	
	/**
	 * The Constructor.
	 * 
	 * @param pw the pw
	 */
	public StreamLogHandler(PrintWriter pw) {
		this.pw = pw;
	}
	
	/**
	 * The Constructor.
	 * 
	 * @param out  the out
	 */
	public StreamLogHandler(Writer out) {
		this(new PrintWriter(out, true)); 
	}
		
	/**
	 * The Constructor.
	 * 
	 * @param stream  the stream
	 */
	public StreamLogHandler(OutputStream stream) {
		this(new PrintWriter(stream, true)); 
	}
	
	/**
	 * The Constructor.
	 * 
	 * @param file the file
	 * 
	 * @throws FileNotFoundException
	 *             the file not found exception
	 */
	public StreamLogHandler(File file) throws FileNotFoundException {
		this(new FileOutputStream(file, true)); 
	}
	
	/**
	 * The Constructor.
	 * 
	 * @param fileName the file name
	 * 
	 * @throws FileNotFoundException
	 *             if the file couldn't be created
	 */
	public StreamLogHandler(String fileName) throws FileNotFoundException {
		this(new FileOutputStream(fileName, true)); 
	}
	
	/* (non-Javadoc)
	 * @see org.javier.browser.event.LogListener#logReported(java.lang.String, int)
	 */
	public void logReported(String description, int level) {
		pw.println(description);
	}
}
