package org.javier.util;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Label;
import java.awt.TextArea;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.event.WindowStateListener;
import java.io.Reader;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import javax.script.Bindings;
import javax.script.Invocable;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptException;
import javax.script.SimpleBindings;

public class ScriptDebugger implements ScriptEngine, Invocable {

	private ScriptEngine se;

	private Frame frame;

	private TextArea txtArea;

	private boolean bypass;

	private boolean step;

	private boolean debugOn;

	private int line = 1;

	private Label lblLine;

	private boolean stop;

	private TextArea txtBindings;
	
	/*Thanks to A. Sundararajan's Weblog*/
	class DebugBindings implements Bindings {
		private Bindings binds;
		
		public DebugBindings(Bindings binds) {
			this.binds = binds;
		}
		
		public Object put(String name, Object value) {
			Object res = binds.put(name, value);
			txtBindings.setText(txtBindings.getText() 
				+ "\nput(" + name + "," + value + ") " + res);
			return res;
		}

		public Object get(Object key) {
			Object res = binds.get(key);
			if(!key.toString().equals("__DEBUG__")) {
				if(key.toString().equals("filled")) {
					String str = "";
					for(Method m:res.getClass().getMethods()) {
						str += "\n" + m.toString();
					}
					txtBindings.setText(txtBindings.getText() 
							+ "\n" + str);					
				}
			}
			return res;
		}

		public boolean containsKey(Object key) {
			boolean res = binds.containsKey(key);
			txtBindings.setText(txtBindings.getText() 
	    		+ "\ncontainsKey(" + key + ") " + res);
			return res;
		}

		public void putAll(Map<? extends String, ? extends Object> toMerge) {
			binds.putAll(toMerge);
			txtBindings.setText(txtBindings.getText() 
				+ "\nputAll(" + toMerge + ")");
		}

		public Object remove(Object key) {
			Object res = binds.remove(key);
			txtBindings.setText(txtBindings.getText() 
				+ "\nremove(" + key + ") " + res);
			return res;
		}

		public void clear() {
			binds.clear();
			txtBindings.setText(txtBindings.getText() 
				+ "\nclear()");
		}

		public boolean containsValue(Object value) {
			boolean res = binds.containsValue(value);
			txtBindings.setText(txtBindings.getText() 
	    		+ "\ncontainsValue(" + value + ") " + res);
			return res;
		}

		public Set<java.util.Map.Entry<String, Object>> entrySet() {
			Set<java.util.Map.Entry<String, Object>> res = binds.entrySet();
			txtBindings.setText(txtBindings.getText() 
		    		+ "\nentrySet() " + res);
			return res;
		}

		public boolean isEmpty() {
			boolean res = binds.isEmpty();
			txtBindings.setText(txtBindings.getText() 
	    		+ "\nisEmpty() " + res);
			return res;
		}

		public Set<String> keySet() {
			Set<String> res = binds.keySet();
			txtBindings.setText(txtBindings.getText() 
		    	+ "\nkeySet() " + res);
			return res;
		}

		public int size() {
			int res = binds.size();
			txtBindings.setText(txtBindings.getText() 
				+ "\nsize() " + res);
			return res;
		}

		public Collection<Object> values() {
			Collection<Object> res = binds.values();
			txtBindings.setText(txtBindings.getText() 
		    	+ "\nvalues() " + res);
			return res;
		}		
	}

