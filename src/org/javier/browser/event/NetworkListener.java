package org.javier.browser.event;

public interface NetworkListener {

	public void readyStateChanged(int readyState);

	public void requestCompleted(Object result);

}
