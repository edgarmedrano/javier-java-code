/**
 * File:        JavierGUI.java
 * Description: JAVIER GUI
 * Author:      Edgar Medrano P�rez
 *              edgarmedrano at gmail dot com
 * Created:     2007.10.12
 * Company:     JAVIER project
 *              http://javier.sourceforge.net
 * Notes:        
 */
package org.javier.browser;

import java.awt.BorderLayout;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.JMenuItem;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JToolBar;
import javax.swing.JButton;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import java.awt.Dimension;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

import javax.swing.BoxLayout;

import org.javier.browser.event.ErrorListener;
import org.javier.browser.event.JavierListener;
import org.javier.browser.event.OutputListener;
import org.javier.browser.handlers.InputHandler;
import org.javier.browser.handlers.MSXMLHTTPNetworkHandler;
import org.javier.browser.handlers.SAPIOutputHandler;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JToggleButton;
import java.awt.GridLayout;
import javax.swing.JScrollPane;

/**
 * JAVIER GUI.
 */
public class JavierGUI extends JFrame implements InputHandler, JavierListener, OutputListener, ErrorListener {
	
	/**
	 * The Enum HistoryManagement.
	 */
	private enum HistoryManagement { /** The ADD. */
		 ADD, /** The NAVIGATE. */
		 NAVIGATE, /** The CLEAR. */
		 CLEAR }
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/** The Constant KEY_1. */
	private static final int KEY_1 = 0;
	
	/** The Constant KEY_AST. */
	private static final int KEY_AST = 9;
	
	/** The Constant KEY_0. */
	private static final int KEY_0 = 10;
	
	/** The Constant KEY_POUND. */
	private static final int KEY_POUND = 11;
	
	/** The j content pane. */
	private JPanel jContentPane = null;
	
	/** The menubar. */
	private JMenuBar menubar = null;
	
	/** The mnu file. */
	private JMenu mnuFile = null;
	
	/** The tlb navigate. */
	private JToolBar tlbNavigate = null;
	
	/** The btn home. */
	private JButton btnHome = null;
	
	/** The spl main. */
	private JSplitPane splMain = null;
	
	/** The sep file1. */
	private JSeparator sepFile1 = null;
	
	/** The mni exit. */
	private JMenuItem mniExit = null;
	
	/** The txt address. */
	private JTextField txtAddress = null;
	
	/** The txt message. */
	private JTextArea txtMessage = null;
	
	/** The txt input. */
	private JTextArea txtInput = null;
	
	/** The btn go. */
	private JButton btnGo = null;
	
	/** The javier. */
	private Javier javier = null;
	
	/** The home address. */
	private String homeAddress;  //  @jve:decl-index=0:
	
	/** The input ready. */
	private boolean inputReady = false;
	
	/** The history. */
	private ArrayList<String> history = new ArrayList<String>();  //  @jve:decl-index=0:
	
	/** The history index. */
	private int historyIndex = -1;
	
	/** The mnu view. */
	private JMenu mnuView = null;
	
	/** The mni open. */
	private JMenuItem mniOpen = null;
	
	/** The mnu bookmarks. */
	private JMenu mnuBookmarks = null;
	
	/** The mni view JS. */
	private JMenuItem mniViewJS = null;
	
	/** The mni view VXML. */
	private JMenuItem mniViewVXML = null;
	
	/** The btn back. */
	private JButton btnBack = null;
	
	/** The btn forward. */
	private JButton btnForward = null;
	
	/** The mnu go to. */
	private JMenu mnuGoTo = null;
	
	/** The sep view1. */
	private JSeparator sepView1 = null;
	
	/** The mni back. */
	private JMenuItem mniBack = null;
	
	/** The mni forward. */
	private JMenuItem mniForward = null;
	
	/** The sep view go to1. */
	private JSeparator sepViewGoTo1 = null;
	
	/** The mni home. */
	private JMenuItem mniHome = null;
	
	/** The mni add bookmark. */
	private JMenuItem mniAddBookmark = null;
	
	/** The sep bookmark1. */
	private JSeparator sepBookmark1 = null;
	
	/** The mni auto run. */
	private JCheckBoxMenuItem mniAutoRun = null;
	
	/** The pnl center. */
	private JPanel pnlCenter = null;
	
	/** The tlb chat. */
	private JToolBar tlbChat = null;
	
