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

import javax.script.Bindings;
import javax.script.Invocable;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptException;
import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.JSplitPane;
import java.awt.FlowLayout;
import javax.swing.JButton;
import javax.swing.JTextField;
import java.awt.Dimension;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.concurrent.TimeUnit;

import javax.swing.JTextArea;
import javax.swing.JScrollPane;

/**
 * A simple JavaScript debugger implemented as an ScriptEngine wrapper.
 * <p><i>Usage:</i>
 * <pre>ScriptEngine seJavaScript = sem.getEngineByName("JavaScript");
 * seJavaScript = new ScriptDebugger(seJavaScript, true);</pre></p>
 */
public class ScriptDebugger extends JFrame implements ScriptEngine, Invocable{

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/** The j content pane. */
	private JPanel jContentPane = null;
	
	/** The j panel. */
	private JPanel jPanel = null;
	
	/** The j split pane. */
	private JSplitPane jSplitPane = null;
	
	/** The btn goto. */
	private JButton btnGoto = null;
	
	/** The txt line. */
	private JTextField txtLine = null;
	
	/** The btn continue. */
	private JButton btnContinue = null;
	
	/** The btn step. */
	private JButton btnStep = null;
	
	/** The btn stop. */
	private JButton btnStop = null;
	
	/** The txt code. */
	private JTextArea txtCode = null;
	
	/** The txt bindings. */
	private JTextArea txtBindings = null;
	
	/** The scr code. */
	private JScrollPane scrCode = null;
	
	/** The scr bindings. */
	private JScrollPane scrBindings = null;
	
	/** The script engine to be watched. */
	private ScriptEngine se;
	
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

	/**
	 * When this field is set to <code>true</code> an exception is thrown to
	 * stop the script execution.
	 */
	private boolean stop;

	/**
	 * This is the default constructor.
	 */
	public ScriptDebugger() {
		super();
		
		initialize();
	}

	/**
	 * The Constructor.
	 * 
	 * @param se
	 *            the script engine to be watched
	 * @param debug
	 *            Enable/disable debug from start
	 */
	public ScriptDebugger(ScriptEngine se, boolean debug) {
		this();
		this.se = se;
		this.debugOn = debug;
	}

	/**
	 * This method initializes this.
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setContentPane(getJContentPane());
		this.setSize(353, 205);
		this.setTitle("JFrame");
		this.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				bypass = true;
				e.getWindow().setVisible(false);
			}
		});
	}

	/**
	 * This method initializes jContentPane.
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			jContentPane = new JPanel();
			jContentPane.setLayout(new BorderLayout());
			jContentPane.add(getJPanel(), BorderLayout.SOUTH);
			jContentPane.add(getJSplitPane(), BorderLayout.CENTER);
		}
		return jContentPane;
	}

	/**
	 * This method initializes jPanel.
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanel() {
		if (jPanel == null) {
			jPanel = new JPanel();
			jPanel.setLayout(new FlowLayout());
			jPanel.add(getBtnGoto(), null);
			jPanel.add(getTxtLine(), null);
			jPanel.add(getBtnContinue(), null);
			jPanel.add(getBtnStep(), null);
			jPanel.add(getBtnStop(), null);
		}
		return jPanel;
	}

	/**
	 * This method initializes jSplitPane.
	 * 
	 * @return javax.swing.JSplitPane
	 */
	private JSplitPane getJSplitPane() {
		if (jSplitPane == null) {
			jSplitPane = new JSplitPane();
			jSplitPane.setDividerLocation(200);
			jSplitPane.setRightComponent(getScrBindings());
			jSplitPane.setLeftComponent(getScrCode());
		}
		return jSplitPane;
	}

	/**
	 * This method initializes btnGoto.
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getBtnGoto() {
		if (btnGoto == null) {
			btnGoto = new JButton();
			btnGoto.setText("Go to");
			btnGoto.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					breakPoint = line;
					bypass = true;
				}
			});
		}
		return btnGoto;
	}

	/**
	 * This method initializes txtLine.
	 * 
	 * @return javax.swing.JTextField
	 */
	public JTextField getTxtLine() {
		if (txtLine == null) {
			txtLine = new JTextField();
			txtLine.setText("");
			txtLine.setPreferredSize(new Dimension(25, 20));
		}
		return txtLine;
	}

