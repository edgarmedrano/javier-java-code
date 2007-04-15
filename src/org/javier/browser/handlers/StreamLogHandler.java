package org.javier.browser.handlers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;

import org.javier.browser.LogListener;

public class StreamLogHandler implements LogListener {
	protected PrintWriter pw;
	
	public StreamLogHandler(PrintWriter pw) {
		this.pw = pw; 
	}
	
	public StreamLogHandler(File file) throws FileNotFoundException {
		this(new PrintWriter(file)); 
	}
	
	public StreamLogHandler(OutputStream stream) {
		this(new PrintWriter(stream)); 
	}
	
	public StreamLogHandler(String fileName) throws FileNotFoundException {
		this(new PrintWriter(fileName)); 
	}
	
	public StreamLogHandler(Writer out) {
		this(new PrintWriter(out)); 
	}
	
	public void logReported(String description, int level) {
		pw.println(description);
	}
}