	/** The tgl keypad. */
	private JToggleButton tglKeypad = null;
	
	/** The tlb keypad. */
	private JToolBar tlbKeypad = null;
	
	/** The btn key. */
	private JButton btnKey[] = null;
	
	/** The pnl keypad. */
	private JPanel pnlKeypad = null;
	
	/** The scr message. */
	private JScrollPane scrMessage = null;
	
	/** The mni debug. */
	private JCheckBoxMenuItem mniDebug = null;
	
	/**
	 * This is the default constructor.
	 */
	public JavierGUI() {
		super();
		initialize();
		this.setJavier(getJavier());
		btnHome.doClick();
	}

	/**
	 * This method initializes this.
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(378, 283);
		this.setJMenuBar(getMenubar());
		this.setContentPane(getJContentPane());
		this.setTitle("JAVIER");
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
			jContentPane.add(getTlbNavigate(), BorderLayout.NORTH);
			jContentPane.add(getPnlCenter(), BorderLayout.CENTER);
		}
		return jContentPane;
	}

	/**
	 * This method initializes menubar.
	 * 
	 * @return javax.swing.JMenuBar
	 */
	private JMenuBar getMenubar() {
		if (menubar == null) {
			menubar = new JMenuBar();		
			menubar.add(getMnuFile());
			menubar.add(getMnuView());
			menubar.add(getMnuBookmarks());
		}
		return menubar;
	}

	/**
	 * This method initializes mnuFile.
	 * 
	 * @return javax.swing.JMenu
	 */
	private JMenu getMnuFile() {
		if (mnuFile == null) {
			mnuFile = new JMenu();
			mnuFile.setText("File");
			mnuFile.add(getMniOpen());
			mnuFile.add(getSepFile1());
			mnuFile.add(getMniExit());
		}
		return mnuFile;
	}


	/**
	 * This method initializes sepFile1.
	 * 
	 * @return javax.swing.JSeparator
	 */
	private JSeparator getSepFile1() {
		if (sepFile1 == null) {
			sepFile1 = new JSeparator();
		}
		return sepFile1;
	}

	/**
	 * This method initializes tlbNavigate.
	 * 
	 * @return javax.swing.JToolBar
	 */
	private JToolBar getTlbNavigate() {
		if (tlbNavigate == null) {
			tlbNavigate = new JToolBar();
			tlbNavigate.setLayout(new BoxLayout(getTlbNavigate(), BoxLayout.X_AXIS));
			tlbNavigate.add(getBtnBack());
			tlbNavigate.add(getBtnForward());
			tlbNavigate.add(getBtnHome());
			tlbNavigate.add(getTxtAddress());
			tlbNavigate.add(getBtnGo());
		}
		return tlbNavigate;
	}

	/**
	 * This method initializes btnHome.
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getBtnHome() {
		if (btnHome == null) {
			btnHome = new JButton();
			btnHome.setText("Home");
			btnHome.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					goTo(homeAddress);
				}
			});
		}
		return btnHome;
	}

	/**
	 * This method initializes splMain.
	 * 
	 * @return javax.swing.JSplitPane
	 */
	private JSplitPane getSplMain() {
		if (splMain == null) {
			splMain = new JSplitPane();
			splMain.setOrientation(JSplitPane.VERTICAL_SPLIT);
			splMain.setBottomComponent(getTxtInput());
			splMain.setTopComponent(getScrMessage());
			splMain.setDividerLocation(100);
		}
		return splMain;
	}

	/**
	 * This method initializes mniExit.
	 * 
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getMniExit() {
		if (mniExit == null) {
			mniExit = new JMenuItem();
			mniExit.setText("Exit");
			mniExit.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					System.exit(0);
				}
			});
		}
		return mniExit;
	}

	/**
	 * This method initializes txtAddress.
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getTxtAddress() {
		if (txtAddress == null) {
			txtAddress = new JTextField();
			txtAddress.setPreferredSize(new Dimension(200, 20));
			txtAddress.addKeyListener(new java.awt.event.KeyAdapter() {
				public void keyTyped(java.awt.event.KeyEvent e) {
					if(e.getKeyChar() == '\n') {
						goTo(txtAddress.getText(), HistoryManagement.CLEAR);
					}
				}
			});
		}
		return txtAddress;
	}

	/**
	 * This method initializes txtMessage.
	 * 
	 * @return javax.swing.JTextArea
	 */
	private JTextArea getTxtMessage() {
		if (txtMessage == null) {
			txtMessage = new JTextArea();
			txtMessage.setLineWrap(true);
			txtMessage.setEditable(false);
		}
		return txtMessage;
	}

