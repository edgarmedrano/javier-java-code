package org.javier.util;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Container;
import java.awt.Frame;
import java.awt.TextArea;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.event.WindowStateListener;
import java.io.Reader;
import java.util.concurrent.TimeUnit;

import javax.script.Bindings;
import javax.script.Invocable;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptException;

public class ScriptDebugger implements ScriptEngine, Invocable {

	private ScriptEngine se;

	private Frame frame;

	private TextArea txtArea;

	private boolean bypass;

	private boolean step;

	public ScriptDebugger(ScriptEngine se) {
		this.se = se;

		frame = new Frame();
		txtArea = new TextArea();
		txtArea.setEditable(false);
		frame.add(txtArea, BorderLayout.CENTER);
		Container cont = new Container();
		frame.add(cont, BorderLayout.SOUTH);
		Button btnContinue = new Button("Continue");
		cont.add(btnContinue);
		btnContinue.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				bypass = true;
				step = true;
			}
		});
		Button btnStep = new Button("Step");
		cont.add(btnStep);
		btnStep.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					step = true;
				}
			});
		
		frame.addWindowStateListener(new WindowAdapter() {
		        public void windowClosing(WindowEvent evt) {
		            frame.setVisible(false);
		            //frame.dispose();
		        }
	        });
	}

	protected void init(String code) {
		se.put("__DEBUG__", this);
		bypass = false;
		txtArea.setText(code);
		frame.pack();
		frame.setVisible(true);
	}

	public boolean cb(int line) {
		String code = txtArea.getText();
		int start = 0;
		int end = code.length();
		int stepLine = 1;
		while(stepLine < line) {
			start = code.indexOf("\n", start) + 1;
			stepLine ++;
		}
		if(code.indexOf("\n", start) > 0) {
			end = code.indexOf("\n", start);
		}
		
		txtArea.setSelectionStart(start);
		txtArea.setSelectionEnd(end);
		//txtArea.setCaretPosition(start);
		step = false;
		if (!bypass) {
			while(!step) {
				try {
					TimeUnit.MILLISECONDS.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return true;
	}

	public Bindings createBindings() {
		return se.createBindings();
	}

	public Object eval(Reader reader) throws ScriptException {
		init("");
		return se.eval(setupReader(reader));
	}

	public Object eval(Reader reader, Bindings n) throws ScriptException {
		init("");
		return se.eval(setupReader(reader), n);
	}

	public Object eval(Reader reader, ScriptContext context)
			throws ScriptException {
		init("");
		return se.eval(setupReader(reader), context);
	}

	public Object eval(String script) throws ScriptException {
		init(script);
		return se.eval(setup(script));
	}

	public Object eval(String script, Bindings n) throws ScriptException {
		init(script);
		return se.eval(setup(script), n);
	}

	public Object eval(String script, ScriptContext context)
			throws ScriptException {
		init(script);
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

	public Object invokeFunction(String name, Object... args)
			throws ScriptException, NoSuchMethodException {
		return ((Invocable) se).invokeFunction(name, args);
	}

	public Object invokeMethod(Object thiz, String name, Object... args)
			throws ScriptException, NoSuchMethodException {
		return ((Invocable) se).invokeMethod(thiz, name, args);
	}

	public void put(String key, Object value) {
		se.put(key, value);
	}

	public void setBindings(Bindings bindings, int scope) {
		se.setBindings(bindings, scope);
	}

	public void setContext(ScriptContext context) {
		se.setContext(context);
	}

	protected String setup(String code) {
		return code;
	}

	protected Reader setupReader(Reader reader) {
		return reader;
	}

	public void setBypass(boolean bypass) {
		this.bypass = bypass;
	}
}
