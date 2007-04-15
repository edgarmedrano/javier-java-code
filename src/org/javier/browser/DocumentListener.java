package org.javier.browser;

import org.javier.browser.Document.State;

public interface DocumentListener {
	public void errorFound(String description);

	public void warningFound(String description);

	public void commentFound(String description);
	
	public void verboseFound(String description);

	public void stateChanged(State state);
}
