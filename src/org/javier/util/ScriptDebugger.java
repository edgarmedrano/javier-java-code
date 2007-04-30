/**
 * File:        ScriptDebugger.java
 * Description: A simple JavaScript debugger
 * Author:      Edgar Medrano Pérez
 *              edgarmedrano at gmail dot com
 * Created:     2007.04.23
 * Company:     JAVIER project
 *              http://javier.sourceforge.net
 * Notes:        
 */
package org.javier.util;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Label;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.script.Bindings;
import javax.script.Invocable;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptException;

// TODO: Auto-generated Javadoc
/**
 * A simple JavaScript debugger
 */
public class ScriptDebugger implements ScriptEngine, Invocable {

	/** The script engine to be watched. */
	private ScriptEngine se;

	/** The frame used to watch. */
	private Frame frame;

	/** The text area used to watch the code and follow the execution. */
	private TextArea txtCode;

	/** It's used to eventually enable/disable debugging. */
	private boolean bypass;

	/** 
	 * The program execution is halt until this field is set to 
	 * <code>true</code>. 
	 */
	private boolean step;

	/** Enable/disable script debugging from the start. */
	private boolean debugOn;

	/** The current execution line. */
	private int line = 1;

	/** The label uses to show the current execution line. */
	private Label lblLine;

	/** 
	 * When this field is set to <code>true</code> an exception is thrown to 
	 * stop the script execution. 
	 */
	private boolean stop;

	/** The text area used to watch the variable's values. */
	private TextArea txtBindings;
	
	/**
	 * This is used to wrap the Bindings.
	 */
	class DebugBindings implements Bindings {
		
		/** The binds to be watched. */
		private Bindings binds;
		
		/**
		 * The Constructor.
		 * 
		 * @param binds the binds to be watched
		 */
		public DebugBindings(Bindings binds) {
			this.binds = binds;
		}
		
		/* (non-Javadoc)
		 * @see javax.script.Bindings#put(java.lang.String, java.lang.Object)
		 */
		public Object put(String name, Object value) {
			Object res = binds.put(name, value);
			txtBindings.setText(txtBindings.getText() 
				+ "\nput(" + name + "," + value + ") " + res);
			return res;
		}
		
		/* (non-Javadoc)
		 * @see javax.script.Bindings#get(java.lang.Object)
		 */
		public Object get(Object key) {
			Object res = binds.get(key);
			txtBindings.setText(txtBindings.getText() 
					+ "\nget(" + key + ") " + res);
			return res;
		}

		/* (non-Javadoc)
		 * @see javax.script.Bindings#containsKey(java.lang.Object)
		 */
		public boolean containsKey(Object key) {
			boolean res = binds.containsKey(key);
			
			if(!key.toString().equals("__DEBUG__")) {
				txtBindings.setText(txtBindings.getText() 
		    		+ "\ncontainsKey(" + key + ") " + res);
			}
			
			return res;
		}

		/* (non-Javadoc)
		 * @see javax.script.Bindings#putAll(java.util.Map)
		 */
		public void putAll(Map<? extends String, ? extends Object> toMerge) {
			binds.putAll(toMerge);
			txtBindings.setText(txtBindings.getText() 
				+ "\nputAll(" + toMerge + ")");
		}

		/* (non-Javadoc)
		 * @see javax.script.Bindings#remove(java.lang.Object)
		 */
		public Object remove(Object key) {
			Object res = binds.remove(key);
			
			if(!key.toString().equals("__DEBUG__")) {
				txtBindings.setText(txtBindings.getText() 
					+ "\nremove(" + key + ") " + res);
			}
			
			return res;
		}

		/* (non-Javadoc)
		 * @see java.util.Map#clear()
		 */
		public void clear() {
			binds.clear();
			txtBindings.setText(txtBindings.getText() 
				+ "\nclear()");
		}

		/* (non-Javadoc)
		 * @see java.util.Map#containsValue(java.lang.Object)
		 */
		public boolean containsValue(Object value) {
			boolean res = binds.containsValue(value);
			txtBindings.setText(txtBindings.getText() 
	    		+ "\ncontainsValue(" + value + ") " + res);
			return res;
		}

		/* (non-Javadoc)
		 * @see java.util.Map#entrySet()
		 */
		public Set<java.util.Map.Entry<String, Object>> entrySet() {
			Set<java.util.Map.Entry<String, Object>> res = binds.entrySet();
			txtBindings.setText(txtBindings.getText() 
		    		+ "\nentrySet() " + res);
			return res;
		}

