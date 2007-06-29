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
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.concurrent.TimeUnit;

import javax.script.Bindings;
import javax.script.Invocable;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptException;

/**
 * A simple JavaScript debugger
 * 
 * @author Edgar Medrano Pérez
 */
public class ScriptDebugger implements ScriptEngine, Invocable {

	/** The script engine to be watched. */
	private ScriptEngine se;

	/** The frame used to watch. */
	private Frame frame;

	/** The text area used to watch the code and follow the execution. */
	private TextArea txtCode;
	
	/** It's used to set a breakpoint. */
	private int breakPoint = -1;

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
	private TextField txtLine;

	/** 
	 * When this field is set to <code>true</code> an exception is thrown to 
	 * stop the script execution. 
	 */
	private boolean stop;

	/** The text area used to watch the variable's values. */
	private TextArea txtBindings;
	
	/**
	 * @param debug Enable/disable debug from start
	 * @param se    the script engine to be watched
	 */
	public ScriptDebugger(ScriptEngine se, boolean debug) {
		this.se = se;
		this.debugOn = debug;

		frame = new Frame();
		frame.setLayout(new BorderLayout());
		txtCode = new TextArea();
		txtCode.setEditable(false);
		txtCode.addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent arg0) {
				String code = txtCode.getText();
				int line = 1;
				int selection = txtCode.getCaretPosition();
				int position = 0;
				
				while(position >= 0 && position < selection) {
					position = code.indexOf("\n", position);
					if(position < selection) {
						position++;
						line ++;						
					}
				}
		
				txtLine.setText(String.valueOf(line));
			}
		});
		
		frame.add(txtCode, BorderLayout.CENTER);
		txtBindings = new TextArea();
		txtBindings.setEditable(false);
		frame.add(txtBindings, BorderLayout.EAST);
		Container cont = new Container();
		cont.setLayout(new FlowLayout());
		frame.add(cont, BorderLayout.SOUTH);
		final Button btnGoto = new Button("Go to");
		cont.add(btnGoto);
		btnGoto.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				breakPoint = Integer.parseInt(txtLine.getText());
				bypass = true;
			}
		});
		txtLine = new TextField();
		cont.add(txtLine);
		final Button btnContinue = new Button("Continue");
		cont.add(btnContinue);
		btnContinue.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				bypass = true;
			}
		});
		final Button btnStep = new Button("Step");
		cont.add(btnStep);
		btnStep.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					step = true;
					bypass = false;
				}
			});
		final Button btnStop = new Button("Stop");
		cont.add(btnStop);
		btnStop.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					stop = true;
					bypass = true;
				}
			});
		
		frame.addWindowListener(new WindowAdapter() {
		        public void windowClosing(WindowEvent evt) {
					bypass = true;
		            frame.setVisible(false);
		        }
	        });
		
		frame.addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent key) { }

			public void keyReleased(KeyEvent key) { }

			public void keyTyped(KeyEvent key) {
				switch(key.getKeyCode()) {
					case KeyEvent.VK_F8:
						btnContinue.dispatchEvent(new ActionEvent(btnContinue,ActionEvent.ACTION_PERFORMED, ""));
						break;
					case KeyEvent.VK_F5:
						btnStep.dispatchEvent(new ActionEvent(btnStep,ActionEvent.ACTION_PERFORMED, ""));
						break;
						/*
					case KeyEvent.VK_F6:
						btnStep.dispatchEvent(new ActionEvent(btnStep,ActionEvent.ACTION_PERFORMED, ""));
						break;
					case KeyEvent.VK_F6:
						btnStep.dispatchEvent(new ActionEvent(btnStep,ActionEvent.ACTION_PERFORMED, ""));
						break;
						*/
				}
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
	public boolean cb(int line, String local, String global) {
		String code;
		int start;
		int end;
		int stepLine;
		
		code = txtCode.getText();
		start = 0;
		end = code.length();
		stepLine = 1;
		
		while(stepLine < line) {
			start = code.indexOf("\n", start) + 1;
			stepLine ++;
		}
		if(code.indexOf("\n", start) > 0) {
			end = code.indexOf("\n", start);
		}

		txtLine.setText(String.valueOf(line));
		txtCode.requestFocus();
		txtCode.setCaretPosition(start);
		txtCode.setSelectionStart(start);
		txtCode.setSelectionEnd(end);
				
		if(!bypass || line == breakPoint) {
			bypass = false;
			breakPoint = -1;
			
			txtBindings.setText("GLOBAL: \n" + global + "\nLOCAL:\n" + local);
			
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
		try {
			return se.eval(setupReader(reader));
		} catch(ScriptException e) {
			throw(e);
		} finally {
	        frame.setVisible(false);			
		}
	}

	/* (non-Javadoc)
	 * @see javax.script.ScriptEngine#eval(java.io.Reader, javax.script.Bindings)
	 */
	public Object eval(Reader reader, Bindings n) throws ScriptException {
		init();
		
		try {
			return se.eval(setupReader(reader), n);
		} catch(ScriptException e) {
			throw(e);
		} finally {
	        frame.setVisible(false);			
		}
	}

	/* (non-Javadoc)
	 * @see javax.script.ScriptEngine#eval(java.io.Reader, javax.script.ScriptContext)
	 */
	public Object eval(Reader reader, ScriptContext context)
			throws ScriptException {
		init();
		
		try {
			return se.eval(setupReader(reader), context);
		} catch(ScriptException e) {
			throw(e);
		} finally {
	        frame.setVisible(false);			
		}
	}

	/* (non-Javadoc)
	 * @see javax.script.ScriptEngine#eval(java.lang.String)
	 */
	public Object eval(String script) throws ScriptException {
		init();
		
		try {
			return se.eval(setup(script));
		} catch(ScriptException e) {
			throw(e);
		} finally {
	        frame.setVisible(false);			
		}
	}

	/* (non-Javadoc)
	 * @see javax.script.ScriptEngine#eval(java.lang.String, javax.script.Bindings)
	 */
	public Object eval(String script, Bindings n) throws ScriptException {
		init();
		
		try {
			return se.eval(setup(script), n);
		} catch(ScriptException e) {
			throw(e);
		} finally {
	        frame.setVisible(false);			
		}
	}

	/* (non-Javadoc)
	 * @see javax.script.ScriptEngine#eval(java.lang.String, javax.script.ScriptContext)
	 */
	public Object eval(String script, ScriptContext context)
			throws ScriptException {
		init();
		
		try {
			return se.eval(setup(script), context);
		} catch(ScriptException e) {
			throw(e);
		} finally {
	        frame.setVisible(false);			
		}
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
		try {
			se.eval(new InputStreamReader(ScriptDebugger.class.getResourceAsStream("ScriptDebugger.js")));
		} catch (ScriptException e) {
			e.printStackTrace();
		}
		frame.pack();
		frame.setVisible(debugOn);
		frame.requestFocus();
	}

	/* (non-Javadoc)
	 * @see javax.script.Invocable#invokeFunction(java.lang.String, java.lang.Object[])
	 */
	public Object invokeFunction(String name, Object... args)
			throws ScriptException, NoSuchMethodException {
		frame.setVisible(debugOn);
		
		try {
			return ((Invocable) se).invokeFunction(name, args);
		} catch(ScriptException e) {
			throw(e);
		} finally {
	        frame.setVisible(false);			
		}
		
	}

	/* (non-Javadoc)
	 * @see javax.script.Invocable#invokeMethod(java.lang.Object, java.lang.String, java.lang.Object[])
	 */
	public Object invokeMethod(Object thiz, String name, Object... args)
			throws ScriptException, NoSuchMethodException {
		frame.setVisible(debugOn);
		
		try {
			return ((Invocable) se).invokeMethod(thiz, name, args);
		} catch(ScriptException e) {
			throw(e);
		} finally {
	        frame.setVisible(false);			
		}
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
		txtLine.setText("");
		txtCode.setText("");
		txtBindings.setText("");
	}

	/* (non-Javadoc)
	 * @see javax.script.ScriptEngine#setBindings(javax.script.Bindings, int)
	 */
	public void setBindings(Bindings bindings, int scope) {
		se.setBindings(bindings, scope);
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
		StringBuilder debugCode;
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
			debugCode = new StringBuilder((int)(code.length() * 2.5));
			debugCode.append("\n");
			debugCode.append(code);
			txtCode.setText(txtCode.getText() + "\n" + code);
			
			if(debugOn) {
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
									callbackCode = "if(__DEBUG__.cb(" + line + ",__DUMP__(this),__DUMP__((function() {}).__parent__))) throw(\"error.debug.stop\");";
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
			int length;
			while((length = reader.read(buff,0,1024)) >= 0) {
				code.append(buff,0,length);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return new StringReader(setup(code.toString()));
	}
}