	/**
	 * This method initializes btnContinue.
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getBtnContinue() {
		if (btnContinue == null) {
			btnContinue = new JButton();
			btnContinue.setText("Continue");
			btnContinue.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					bypass = true;
				}
			});
		}
		return btnContinue;
	}

	/**
	 * This method initializes btnStep.
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getBtnStep() {
		if (btnStep == null) {
			btnStep = new JButton();
			btnStep.setText("Step");
			btnStep.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					step = true;
					bypass = false;
				}
			});
		}
		return btnStep;
	}

	/**
	 * This method initializes btnStop.
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getBtnStop() {
		if (btnStop == null) {
			btnStop = new JButton();
			btnStop.setText("Stop");
			btnStop.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					stop = true;
					bypass = true;
				}
			});
		}
		return btnStop;
	}

	/**
	 * This method initializes txtCode.
	 * 
	 * @return javax.swing.JTextArea
	 */
	public JTextArea getTxtCode() {
		if (txtCode == null) {
			txtCode = new JTextArea();
			txtCode.setTabSize(4);
			txtCode.setEditable(false);
			txtCode.setBorder(new LineNumberedBorder(LineNumberedBorder.LEFT_SIDE, LineNumberedBorder.RIGHT_JUSTIFY));
			txtCode.addMouseListener(new java.awt.event.MouseAdapter() {
				public void mouseReleased(java.awt.event.MouseEvent e) {
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
		}
		return txtCode;
	}

	/**
	 * This method initializes txtBindings.
	 * 
	 * @return javax.swing.JTextArea
	 */
	public JTextArea getTxtBindings() {
		if (txtBindings == null) {
			txtBindings = new JTextArea();
			txtBindings.setEditable(false);
		}
		return txtBindings;
	}

	/**
	 * This method initializes scrCode.
	 * 
	 * @return javax.swing.JScrollPane
	 */
	private JScrollPane getScrCode() {
		if (scrCode == null) {
			scrCode = new JScrollPane();
			scrCode.setViewportView(getTxtCode());
		}
		return scrCode;
	}

	/**
	 * This method initializes scrBindings.
	 * 
	 * @return javax.swing.JScrollPane
	 */
	private JScrollPane getScrBindings() {
		if (scrBindings == null) {
			scrBindings = new JScrollPane();
			scrBindings.setViewportView(getTxtBindings());
		}
		return scrBindings;
	}

	/**
	 * This method is used to make a callback. A call to this method is inserted
	 * in almost every script line, like this:
	 * 
	 * <code>if(__DEBUG__.cb(111,__DUMP__(this),__DUMP__((function() {}).__parent__))
	 * throw("error.debug.stop");</code>
	 * 
	 * @param global
	 *            the global scope variables dump
	 * @param line
	 *            the current execution line
	 * @param local
	 *            the local scope variables dump
	 * 
	 * @return <code>true</code> if the user press stop button (in the script
	 *         this will fire the "error.debug.stop" exception.<br>
	 *         <code>false</code> if the user press step (in the script this
	 *         will continue with the execution)
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
	        setVisible(false);			
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
	        setVisible(false);			
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
	        setVisible(false);			
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
	        setVisible(false);			
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
	        setVisible(false);			
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
	        setVisible(false);			
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
		pack();
		setVisible(debugOn);
		requestFocus();
	}

	/* (non-Javadoc)
	 * @see javax.script.Invocable#invokeFunction(java.lang.String, java.lang.Object[])
	 */
	public Object invokeFunction(String name, Object... args)
			throws ScriptException, NoSuchMethodException {
		setVisible(debugOn);
		
		try {
			return ((Invocable) se).invokeFunction(name, args);
		} catch(ScriptException e) {
			throw(e);
		} finally {
	        setVisible(false);			
		}
		
	}

	/* (non-Javadoc)
	 * @see javax.script.Invocable#invokeMethod(java.lang.Object, java.lang.String, java.lang.Object[])
	 */
	public Object invokeMethod(Object thiz, String name, Object... args)
			throws ScriptException, NoSuchMethodException {
		setVisible(debugOn);
		
		try {
			return ((Invocable) se).invokeMethod(thiz, name, args);
		} catch(ScriptException e) {
			throw(e);
		} finally {
	        setVisible(false);			
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
	 * Turns on/off the debug for next script execution. It won't enable debug
	 * in the current execution, but sure it will disable if you set it to
	 * <code>false</code>.
	 * 
	 * @param debugOn
	 *            <code>true</code> to turn on the debug
	 */
	public void setDebugOn(boolean debugOn) {
		this.debugOn = debugOn;
		bypass = !debugOn;
		setVisible(debugOn);
	}

	/**
	 * Insert debug calls to code.
	 * 
	 * @param code
	 *            the code to be modified
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
	 * Creates a wrapper to insert debug calls in the code read. It actually
	 * reads all the content from the reader, setup the code and returns a
	 * StringReader.
	 * 
	 * @param reader
	 *            the reader to be wrapped
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

}  //  @jve:decl-index=0:visual-constraint="10,10"