		/* (non-Javadoc)
		 * @see java.util.Map#isEmpty()
		 */
		public boolean isEmpty() {
			boolean res = binds.isEmpty();
			txtBindings.setText(txtBindings.getText() 
	    		+ "\nisEmpty() " + res);
			return res;
		}

		/* (non-Javadoc)
		 * @see java.util.Map#keySet()
		 */
		public Set<String> keySet() {
			Set<String> res = binds.keySet();
			txtBindings.setText(txtBindings.getText() 
		    	+ "\nkeySet() " + res);
			return res;
		}

		/* (non-Javadoc)
		 * @see java.util.Map#size()
		 */
		public int size() {
			int res = binds.size();
			txtBindings.setText(txtBindings.getText() 
				+ "\nsize() " + res);
			return res;
		}

		/* (non-Javadoc)
		 * @see java.util.Map#values()
		 */
		public Collection<Object> values() {
			Collection<Object> res = binds.values();
			txtBindings.setText(txtBindings.getText() 
		    	+ "\nvalues() " + res);
			return res;
		}		
	}

	/**
	 * @param debug Enable/disable debug from start
	 * @param se    the script engine to be watched
	 */
	public ScriptDebugger(ScriptEngine se, boolean debug) {
		this.se = se;
		if(!(getBindings(ScriptContext.ENGINE_SCOPE) instanceof DebugBindings)) {
			setBindings(new DebugBindings(getBindings(ScriptContext.ENGINE_SCOPE))
				, ScriptContext.ENGINE_SCOPE);
		}
		if(!(getBindings(ScriptContext.GLOBAL_SCOPE) instanceof DebugBindings)) {
			setBindings(new DebugBindings(getBindings(ScriptContext.GLOBAL_SCOPE))
				, ScriptContext.GLOBAL_SCOPE);
		}
		
		this.debugOn = debug;

		frame = new Frame();
		frame.setLayout(new BorderLayout());
		txtCode = new TextArea();
		txtCode.setEditable(false);
		frame.add(txtCode, BorderLayout.CENTER);
		txtBindings = new TextArea();
		txtBindings.setEditable(false);
		frame.add(txtBindings, BorderLayout.EAST);
		Container cont = new Container();
		cont.setLayout(new FlowLayout());
		frame.add(cont, BorderLayout.SOUTH);
		lblLine = new Label();
		cont.add(lblLine);
		Button btnContinue = new Button("Continue");
		cont.add(btnContinue);
		btnContinue.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				bypass = true;
			}
		});
		Button btnStep = new Button("Step");
		cont.add(btnStep);
		btnStep.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					step = true;
					bypass = false;
				}
			});
		Button btnStop = new Button("Stop");
		cont.add(btnStop);
		btnStop.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					stop = true;
					bypass = true;
				}
			});
		Button btnClose = new Button("Close");
		cont.add(btnClose);
		btnClose.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					bypass = true;
		            frame.setVisible(false);
		            frame.dispose();
				}
			});
		
		frame.addWindowStateListener(new WindowAdapter() {
		        public void windowClosing(WindowEvent evt) {
		            frame.setVisible(false);
		            //frame.dispose();
		        }
	        });
	}
	
	/**
	 * @param se    the script engine to be watched
	 */
	public ScriptDebugger(ScriptEngine se) {
		this(se,true);
	}

	/**
	 * This method is used to make a callback. A call to this method is 
	 * inserted in almost every script line, like this:
	 * 
	 *  <code>if(__DEBUG__.cb(111,(function() {}).__parent__)) 
	 *  	throw("error.debug.stop");</code>
	 * 
	 * @param ref the reference to the current scope
	 * @param line the current execution line
	 * @return <code>true</code> if the user press stop button (in the script
	 *               this will fire the "error.debug.stop" exception.<br>
	 *         <code>false</code> if the user press step (in the script this
	 *               will continue with the execution)
	 */
	public boolean cb(int line, Object ref) {
		String code;
		int start;
		int end;
		int stepLine;
		
		if(!bypass) {
			txtBindings.setText(dumpValues(ref));
			
			code = txtCode.getText();
			start = 0;
			end = code.length();
			stepLine = 0;
			
			while(stepLine < line) {
				start = code.indexOf("\n", start) + 1;
				stepLine ++;
			}
			if(code.indexOf("\n", start) > 0) {
				end = code.indexOf("\n", start);
			}
	
			lblLine.setText(String.valueOf(line));
			txtCode.requestFocus();
			txtCode.setCaretPosition(start);
			txtCode.setSelectionStart(start);
			txtCode.setSelectionEnd(end);
			step = false;
			stop = false;
			while(!bypass && !step && !stop) {
				try {
					TimeUnit.MILLISECONDS.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		if(stop) {
			return true;
		}
		return false;
	}

	/**
	 * Dumps the variable's values in the execution scope.
	 * 
	 * @param ref the reference to the current scope
	 * @return the variable's values
	 */
	private String dumpValues(Object ref) {
		StringBuffer sb = new StringBuffer();
		Object value = ref;
		
		if(ref == null) {
			return "";
		}
		
		Method getDefaultValue = null;
		Method getIds = null;
		Method getInt = null;
		Method getString = null;
		Method getParentScope = null; 
		Object ids[] = {};
		
		for(Method m: ref.getClass().getMethods()) {
			if(m.getName().equals("get") && m.getParameterTypes().length == 2) {
				if(m.getParameterTypes()[0] == Integer.TYPE) {
					getInt = m;
				} else if(m.getParameterTypes()[0] == String.class) {
					getString = m;
				}
			} else if(m.getName().equals("getDefaultValue")) {
				getDefaultValue = m;
			} else if(m.getName().equals("getIds")) {
				getIds = m;
			} else if(m.getName().equals("getParentScope")) {
				getParentScope = m;
			}
		}
		
		if(getParentScope == null) {
			return "";
		}
		
		try {
			sb.append(dumpValues(getParentScope.invoke(ref, new Object[] {})));
		} catch (IllegalArgumentException e1) {
		} catch (IllegalAccessException e1) {
		} catch (InvocationTargetException e1) {
		}
		
		sb.append("\n");
		sb.append(ref);
		sb.append(":");
		if(getDefaultValue != null) {
			try {
				value = getDefaultValue.invoke(ref, new Object[] { null });
				if(value != null) {
					sb.append(value);
				}
			} catch (SecurityException e) {
			} catch (IllegalArgumentException e) {
			} catch (IllegalAccessException e) {
			} catch (InvocationTargetException e) {
			}
		}
		
		if(getIds != null && getInt != null && getString != null) {
			ids = new Object[] {};
			try {
				ids = (Object []) getIds.invoke(ref, new Object[] {});
			} catch (SecurityException e) {
			} catch (IllegalArgumentException e) {
			} catch (IllegalAccessException e) {
			} catch (InvocationTargetException e) {
			}
			if(ids != null && ids.length > 0) {
				sb.append("{ ");
				for(int i = 0; i < ids.length; i++) {
					sb.append("\n\t");
					if(i > 0) {
						sb.append(", ");
					}
					
					value = null;
					try {	
						if(ids[i] instanceof String) {
							value = getString.invoke(ref, new Object[] {ids[i], ref});
						} else {
							value = getInt.invoke(ref, new Object[] {ids[i], ref});
						}
						if(value !=  null) {
							getDefaultValue = value.getClass().getMethod("getDefaultValue"
									, new Class<?>[] { Class.class });
							value = getDefaultValue.invoke(value, new Object[] { null });
						}
					} catch (SecurityException e) {
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
					} catch (InvocationTargetException e) {
					} catch (NoSuchMethodException e) {
					}
					/*
					if(value !=  null) {
						sb.append(getString(ids[i].toString(),value,level));
					}
					*/
					sb.append(ids[i].toString());
					sb.append(": ");
					if(value !=  null) {
						if(value.toString().length() > 128) {
							sb.append(value.toString().substring(0, 128).replaceAll("\n", " "));
						} else {
							sb.append(value.toString().replaceAll("\n", " "));
						}
					} else {
						sb.append("null");						
					}
						
				}
				sb.append(" }");
			}
		} else {
			sb.append("null");
		}
			
		return sb.toString();
	}

	/* (non-Javadoc)
	 * @see javax.script.ScriptEngine#createBindings()
	 */
	public Bindings createBindings() {
		return se.createBindings();
	}

	/* (non-Javadoc)
	 * @see javax.script.ScriptEngine#eval(java.io.Reader)
	 */
	public Object eval(Reader reader) throws ScriptException {
		init();
		return se.eval(setupReader(reader));
	}

	/* (non-Javadoc)
	 * @see javax.script.ScriptEngine#eval(java.io.Reader, javax.script.Bindings)
	 */
	public Object eval(Reader reader, Bindings n) throws ScriptException {
		init();
		
		if(!(n instanceof DebugBindings)) {
			n = new DebugBindings(n);
		}
		
		return se.eval(setupReader(reader), n);
	}

	/* (non-Javadoc)
	 * @see javax.script.ScriptEngine#eval(java.io.Reader, javax.script.ScriptContext)
	 */
	public Object eval(Reader reader, ScriptContext context)
			throws ScriptException {
		init();
		return se.eval(setupReader(reader), context);
	}

	/* (non-Javadoc)
	 * @see javax.script.ScriptEngine#eval(java.lang.String)
	 */
	public Object eval(String script) throws ScriptException {
		init();
		return se.eval(setup(script));
	}

	/* (non-Javadoc)
	 * @see javax.script.ScriptEngine#eval(java.lang.String, javax.script.Bindings)
	 */
	public Object eval(String script, Bindings n) throws ScriptException {
		init();
		
		if(!(n instanceof DebugBindings)) {
			n = new DebugBindings(n);
		}
		
		return se.eval(setup(script), n);
	}

	/* (non-Javadoc)
	 * @see javax.script.ScriptEngine#eval(java.lang.String, javax.script.ScriptContext)
	 */
	public Object eval(String script, ScriptContext context)
			throws ScriptException {
		init();
		return se.eval(setup(script), context);
	}

	/* (non-Javadoc)
	 * @see javax.script.ScriptEngine#get(java.lang.String)
	 */
	public Object get(String key) {
		return se.get(key);
	}

	/* (non-Javadoc)
	 * @see javax.script.ScriptEngine#getBindings(int)
	 */
	public Bindings getBindings(int scope) {
		return se.getBindings(scope);
	}

	/* (non-Javadoc)
	 * @see javax.script.ScriptEngine#getContext()
	 */
	public ScriptContext getContext() {
		return se.getContext();
	}

	/* (non-Javadoc)
	 * @see javax.script.ScriptEngine#getFactory()
	 */
	public ScriptEngineFactory getFactory() {
		return se.getFactory();
	}

	/* (non-Javadoc)
	 * @see javax.script.Invocable#getInterface(java.lang.Class)
	 */
	public <T> T getInterface(Class<T> clasz) {
		return ((Invocable) se).getInterface(clasz);
	}

	/* (non-Javadoc)
	 * @see javax.script.Invocable#getInterface(java.lang.Object, java.lang.Class)
	 */
	public <T> T getInterface(Object thiz, Class<T> clasz) {
		return ((Invocable) se).getInterface(thiz, clasz);
	}	
	
	/**
	 * Prepares environment to watch the execution.
	 */
	protected void init() {
		se.put("__DEBUG__", this);
		frame.pack();
		frame.setVisible(debugOn);
	}

	/* (non-Javadoc)
	 * @see javax.script.Invocable#invokeFunction(java.lang.String, java.lang.Object[])
	 */
	public Object invokeFunction(String name, Object... args)
			throws ScriptException, NoSuchMethodException {
		return ((Invocable) se).invokeFunction(name, args);
	}

	/* (non-Javadoc)
	 * @see javax.script.Invocable#invokeMethod(java.lang.Object, java.lang.String, java.lang.Object[])
	 */
	public Object invokeMethod(Object thiz, String name, Object... args)
			throws ScriptException, NoSuchMethodException {
		return ((Invocable) se).invokeMethod(thiz, name, args);
	}

	/**
	 * Checks if debug is turned on.
	 * 
	 * @return true, if debug is turned on
	 */
	public boolean isDebugOn() {
		return debugOn;
	}

	/* (non-Javadoc)
	 * @see javax.script.ScriptEngine#put(java.lang.String, java.lang.Object)
	 */
	public void put(String key, Object value) {
		se.put(key, value);
	}
	
	/**
	 * Prepares the environment to debug another script.
	 */
	public void clear() {
		line = 1;
		lblLine.setText("");
		txtCode.setText("");
		txtBindings.setText("");
	}

	/* (non-Javadoc)
	 * @see javax.script.ScriptEngine#setBindings(javax.script.Bindings, int)
	 */
	public void setBindings(Bindings bindings, int scope) {
		
		if(!(bindings instanceof DebugBindings)) {
			bindings = new DebugBindings(bindings);
		} 
		
		se.setBindings(bindings, scope);
	}

	/**
	 * Sets the bypass.
	 * 
	 * @param bypass
	 *            the bypass
	 */
	public void setBypass(boolean bypass) {
		this.bypass = bypass;
	}

	/* (non-Javadoc)
	 * @see javax.script.ScriptEngine#setContext(javax.script.ScriptContext)
	 */
	public void setContext(ScriptContext context) {
		se.setContext(context);
	}

	/**
	 * Turns on/off the debug for next script execution. It won't enable
	 * debug in the current execution, but sure it will disable if you
	 * set it to <code>false</code>.
	 * 
	 * @param debugOn	<code>true</code> to turn on the debug
	 */
	public void setDebugOn(boolean debugOn) {
		this.debugOn = debugOn;
		bypass = !debugOn;
		frame.setVisible(debugOn);
	}

	/**
	 * Insert debug calls to code.
	 * 
	 * @param code the code to be modified
	 * 
	 * @return the code with debug calls
	 */
	protected String setup(String code) {
		StringBuffer debugCode;
		String callbackCode;
		int parentesis = 0;
		int sparentesis = 0;
		boolean quotes = false;
		boolean squotes = false;
		boolean escape = false;
		boolean comment = false;
		boolean lcomment = false;
		boolean ternary = false;
		boolean switchStament = false;
		
		if(debugOn) {
			debugCode = new StringBuffer(code);
			txtCode.setText(txtCode.getText() + "\n" + code);
			if(debugOn) {
				debugCode = new StringBuffer(code);
				for(int i = 0; i < debugCode.length(); i++) {
					if(!escape) {
						switch(debugCode.charAt(i)) {
							case '\n': 
								line++;
								if(lcomment) {
									lcomment = false;
								}
								break;
							case '(': 
								if(!comment && !lcomment 
									&& !quotes && !squotes) {
									parentesis++;
								}
								break;
							case ')': 
								if(!comment && !lcomment 
									&& !quotes && !squotes) {
									parentesis--;
								}
								break;
							case '[': 
								if(!comment && !lcomment 
									&& !quotes && !squotes) {
									sparentesis++;
								}
								break;
							case ']': 
								if(!comment && !lcomment 
									&& !quotes && !squotes) {
									sparentesis--;
								}
								break;
							case '\\':
								if(quotes || squotes) {
									escape = true;
								}
								break;
							case '\'':
								if(!comment && !lcomment && !quotes) {
									squotes = !squotes;
								}
								break;
							case '"':
								if(!comment && !lcomment 
									&& !escape && !squotes) {
									quotes = !quotes;
								}
								break;
							case '/':
								if(!comment && !lcomment && !quotes && !squotes) {
									if(i + 1 <= debugCode.length()) {
										if(debugCode.charAt(i + 1) == '*') {
											comment = true;
											i++;
										} else if(debugCode.charAt(i + 1) == '/') {
											lcomment = true;
											i++;
										}
									}
								}
								break;
							case '*':
								if(comment) {
									if(i + 1 <= debugCode.length()) {
										if(debugCode.charAt(i + 1) == '/') {
											comment = false;
											i++;
										}
									}
								}
								break;
							case 's':
								if(!comment && !lcomment && !quotes && !squotes) {
									if(debugCode.indexOf("switch", i) == i) {
										switchStament = true;
									}
								}
								break;
							case '?':
								if(!comment && !lcomment && !quotes && !squotes) {
									ternary = true;
								}
								break;
							case ':':
								if(!comment && !lcomment && !quotes && !squotes) {
									if(ternary) {
										ternary = false;
										break;
									} else if(switchStament) {
										switchStament = false;
										break;
									}
								}
								// break; // no break here
							case '{':
							case ';':
								if(sparentesis == 0 && parentesis == 0
									&& !switchStament	
									&& !comment && !lcomment 
									&& !quotes && !squotes) {
									callbackCode = "if(__DEBUG__.cb(" + line + ",(function() {}).__parent__)) throw(\"error.debug.stop\");";
									debugCode.replace(i + 1, i + 1, callbackCode);
									i += callbackCode.length();
								}
								break;
						} 
					} else {	
						escape = false;
					}
				}
				code = debugCode.toString();
			}
		}
		
		return code;
	}

	/**
	 * Creates a wrapper to insert debug calls in the code read.
	 * It actually reads all the content from the reader,
	 * setup the code and returns a StringReader.
	 * 
	 * @param reader the reader to be wrapped
	 * 
	 * @return the wrapped reader with debug calls
	 */
	protected Reader setupReader(Reader reader) {
		StringBuffer code = new StringBuffer();
		char buff[] = new char[1024];
		
		try {
			while(reader.read(buff,0,1024) >= 0) {
				code.append(buff);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return new StringReader(setup(code.toString()));
	}
}
