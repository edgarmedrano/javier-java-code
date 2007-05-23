/**
 * File:        Document.java
 * Description: Excecutes the VoiceXML document
 * Author:      Edgar Medrano Pérez 
 *              edgarmedrano at gmail dot com
 * Created:     2007.04.14
 * Company:     JAVIER project
 *              http://javier.sourceforge.net
 * Notes:        
 */

package org.javier.browser;

import java.util.Hashtable;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.script.Bindings;
import javax.script.Invocable;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.javier.browser.event.DocumentListener;
import org.javier.util.EscapeUnescape;
import org.javier.util.FastConcatenation;
import org.javier.util.ScriptDebugger;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * This class have three purpouses. First, to specify how to load the document
 * (see constructors for better understanding). Second, to parse the VoiceXML
 * document translating it to JavaScript. And third, to execute the script.
 * 
 * @author Edgar Medrano Pérez
 */
public class Document {
	
	/**
	 * Enumerates the document states.
	 */
	static public enum State {
		CREATED, LOADING, LOADED, PARSING, PARSED, EXECUTING, EXECUTED, ERROR
	}
	
	/**
	 * All the VoiceXML tags.
	 */
	static protected enum Tag {
		_Text, _Comment, Assign, Audio, Block, Catch, Choice, Clear, 
		Disconnect, Else, ElseIf, Enumerate, Error, Exit, Field, Filled, 
		Form, GoTo, Grammar, Help, If, Initial, Link, Log, Menu, Meta, 
		NoInput, NoMatch, Object, Option, Param, Prompt, Property, Record,
		Reprompt, Return, Script, Subdialog, Submit, Throw, Transfer, Value,
		Var, Vxml, Xml
	}
	
	/**
	 * The script engine manager that will produce all script engines used to
	 * evaluate documents.
	 */
	static protected final ScriptEngineManager sem = new ScriptEngineManager();
	
	/** Regular expresion to extract the JavaScript exception. */
	static protected final Pattern re_excep = Pattern.compile("(\\w+)?:\\s*(.*)\\s*\\(.*\\).*");
	
	/** Maps the tag names to {@link Tag} enum. */
	static protected final Hashtable<String, Tag> htTagEnum 
		= new Hashtable<String, Tag>(Tag.values().length);
	static {
		for(Tag tag: Tag.values()) {
			if(tag.toString().indexOf('_') == 0) {
				htTagEnum.put('#' + tag.toString().toLowerCase().substring(1),tag);
			} else {
				htTagEnum.put(tag.toString().toLowerCase(),tag);
			}
		}
	}	

	/** The document's state. */
	volatile private State state;
	
	/** The document's url. */
	private String url;
	
	/** The document's xml. */
	private Node xml;
	
	/** The document's JavaScript code. */
	private String js;
	
	/** The HTTP method used to get the VoiceXML document (GET/POST). */
	private String method;
	
	/** The encode type used to get the VoiceXML document. */
	private String enctype;
	
	/** The time to wait while getting the VoiceXML document before desist. */
	private int timeout;
	
	/** The maximum age (in cache) of the required VoiceXML document. */
	private int maxage;
	
	/** The maximum stale of the required VoiceXML document. */
	private int maxstale;
	
	/** The document's listeners. */
	private final Vector<DocumentListener> vecListeners = new Vector<DocumentListener>();

	/** The script engine used to evaluate the document's JavaScript code. */
	protected ScriptEngine seJavaScript;
	
	/**
	 * Default constructor rarely used.
	 */
	public Document() {
		this("");
	}

	/**
	 * Creates a document which will load it's associated VoiceXML document
	 * from the specified url. Note that the document is not loaded, actually 
	 * the document is not loaded by this class at all. 
	 * 
	 * @param url the document's url
	 */
	public Document(String url) {
		this(url,"GET","",0,0,0);
		state = State.CREATED;
		//seJavaScript = new ScriptDebugger(sem.getEngineByName("JavaScript"), false);
		seJavaScript = sem.getEngineByName("JavaScript");
	}
		
	/**
	 * Creates a document which will load it's associated VoiceXML document
	 * from the specified url and all other parameters
	 * 
	 * @param maxAge   the document's max age
	 * @param enctype  the document's encode type
	 * @param maxStale the document's max stale
	 * @param method   the HTTP method (GET/POST)
	 * @param timeout  the time to wait while getting the document before 
	 *                 desist
	 * @param url      the document's URL
	 */
	public Document(String url
			, String method
			, String enctype
			, int timeout
			, int maxAge
			, int maxStale) {
		this.url = url;
		this.method = method;
		this.enctype = enctype;
		this.timeout = timeout;
		this.maxage = maxAge;
		this.maxstale = maxStale;
		this.timeout = timeout;
		this.xml = null;
	}
	
	/**
	 * Adds a document listener.
	 * 
	 * @param l the listener object
	 */
	public void addDocumentListener(DocumentListener l) {
		vecListeners.add(l);
	}	
	
