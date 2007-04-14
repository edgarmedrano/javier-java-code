package org.javier.browser;

public interface JavierListener {
	
	public void loadStateChanged(int readyState);

	public void errorLogged(String description);

	public void warningLogged(String description);

	public void commentLogged(String description);
	
	public void verboseLogged(String description);
	
	public void urlChanged(String url);
		
	public void excecutionEnded(int endCode);
	
}
