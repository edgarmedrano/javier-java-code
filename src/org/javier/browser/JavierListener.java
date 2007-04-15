package org.javier.browser;

public interface JavierListener {
	
	public void loadStateChanged(int readyState);
	
	public void urlChanged(String url);
		
	public void excecutionEnded(int endCode);
	
}
