package org.javier.browser.handlers;

public interface LogHandler {
	static final int ERROR = 1;
	static final int WARNING = 2;
	static final int COMMENT = 3;
	static final int VERBOSE = 4;
	void writeln(String string);
}
