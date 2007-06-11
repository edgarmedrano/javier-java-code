/**
 * File:        Javier.java
 * Description: JAvascript Voicexml InterpretER
 *              Java version
 * Author:      Edgar Medrano Pérez 
 *              edgarmedrano at gmail dot com
 * Created:     2007.04.12
 * Company:     JAVIER project
 *              http://javier.sourceforge.net
 * Notes:       
 */

package org.javier.browser;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import java.util.Vector;
import java.util.concurrent.TimeUnit;

import org.javier.browser.Document.State;
import org.javier.browser.event.DocumentListener;
import org.javier.browser.event.ErrorListener;
import org.javier.browser.event.JavierListener;
import org.javier.browser.event.LogListener;
import org.javier.browser.event.NetworkListener;
import org.javier.browser.event.OutputListener;
import org.javier.browser.handlers.ConsoleErrorHandler;
import org.javier.browser.handlers.ConsoleInputHandler;
import org.javier.browser.handlers.ConsoleLogHandler;
import org.javier.browser.handlers.ConsoleOutputHandler;
import org.javier.browser.handlers.InputHandler;
import org.javier.browser.handlers.MSXMLHTTPNetworkHandler;
import org.javier.browser.handlers.NetworkHandler;
import org.javier.browser.handlers.SAPIOutputHandler;
import org.javier.browser.handlers.StreamLogHandler;
import org.w3c.dom.Node;


/**
 * The JAvascript Voicexml InterpretER, this is actually the VoiceXML
 * browser.
 */
