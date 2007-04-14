/**
 * File:        Document.java
 * Description: The VoiceXML document
 * Author:      Edgar Medrano Pérez 
 *              edgarmedrano at gmail dot com
 * Created:     2007.04.14
 * Company:     JAVIER project
 *              http://javier.sourceforge.net
 * Notes:        
 */

package org.javier.browser;

import java.net.URLEncoder;
import java.util.Hashtable;
import java.util.Vector;

import org.javier.util.FastConcatenation;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Document {
	static protected enum Tag {
		_Text, _Comment, Assign, Audio, Block, Catch, Choice,	Clear, 
		Disconnect, Else, ElseIf, Enumerate, Error, Exit, Field, Filled, 
		Form, GoTo, Grammar, Help, If, Initial, Link, Log, Menu, Meta, 
		NoInput, NoMatch, Object, Option, Param, Prompt, Property, Record,
		Reprompt, Return, Script, Subdialog, Submit, Throw, Transfer, Value,
		Var, Vxml, Xml
	}
	static protected final Hashtable<String, Tag> htTagEnum 
		= new Hashtable<String, Tag>(60);
	static {
		htTagEnum.put("#text", Tag._Text);
		htTagEnum.put("#comment", Tag._Comment);
		htTagEnum.put("assign", Tag.Assign);
		htTagEnum.put("audio", Tag.Audio);
		htTagEnum.put("block", Tag.Block);
		htTagEnum.put("catch", Tag.Catch);
		htTagEnum.put("choice", Tag.Choice);
		htTagEnum.put("clear", Tag.Clear);
		htTagEnum.put("disconnect", Tag.Disconnect);
		htTagEnum.put("else", Tag.Else);
		htTagEnum.put("elseif", Tag.ElseIf);
		htTagEnum.put("enumerate", Tag.Enumerate);
		htTagEnum.put("error", Tag.Error);
		htTagEnum.put("exit", Tag.Exit);
		htTagEnum.put("field", Tag.Field);
		htTagEnum.put("filled", Tag.Filled);
		htTagEnum.put("form", Tag.Form);
		htTagEnum.put("goto", Tag.GoTo);
		htTagEnum.put("grammar", Tag.Grammar);
		htTagEnum.put("help", Tag.Help);
		htTagEnum.put("if", Tag.If);
		htTagEnum.put("initial", Tag.Initial);
		htTagEnum.put("link", Tag.Link);
		htTagEnum.put("log", Tag.Log);
		htTagEnum.put("menu", Tag.Menu);
		htTagEnum.put("meta", Tag.Meta);
		htTagEnum.put("noinput", Tag.NoInput);
		htTagEnum.put("nomatch", Tag.NoMatch);
		htTagEnum.put("object", Tag.Object);
		htTagEnum.put("option", Tag.Option);
		htTagEnum.put("param", Tag.Param);
		htTagEnum.put("prompt", Tag.Prompt);
		htTagEnum.put("property", Tag.Property);
		htTagEnum.put("record", Tag.Record);
		htTagEnum.put("reprompt", Tag.Reprompt);
		htTagEnum.put("return", Tag.Return);
		htTagEnum.put("script", Tag.Script);
		htTagEnum.put("subdialog", Tag.Subdialog);
		htTagEnum.put("submit", Tag.Submit);
		htTagEnum.put("throw", Tag.Throw);
		htTagEnum.put("transfer", Tag.Transfer);
		htTagEnum.put("value", Tag.Value);
		htTagEnum.put("var", Tag.Var);
		htTagEnum.put("vxml", Tag.Vxml);
	}	

	private String url;
	private String text;
	private Node xml;
	private String js;
	private String method;
	private String enctype;
	private int timeout;
	private int maxage;
	private int maxstale;
	private final Vector<DocumentListener> vecListeners = new Vector<DocumentListener>();

	public Document(String url) {
		this(url,"GET","",0,0,0);
	}
	
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

	public Document() {
		this("");
	}
		
	public void addDocumentListener(DocumentListener l) {
		vecListeners.add(l);
	}
	
	public void removeDocumentListener(DocumentListener l) {
		vecListeners.remove(l);
	}	
	
	protected void fireErrorFound(String description) {
		for(DocumentListener l: vecListeners) {
			l.errorFound(description);
		}
	}
	
	protected void fireWarningFound(String description) {
		for(DocumentListener l: vecListeners) {
			l.warningFound(description);
		}
	}
	
	protected void fireCommentFound(String description) {
		for(DocumentListener l: vecListeners) {
			l.commentFound(description);
		}
	}
	
	protected void fireVerboseFound(String description) {
		for(DocumentListener l: vecListeners) {
			l.verboseFound(description);
		}
	}	
	
    protected static String encodeURIComponent(String str) {
		try {
			str = URLEncoder.encode(str, "UTF-8");
		} catch(Exception e) { }
		
		return str.replaceAll("\\+", "%20").replaceAll("%2B", "+");
    }
	
    protected String escape(String str) {
		return encodeURIComponent(str);
	}
	
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

    protected String endTag(Node node) {
		if(node.hasChildNodes()) {
			return "/* </" + node.getNodeName() + "> */";
		}
		
		return "";
	}

	/**
	 * @return the js
	 */
	public String getJs() {
		return js;
	}

	/**
	 * @return the text
	 */
	public String getText() {
		return text;
	}

	/**
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * @return the xml
	 */
	public org.w3c.dom.Node getXML() {
		return xml;
	}

	public String getEnctype() {
		return enctype;
	}

	public int getMaxage() {
		return maxage;
	}

	public int getMaxstale() {
		return maxstale;
	}

	public String getMethod() {
		return method;
	}

	public int getTimeout() {
		return timeout;
	}
	
	public void setXML(Node xml) {
		this.xml = xml;
		this.js = parse().toString();
	}
	
	private FastConcatenation parse() {
    	return parse(xml);
    }	
	
    private FastConcatenation parse(Node node) {
    	return parse(node, 0);
    }	
    
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
					fc.push(snst, "case \"", childA.getNamedItem("name"), "\":"); 
					fc.push(snst, "\ttry {");
					fc.push(snst, "\t\tthis."
							, childA.getNamedItem("name")
							, "_count = (!this."
							, childA.getNamedItem("name")
							, "_count? 0 : this."
							, childA.getNamedItem("name")
							, "_count) + 1;");
					fc.push(snst, "\t\t_count = this."
							, childA.getNamedItem("name")
							, "_count;"
							, this.parse(child,level + 2));
					fc.push(snst, "\t\tfilled = "
							, "__browser__.getInput(\""
							, childA.getNamedItem("name")
							, "\","
							, childA.getNamedItem("name")
							, ");");
					fc.push(snst, "\t\tif(filled) {");
					fc.push(snst, "\t\t\t"
							, childA.getNamedItem("name")
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
						if(childC.getNodeName() == "catch" 
							|| childC.getNodeName() == "filled" 
							|| childC.getNodeName() == "error" 
							|| childC.getNodeName() == "noinput" 
							|| childC.getNodeName() == "nomatch" 
							|| childC.getNodeName() == "help" ) {
							String eventName = childC.getNodeName();
							
							if(eventName == "catch") {
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
							, childA.getNamedItem("name")
							, "\"; break;");
					fc.push(snst, "\t\t\t\t} else {");
					fc.push(snst, "\t\t\t\t\t_throw = true; break;");
					fc.push(snst, "\t\t\t\t}");
					fc.push(snst, "\t\t\t} catch(_error2) {");
					fc.push(snst, "\t\t\t\t_error1 = _error2;");
					fc.push(snst, "\t\t\t}");
					fc.push(snst, "\t\t}");
					fc.push(snst, "\t\tif(_throw) { throw(_error1); }");
					fc.push(snst, "\t\tif(_break) { break; }");
					fc.push(snst, "\t}");
					break;
				case Form:
				case Menu: // Menu is equivalent to form don't move this code
					fc.push(snst, "_form[_form.length] = ");
					if(childA.getNamedItem("id") != null) {
						fc.push("_form[\""
								, childA.getNamedItem("id")
								, "\"] = ");
					}
					fc.push(snst, "\tfunction() {");
					StringBuffer fields = new StringBuffer();
					for(int j = 0; j < childNL; j++) {
						childC = childN.item(j);
						childCA = childC.getAttributes();
						if(childC.getNodeName() == "field") {
							fc.push(snst, "\t\tvar ");
							fc.push(childCA.getNamedItem("name"));
							if(childCA.getNamedItem("expr") != null) {
								fc.push(" = ");
								fc.push(childCA.getNamedItem("expr").getNodeValue().replaceAll("\n",snst + "\t"));
							}
							fc.push(";");
							fields.append(" "); fields.append(childCA.getNamedItem("name"));
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
						if(childC.getNodeName() == "field") {
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
						fc.push(snst, "\t\t\t\t\t\tfilled = __browser__.getInput(\"type your choice\",\"\");");
						fc.push(snst, "\t\t\t\t\t\tif(filled) {");
						fc.push(snst, "\t\t\t\t\t\t\tswitch(filled) { ");
						for(int j = 0; j < childNL; j++) {
							childC = childN.item(j);
							childCA = childC.getAttributes();
							if(childC.getNodeName() == "choice") {
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
						if(childC.getNodeName() == "catch" 
							|| childC.getNodeName() == "filled" 
							|| childC.getNodeName() == "error" 
							|| childC.getNodeName() == "noinput" 
							|| childC.getNodeName() == "nomatch" 
							|| childC.getNodeName() == "help" ) {
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
								fc.push(childCA.getNamedItem("count"));
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
					fc.push(snst, "\t\t\t\tif(_throw) { throw(_error1); }");
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
								, childA.getNamedItem("_nextitem")
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
						, childA.getNamedItem("name")
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
								, childA.getNamedItem("count")
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
						, childA.getNamedItem("name")
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
								, childA.getNamedItem("next")
								, "\"");
					} else {
						fc.push(",\"\"");
					}
					
				    if(childA.getNamedItem("namelist") != null) { 
						fc.push(",\""
								, childA.getNamedItem("namelist")
								, "\"");
					} else {
						fc.push(",this.fields");
					}
					
					/*
				    if(childA.getNamedItem("method") != null) { 
						result.push(",\""
								, childA.getNamedItem("method")
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
									, childA.getNamedItem("enctype")
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
							, childA.getNamedItem("event")
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
}
