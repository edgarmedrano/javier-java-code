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

import java.net.URLEncoder;
import java.util.Hashtable;
import java.util.Vector;

import javax.script.Bindings;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.javier.util.FastConcatenation;
import org.javier.browser.handlers.ConsoleErrorHandler;
import org.javier.browser.handlers.ConsoleInputHandler;
import org.javier.browser.handlers.ConsoleLogHandler;
import org.javier.browser.handlers.ConsoleOutputHandler;
import org.javier.browser.handlers.ErrorHandler;
import org.javier.browser.handlers.InputHandler;
import org.javier.browser.handlers.LogHandler;
import org.javier.browser.handlers.NetworkHandler;
import org.javier.browser.handlers.NetworkListener;
import org.javier.browser.handlers.OutputHandler;
import org.javier.browser.handlers.XMLHTTPNetworkHandler;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

@SuppressWarnings("unused")
public class Javier 
	implements DocumentListener {
	static public final int END_CODE_SUCCESS = 0;
	static public final int END_CODE_ERROR = 1;
	
	protected final ScriptEngineManager sem = new ScriptEngineManager();
	protected ScriptEngine seJavaScript;
	private final Vector<JavierListener> vecListeners = new Vector<JavierListener>();
	private final InputHandler __input__;
	private final OutputHandler __output__;
	private final ErrorHandler __error__;
	private final NetworkHandler __network__;
	private final LogHandler __log__;
	private int maxlogLevel = LogHandler.VERBOSE ; // 0 to 4 = none to verbose
	public Document document = new Document();
	protected boolean autoEval = true;
	
	Javier(InputHandler __input__
			,OutputHandler __output__
			,ErrorHandler __error__
			,NetworkHandler __network__
			,LogHandler __log__
			,String jsEngineName) {
		final Javier _this = this;
		
		this.__input__ = __input__;
		this.__output__ = __output__;
		this.__error__ = __error__;
		this.__network__ = __network__;
		this.__log__ = __log__;
		
		seJavaScript = sem.getEngineByName(jsEngineName);
		__network__.addNetworkListener(new NetworkListener() {
				public void readyStateChanged(int readyState) {
					fireLoadStateChanged(readyState);
				}
	
				public void requestCompleted(Object result) {
					process(result);
				}
			});
	}
	
	Javier(InputHandler __input__
			,OutputHandler __output__
			,ErrorHandler __error__
			,NetworkHandler __network__
			,LogHandler __log__) {
		this(__input__
				,__output__
				,__error__
				,__network__
				,__log__
				,"JavaScript");
	}

	public void addJavierListener(JavierListener l) {
		vecListeners.add(l);
	}
	
	public void removeJavierListener(JavierListener l) {
		vecListeners.remove(l);
	}
	
	protected void fireLoadStateChanged(int readyState) {
		for(JavierListener l: vecListeners) {
			l.loadStateChanged(readyState);
		}
	}
	
	protected void fireErrorLogged(String description) {
		for(JavierListener l: vecListeners) {
			l.errorLogged(description);
		}
	}
	
	protected void fireWarningLogged(String description) {
		for(JavierListener l: vecListeners) {
			l.warningLogged(description);
		}
	}
	
	protected void fireCommentLogged(String description) {
		for(JavierListener l: vecListeners) {
			l.commentLogged(description);
		}
	}
	
	protected void fireVerboseLogged(String description) {
		for(JavierListener l: vecListeners) {
			l.verboseLogged(description);
		}
	}
	
	protected void fireUrlChanged(String url) {
		for(JavierListener l: vecListeners) {
			l.urlChanged(url);
		}
	}
	
	protected void fireExcecutionEnded(int endCode) {
		for(JavierListener l: vecListeners) {
			l.excecutionEnded(endCode);
		}
	}
	
	public void log(Object source, String text,int level) {
		if(level <= maxlogLevel) {
			__log__.writeln(source.getClass().getName() + ": " + text);
		}
	}
	
	
	public void error(Object source, String message) {
		log(source,message,LogHandler.ERROR);
		__error__.writeln(source.getClass().getName() + ": " + message);
		fireErrorLogged(message);
	}
    
	public void warning(Object source, String message) {
		log(source,message,LogHandler.WARNING);
		fireWarningLogged(message);
	}
    
	public void comment(Object source, String message) {
		log(source,message,LogHandler.COMMENT);
		fireCommentLogged(message);
	}
	
	public void verbose(Object source, String message) {
		log(source,message,LogHandler.VERBOSE);
		fireVerboseLogged(message);
	}

	public boolean load(String docURL) {
		return load(new Document(docURL));
	}
	
	public boolean load(Document docRef) {
		if(!docRef.getUrl().equals("")) {
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
			this.end(0);
		}
		
		return false;
	}

	private void process(Object result) {
		if(result != null) {
			if(result instanceof Node) {
				Node xml = (Node) result;
				try {
					document.setXML(xml);
					if(autoEval) {
						run(document);
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
	
	
	protected void run(Document docRef) {
		try {
			document = execute(docRef);
		} catch(VXMLException e) {
			if(e.getMessage().equals("error")
				|| e.getMessage().equals("exit")
				|| e.getMessage().equals("telephone.disconnect")) {
				end(END_CODE_SUCCESS);
			} else {
				error(this,"Error: " + e.getMessage());
				end(END_CODE_ERROR);			
			}
		} catch(Exception e) {
			error(this,"Error: " + e.getClass().getName() + " " + e.getMessage());
			end(END_CODE_ERROR);			
		}
	}
	
	private Document execute(Document docRef) throws VXMLException {
		Object nextDoc = null;
		try {
			Bindings newBindings = seJavaScript.createBindings();
			newBindings.put("__browser__", this);
			newBindings.put("__document__", Document.class);
			nextDoc = seJavaScript.eval(docRef.getJs(),newBindings);
			if(nextDoc instanceof String) {
				nextDoc = new Document((String) nextDoc);
			}
		} catch (ScriptException ex) {
			ex.printStackTrace();
		}
		
		return (Document) nextDoc;
	}

	void end(int endCode) {
		__output__.waitUntilDone();
		fireExcecutionEnded(endCode);
	}
	
	public String getInput(String text) {
		String result = __input__.getInput(text);
		__output__.clearText();
		return result;
	}
	
	public void addText(String text) {
		__output__.addText(text);
	}
	
	public void clearText() {
		__output__.clearText();
	}

	public void commentFound(String description) {
		comment(document,description);
	}

	public void errorFound(String description) {
		error(document,description);
	}

	public void verboseFound(String description) {
		verbose(document,description);
	}

	public void warningFound(String description) {
		warning(document,description);
	}

	public boolean isAutoEval() {
		return autoEval;
	}

	public void setAutoEval(boolean autoEval) {
		this.autoEval = autoEval;
	}
	
	public static void main(String[] argv) {
		Javier javier;
		int exitCode;
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
			,new ConsoleOutputHandler(strVoice)
			,new ConsoleErrorHandler()
			,new XMLHTTPNetworkHandler() 
			,new ConsoleLogHandler());
		
		javier.load(strAppURL);
	}
}