public class Javier 
	implements DocumentListener {
	
	/** Signals a successfully ended interpretation. */
	static public final int END_CODE_SUCCESS = 0;
	
	/** Signals a erroneously ended interpretation. */
	static public final int END_CODE_ERROR = 1;
	
	/**
	 * The main method.
	 * 
	 * @param argv the argv
	 * @throws IOException 
	 */
	public static void main(String[] argv) throws IOException {
		final Javier javier;
		String strAppURL = "http://localhost/javier/default.vxml";
		String strVoice = "";
		
		if(argv.length > 0) {
			strAppURL = argv[0];
		}
		
		if(argv.length > 1) {
			strVoice = argv[1];
		}
		
		javier = new Javier
			(new ConsoleInputHandler()
			,new MSXMLHTTPNetworkHandler());
		
		javier.addOutputListener(new ConsoleOutputHandler());
		javier.addOutputListener(new SAPIOutputHandler(strVoice));
		javier.addErrorListener(new ConsoleErrorHandler());
		javier.addLogListener(new ConsoleLogHandler());
		try {
			javier.addLogListener(new StreamLogHandler("Javier.log"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		javier.addJavierListener(new JavierListener() {
				public void excecutionEnded(int endCode) {
					System.out.println("Done!");
					System.exit(endCode);
				}
				public void loadStateChanged(int readyState) {
					String advance = "";
					
					for(int i = 1; i < readyState - 1; i++) {
						advance += "\b\b\b"; 
					}
					
					for(int i = 1; i < readyState; i++) {
						advance += "|||"; 
					}
					
					System.out.print(advance);
				}
				public void urlChanged(String url) {
					System.out.println("Loading: " + url);
				}
			});
		javier.addErrorListener(new ErrorListener() {
				public void errorFound(String description) {
					System.out.println(javier.document.getJs());
				}
			});
		
		javier.mainLoop(strAppURL);
	}
	
	/** Javier listeners. */
	private final Vector<JavierListener> vecJavierLs= new Vector<JavierListener>();
	
	/** Output listeners. */
	private final Vector<OutputListener> vecOutputLs = new Vector<OutputListener>();
	
	/** Error listeners. */
	private final Vector<ErrorListener> vecErrorLs = new Vector<ErrorListener>();
	
	/** Log listeners. */
	private final Vector<LogListener> vecLogLs = new Vector<LogListener>();
	
	/** The input handler. */
	private final InputHandler inHandler;
	
	/** The network handler. */
	private final NetworkHandler netHandler;
	
	/** 
	 * The max log level to log. The default is {@link LogListener.WARNING} 
	 * @see LogListener 
	 */
	private int maxlogLevel = LogListener.WARNING ;
	
	/** The current document. */
	public Document document = new Document();
	
	/** Enables/Disables automatic document evaluation. */
	protected boolean autoEval = true;
	
	/** The execution end code. */
	protected int endCode;
	
	/** Use this to break the main loop. */
	protected boolean exitMainLoop = false;
	
	/** The properties. */
	protected Properties properties;	
	
	/**
	 * Creates a browser without output interaction.
	 * 
	 * @param netHandler the network handler
	 * @param inHandler  the input handler
	 */
	public Javier(InputHandler inHandler, NetworkHandler netHandler) {
		this(inHandler, netHandler, "JavaScript");
	}

	/**
	 * Creates a browser without output interaction. It also admits a
	 * custom script engine name.  
	 * 
	 * @param netHandler the network handler
	 * @param jsEngineName the javascript engine name used to evaluate 
	 *                     documents
	 * @param inHandler  the input handler
	 */
	Javier(InputHandler inHandler
			,NetworkHandler netHandler
			,String jsEngineName) {
		properties = new Properties();
		this.inHandler = inHandler;
		this.netHandler = netHandler;
		
		netHandler.addNetworkListener(new NetworkListener() {
				public void readyStateChanged(int readyState) {
					fireLoadStateChanged(readyState);
				}
	
				public void requestCompleted(Object result) {
					process(result);
				}
			});
	}
	
	/**
	 * Adds an error listener.
	 * 
	 * @param l the error listener
	 */
	public void addErrorListener(ErrorListener l) {
		vecErrorLs.add(l);
	}

	/**
	 * Adds a javier listener.
	 * 
	 * @param l the javier listener
	 */
	public void addJavierListener(JavierListener l) {
		vecJavierLs.add(l);
	}

	/**
	 * Adds a log listener.
	 * 
	 * @param l the log listener
	 */
	public void addLogListener(LogListener l) {
		vecLogLs.add(l);
	}

	/**
	 * Adds an output listener.
	 * 
	 * @param l the output listener
	 */
	public void addOutputListener(OutputListener l) {
		vecOutputLs.add(l);
	}
	
	/**
	 * Output additional text. This text could be syntethized by a TTS engine
	 * or it could be showed on a screen. 
	 * 
	 * @param text the text
	 * @throws IOException 
	 */
	public void addText(String text) throws IOException {
		fireOuputAddText(text);
	}
	
	/**
	 * Clear text. Stops syntethization or screen output of text.
	 * @throws IOException 
	 */
	public void clearText() throws IOException {
		fireOuputClearText();
	}
	
	/**
	 * Reports a comment. This is intended to be called while parsing
	 * or evaluating the document's code.
	 * 
	 * @param message the comment
	 * @param source  the comment's source
	 */
	public void comment(Object source, String message) {
		log(source,message,LogListener.COMMENT);
	}
	
	/* (non-Javadoc)
	 * @see org.javier.browser.DocumentListener#commentFound(java.lang.String)
	 */
	public void commentFound(String description) {
		comment(document,description);
	}
	
	/**
	 * This method make all the proper calls before ending 
	 * the document's evaluation. 
	 * 
	 * @param endCode the end code
	 * @throws IOException 
	 */
	public void end(int endCode) {
		if(endCode != END_CODE_SUCCESS) {
			document.setState(State.ERROR);
		}
		this.endCode = endCode;
		try {
			fireOuputWaitUntilDone();
		} catch (IOException e) {
			e.printStackTrace();
		}
		fireExcecutionEnded(endCode);
		exitMainLoop = true;
	}

	/**
	 * Reports an error message. This is intended to be called while parsing
	 * or evaluating the document's code.
	 * 
	 * @param message the message
	 * @param source  the error's source
	 */
	public void error(Object source, String message) {
		document.setState(State.ERROR);
		fireErrorFound(source.getClass().getName() + ": " + message);
		log(source,message,LogListener.ERROR);
	}
	
	/* (non-Javadoc)
	 * @see org.javier.browser.DocumentListener#errorFound(java.lang.String)
	 */
	public void errorFound(String description) {
		error(document,description);
	}
	
	/**
	 * Propagates the "error found" event to javier's listeners
	 * 
	 * @param description the error's description
	 */
	protected void fireErrorFound(String description) {
		for(ErrorListener l: vecErrorLs) {
			l.errorFound(description);
		}
	}

	/**
	 * Propagates the "excecution ended" event to javier's listeners.
	 * 
	 * @param endCode the end code
	 */
	protected void fireExcecutionEnded(int endCode) {
		for(JavierListener l: vecJavierLs) {
			l.excecutionEnded(endCode);
		}
	}
	
	/**
	 * Propagates the "load state changed" event to javier's listeners.
	 * 
	 * @param readyState the new ready state
	 */
	protected void fireLoadStateChanged(int readyState) {
		for(JavierListener l: vecJavierLs) {
			l.loadStateChanged(readyState);
		}
	}
	
	/**
	 * Propagates the "log reported" event to javier's listeners.
	 * 
	 * @param level        the log level
	 * @param description  the text to be logged
	 * @see LogListener
	 */
	protected void fireLogReported(String description, int level) {
		for(LogListener l: vecLogLs) {
			l.logReported(description, level);
		}
	}
	
	/**
	 * Propagates the "output add text" event to javier's listeners.
	 * 
	 * @param text the text
	 * @throws IOException 
	 */
	protected void fireOuputAddText(String text) throws IOException {
		for(OutputListener l: vecOutputLs) {
			l.addText(text);
		}
	}
	
	/**
	 * Propagates the "ouput clear text" event to javier's listeners.
	 * @throws IOException 
	 */
	protected void fireOuputClearText() throws IOException {
		for(OutputListener l: vecOutputLs) {
			l.clearText();
		}
	}
    
	/**
	 * Propagates the "ouput wait until done" event to javier's listeners.
	 * @throws IOException 
	 */
	protected void fireOuputWaitUntilDone() throws IOException {
		for(OutputListener l: vecOutputLs) {
			l.waitUntilDone();
		}
	}
    
	/**
	 * Propagates the "url changed" event to javier's listeners.
	 * 
	 * @param url the new url
	 */
	protected void fireUrlChanged(String url) {
		for(JavierListener l: vecJavierLs) {
			l.urlChanged(url);
		}
	}
	
	/**
	 * Gets the input.
	 * 
	 * @param text the text used to prompt
	 * @return the captured input
	 * @throws IOException 
	 */
	public String getInput(String text) throws IOException {
		return getInput(text,"","","",false);
	}

	/**
	 * Gets the input. This is intended to be called while evaluating
	 * the document's code.
	 * 
	 * @param text the text to be used while prompting
	 * @param value the default value
	 * @return the captured input
	 * @throws IOException 
	 */
	public String getInput(String text,String value, String type, String slot, boolean modal) throws IOException {
		String result = inHandler.getInput(text,value, type, slot, modal);
		clearText();
		return result;
	}
	
	/**
	 * Checks if auto evaluation is turned on.
	 * 
	 * @return <code>true</code>, if auto evaluation is turned on
	 */
	public boolean isAutoEval() {
		return autoEval;
	}

	/**
	 * Load the specified document.
	 * 
	 * @param docRef the document
	 * @return <code>true</code>, if load is successfully set
	 * @throws IOException 
	 */
	public boolean load(Document docRef) throws IOException {
		if(!docRef.getUrl().equals("")) {
			document.setState(State.LOADING);
			document = docRef;
			document.addDocumentListener(this);
			fireUrlChanged(document.getUrl());
			try {
				return netHandler.loadXML(document);
			} catch(Exception e) {
				error(this,"Unable to load document: " + document.getUrl()); 
			}
			
			return false;
		} else {
			this.end(END_CODE_SUCCESS);
		}
		
		return false;
	}
	
	/**
	 * Loads a document from the specified URL.
	 * 
	 * @param docURL the document's URL
	 * 
	 * @return <code>true</code>, if load is successfully set
	 * @throws IOException 
	 */
	public boolean load(String docURL) throws IOException {
		return load(new Document(docURL));
	}

	/**
	 * Log a text message.
	 * 
	 * @param text the text message
	 * @param level the level
	 * @param source the object reporting the text message
	 * @see LogListener           
	 */
	public void log(Object source, String text,int level) {
		if(level <= maxlogLevel) {
			fireLogReported(source.getClass().getName() + ": " + text, level);
		}
	}

	/**
	 * This is the main loop, which starts the VoiceXML document 
	 * interpretation. It will end when the last document do not 
	 * return a document or the disconnect instruction is evaluated.
	 * The {@link #document} field must be set before calling this method.
	 * 
	 * @return the end code
	 * @throws IOException 
	 * @see #end(int)
	 */
	public int mainLoop() throws IOException {
		exitMainLoop = false;
		while(!exitMainLoop) {
			if(document.getState() == State.CREATED) {
				load(document);
			}
			
			try {
				//System.out.println("MAIN");
				TimeUnit.MILLISECONDS.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			if(document.getUrl().equals("")) {
				break;
			}
			if(document.getState() == State.ERROR) {
				break;
			}
		}
		//System.out.println("out of main loop");
		return endCode;
	}
	
	/**
	 * This is the main loop, which starts the VoiceXML document 
	 * interpretation. It will end when the last document do not 
	 * return a document or the disconnect instruction is evaluated.
	 * 
	 * @param home the home document
	 * 
	 * @return the end code
	 * @throws IOException 
	 * @see #end(int)
	 */
	public int mainLoop(Document home) throws IOException {
		this.document = home;
		return mainLoop();
	}
	
	/**
	 * This is the main loop, which starts the VoiceXML document 
	 * interpretation. It will end when the last document do not 
	 * return a document or the disconnect instruction is evaluated.
	 * 
	 * @param homeURL the home document's URL
	 * 
	 * @return the end code
	 * @throws IOException 
	 * @see #end(int)
	 */
	public int mainLoop(String homeURL) throws IOException {
		return mainLoop(new Document(homeURL));
	}
	
	/**
	 * Process.
	 * 
	 * @param result the result
	 * @throws IOException 
	 */
	private void process(Object result) {
		document.setState(State.LOADED);
		if(result != null) {
			if(result instanceof Node) {
				Node xml = (Node) result;
				try {
					document.setXML(xml);
					if(autoEval) {
						run();
					}
				} catch(Exception e) {
					e.printStackTrace();
					this.error(this,"Execution error: " + e.getClass().getName() + " " + e.getMessage());
					this.end(END_CODE_ERROR);
				}
			} else {
				this.error(this,"Execution error: " + result);
				this.end(1);
			}
		} else {
			this.error(this,"Execution error: " + netHandler.getClass().getName() + " was unable to load page");
			this.end(END_CODE_ERROR);
		}
	}
	
	/**
	 * Removes an error listener.
	 * 
	 * @param l the listener to be removed
	 */
	public void removeErrorListener(ErrorListener l) {
		vecErrorLs.remove(l);
	}
	
	/**
	 * Removes a javier listener.
	 * 
	 * @param l the listener to be removed
	 */
	public void removeJavierListener(JavierListener l) {
		vecJavierLs.remove(l);
	}
	
	/**
	 * Removes a log listener.
	 * 
	 * @param l the listener to be removed
	 */
	public void removeLogListener(LogListener l) {
		vecLogLs.remove(l);
	}

	/**
	 * Removes an output listener.
	 * 
	 * @param l the listener to be removed
	 */
	public void removeOutputListener(OutputListener l) {
		vecOutputLs.remove(l);
	}

	/**
	 * Executes the current document. If {@link #autoEval} is 
	 * <code>false</code>, you must call this method to execute the document. 
	 * @throws IOException 
	 * 
	 * @see #document
	 */
	protected void run() throws IOException {
		try {
			document = document.execute(this);
		} catch(Exception e) {
			if(e.getMessage().equals("error")
				|| e.getMessage().equals("exit")
				|| e.getMessage().equals("telephone.disconnect")) {
				end(END_CODE_SUCCESS);
			} else {
				error(this,"Error: " + e.getClass().getName() + " " + e.getMessage());
				end(END_CODE_ERROR);			
			}
		}
	}

	/**
	 * Sets the auto evaluation flag. If autoEval is <code>true</code> the 
	 * document will be executed as soon as it's loaded. 
	 * 
	 * @param autoEval the auto evaluation flag
	 */
	public void setAutoEval(boolean autoEval) {
		this.autoEval = autoEval;
	}

	/* (non-Javadoc)
	 * @see org.javier.browser.DocumentListener#stateChanged(org.javier.browser.Document.State)
	 */
	public void stateChanged(State state) {
		// do nothing
	}

	/**
	 * Reports a verbose message. This is intended to be called while parsing
	 * or evaluating the document's code.
	 * 
	 * @param message the verbose message
	 * @param source  the object reporting the verbose message
	 */
	public void verbose(Object source, String message) {
		log(source,message,LogListener.VERBOSE);
	}
	
	/* (non-Javadoc)
	 * @see org.javier.browser.DocumentListener#verboseFound(java.lang.String)
	 */
	public void verboseFound(String description) {
		verbose(document,description);
	}

	/**
	 * Reports a warning message. This is intended to be called while parsing
	 * or evaluating the document's code.
	 * 
	 * @param message the warning message
	 * @param source  the object reporting the warning message
	 */
	public void warning(Object source, String message) {
		log(source,message,LogListener.WARNING);
	}
	
	/* (non-Javadoc)
	 * @see org.javier.browser.DocumentListener#warningFound(java.lang.String)
	 */
	public void warningFound(String description) {
		warning(document,description);
	}

	/**
	 * Stops the document execution and exits main loop.
	 */
	public void stop() {
		if(document.getState() == State.LOADING) {
			netHandler.abort();
		} 
		exitMainLoop = true;
	}
	

	/**
	 * Gets the property.
	 * 
	 * @param name the name
	 * 
	 * @return the property
	 */
	public String getProperty(String name) {
		String result = "";
		if(document !=  null) {
			result = document.getProperty(name); 
		} 
		
		if(result.equals("")) {
			result = properties.getProperty(name, "");
		}
		
		return result;
	}

	/**
	 * Sets the property.
	 * 
	 * @param name  the name
	 * @param value the value
	 */
	public void setProperty(String name, String value) {
		properties.setProperty(name, value);
	}	
}