	public ScriptDebugger(ScriptEngine se, boolean debug) {
		this.se = se;
		/*
		setBindings(getBindings(ScriptContext.GLOBAL_SCOPE), ScriptContext.GLOBAL_SCOPE);		
		setBindings(getBindings(ScriptContext.ENGINE_SCOPE), ScriptContext.ENGINE_SCOPE);
		*/
		
		this.debugOn = debug;

		frame = new Frame();
		frame.setLayout(new BorderLayout());
		txtArea = new TextArea();
		txtArea.setEditable(false);
		frame.add(txtArea, BorderLayout.CENTER);
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
					bypass = false;
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
	
	public ScriptDebugger(ScriptEngine se) {
		this(se,true);
	}

	public boolean cb(int line) {
		String code;
		int start;
		int end;
		int stepLine;
		
		if(!bypass) {
			 
			Bindings bind = getBindings(ScriptContext.ENGINE_SCOPE);
			/*
			Set<Entry<String, Object>> set = bind.entrySet();
			StringBuffer bufBinds = new StringBuffer();
			for(Entry e:set) {
				bufBinds.append(e.getKey());
				bufBinds.append(':');
				bufBinds.append(e.getValue());
				bufBinds.append('\n');
			}
			bind = getBindings(ScriptContext.GLOBAL_SCOPE);
			set = bind.entrySet();
			for(Entry e:set) {
				bufBinds.append(e.getKey());
				bufBinds.append(':');
				bufBinds.append(e.getValue());
				bufBinds.append('\n');
			}
			
			txtBindings.setText(bufBinds.toString());
			*/
			code = txtArea.getText();
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
			txtArea.requestFocus();
			txtArea.setCaretPosition(start);
			txtArea.setSelectionStart(start);
			txtArea.setSelectionEnd(end);
			step = false;
			stop = false;
			while(!bypass && !step && !stop) {
				try {
					TimeUnit.MILLISECONDS.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		if(stop) {
			return true;
		}
		return false;
	}

	public Bindings createBindings() {
		return se.createBindings();
	}

	public Object eval(Reader reader) throws ScriptException {
		init();
		return se.eval(setupReader(reader));
	}

	public Object eval(Reader reader, Bindings n) throws ScriptException {
		init();
		
		if(!(n instanceof DebugBindings)) {
			n = new DebugBindings(n);
		}
		
		return se.eval(setupReader(reader), n);
	}

	public Object eval(Reader reader, ScriptContext context)
			throws ScriptException {
		init();
		return se.eval(setupReader(reader), context);
	}

	public Object eval(String script) throws ScriptException {
		init();
		return se.eval(setup(script));
	}

	public Object eval(String script, Bindings n) throws ScriptException {
		init();
		
		if(!(n instanceof DebugBindings)) {
			n = new DebugBindings(n);
		}
		
		return se.eval(setup(script), n);
	}

	public Object eval(String script, ScriptContext context)
			throws ScriptException {
		init();
		return se.eval(setup(script), context);
	}

	public Object get(String key) {
		return se.get(key);
	}

	public Bindings getBindings(int scope) {
		return se.getBindings(scope);
	}

	public ScriptContext getContext() {
		return se.getContext();
	}

	public ScriptEngineFactory getFactory() {
		return se.getFactory();
	}

	public <T> T getInterface(Class<T> clasz) {
		return ((Invocable) se).getInterface(clasz);
	}

	public <T> T getInterface(Object thiz, Class<T> clasz) {
		return ((Invocable) se).getInterface(thiz, clasz);
	}

	protected void init() {
		se.put("__DEBUG__", this);
		bypass = false;
		frame.pack();
		frame.setVisible(true);
	}

	public Object invokeFunction(String name, Object... args)
			throws ScriptException, NoSuchMethodException {
		return ((Invocable) se).invokeFunction(name, args);
	}

	public Object invokeMethod(Object thiz, String name, Object... args)
			throws ScriptException, NoSuchMethodException {
		return ((Invocable) se).invokeMethod(thiz, name, args);
	}

	public boolean isDebugOn() {
		return debugOn;
	}

	public void put(String key, Object value) {
		se.put(key, value);
	}
	
	public void clear() {
		line = 1;
		txtArea.setText("");
	}

	public void setBindings(Bindings bindings, int scope) {
		
		if(!(bindings instanceof DebugBindings)) {
			bindings = new DebugBindings(bindings);
		} 
		
		se.setBindings(bindings, scope);
	}

	public void setBypass(boolean bypass) {
		this.bypass = bypass;
	}

	public void setContext(ScriptContext context) {
		se.setContext(context);
	}

	public void setDebugOn(boolean debugOn) {
		this.debugOn = debugOn;
	}

	protected String setup(String code) {
		StringBuffer debugCode = new StringBuffer(code);
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
		
		
		txtArea.setText(txtArea.getText() + "\n" + code);
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
								callbackCode = "if(__DEBUG__.cb(" + line + ")) return;";
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
		return code;
	}

	protected Reader setupReader(Reader reader) {
		return reader;
	}
}