	/**
	 * End tag string representation.
	 * 
	 * @param node the node
	 * @return the end tag string representation
	 */
	protected String endTag(Node node) {
		if(node.hasChildNodes()) {
			return "/* </" + node.getNodeName() + "> */";
		}
		
		return "";
	}
	
	/**
	 * JavaScript escape.
	 * 
	 * @param src the string to be escaped
	 * @return the escaped string
	 */
	protected String escape(String src) {
		return EscapeUnescape.escape(src);
	}
	
	/**
	 * Executes the document.
	 * 
	 * @param __browser__ points to the broswer who loaded the document
	 * @return the next document to be loaded and executed
	 * @throws ScriptException if an scripting error is reached or if
	 *                         the script throws an unhandled exception
	 * @throws NoSuchMethodException if the VoiceXML document cann´t be
	 *                               parsed or has errors 
	 */
	public Document execute(Javier __browser__) 
		throws ScriptException
			, NoSuchMethodException {
		Object nextDoc = null;
		Invocable invocableEngine = (Invocable)seJavaScript;
		String jsFunction 
			= "function aDocument(x) {" 
				+ this.getJs() 
				+ "\n}";
		Bindings newBindings = seJavaScript.createBindings();
		seJavaScript.setBindings(newBindings, ScriptContext.ENGINE_SCOPE );
		seJavaScript.put("__browser__", __browser__);
		seJavaScript.put("__document__", Document.class);
		seJavaScript.eval(jsFunction);
		try {
			setState(State.EXECUTING);
			nextDoc = invocableEngine.invokeFunction("aDocument","x");
			setState(State.EXECUTED);
		} catch(ScriptException e) {
			Matcher matcher = re_excep.matcher(e.getMessage());
			if(matcher.find()) {
				if(matcher.groupCount() >= 2) {
					throw(new ScriptException(matcher.group(2).trim()));
				} 
				if(matcher.groupCount() >= 1) {
					throw(new ScriptException(matcher.group(1).trim()));
				} 
			}
			
			throw(e);
		}
		if(nextDoc instanceof String) {
			nextDoc = new Document((String) nextDoc);
		}
		
		return (Document) nextDoc;		
	}
	
	/**
	 * Propagates the "comment found" event to document's listeners.
	 * 
	 * @param description the comment found
	 */
	protected void fireCommentFound(String description) {
		for(DocumentListener l: vecListeners) {
			l.commentFound(description);
		}
	}	

	/**
	 * Propagates the "error found" event to document's listeners.
	 * 
	 * @param description the error found
	 */
	protected void fireErrorFound(String description) {
		for(DocumentListener l: vecListeners) {
			l.errorFound(description);
		}
	}
	
	/**
	 * Propagates the "state changed" event to document's listeners.
	 * 
	 * @param state the new state
	 */
	private void fireStateChanged(State state) {
		for(DocumentListener l: vecListeners) {
			l.stateChanged(state);
		}
	}

	/**
	 * Propagates the "verbose found" event to document's listeners.
	 * 
	 * @param description the description
	 */
	protected void fireVerboseFound(String description) {
		for(DocumentListener l: vecListeners) {
			l.verboseFound(description);
		}
	}
		
    /**
	 * Propagates the "warning found" event to document's listeners.
	 * 
	 * @param description the warning description
	 */
    protected void fireWarningFound(String description) {
		for(DocumentListener l: vecListeners) {
			l.warningFound(description);
		}
	}
	
    /**
	 * Gets the document's encode type.
	 * 
	 * @return the document's encode type
	 */
    public String getEnctype() {
		return enctype;
	}
	
    /**
	 * Gets the document's JavaScript code.
	 * 
	 * @return the document's script
	 */
	public String getJs() {
		if(js == "") {
			setState(State.PARSING);
			js = parse().toString();
			setState(State.PARSED);
		}
		return js;
	}

    /**
	 * Gets the document's max age.
	 * 
	 * @return the document's max age
	 */
    public int getMaxage() {
		return maxage;
	}

	/**
	 * Gets the document's max stale.
	 * 
	 * @return the document's max stale
	 */
	public int getMaxstale() {
		return maxstale;
	}

	/**
	 * Gets the HTTP method used to get the document.
	 * 
	 * @return the HTTP method (GET/POST)
	 */
	public String getMethod() {
		return method;
	}

	/**
	 * Gets the document's state.
	 * 
	 * @return the document's state
	 */
	public State getState() {
		return state;
	}

	/**
	 * Gets the VoiceXML document itself.
	 * 
	 * @return the VoiceXML document itself
	 */
	public String getText() {
		if(xml == null) {
			return "";
		}
		
		return xml.toString();
	}

	/**
	 * Gets the document's timeout.
	 * 
	 * @return the document's timeout
	 */
	public int getTimeout() {
		return timeout;
	}

	/**
	 * Gets the document's URL.
	 * 
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * Gets the VoiceXML document's root Node.
	 * 
	 * @return the VoiceXML document's root Node
	 */
	public org.w3c.dom.Node getXML() {
		return xml;
	}

