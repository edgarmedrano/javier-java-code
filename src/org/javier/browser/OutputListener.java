package org.javier.browser;

public interface OutputListener {

	void waitUntilDone();

	void addText(String text);

	void clearText();

}
