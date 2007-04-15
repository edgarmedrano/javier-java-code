package org.javier.browser;

public interface LogListener {
	public final int NONE = 0;
	public final int ERROR = 1;
	public final int WARNING = 2;
	public final int COMMENT = 3;
	public final int VERBOSE = 4;
	
	void logReported(String description, int level);

}