	/**
	 * Parse the VoiceXML document and returns the JavaScript code 
	 * representation. {@link #setXML(Node)} must be called 
	 * before this method.
	 * 
	 * @return the JavaScript code inside a {@link FastConcatenation} object
	 */
	private FastConcatenation parse() {
    	return parse(xml);
    }

	/**
	 * Parse the node from the VoiceXML document and returns the JavaScript 
	 * code representation.
	 * 
	 * @param node the node to be parsed
	 * @return the JavaScript code inside a {@link FastConcatenation} object
	 */
	private FastConcatenation parse(Node node) {
    	return parse(node, 0);
    }
	
	/**
	 * Parse the node from the VoiceXML document and returns the JavaScript 
	 * code representation.
	 * 
	 * @return the JavaScript code inside a {@link FastConcatenation} object
	 * 
	 * @param node the node to be parsed
	 * @param level the current depth level inside the document tree, it's
	 *              used to adjust code indentation 
	 * 
	 * @return the JavaScript code inside a {@link FastConcatenation} object
	 */
	private FastConcatenation parse(Node node,int level) {
		final NodeList childs = node.getChildNodes();
		final int n = childs.getLength();
		final FastConcatenation fc = new FastConcatenation(16 * (16 * n));
		final FastConcatenation snst = new FastConcatenation(level + 1);
		Node child;
		NamedNodeMap childA;
		NodeList childN;
		int childNL;
		Node childC;
		NamedNodeMap childCA;
		Tag childTag;
		
		if(n > 0) {
			snst.push("\n");
			for(int i = 0; i < level; i++) {
				snst.push("\t");
			}
		}
		
		for(int i = 0; i < n ;i++) {
		    child = childs.item(i);
			childA = child.getAttributes();
			childN = child.getChildNodes();
			childNL = childN.getLength(); 
			childTag = htTagEnum.get(child.getNodeName());
			
			switch(childTag) {
				case _Text:
					if(!child.getNodeValue().trim().equals("")) {
						fc.push(snst, "/* "
								, child.getNodeValue().trim().replaceAll("\n", snst + "   ")
								, " */");
					}
					break;
				/*the following tags won't be parsed here*/	
				case _Comment: 
				case Catch:
				case Choice:
				case Error:
				case Filled:
				case Help:
				case NoInput:
				case NoMatch:
				    continue;
				default:
					fc.push(snst, startTag(child).replaceAll("\n",snst + "   "));
			}
			
			switch(childTag) {
				case _Comment:
					break;
				case _Text:
				    if(!child.getNodeValue().trim().equals("")) {
						fc.push(snst
								, "__browser__.addText(unescape(\""
								, escape(child.getNodeValue().trim())
								, "\"));");
					}
					break;
				case Assign:
					fc.push(snst, childA.getNamedItem("name").getNodeValue());
					if(childA.getNamedItem("expr") != null) {
						fc.push(" = ", childA.getNamedItem("expr").getNodeValue().replaceAll("\n",snst + "\t"));
					}
					fc.push(";");
					break;
				case Block:
					if(childA.getNamedItem("name") != null) {
						fc.push(snst, "case \"", childA.getNamedItem("name").getNodeValue(), "\":");
					}
					
					if(childA.getNamedItem("cond") != null) {
						fc.push(snst
								, "\tif("
								, childA.getNamedItem("cond").getNodeValue().replaceAll("\n",snst + "\t")
								, ") {"
								, this.parse(child,level + 2));
						fc.push(snst, "\t}");
					} else {
						fc.push(this.parse(child,level + 1));
					}
					
					break;
				case Clear:
				    if(childA.getNamedItem("namelist") != null) { 
						fc.push(snst, "_clear(\"", childA.getNamedItem("namelist").getNodeValue(), "\");");
					} else {
						fc.push(snst, "_clear(this.fields);");
					}
					break;
				case Disconnect:
					fc.push(snst, "_form[_form.length] = function() { throw(\"telephone.disconnect\"); } ");
					break;
				case Else:
					fc.push(snst, "} else {");
					break;
				case ElseIf:
					fc.push(snst, "} else if(");
					fc.push(childA.getNamedItem("cond").getNodeValue().replaceAll("\n",snst + "\t"));
					fc.push(") {");
					break;
				case Exit:
					if(childA.getNamedItem("expr") != null) {
						fc.push(snst, "returnValue = ");
						fc.push(childA.getNamedItem("expr").getNodeValue().replaceAll("\n",snst + "\t"));
						fc.push(";");
					}
					fc.push(snst); 
					fc.push("return \"#_int_exit\";");
					break;
				case Field:
					fc.push(snst, "case \"", childA.getNamedItem("name").getNodeValue(), "\":"); 
					fc.push(snst, "\ttry {");
					fc.push(snst, "\t\tthis."
							, childA.getNamedItem("name").getNodeValue()
							, "_count = (!this."
							, childA.getNamedItem("name").getNodeValue()
							, "_count? 0 : this."
							, childA.getNamedItem("name").getNodeValue()
							, "_count) + 1;");
					fc.push(snst, "\t\t_count = this."
							, childA.getNamedItem("name").getNodeValue()
							, "_count;"
							, this.parse(child,level + 2));
					fc.push(snst, "\t\tfilled = \"\" + "
							, "__browser__.getInput(\""
							, childA.getNamedItem("name").getNodeValue()
							, "\","
							, childA.getNamedItem("name").getNodeValue()
							, ");");
					fc.push(snst, "\t\tif(filled) {");
					fc.push(snst, "\t\t\t"
							, childA.getNamedItem("name").getNodeValue()
							, " = filled;");
					fc.push(snst, "\t\t\tthrow(\"filled\");");
					fc.push(snst, "\t\t} else {");
					fc.push(snst, "\t\t\tthrow(\"noinput\");");
					fc.push(snst, "\t\t}");
					fc.push(snst, "\t} catch(_error1) {");
					fc.push(snst, "\t\tvar _throw = false;");
					fc.push(snst, "\t\tvar _break = true;");
					fc.push(snst, "\t\twhile(_error1) {");
					fc.push(snst, "\t\t\ttry {");
					
					for(int j=0; j < childNL; j++) {
						childC = childN.item(j);
						childCA = childC.getAttributes();
						if(childC.getNodeName().equals("catch") 
							|| childC.getNodeName().equals("filled") 
							|| childC.getNodeName().equals("error") 
							|| childC.getNodeName().equals("noinput") 
							|| childC.getNodeName().equals("nomatch") 
							|| childC.getNodeName().equals("help")) {
							String eventName = childC.getNodeName();
							
							if(eventName.equals("catch")) {
								eventName = childCA.getNamedItem("event").getNodeValue();
							}
						   
							fc.push(snst, "\t\t\t\t", startTag(childC));
							fc.push(snst, "\t\t\t\tif(_error1 == \""
									, eventName
									, "\"");
							
							if(childCA.getNamedItem("cond") != null) {
								fc.push(snst, " && ("
										, childCA.getNamedItem("cond").getNodeValue().replaceAll("\n",snst + "\t")
										, ")"); 
							}	
							if(childCA.getNamedItem("count") != null) {
								fc.push(snst
										, " && _count == "
										, childCA.getNamedItem("count").getNodeValue());
							}	
							fc.push(") {"
									, this.parse(childC,level + 5));
							fc.push(snst, "\t\t\t\t\t_break = false; break;");
							fc.push(snst, "\t\t\t\t}");
							fc.push(snst, "\t\t\t\t", endTag(childC));
							fc.push(snst, "\t\t\t\telse");
						}
					}
					
					fc.push(snst, "\t\t\t\tif(_error1 == \"filled\"");
					fc.push(snst, "\t\t\t\t\t|| _error1 == \"cancel\") {"); 
					fc.push(snst, "\t\t\t\t\t/*This is the default implementation, do nothing*/");
					fc.push(snst, "\t\t\t\t\t_break = false; break;");
					fc.push(snst, "\t\t\t\t} else if(_error1 == \"help\"");
					fc.push(snst, "\t\t\t\t\t|| _error1 == \"noinput\"");
					fc.push(snst, "\t\t\t\t\t|| _error1 == \"nomatch\"");
					fc.push(snst, "\t\t\t\t\t|| _error1 == \"maxspeechtimeout\") {");
					fc.push(snst, "\t\t\t\t\t_nextitem=\""
							, childA.getNamedItem("name").getNodeValue()
							, "\"; break;");
					fc.push(snst, "\t\t\t\t} else {");
					fc.push(snst, "\t\t\t\t\t_throw = true; break;");
					fc.push(snst, "\t\t\t\t}");
					fc.push(snst, "\t\t\t} catch(_error2) {");
					fc.push(snst, "\t\t\t\t_error1 = _error2;");
					fc.push(snst, "\t\t\t}");
					fc.push(snst, "\t\t}");
					fc.push(snst, "\t\tif(_throw) {");
					fc.push(snst, "\t\t\tvar _error1_m = [];");
					fc.push(snst, "\t\t\tif(typeof(_error1) == \"string\") {");
					fc.push(snst, "\t\t\t\t_error1_m[0] = _error1;");
					fc.push(snst, "\t\t\t} else {");
					fc.push(snst, "\t\t\t\tfor(var i in _error1) {");
					fc.push(snst, "\t\t\t\t\t_error1_m.push(i + \":\" + _error1[i]);");
					fc.push(snst, "\t\t\t\t}");
					fc.push(snst, "\t\t\t}");
					fc.push(snst, "\t\t\tthrow(\"{\" + _error1_m.join(\",\") + \"}\");");
					fc.push(snst, "\t\t}");
					fc.push(snst, "\t\tif(_break) { break; }");
					fc.push(snst, "\t}");
					break;
				case Form:
				case Menu: // Menu is equivalent to form don't move this code
					fc.push(snst, "_form[_form.length] = ");
					if(childA.getNamedItem("id") != null) {
						fc.push("_form[\""
								, childA.getNamedItem("id").getNodeValue()
								, "\"] = ");
					}
					fc.push(snst, "\tfunction() {");
					StringBuffer fields = new StringBuffer();
					for(int j = 0; j < childNL; j++) {
						childC = childN.item(j);
						childCA = childC.getAttributes();
						if(childC.getNodeName().equals("field")) {
							fc.push(snst, "\t\tvar ");
							fc.push(childCA.getNamedItem("name").getNodeValue(), " = ");
							if(childCA.getNamedItem("expr") != null) {
								fc.push(childCA.getNamedItem("expr").getNodeValue().replaceAll("\n",snst + "\t"));
							} else {
								fc.push("\"\"");
							}
							fc.push(";");
							fields.append(" "); 
							fields.append(childCA.getNamedItem("name").getNodeValue());
						}
					}
					fc.push(snst, "\t\tthis.fields = \""
							, fields.toString().trim()
							, "\";");
					fc.push(snst, "\t\tfunction _clear (_namelist) {");
					fc.push(snst, "\t\t\t_namelist = _namelist.split(\" \");");
					fc.push(snst, "\t\t\tfor(var _i=0; _i < _namelist.length; _i++) {");
					for(int j=0; j < childNL; j++) {
						childC = childN.item(j);
						childCA = childC.getAttributes();
						if(childC.getNodeName().equals("field")) {
							fc.push(snst, "\t\t\t\tif(_namelist[_i] == \""
									, childCA.getNamedItem("name").getNodeValue()
									, "\") "
									, childCA.getNamedItem("name").getNodeValue()
									, " = ");
							if(childCA.getNamedItem("expr") != null) {
								fc.push(childCA.getNamedItem("expr").getNodeValue().replaceAll("\n",snst + "\t"));
							} else {
								fc.push("\"\"");
							}
							fc.push(";");
						}
					}
					fc.push(snst, "\t\t\t}");
					fc.push(snst, "\t\t}");
					
					fc.push(snst, "\t\tfunction _get(_field) { return eval(_field); }");
					fc.push(snst, "\t\t_clear(this.fields);");
					fc.push(snst, "\t\tvar _nextitem = true;");
					fc.push(snst, "\t\tthis.count = (!this.count? 0 : this.count) + 1;");
					fc.push(snst, "\t\tvar _count;");
					fc.push(snst, "\t\twhile(_nextitem != false) {");
					fc.push(snst, "\t\t\ttry {");
					fc.push(snst, "\t\t\t\t_count = this.count;");
					fc.push(snst, "\t\t\t\tswitch(_nextitem) {");
					fc.push(snst, "\t\t\t\t\tcase true: ");
					fc.push(this.parse(child,level + 5));
					
					if(childTag == Tag.Menu) {
						fc.push(snst, "\t\t\t\t\t\tfilled = \"\" + __browser__.getInput(\"type your choice\",\"\");");
						fc.push(snst, "\t\t\t\t\t\tif(filled) {");
						fc.push(snst, "\t\t\t\t\t\t\tswitch(filled) { ");
						for(int j = 0; j < childNL; j++) {
							childC = childN.item(j);
							childCA = childC.getAttributes();
							if(childC.getNodeName().equals("choice")) {
								fc.push(snst, "\t\t\t\t\t\t\t\tcase ");
								if(childCA.getNamedItem("dtmf") != null) {
									fc.push("\"");
									fc.push(childCA.getNamedItem("dtmf").getNodeValue());
									fc.push("\"");
								}
								if(childCA.getNamedItem("next") != null) {
									fc.push(": return \"");
									fc.push(childCA.getNamedItem("next").getNodeValue());
									fc.push("\";");
								}
								if(childCA.getNamedItem("expr") != null) {
									fc.push(": return ");
									fc.push(childCA.getNamedItem("expr").getNodeValue().replaceAll("\n",snst + "\t"));
									fc.push(";");
								}
							}
						}
						fc.push(snst, "\t\t\t\t\t\t\t\tdefault:");
						fc.push(snst, "\t\t\t\t\t\t\t\t\tthrow(\"nomatch\");");
						fc.push(snst, "\t\t\t\t\t\t\t}");
						fc.push(snst, "\t\t\t\t\t\t} else {");
						fc.push(snst, "\t\t\t\t\t\t\tthrow(\"noinput\");");
						fc.push(snst, "\t\t\t\t\t\t}");
					}
					
					fc.push(snst, "\t\t\t\t\t\t_nextitem = false;");
					fc.push(snst, "\t\t\t\t\t\tbreak;");
					fc.push(snst, "\t\t\t\t\tdefault:");
					fc.push(snst, "\t\t\t\t\t\tthrow(\"Unknown item '\" + _nextitem + \"'\");");
					fc.push(snst, "\t\t\t\t}");
					fc.push(snst, "\t\t\t} catch(_error1) {");
					fc.push(snst, "\t\t\t\tvar _throw = false;");
					fc.push(snst, "\t\t\t\tvar _break = true;");
					fc.push(snst, "\t\t\t\twhile(_error1) {");
					fc.push(snst, "\t\t\t\t\ttry {");
					
					for(int j=0; j < childNL; j++) {
						childC = childN.item(j);
						childCA = childC.getAttributes();
						if(childC.getNodeName().equals("catch") 
							|| childC.getNodeName().equals("filled") 
							|| childC.getNodeName().equals("error") 
							|| childC.getNodeName().equals("noinput") 
							|| childC.getNodeName().equals("nomatch") 
							|| childC.getNodeName().equals("help")) {
							String eventName = childC.getNodeName();
							
							if(eventName.equals("catch")) {
								eventName = childCA.getNamedItem("event").getNodeValue();
							}
						   
							fc.push(snst, "\t\t\t\t\t\t");
							fc.push(startTag(childC));
							fc.push(snst, "\t\t\t\t\t\tif(_error1 == \"");
							fc.push(eventName);
							fc.push("\"");
							if(childCA.getNamedItem("cond") != null) {
								fc.push(snst, " && (");
								fc.push(childCA.getNamedItem("cond").getNodeValue().replaceAll("\n",snst + "\t"));
								fc.push(")");
							}	
							if(childCA.getNamedItem("count") != null) {
								fc.push(snst, " && this.count == ");
								fc.push(childCA.getNamedItem("count").getNodeValue());
							}	
							fc.push(") {");
							fc.push(this.parse(childC,level + 7));
							fc.push(snst, "\t\t\t\t\t\t\t_break = false; break;");
							fc.push(snst, "\t\t\t\t\t\t}");
							fc.push(snst, "\t\t\t\t\t\t");
							fc.push(endTag(childC));
							fc.push(snst, "\t\t\t\t\t\telse");
						}
					}
					fc.push(snst, "\t\t\t\t\t\tif(_error1 == \"filled\"");
					fc.push(snst, "\t\t\t\t\t\t\t|| _error1 == \"cancel\") {"); 
					fc.push(snst, "\t\t\t\t\t\t\t/*This is the default implementation, do nothing*/");
					fc.push(snst, "\t\t\t\t\t\t\t_break = false; break;");
					fc.push(snst, "\t\t\t\t\t\t} else if(_error1 == \"help\"");
					fc.push(snst, "\t\t\t\t\t\t\t|| _error1 == \"noinput\"");
					fc.push(snst, "\t\t\t\t\t\t\t|| _error1 == \"nomatch\"");
					fc.push(snst, "\t\t\t\t\t\t\t|| _error1 == \"maxspeechtimeout\") {");
					fc.push(snst, "\t\t\t\t\t\t\t_nextitem=true; break;");
					fc.push(snst, "\t\t\t\t\t\t} else {");
					fc.push(snst, "\t\t\t\t\t\t\t_throw = true; break;");
					fc.push(snst, "\t\t\t\t\t\t}");
					fc.push(snst, "\t\t\t\t\t} catch(_error2) {");
					fc.push(snst, "\t\t\t\t\t\t_error1 = _error2;");
					fc.push(snst, "\t\t\t\t\t}");
					fc.push(snst, "\t\t\t\t}");

					fc.push(snst, "\t\t\t\tif(_throw) {");
					fc.push(snst, "\t\t\t\t\tvar _error1_m = [];");
					fc.push(snst, "\t\t\t\t\tif(typeof(_error1) == \"string\") {");
					fc.push(snst, "\t\t\t\t\t\t_error1_m[0] = _error1;");
					fc.push(snst, "\t\t\t\t\t} else {");
					fc.push(snst, "\t\t\t\t\t\tfor(var i in _error1) {");
					fc.push(snst, "\t\t\t\t\t\t\t_error1_m.push(i + \":\" + _error1[i]);");
					fc.push(snst, "\t\t\t\t\t\t}");
					fc.push(snst, "\t\t\t\t\t}");
					fc.push(snst, "\t\t\t\t\tthrow(\"{\" + _error1_m.join(\",\") + \"}\");");
					fc.push(snst, "\t\t\t\t}");
					fc.push(snst, "\t\t\t\tif(_break) { break; }");
					fc.push(snst, "\t\t\t}");
					fc.push(snst, "\t\t}");
					fc.push(snst, "\t\treturn \"\";");
					fc.push(snst, "\t}");
					break;
				case GoTo:
					if(childA.getNamedItem("next") != null) {
						fc.push(snst, "return \""
								, childA.getNamedItem("next").getNodeValue()
								, "\";");
					} else if(childA.getNamedItem("expr") != null) {
					    /************************************************************
						IT MUST BE:
							result.push(snst, "return ");
							result.push(childA.getNamedItem("expr").getNodeValue().replaceAll("\n",snst + "\t")));
							result.push(";");
						BUT I HAVE TO DO THIS WORKAROUND:
						*************************************************************/
						if(childA.getNamedItem("expr").getNodeValue().indexOf("#") >= 0
						  || childA.getNamedItem("expr").getNodeValue().indexOf("//") >= 0) {
							fc.push(snst, "return \""
									, childA.getNamedItem("expr").getNodeValue()
									, "\";");
						} else {
							fc.push(snst, "return "
									, childA.getNamedItem("expr").getNodeValue().replaceAll("\n",snst + "\t")
									, ";");
						}
					} else if(childA.getNamedItem("_nextitem") != null) {
						fc.push(snst, "_nextitem = \""
								, childA.getNamedItem("_nextitem").getNodeValue()
								, "\"; break;");
					} else if(childA.getNamedItem("expritem") != null) {
						fc.push(snst, "_nextitem = "
								, childA.getNamedItem("expritem").getNodeValue().replaceAll("\n",snst + "\t")
								, "; break;");
					}
					break;
					
				case Grammar:
					//*************************************************************
					//* Falta esta implementacion
					//*************************************************************
					fireWarningFound("Unsupported element: grammar");
					break;
				case If:
					fc.push(snst, "if("
							, childA.getNamedItem("cond").getNodeValue().replaceAll("\n",snst + "\t")
							, ") {");
					fc.push(this.parse(child, level + 1));
					fc.push(snst, "}");
					break;
				case Meta:
					/*
					result.push(snst, "this."
						, childA.getNamedItem("name").getNodeValue()
						, " = \""
						, childA.getNamedItem("value").getNodeValue().replaceAll("\n",snst + "\t")
						, "\";");
					*/
					break;				
				case Prompt:
					if(childA.getNamedItem("timeout") != null) {
						fireWarningFound("Unsupported attribute: timeout");
					}
					if(childA.getNamedItem("cond") != null) {
						fc.push(snst, "if("
								, childA.getNamedItem("cond").getNodeValue().replaceAll("\n",snst + "\t")
								, ") {");
					}	
					if(childA.getNamedItem("count") != null) {
						fc.push(snst, "if(_count == "
								, childA.getNamedItem("count").getNodeValue()
								, ") {");
					}
					fc.push(this.parse(child, level + 1));
					if(childA.getNamedItem("count") != null) {
						fc.push(snst, "}");
					}
					if(childA.getNamedItem("cond") != null) {
						fc.push(snst, "}");
					}
					break;
				case Property:
					/*
					result.push(snst, "setProperty(\""
						, childA.getNamedItem("name").getNodeValue()
						, "\",\""
						, childA.getNamedItem("value").getNodeValue().replaceAll("\n",snst + "\t")
						, "\");");
					*/
					break;
				case Script:
					if(child.getFirstChild() != null) {
					   fc.push(snst, ""
								, child.getFirstChild().getNodeValue());
					}
					break;
				case Submit:
					if (childA.getNamedItem("expr") != null) {
						fc.push(snst, "__expr = "
								, childA.getNamedItem("expr").getNodeValue().replaceAll("\n",snst + "\t")
								, ";");
					}
					
					fc.push(snst, "return getQuery(_get");
					
					if (childA.getNamedItem("expr") != null) {
						fc.push(",__expr");
					} else if (childA.getNamedItem("next") != null) {
						fc.push(",\""
								, childA.getNamedItem("next").getNodeValue()
								, "\"");
					} else {
						fc.push(",\"\"");
					}
					
				    if(childA.getNamedItem("namelist") != null) { 
						fc.push(",\""
								, childA.getNamedItem("namelist").getNodeValue()
								, "\"");
					} else {
						fc.push(",this.fields");
					}
					
					/*
				    if(childA.getNamedItem("method") != null) { 
						result.push(",\""
								, childA.getNamedItem("method").getNodeValue()
								, "\"");
					} else {
						result.push(",\"\"");
					}
					*/
					if(childA.getNamedItem("method") != null) {
						fireWarningFound("Unsupported attribute: method");
					}

					/*
				    if(childA.getNamedItem("enctype") != null) { 
						result.push(",\""
									, childA.getNamedItem("enctype").getNodeValue()
									, "\"");
					} else {
						result.push(",\"\"");
					}
					*/
					if(childA.getNamedItem("enctype") != null) {
						fireWarningFound("Unsupported attribute: enctype");
					}
					
					fc.push(");");
					break;
				case Throw:
					fc.push(snst, "throw(\""
							, childA.getNamedItem("event").getNodeValue()
							, "\");");
					break;
				case Value:
					if(childA.getNamedItem("class") != null) {
						fireWarningFound("Unsupported attribute: class");
					}
					if(childA.getNamedItem("mode") != null) {
						fireWarningFound("Unsupported attribute: mode");
					}
					if(childA.getNamedItem("recSrc") != null) {
						fireWarningFound("Unsupported attribute: recSrc");
					}
					if(childA.getNamedItem("expr") != null) {
						fc.push(snst, "__expr = "
								, childA.getNamedItem("expr").getNodeValue().replaceAll("\n",snst + "\t")
								, ";");
						fc.push(snst, "__browser__.addText(__expr);");
					}
					break;
				case Var:
					fc.push(snst, "var "
							, childA.getNamedItem("name").getNodeValue());
					if(childA.getNamedItem("expr") != null) {
						fc.push(" = "
								, childA.getNamedItem("expr").getNodeValue().replaceAll("\n",snst + "\t"));
					}
					fc.push(";");
					break;
				case Vxml:
					fc.push(snst, "var _next = 0;");
					fc.push(snst, "var _form = new Array();");
					fc.push(snst, "function getQuery(_get, url, namelist) {");
					fc.push(snst, "\tvar separator = url.indexOf(\"?\") >= 0 ? \"&\" : \"?\";");
					fc.push(snst, "\turl = url.split(\"#\");");
					fc.push(snst, "\tnamelist = namelist.split(\" \");");
					fc.push(snst, "\tfor(var i=0; i < namelist.length; i++) {");
					fc.push(snst, "\t\turl[0] += separator + namelist[i] + \"=\" + _get(namelist[i]);");
					fc.push(snst, "\t\tif(separator == \"?\") {");
					fc.push(snst, "\t\t\tseparator = \"&\";");
					fc.push(snst, "\t\t}");
					fc.push(snst, "\t}");
					fc.push(snst, "\treturn url.join(\"#\");");
					fc.push(snst, "}");
					fc.push(this.parse(child, level + 1));
					fc.push(snst, "_form[_form.length] = _form._int_exit = function() { return \"\"; }");
					fc.push(snst, "\twhile(_form[_next] && \"\" + _next != \"\") { ");
					fc.push(snst, "\t\tvar _newnext = _form[_next]();");
					fc.push(snst, "\t\tif(_newnext == \"\") {");
					fc.push(snst, "\t\t\tfor(_newnext = 0; _newnext < _form.length; _newnext++) {");
					fc.push(snst, "\t\t\t\tif(_form[_next] == _form[_newnext]) {");
					fc.push(snst, "\t\t\t\t\t_newnext = _newnext + 1;");
					fc.push(snst, "\t\t\t\t\tbreak;");
					fc.push(snst, "\t\t\t\t}");
					fc.push(snst, "\t\t\t}");
					fc.push(snst, "\t\t\tif(_newnext == _form.length) {");
					fc.push(snst, "\t\t\t\t_newnext = \"\";");
					fc.push(snst, "\t\t\t}");
					fc.push(snst, "\t\t}");
					fc.push(snst, "\t\t_next = _newnext;");
					fc.push(snst, "\t\tif(typeof(_next) == \"string\") {");
					fc.push(snst, "\t\t\tif(_next.indexOf(\"#\") >= 0) {");
					fc.push(snst, "\t\t\t\t_newnext = _next.split(\"#\");");
					fc.push(snst, "\t\t\t\tif(_newnext[0] == \"\" || _newnext[0] == this.url) {");
					fc.push(snst, "\t\t\t\t\t_next = _newnext[1];");
					fc.push(snst, "\t\t\t\t}");
					fc.push(snst, "\t\t\t} ");
					fc.push(snst, "\t\t}");
					fc.push(snst, "\t}");
					fc.push(snst, "\treturn _next;");
					break;
				case Xml: /*this is a workaround for IE, don't move*/
					break;
				default:
				    fireErrorFound("error.unsupported." + child.getNodeName());
			}
			
			if(child.hasChildNodes()) {
				fc.push(snst, endTag(child));
			}
		}
		
		return fc; 
	}
	
