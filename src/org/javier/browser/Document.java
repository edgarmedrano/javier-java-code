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

import java.io.InputStreamReader;
import java.util.Hashtable;
import java.util.Properties;
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
		Data, Disconnect, Else, ElseIf, Enumerate, Error, Exit, Field, Filled, 
		Foreach, Form, GoTo, Grammar, Help, If, Initial, Link, Log, Mark, Menu, 
		Meta, NoInput, NoMatch, Object, Option, Param, Prompt, Property, Record,
		Reprompt, Return, Script, Subdialog, Submit, Throw, Transfer, Value,
		Var, Vxml, Xml
	}

	static public enum Type {
		Boolean, Date, Digits, Currency, Number, Phone, Time, Custom
	}
	
	/** Maps the tag names to {@link Tag} enum. */
	static protected final Hashtable<String, Type> htTypeEnum 
		= new Hashtable<String, Type>(Type.values().length);
	static {
		for(Type type: Type.values()) {
			htTypeEnum.put(type.toString().toLowerCase(),type);
		}
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
	
	/** The properties. */
	protected Properties properties;
	
	/** The meta-tags. */
	protected Properties meta;
	
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
		seJavaScript = new ScriptDebugger(sem.getEngineByName("JavaScript"), true);
		//seJavaScript = sem.getEngineByName("JavaScript");
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
		properties = new Properties();
		meta = new Properties();
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
		
		try {
			seJavaScript.eval(new InputStreamReader(Document.class.getResourceAsStream("document.js")));
		} catch (ScriptException e) {
			e.printStackTrace();
		}
		
		seJavaScript.put("__browser__", __browser__);
		seJavaScript.put("__document__", this);
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
				case Grammar:
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
					
					if(childA.getNamedItem("cond") != null) {
						fc.push(snst, "\tif("
								, childA.getNamedItem("cond").getNodeValue().replaceAll("\n",snst + "\t")
								, ") {");
					}	
					
					fc.push(snst, "\ttry {");
					
					if(childA.getNamedItem("modal") != null 
							&& childA.getNamedItem("modal").getNodeValue().equalsIgnoreCase("true")) {
						fc.push(snst, "\t\tthis."
								, childA.getNamedItem("name").getNodeValue()
								, "_grammars = new Array("
								, collectGrammars(child)
								, ");");
					} else {
						fc.push(snst, "\t\tthis."
								, childA.getNamedItem("name").getNodeValue()
								, "_grammars = this.grammars.slice(0);");
						fc.push(snst, "\t\tthis."
								, childA.getNamedItem("name").getNodeValue()
								, "_grammars.push("
								, collectGrammars(child)
								, ");");
					}
										
					if(childA.getNamedItem("type") != null) {
						Properties typeProperties = parseType(childA.getNamedItem("type").getNodeValue());
						String min = typeProperties.getProperty("minlength", "");
						String max = typeProperties.getProperty("maxlength", "");
						if(min == "") {
							fc.push(snst, "\t\tthis."
									, childA.getNamedItem("name").getNodeValue()
									, "_min = __grammar_length(this."
									, childA.getNamedItem("name").getNodeValue()
									, "_grammars, 0, __browser__.getProperty(\"inputmode\"));");
						} else {
							fc.push(snst, "\t\tthis."
									, childA.getNamedItem("name").getNodeValue()
									, "_min = "
									, min
									, ";");							
						}
						if(max == "") {
							fc.push(snst, "\t\tthis."
									, childA.getNamedItem("name").getNodeValue()
									, "_max = __grammar_length(this."
									, childA.getNamedItem("name").getNodeValue()
									, "_grammars, 10, __browser__.getProperty(\"inputmode\"));");
						} else {
							fc.push(snst, "\t\tthis."
									, childA.getNamedItem("name").getNodeValue()
									, "_max = "
									, max
									, ";");							
						}
					}
					
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
							, ", this."
							, childA.getNamedItem("name").getNodeValue()
							, "_min, this."
							, childA.getNamedItem("name").getNodeValue()
							, "_max);");
					fc.push(snst, "\t\tfilled = \"\" + "
							, "__parse_input(filled, this."
							, childA.getNamedItem("name").getNodeValue()
							, "_grammars, \""
							, childA.getNamedItem("slot") != null ? childA.getNamedItem("slot").getNodeValue() : childA.getNamedItem("name").getNodeValue()
							, "\", __browser__.getProperty(\"inputmode\"));");
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
					
					if(childA.getNamedItem("cond") != null) {
						fc.push(snst, "}");
					}	
					
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
					fc.push(snst, "\t\tthis.grammars = _document_grammars.slice(0);");
					fc.push(snst, "\t\tthis.grammars.push(", collectGrammars(child), ");");
					fc.push(snst, "\t\tthis.count = (!this.count? 0 : this.count) + 1;");
					fc.push(snst, "\t\tvar _count;");
					fc.push(snst, "\t\twhile(_nextitem != false) {");
					fc.push(snst, "\t\t\ttry {");
					fc.push(snst, "\t\t\t\t_count = this.count;");
					fc.push(snst, "\t\t\t\tswitch(_nextitem) {");
					fc.push(snst, "\t\t\t\t\tcase true: ");
					fc.push(this.parse(child,level + 5));
					
					if(childTag == Tag.Menu) {
						fc.push(snst, "\t\t\t\t\t\tthis.menu_choice_min = __grammar_length(this.grammars,0, __browser__.getProperty(\"inputmode\"));");						
						fc.push(snst, "\t\t\t\t\t\tthis.menu_choice_max = __grammar_length(this.grammars,10, __browser__.getProperty(\"inputmode\"));");						
						fc.push(snst, "\t\t\t\t\t\tfilled = \"\" + __browser__.getInput(\"type your choice\",\"\", this.menu_choice_min, this.menu_choice_max);");
						fc.push(snst, "\t\t\t\t\t\tfilled = \"\" + __parse_input(filled,this.grammars, __browser__.getProperty(\"inputmode\"));");
						fc.push(snst, "\t\t\t\t\t\tif(filled) {");
						fc.push(snst, "\t\t\t\t\t\t\tswitch(filled) { ");
						for(int j = 0; j < childNL; j++) {
							childC = childN.item(j);
							childCA = childC.getAttributes();
							if(childC.getNodeName().equals("choice")) {
								if(childCA.getNamedItem("dtmf") != null) {
									fc.push(snst, "\t\t\t\t\t\t\t\tcase \"");
									fc.push(childCA.getNamedItem("dtmf").getNodeValue());
									fc.push("\": ");
								}
								if(childC.hasChildNodes()) {
									fc.push(snst, "\t\t\t\t\t\t\t\tcase \"");
									fc.push(childC.getFirstChild().getNodeValue());
									fc.push("\": ");
								}
								if(childCA.getNamedItem("next") != null) {
									fc.push("return \"");
									fc.push(childCA.getNamedItem("next").getNodeValue());
									fc.push("\";");
								}
								if(childCA.getNamedItem("expr") != null) {
									fc.push("return ");
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
							fc.push(snst, "return ");
							fc.push(childA.getNamedItem("expr").getNodeValue().replaceAll("\n",snst + "\t")));
							fc.push(";");
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
				case If:
					fc.push(snst, "if("
							, childA.getNamedItem("cond").getNodeValue().replaceAll("\n",snst + "\t")
							, ") {");
					fc.push(this.parse(child, level + 1));
					fc.push(snst, "}");
					break;
				case Meta:
					if(childA.getNamedItem("name") != null
							&& childA.getNamedItem("content") != null) {
						fc.push(snst, "__document__.setMeta(\""
								, childA.getNamedItem("name").getNodeValue()
								, "\",\""
								, childA.getNamedItem("content").getNodeValue().replaceAll("\n",snst + "\t")
								, "\");");
					}
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
					fc.push(snst, "__document__.setProperty(\""
						, childA.getNamedItem("name").getNodeValue()
						, "\",\""
						, childA.getNamedItem("value").getNodeValue()
						, "\");");
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
					fc.push(snst, "var _document_grammars = new Array(", collectGrammars(child), ");");
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
	
	protected FastConcatenation collectGrammars(Node node) {
		final FastConcatenation fc = new FastConcatenation();
		
		final NodeList childs = node.getChildNodes();
		final int n = childs.getLength();
		Node child;
		NamedNodeMap childA;
		NodeList childN;
		int childNL;
		Tag childTag;

		if(htTagEnum.get(node.getNodeName()) == Tag.Field) {
			FastConcatenation fcOptions = new FastConcatenation("");
			
			for(int i = 0; i < n ;i++) {
			    child = childs.item(i);
				childA = child.getAttributes();
				childN = child.getChildNodes();
				childNL = childN.getLength(); 
				childTag = htTagEnum.get(child.getNodeName());			
				
				if(childTag == Tag.Option) {
					String value = "";
					String text = "";
					
					for(int j = 0; j < childNL ;j++) {
						if(htTagEnum.get(childN.item(j).getNodeName()) == Tag._Text) {
							text += childN.item(j).getNodeValue().trim();
						}
					}
					
					if(childA.getNamedItem("value") != null) {
						value = childA.getNamedItem("value").getNodeValue();
					} else {
						value = text;
					}
					
					if(childA.getNamedItem("dtmf") != null) {
						if(fcOptions.length() > 0) {
							fcOptions.push(",");
						}
						
						fcOptions.push("{ type:\"dtmf\", regexp:/"
								, childA.getNamedItem("dtmf").getNodeValue()
								, "/, value:\""
								, value
								, "\"}");
					}
					
					if(!value.equals("")) {
						if(fcOptions.length() > 0) {
							fcOptions.push(", ");
						}
						
						fcOptions.push("{ type:\"voice\", regexp:/"
								, text
								, "/, value:\""
								, value
								, "\"}");
					}
				}
			}
			
			if(fcOptions.length() > 0) {
				fc.push("{ event:\"\", next:\"\""
						, ", eventexpr:\"\", expr:\"\", rules: ["
						, fcOptions
						, "], weight:1.0, scope:20 }");
			}
			
			//IMPLEMENT BUILTIN GRAMMARS HERE
			childA = node.getAttributes();
			if(childA.getNamedItem("type") != null) {
				if(fc.length() > 0) {
					fc.push(", ");
				}

				
				
				fc.push("{ event:\"\", next:\"\""
						, ", eventexpr:\"\", expr:\"\", rules: ["
						, parseType(childA.getNamedItem("type").getNodeValue()).getProperty("grammar")
						, "], weight:1.0, scope:20 }");
				
			}
		}
		
		for(int i = 0; i < n ;i++) {
		    child = childs.item(i);
			childA = child.getAttributes();
			childN = child.getChildNodes();
			childNL = childN.getLength(); 
			childTag = htTagEnum.get(child.getNodeName());			
			
			switch(childTag) {
				case Form:
				case Menu:
				case Field:
				case Option:
					continue; // do not collect inner grammars
				case Grammar:
					
					/*********************************************
					 * RIGHT NOW ONLY BNF SIMPLE GRAMMARS ARE SUPPORTED
					 * 
					 */
					/*
						fc.push("{ type:\""
							, childA.getNamedItem("mode") == null ? "any" : childA.getNamedItem("mode").getNodeValue() 
							, "\", regexp:/"
							, grammar
							, "/, value:\"\"}");
					 */
					String grammar = "";
					
					for(int j = 0; j < childNL ;j++) {
						if(htTagEnum.get(childN.item(j).getNodeName()) == Tag._Text) {
							grammar += childN.item(j).getNodeValue().trim();
						}
					}
					
					if(!grammar.equals("")) {
						if(fc.length() > 0) {
							fc.push(", ");
						}
						
						fc.push("{ type:\""
							, childA.getNamedItem("mode") == null ? "any" : childA.getNamedItem("mode").getNodeValue() 
							, "\", regexp:/"
							, grammar
							, "/, value:\"\"}");
					} 
					
					break;
				case Link:
				case Choice:
					FastConcatenation fcOptions = new FastConcatenation();
					String next = "";
					String expr = "";
					String event = "";
					String eventexpr = "";
					String text = "";
					
					for(int j = 0; j < childNL ;j++) {
						Tag tag = htTagEnum.get(childN.item(j).getNodeName()); 
						if(tag == Tag._Text) {
							text += childN.item(j).getNodeValue().trim();
						}
					}
					
					if(childA.getNamedItem("next") != null) {
						next = childA.getNamedItem("next").getNodeValue();
					}
					
					if(childA.getNamedItem("expr") != null) {
						expr = childA.getNamedItem("expr").getNodeValue();
					}
					
					if(childA.getNamedItem("event") != null) {
						event = childA.getNamedItem("event").getNodeValue();
					}
					
					if(childA.getNamedItem("eventexpr") != null) {
						eventexpr = childA.getNamedItem("eventexpr").getNodeValue();
					}
					
					if(childA.getNamedItem("dtmf") != null) {
						if(fcOptions.length() > 0) {
							fcOptions.push(",");
						}
						
						fcOptions.push("{ type:\"dtmf\", regexp:/"
								, childA.getNamedItem("dtmf").getNodeValue()
								, "/, value:\"\"}");
					}
					
					if(text.equals("")) {
						if(child.hasChildNodes()) {
							FastConcatenation fc_grammar = collectGrammars(child);
							if(fc_grammar.length() > 0) {
								if(fcOptions.length() > 0) {
									fcOptions.push(", ");
								}
								fcOptions.push(fc_grammar);
							}
						}
					} else {
						if(fcOptions.length() > 0) {
							fcOptions.push(", ");
						}
						
						fcOptions.push("{ type:\"voice\", regexp:/"
								, text
								, "/, value:\"\"}");
					}
					
					if(fcOptions.length() > 0) {
						if(fc.length() > 0) {
							fc.push(", ");
						}
						fc.push("{ event:\""
								, event
								, "\", next:\""
								, next
								, "\""
								, ", eventexpr:\""
								, eventexpr
								, "\", expr:\""
								, expr
								, "\", rules: ["
								, fcOptions
								, "], weight:1.0, scope:20 }");
					}
										
					break;
					default:
						/*
						if(child.hasChildNodes()) {
							FastConcatenation fc_child = collectGrammars(child);
							if(fc_child.length() > 0) {
								if(fc.)
							}
						}
						*/
			}
		}
		
		return fc;
	}
	
	private Properties parseType(String type) {
		int index;
		String[] args;
		Type typeEnum;
		Properties typeProperties = new Properties();
		int min;
		int max;
		FastConcatenation fc = new FastConcatenation();
		
		index = type.indexOf('?');
		if(index >= 0) {
			args = type.substring(index + 1).split(";");
			type = type.substring(0, index);
			for(String arg : args) {
				index = arg.indexOf('=');
				if(index >= 0) {
					typeProperties.setProperty(arg.substring(0, index), arg.substring(index + 1));
				} else {
					typeProperties.setProperty(arg, "");
				}
			}
		}
		
		index = type.lastIndexOf('/');
		if(index >= 0) {
			type = type.substring(index + 1);
		}
		
		typeEnum = htTypeEnum.get(type);
		
		if(typeEnum == null) {
			typeEnum = Type.Custom;
		}

		switch(typeEnum) {
			case Boolean:
				fc.push("{ type:\"dtmf\", regexp:/"
						, typeProperties.getProperty("y","1")
						, "/, value:true}");
				fc.push("{ type:\"dtmf\", regexp:/"
						, typeProperties.getProperty("n","2")
						, "/, value:false}");
				fc.push("{ type:\"voice\", regexp:/"
						, typeProperties.getProperty("y","yes")
						, "/, value:true}");
				fc.push("{ type:\"voice\", regexp:/"
						, typeProperties.getProperty("n","no")
						, "/, value:false}");
				break;
			case Currency:
				min = Integer.valueOf(typeProperties.getProperty("length",typeProperties.getProperty("minlength","1")));
				max = Integer.valueOf(typeProperties.getProperty("length",typeProperties.getProperty("maxlength","12")));
				fc.push("{ type:\"dtmf\", regexp:/"
						, "\\d{", min, ",", max, "}(\\*\\d{0,2})?"
						, "/, value:\"\"}");
				fc.push("{ type:\"voice\", regexp:/"
						, "\\d{", min, ",", max, "}(\\.\\d{0,2})?"
						, "/, value:\"\"}");
				break;
			case Date:
				fc.push("{ type:\"dtmf\", regexp:/"
						, "\\d{0,4}(0[1-9]|1[012])(0[1-9]|[12][0-9]|3[01])"
						, "/, value:\"\"}");
				fc.push("{ type:\"voice\", regexp:/"
						, "\\d{0,4}[- /.]?(0[1-9]|1[012])[- /.]?(0[1-9]|[12][0-9]|3[01])"
						, "/, value:\"\"}");
				break;
			case Digits:
				min = Integer.valueOf(typeProperties.getProperty("length",typeProperties.getProperty("minlength","1")));
				max = Integer.valueOf(typeProperties.getProperty("length",typeProperties.getProperty("maxlength","255")));
				fc.push("{ type:\"any\", regexp:/"
						, "\\d{" + min + "," + max + "}"
						, "/, value:\"\"}");
				break;
			case Number:
				min = Integer.valueOf(typeProperties.getProperty("length",typeProperties.getProperty("minlength","1")));
				max = Integer.valueOf(typeProperties.getProperty("length",typeProperties.getProperty("maxlength","9")));
				fc.push("{ type:\"any\", regexp:/"
						, "\\d{" + min + "," + max + "}"
						, "/, value:\"\"}");
				break;
			case Phone:
				min = Integer.valueOf(typeProperties.getProperty("length",typeProperties.getProperty("minlength","1")));
				max = Integer.valueOf(typeProperties.getProperty("length",typeProperties.getProperty("maxlength","13")));
				fc.push("{ type:\"any\", regexp:/"
						, "\\d{" + min + "," + max + "}"
						, "/, value:\"\"}");
				break;
			case Time:
				min = Integer.valueOf(typeProperties.getProperty("length",typeProperties.getProperty("minlength","1")));
				max = Integer.valueOf(typeProperties.getProperty("length",typeProperties.getProperty("maxlength","6")));
				fc.push("{ type:\"any\", regexp:/"
						, "\\d{" + min + "," + max + "}"
						, "/, value:\"\"}");
				break;
		}
		
		typeProperties.put("grammar", fc.toString());
		
		return typeProperties;
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

	/**
	 * Gets the property.
	 * 
	 * @param name the name
	 * 
	 * @return the property
	 */
	public String getProperty(String name) {
		return properties.getProperty(name, "");
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

	/**
	 * Gets the meta-tag value.
	 * 
	 * @param name the meta-tag name
	 * 
	 * @return the property
	 */
	public String getMeta(String name) {
		return meta.getProperty(name, "");
	}

	/**
	 * Sets the meta-tag value.
	 * 
	 * @param name  the meta-tag name
	 * @param value the value
	 */
	public void setMeta(String name, String value) {
		meta.setProperty(name, value);
	}
}
