package org.javier.browser;

public interface DocumentListener {
	public void errorFound(String description);

	public void warningFound(String description);

	public void commentFound(String description);
	
	public void verboseFound(String description);
}
