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

import org.javier.browser.handlers.ErrorHandler;
import org.javier.browser.handlers.InputHandler;
import org.javier.browser.handlers.LogHandler;
import org.javier.browser.handlers.NetworkHandler;
import org.javier.browser.handlers.OutputHandler;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Javier {
	protected static enum VXMLTag {
		Text, Comment, Assign, Audio, Block, Catch, Choice,	Clear, Disconnect, 
		Else, ElseIf, Enumerate, Error, Exit, Field, Filled, Form, GoTo, 
		Grammar, Help, If, Initial, Link, Log, Menu, Meta, NoInput, NoMatch, 
		Object, Option, Param, Prompt, Property, Record, Reprompt, Return, 
		Script, Subdialog, Submit, Throw, Transfer, Value, Var, Vxml, Xml
	}
	final protected static Hashtable<String, VXMLTag> htTags;
	static {
		htTags = new Hashtable<String, VXMLTag>(60);
		htTags.put("#text", VXMLTag.Text);
		htTags.put("#comment", VXMLTag.Comment);
		htTags.put("assign", VXMLTag.Assign);
		htTags.put("audio", VXMLTag.Audio);
		htTags.put("block", VXMLTag.Block);
		htTags.put("catch", VXMLTag.Catch);
		htTags.put("choice", VXMLTag.Choice);
		htTags.put("clear", VXMLTag.Clear);
		htTags.put("disconnect", VXMLTag.Disconnect);
		htTags.put("else", VXMLTag.Else);
		htTags.put("elseif", VXMLTag.ElseIf);
		htTags.put("enumerate", VXMLTag.Enumerate);
		htTags.put("error", VXMLTag.Error);
		htTags.put("exit", VXMLTag.Exit);
		htTags.put("field", VXMLTag.Field);
		htTags.put("filled", VXMLTag.Filled);
		htTags.put("form", VXMLTag.Form);
		htTags.put("goto", VXMLTag.GoTo);
		htTags.put("grammar", VXMLTag.Grammar);
		htTags.put("help", VXMLTag.Help);
		htTags.put("if", VXMLTag.If);
		htTags.put("initial", VXMLTag.Initial);
		htTags.put("link", VXMLTag.Link);
		htTags.put("log", VXMLTag.Log);
		htTags.put("menu", VXMLTag.Menu);
		htTags.put("meta", VXMLTag.Meta);
		htTags.put("noinput", VXMLTag.NoInput);
		htTags.put("nomatch", VXMLTag.NoMatch);
		htTags.put("object", VXMLTag.Object);
		htTags.put("option", VXMLTag.Option);
		htTags.put("param", VXMLTag.Param);
		htTags.put("prompt", VXMLTag.Prompt);
		htTags.put("property", VXMLTag.Property);
		htTags.put("record", VXMLTag.Record);
		htTags.put("reprompt", VXMLTag.Reprompt);
		htTags.put("return", VXMLTag.Return);
		htTags.put("script", VXMLTag.Script);
		htTags.put("subdialog", VXMLTag.Subdialog);
		htTags.put("submit", VXMLTag.Submit);
		htTags.put("throw", VXMLTag.Throw);
		htTags.put("transfer", VXMLTag.Transfer);
		htTags.put("value", VXMLTag.Value);
		htTags.put("var", VXMLTag.Var);
		htTags.put("vxml", VXMLTag.Vxml);
	}
	
	final class FastConcatenation {
		private StringBuffer stb;
		
		FastConcatenation() {
			stb = new StringBuffer();
		}
		
		FastConcatenation(String str) {
			this();
			stb.append(str);
		}
		
		FastConcatenation push(Object ... args) {
			for(int i = 0; i < args.length; i++) {
				stb.append(args[i]);
			}
			return this;
		}

		@Override
		public String toString() {
			String strJoin;
			
			if(stb.length() == 0) {
				return "";
			}
			
			if(stb.length() == 1) {
				return stb.toString();
			}
			
			strJoin = stb.toString();
			stb = new StringBuffer();
			stb.append(strJoin);
			
			return strJoin;
		}
		
		public String replace(String str1, String str2) {
			String strJoin = stb.toString();
			stb = new StringBuffer(strJoin);
			return strJoin.replace(str1, str2);
		}
	}

	private InputHandler __input__;
	private OutputHandler __output__;
	private ErrorHandler __error__;
	private NetworkHandler __network__;
	private LogHandler __log__;
	
	Javier(InputHandler __input__
			,OutputHandler __output__
			,ErrorHandler __error__
			,NetworkHandler __network__
			,LogHandler __log__) {
		this.__input__ = __input__;
		this.__output__ = __output__;
		this.__error__ = __error__;
		this.__network__ = __network__;
		this.__log__ = __log__;		
	}
	
    protected static String encodeURIComponent(String str) {
		try {
			str = URLEncoder.encode(str, "UTF-8");
		} catch(Exception e) { }
		
		return str.replaceAll("+", "%20").replaceAll("%2B", "+");
    }
	
    protected String escape(String str) {
		return encodeURIComponent(str);
	}
	
    protected FastConcatenation startTag(Node node) {
		FastConcatenation stbResult = new FastConcatenation("/* <");
		int n = node.getAttributes().getLength();
		
		stbResult.push(node.getNodeName());
		
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

    protected FastConcatenation endTag(Node node) {
		FastConcatenation stbResult = new FastConcatenation();
		
		if(node.hasChildNodes()) {
			stbResult.push("/* </", node.getNodeName(), "> */");
		}
		
		return stbResult;
	}

	private void warning(Object source, String message) {
		
	}
	
    protected FastConcatenation parse(Node node,int level) throws VXMLException {
		FastConcatenation fc = new FastConcatenation();
		NodeList childs = node.getChildNodes();
		int n = childs.getLength();
		Node child;
		NamedNodeMap childA;
		NodeList childN;
		int childNL;
		NamedNodeMap childCA;
		Node childC;
		VXMLTag childTag;
		//ArrayList list;
		StringBuffer snst = new StringBuffer("\n");
		
		for(int i = 0; i < level; i++) {
			snst.append("\t");
		}
		
		for(int i = 0; i < n ;i++) {
		    child = node.getChildNodes().item(i);
			childTag = htTags.get(child.getNodeName());
			childA = child.getAttributes();
			childN = child.getChildNodes();
			childNL = childN.getLength(); 
			
			switch(childTag) {
				case Text:
					if(child.getNodeValue().trim() != "") {
						fc.push(snst, "/* "
								, child.getNodeValue().trim().replaceAll("\n", snst + "   ")
								, " */");
					}
					break;
				/*the following tags won't be parsed here*/	
				case Comment: 
				case Catch:
				case Choice:
				case Error:
				case Filled:
				case Help:
				case NoInput:
				case NoMatch:
				    continue;
				default:
					fc.push(snst, startTag(child).replace("\n",snst + "   "));
			}
			
			switch(childTag) {
				case Comment:
					break;
				case Text:
				    if(child.getNodeValue().trim() != "") {
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
					
					if(child.getNodeName() == "menu") {
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
					warning(this,"Unsupported element: grammar");
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
						warning(this,"Unsupported attribute: timeout");
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
						warning(this,"Unsupported attribute: method");
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
						warning(this,"Unsupported attribute: enctype");
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
						warning(this,"Unsupported attribute: class");
					}
					if(childA.getNamedItem("mode") != null) {
						warning(this,"Unsupported attribute: mode");
					}
					if(childA.getNamedItem("recSrc") != null) {
						warning(this,"Unsupported attribute: recSrc");
					}
					if(childA.getNamedItem("expr") != null) {
						fc.push(snst, "__expr = "
								, childA.getNamedItem("expr").getNodeValue().replaceAll("\n",snst + "\t")
								, ";");
						fc.push(snst, "addText(__expr);");
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
				    throw(new VXMLException("error.unsupported." + child.getNodeName()));
			}
			
			if(child.hasChildNodes()) {
				fc.push(snst, endTag(child));
			}
		}
		
		return fc; 
	}
}
