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
import java.util.Vector;
import java.util.concurrent.TimeUnit;

import org.javier.browser.Document.State;
import org.javier.browser.handlers.ConsoleErrorHandler;
import org.javier.browser.handlers.ConsoleInputHandler;
import org.javier.browser.handlers.ConsoleLogHandler;
import org.javier.browser.handlers.ConsoleOutputHandler;
import org.javier.browser.handlers.InputHandler;
import org.javier.browser.handlers.MSXMLHTTPNetworkHandler;
import org.javier.browser.handlers.NetworkHandler;
import org.javier.browser.handlers.NetworkListener;
import org.javier.browser.handlers.SAPIOutputHandler;
import org.javier.browser.handlers.StreamLogHandler;
import org.w3c.dom.Node;


public class Javier 
	implements DocumentListener {
	static public final int END_CODE_SUCCESS = 0;
	static public final int END_CODE_ERROR = 1;
	
	public static void main(String[] argv) {
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
	private final Vector<JavierListener> vecJavierLs= new Vector<JavierListener>();
	private final Vector<OutputListener> vecOutputLs = new Vector<OutputListener>();
	private final Vector<ErrorListener> vecErrorLs = new Vector<ErrorListener>();
	private final Vector<LogListener> vecLogLs = new Vector<LogListener>();
	private final InputHandler __input__;
	private final NetworkHandler __network__;
	private int maxlogLevel = LogListener.WARNING ; // 0 to 4 = none to verbose
	public Document document = new Document();
	
	protected boolean autoEval = true;
	private int endCode;
	
	Javier(InputHandler __input__
			,NetworkHandler __network__) {
		this(__input__
				,__network__
				,"JavaScript");
	}

	Javier(InputHandler __input__
			,NetworkHandler __network__
			,String jsEngineName) {
		this.__input__ = __input__;
		this.__network__ = __network__;
		
		__network__.addNetworkListener(new NetworkListener() {
				public void readyStateChanged(int readyState) {
					fireLoadStateChanged(readyState);
				}
	
				public void requestCompleted(Object result) {
					process(result);
				}
			});
	}
	
	public void addErrorListener(ErrorListener l) {
		vecErrorLs.add(l);
	}

	public void addJavierListener(JavierListener l) {
		vecJavierLs.add(l);
	}

	public void addLogListener(LogListener l) {
		vecLogLs.add(l);
	}

	public void addOutputListener(OutputListener l) {
		vecOutputLs.add(l);
	}
	
	public void addText(String text) {
		fireOuputAddText(text);
	}
	
	public void clearText() {
		fireOuputClearText();
	}
	
	public void comment(Object source, String message) {
		log(source,message,LogListener.COMMENT);
	}
	
	public void commentFound(String description) {
		comment(document,description);
	}
	
	public void end(int endCode) {
		if(endCode != END_CODE_SUCCESS) {
			document.setState(State.ERROR);
		}
		this.endCode = endCode;
		fireOuputWaitUntilDone();
		fireExcecutionEnded(endCode);
	}

	public void error(Object source, String message) {
		document.setState(State.ERROR);
		fireErrorFound(source.getClass().getName() + ": " + message);
		log(source,message,LogListener.ERROR);
	}
	
	public void errorFound(String description) {
		error(document,description);
	}
	
	protected void fireErrorFound(String description) {
		for(ErrorListener l: vecErrorLs) {
			l.errorFound(description);
		}
	}

	protected void fireExcecutionEnded(int endCode) {
		for(JavierListener l: vecJavierLs) {
			l.excecutionEnded(endCode);
		}
	}
	
	protected void fireLoadStateChanged(int readyState) {
		for(JavierListener l: vecJavierLs) {
			l.loadStateChanged(readyState);
		}
	}
	
	protected void fireLogReported(String description, int level) {
		for(LogListener l: vecLogLs) {
			l.logReported(description, level);
		}
	}
	
	protected void fireOuputAddText(String text) {
		for(OutputListener l: vecOutputLs) {
			l.addText(text);
		}
	}
	
	protected void fireOuputClearText() {
		for(OutputListener l: vecOutputLs) {
			l.clearText();
		}
	}
    
	protected void fireOuputWaitUntilDone() {
		for(OutputListener l: vecOutputLs) {
			l.waitUntilDone();
		}
	}
    
	protected void fireUrlChanged(String url) {
		for(JavierListener l: vecJavierLs) {
			l.urlChanged(url);
		}
	}
	
	public String getInput(String text) {
		return getInput(text,"");
	}

	public String getInput(String text,String value) {
		String result = __input__.getInput(text,value);
		clearText();
		return result;
	}
	
	public boolean isAutoEval() {
		return autoEval;
	}

	public boolean load(Document docRef) {
		if(!docRef.getUrl().equals("")) {
			document.setState(State.LOADING);
			document = docRef;
			document.addDocumentListener(this);
			fireUrlChanged(document.getUrl());
			try {
				return __network__.loadXML(document);
			} catch(Exception e) {
				error(this,"Unable to load document: " + document.getUrl()); 
			}
			
			return false;
		} else {
			this.end(END_CODE_SUCCESS);
		}
		
		return false;
	}
	
	public boolean load(String docURL) {
		return load(new Document(docURL));
	}

	public void log(Object source, String text,int level) {
		if(level <= maxlogLevel) {
			fireLogReported(source.getClass().getName() + ": " + text, level);
		}
	}

	public int mainLoop() {
		for(;;) {
			if(document.getState() == State.CREATED) {
				load(document);
			}
			try {
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
		return endCode;
	}
	
	public int mainLoop(Document homeDocument) {
		this.document = homeDocument;
		return mainLoop();
	}
	
	public int mainLoop(String homeURL) {
		return mainLoop(new Document(homeURL));
	}
	
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
					e.printStackTrace(System.out);
					this.error(this,"Execution error: " + e.getClass().getName() + " " + e.getMessage());
					this.end(END_CODE_ERROR);
				}
			} else {
				this.error(this,"Execution error: " + result);
				this.end(1);
			}
		} else {
			this.error(this,"Execution error: " + __network__.getClass().getName() + " was unable to load page");
			this.end(END_CODE_ERROR);
		}
	}
	
	public void removeErrorListener(ErrorListener l) {
		vecErrorLs.remove(l);
	}
	
	public void removeJavierListener(JavierListener l) {
		vecJavierLs.remove(l);
	}
	
	public void removeLogListener(LogListener l) {
		vecLogLs.remove(l);
	}

	public void removeOutputListener(OutputListener l) {
		vecOutputLs.remove(l);
	}

	protected void run() {
		try {
			document = document.execute(this);
		} catch(Exception e) {
			e.printStackTrace();
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

	public void setAutoEval(boolean autoEval) {
		this.autoEval = autoEval;
	}

	public void stateChanged(State state) {
		//TODO: Wake up thread
	}

	public void verbose(Object source, String message) {
		log(source,message,LogListener.VERBOSE);
	}
	
	public void verboseFound(String description) {
		verbose(document,description);
	}

	public void warning(Object source, String message) {
		log(source,message,LogListener.WARNING);
	}
	
	public void warningFound(String description) {
		warning(document,description);
	}
}