	/**
	 * Removes a document's listener.
	 * 
	 * @param l the document's listener
	 */
	public void removeDocumentListener(DocumentListener l) {
		vecListeners.remove(l);
	}
	
	/**
	 * Sets the document's state.
	 * 
	 * @param state
	 *            the state
	 */
	public void setState(State state) {
		this.state = state;
		fireStateChanged(state);
	}	
	
    /**
	 * Sets the document's XML.
	 * 
	 * @param xml the document's XML
	 */
    public void setXML(Node xml) {
		this.xml = xml;
		this.js = "";
	}	
    
    /**
	 * Start tag string representation.
	 * 
	 * @param node the node
	 * @return the start tag string representation
	 */
    protected FastConcatenation startTag(Node node) {
		int n = node.getAttributes().getLength();
		FastConcatenation stbResult = new FastConcatenation(16 * (4 + 5 * n));
		
		stbResult.push("/* <", node.getNodeName());
		
		if(n > 0) {
			for(int j = 0; j < n; j++) {
				NamedNodeMap nnm = node.getAttributes();
				stbResult.push(" ", nnm.item(j).getNodeName()
						, "=\"", nnm.item(j).getNodeValue(),"\"");
			}
		}
		
		if(!node.hasChildNodes()) {
			stbResult.push(" /");
		}
		
		stbResult.push("> */");
		
		return stbResult;
	}
}