	/**
	 * This method initializes txtInput.
	 * 
	 * @return javax.swing.JTextArea
	 */
	private JTextArea getTxtInput() {
		if (txtInput == null) {
			txtInput = new JTextArea();
			txtInput.setLineWrap(true);
			txtInput.addKeyListener(new java.awt.event.KeyAdapter() {
				public void keyTyped(java.awt.event.KeyEvent e) {
					if(e.getKeyChar() == '\n' || e.getKeyChar() == '#') {
						inputReady = true;
					}
				}
			});
		}
		return txtInput;
	}

	/**
	 * This method initializes btnGo.
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getBtnGo() {
		if (btnGo == null) {
			btnGo = new JButton();
			btnGo.setText("Go");
			btnGo.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					goTo(txtAddress.getText(), HistoryManagement.CLEAR);
				}
			});
		}
		return btnGo;
	}

	/**
	 * Gets the javier engine.
	 * 
	 * @return the javier
	 */
	private Javier getJavier() {
		if(javier == null) {
		    String ttsProvider = "MSSAPI";
		    String voiceName = "Rosa";
		    String logFile = "Javier.log";
		    homeAddress = "http://localhost/javier/default.vxml";
			
		    try {
			    Properties properties = new Properties();
			    
		        properties.load(new FileInputStream("Javier.properties"));
		        ttsProvider = properties.getProperty("tts_class", ttsProvider);
			    voiceName = properties.getProperty("tts_voice", voiceName);
		    	homeAddress = properties.getProperty("home_address", homeAddress);
		    	logFile = properties.getProperty("log_file", logFile);
		    } catch (IOException e) {
		    	
		    }
			
			javier = new Javier(this,new MSXMLHTTPNetworkHandler());
			javier.addJavierListener(this);
			javier.addOutputListener(this);
			javier.addOutputListener(new SAPIOutputHandler(voiceName));
			javier.addErrorListener(this);
			/*
			javier.addLogListener(new ConsoleLogHandler());
			*/	
			/*
			try {
				javier.mainLoop(homeAddress);
			} catch (IOException ex) {
				ex.printStackTrace();
			}
			*/
		}
		return javier;
	}

	/**
	 * Sets the javier engine.
	 * 
	 * @param javier
	 *            the javier
	 */
	private void setJavier(Javier javier) {
		this.javier = javier;
	}

	/* (non-Javadoc)
	 * @see org.javier.browser.handlers.InputHandler#getInput(java.lang.String)
	 */
	public String getInput(String text) throws IOException {
		return getInput(text,"");
	}

	/* (non-Javadoc)
	 * @see org.javier.browser.handlers.InputHandler#getInput(java.lang.String, java.lang.String)
	 */
	public String getInput(String text, String value) throws IOException {
		return getInput(text,"",0,256);
	}

	/* (non-Javadoc)
	 * @see org.javier.browser.handlers.InputHandler#getInput(java.lang.String, java.lang.String, int, int)
	 */
	public String getInput(String text, String value, int min, int max)
			throws IOException {
		
		String strInput = "";
		String timeout = javier.getProperty("timeout");
		long time = 0;
		inputReady = false;
		
		if(timeout.indexOf("ms") >= 0) {
			timeout = timeout.replaceFirst("ms", "");
			time = Long.parseLong(timeout);
		} else {
			if(timeout.indexOf("s") > 0) {
				timeout = timeout.replaceFirst("s", "");
				time = Long.parseLong(timeout) * 1000;
			}
		}		
		
		if(time == 0) {
			time = 10000;
		}

		for (long i =0; i < time; i += 10) {
			strInput = txtInput.getText();
			
			if(strInput.length() >= max) {
				break;
			}
			
			if(strInput.contains("#")) {
				break;
			}
			
			if(inputReady) {
				break;
			}
			
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} 
		
		txtInput.setText("");
		txtMessage.setText(txtMessage.getText() + "\nYou: " + strInput);
		txtMessage.setCaretPosition(txtMessage.getText().length());
		return strInput;
		
		/*return JOptionPane.showInputDialog(this, text, value);*/
	}

