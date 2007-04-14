package org.javier.browser.handlers;

public interface NetworkListener {

	public void readyStateChanged(int readyState);

	public void requestCompleted(Object result);

}