	/* (non-Javadoc)
	 * @see org.javier.browser.event.JavierListener#excecutionEnded(int)
	 */
	@Override
	public void excecutionEnded(int endCode) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.javier.browser.event.JavierListener#loadStateChanged(int)
	 */
	@Override
	public void loadStateChanged(int readyState) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.javier.browser.event.JavierListener#urlChanged(java.lang.String)
	 */
	@Override
	public void urlChanged(String url) {
		txtAddress.setText(url);
	}

	/* (non-Javadoc)
	 * @see org.javier.browser.event.OutputListener#addText(java.lang.String)
	 */
	@Override
	public void addText(String text) throws IOException {
		txtMessage.setText(txtMessage.getText() + "\nJavier: " + text);
		txtMessage.setCaretPosition(txtMessage.getText().length());
	}

	/* (non-Javadoc)
	 * @see org.javier.browser.event.OutputListener#clearText()
	 */
	@Override
	public void clearText() throws IOException {
		//txtMessage.setText("");
	}

	/* (non-Javadoc)
	 * @see org.javier.browser.event.OutputListener#waitUntilDone()
	 */
	@Override
	public void waitUntilDone() throws IOException {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.javier.browser.event.ErrorListener#errorFound(java.lang.String)
	 */
	@Override
	public void errorFound(String description) {
		JOptionPane.showMessageDialog(this, description);
	}

	/**
	 * This method initializes mnuView.
	 * 
	 * @return javax.swing.JMenu
	 */
	private JMenu getMnuView() {
		if (mnuView == null) {
			mnuView = new JMenu();
			mnuView.setText("View");
			mnuView.add(getMnuGoTo());
			mnuView.add(getSepView1());
			mnuView.add(getMniDebug());
			mnuView.add(getMniAutoRun());
			mnuView.add(getMniViewVXML());
			mnuView.add(getMniViewJS());
		}
		return mnuView;
	}

	/**
	 * This method initializes mniOpen.
	 * 
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getMniOpen() {
		if (mniOpen == null) {
			mniOpen = new JMenuItem();
			mniOpen.setText("Open...");
			mniOpen.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					String url = JOptionPane.showInputDialog(mniOpen,"Address","Open",JOptionPane.QUESTION_MESSAGE);
					if(url != null) {
						if(!url.equals("")) {
							goTo(url, HistoryManagement.CLEAR);
						}
					} 
				}
			});
		}
		return mniOpen;
	}

	/**
	 * This method initializes mnuBookmarks.
	 * 
	 * @return javax.swing.JMenu
	 */
	private JMenu getMnuBookmarks() {
		if (mnuBookmarks == null) {
			mnuBookmarks = new JMenu();
			mnuBookmarks.setText("Bookmarks");
			mnuBookmarks.add(getMniAddBookmark());
			mnuBookmarks.add(getSepBookmark1());
		}
		return mnuBookmarks;
	}

	/**
	 * This method initializes mniViewJS.
	 * 
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getMniViewJS() {
		if (mniViewJS == null) {
			mniViewJS = new JMenuItem();
			mniViewJS.setText("View JavaScript");
			mniViewJS.setEnabled(false);
		}
		return mniViewJS;
	}

	/**
	 * This method initializes mniViewVXML.
	 * 
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getMniViewVXML() {
		if (mniViewVXML == null) {
			mniViewVXML = new JMenuItem();
			mniViewVXML.setText("View VoiceXML");
			mniViewVXML.setEnabled(false);
		}
		return mniViewVXML;
	}

	/**
	 * This method initializes btnBack.
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getBtnBack() {
		if (btnBack == null) {
			btnBack = new JButton();
			btnBack.setText("<");
			btnBack.setToolTipText("Back");
			btnBack.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					if(historyIndex > 0) {
						historyIndex --;
						goTo(history.get(historyIndex), HistoryManagement.NAVIGATE);
					}
				}
			});
		}
		return btnBack;
	}

	/**
	 * This method initializes btnForward.
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getBtnForward() {
		if (btnForward == null) {
			btnForward = new JButton();
			btnForward.setText(">");
			btnForward.setToolTipText("Forward");
			btnForward.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					if(historyIndex < history.size() - 1) {
						historyIndex ++;
						goTo(history.get(historyIndex), HistoryManagement.NAVIGATE);
					}
				}
			});
		}
		return btnForward;
	}

	/**
	 * This method initializes mnuGoTo.
	 * 
	 * @return javax.swing.JMenu
	 */
	private JMenu getMnuGoTo() {
		if (mnuGoTo == null) {
			mnuGoTo = new JMenu();
			mnuGoTo.setText("Go to");
			mnuGoTo.add(getMniBack());
			mnuGoTo.add(getMniForward());
			mnuGoTo.add(getSepViewGoTo1());
			mnuGoTo.add(getMniHome());
		}
		return mnuGoTo;
	}

	/**
	 * This method initializes sepView1.
	 * 
	 * @return javax.swing.JSeparator
	 */
	private JSeparator getSepView1() {
		if (sepView1 == null) {
			sepView1 = new JSeparator();
		}
		return sepView1;
	}

	/**
	 * This method initializes mniBack.
	 * 
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getMniBack() {
		if (mniBack == null) {
			mniBack = new JMenuItem();
			mniBack.setText("Back");
			mniBack.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					btnBack.doClick();
				}
			});
		}
		return mniBack;
	}

	/**
	 * This method initializes mniForward.
	 * 
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getMniForward() {
		if (mniForward == null) {
			mniForward = new JMenuItem();
			mniForward.setText("Forward");
			mniForward.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					btnForward.doClick();
				}
			});
		}
		return mniForward;
	}
	
	/**
	 * This method initializes sepViewGoTo1.
	 * 
	 * @return javax.swing.JSeparator
	 */
	private JSeparator getSepViewGoTo1() {
		if (sepViewGoTo1 == null) {
			sepViewGoTo1 = new JSeparator();
		}
		return sepViewGoTo1;
	}

	/**
	 * This method initializes mniHome.
	 * 
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getMniHome() {
		if (mniHome == null) {
			mniHome = new JMenuItem();
			mniHome.setText("Home");
			mniHome.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					btnHome.doClick();
				}
			});
		}
		return mniHome;
	}

	/**
	 * This method initializes mniAddBookmark.
	 * 
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getMniAddBookmark() {
		if (mniAddBookmark == null) {
			mniAddBookmark = new JMenuItem();
			mniAddBookmark.setText("Add bookmark...");
		}
		return mniAddBookmark;
	}
	
	/**
	 * This method initializes sepBookmark1.
	 * 
	 * @return javax.swing.JSeparator
	 */
	private JSeparator getSepBookmark1() {
		if (sepBookmark1 == null) {
			sepBookmark1 = new JSeparator();
		}
		return sepBookmark1;
	}

	/**
	 * This method initializes mniAutoRun.
	 * 
	 * @return javax.swing.JCheckBoxMenuItem
	 */
	private JCheckBoxMenuItem getMniAutoRun() {
		if (mniAutoRun == null) {
			mniAutoRun = new JCheckBoxMenuItem();
			mniAutoRun.setSelected(true);
			mniAutoRun.setText("Auto run");
			mniAutoRun.addItemListener(new java.awt.event.ItemListener() {
				public void itemStateChanged(java.awt.event.ItemEvent e) {
					boolean blnAutoRun = mniAutoRun.isSelected();
					javier.setAutoEval(blnAutoRun);
					mniViewJS.setEnabled(!blnAutoRun);
					mniViewVXML.setEnabled(!blnAutoRun);
				}
			});
		}
		return mniAutoRun;
	}

	/**
	 * This method initializes pnlCenter.
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getPnlCenter() {
		if (pnlCenter == null) {
			pnlCenter = new JPanel();
			pnlCenter.setLayout(new BorderLayout());
			pnlCenter.add(getSplMain(), BorderLayout.CENTER);
			pnlCenter.add(getTlbChat(), BorderLayout.NORTH);
			pnlCenter.add(getTlbKeypad(), BorderLayout.EAST);
		}
		return pnlCenter;
	}

	/**
	 * This method initializes tlbChat.
	 * 
	 * @return javax.swing.JToolBar
	 */
	private JToolBar getTlbChat() {
		if (tlbChat == null) {
			tlbChat = new JToolBar();
			tlbChat.add(getTglKeypad());
		}
		return tlbChat;
	}

	/**
	 * This method initializes tglKeypad.
	 * 
	 * @return javax.swing.JToggleButton
	 */
	private JToggleButton getTglKeypad() {
		if (tglKeypad == null) {
			tglKeypad = new JToggleButton();
			tglKeypad.setText(" # ");
			tglKeypad.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					tlbKeypad.setVisible(tglKeypad.isSelected());
				}
			});
		}
		return tglKeypad;
	}

	/**
	 * This method initializes tlbKeypad.
	 * 
	 * @return javax.swing.JToolBar
	 */
	private JToolBar getTlbKeypad() {
		if (tlbKeypad == null) {
			tlbKeypad = new JToolBar();
			tlbKeypad.setOrientation(JToolBar.VERTICAL);
			tlbKeypad.setVisible(false);
			tlbKeypad.add(getPnlKeypad());
		}
		return tlbKeypad;
	}

	/**
	 * This method initializes btnKey1.
	 * 
	 * @param i
	 *            the i
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getBtnKey(int i) {
		if (btnKey == null) {
			btnKey = new JButton[12];
		}
		
		if (btnKey[i] == null) {
			btnKey[i] = new JButton(String.valueOf(i + 1));
			switch(i) {
				case KEY_AST:
					btnKey[i].setText("*");
					break;
				case KEY_0:
					btnKey[i].setText("0");
					break;
				case KEY_POUND:
					btnKey[i].setText("#");
					break;
			}
			
			btnKey[i].addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					txtInput.setText(txtInput.getText() + ((JButton)e.getSource()).getText());
				}
			});

		}
		
		return btnKey[i];
	}

	/**
	 * This method initializes pnlKeypad.
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getPnlKeypad() {
		if (pnlKeypad == null) {
			GridLayout gridLayout = new GridLayout(4,3);
			gridLayout.setRows(4);
			pnlKeypad = new JPanel();
			pnlKeypad.setLayout(gridLayout);
			for(int i = KEY_1; i <= KEY_POUND; i++) {
				pnlKeypad.add(getBtnKey(i), null);
			}
		}
		return pnlKeypad;
	}

    /**
	 * This method initializes scrMessage.
	 * 
	 * @return javax.swing.JScrollPane
	 */
	private JScrollPane getScrMessage() {
		if (scrMessage == null) {
			scrMessage = new JScrollPane();
			scrMessage.setViewportView(getTxtMessage());
		}
		return scrMessage;
	}

	/**
	 * This method initializes mniDebug.
	 * 
	 * @return javax.swing.JCheckBoxMenuItem
	 */
	private JCheckBoxMenuItem getMniDebug() {
		if (mniDebug == null) {
			mniDebug = new JCheckBoxMenuItem();
			mniDebug.setText("Debug");
			mniDebug.addItemListener(new java.awt.event.ItemListener() {
				public void itemStateChanged(java.awt.event.ItemEvent e) {
					javier.setDebugEnabled(mniDebug.isSelected());
				}
			});
		}
		return mniDebug;
	}

	/**
	 * The main method.
	 * 
	 * @param args
	 *            the args
	 */
	public static void main( String[] args ) {
    	(new JavierGUI()).setVisible(true);
    }
	
	/**
	 * Go to.
	 * 
	 * @param url
	 *            the url
	 */
	private void goTo(String url) {
		goTo(url, HistoryManagement.ADD);
	}
	
	/**
	 * Go to.
	 * 
	 * @param management
	 *            the management
	 * @param url
	 *            the url
	 */
	private void goTo(String url, HistoryManagement management) {
		txtAddress.setText(url);
		
		new Thread() {
			@Override
			public void run() {
				try {
					javier.mainLoop(txtAddress.getText());
				} catch (IOException ex) {
					ex.printStackTrace();
				}
				txtAddress.requestFocus();
			}
		}.start();
		txtInput.requestFocus();
				
		if(management == HistoryManagement.ADD) {
			historyIndex ++;						
		}
		
		if(historyIndex >= history.size()) {
			history.add(url);								
		} else {
			history.set(historyIndex, url);			
		}
		
		if(management == HistoryManagement.CLEAR) {
			while(history.size() > historyIndex + 1) {
				history.remove(history.size() - 1);
			}
		}
	}

}  //  @jve:decl-index=0:visual-constraint="97,30"
