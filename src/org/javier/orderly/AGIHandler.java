/**
 * File:        AGIHandler.java
 * Description: AGI Input/Output handler 
 * Author:      Edgar Medrano Pérez
 *              edgarmedrano at gmail dot com
 * Created:     2007.05.02
 * Company:     JAVIER project
 *              http://javier.sourceforge.net
 * Notes:        
 */
package org.javier.orderly;

import static org.javier.jacob.OleAutomation.createActiveXObject;
import static org.javier.jacob.SAPI.SpeechAudioFormatType.*;
import static org.javier.jacob.SAPI.SpeechStreamFileMode.*;
import static org.javier.jacob.SAPI.SpeechVoiceSpeakFlags.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.javier.browser.Javier;
import org.javier.browser.event.JavierListener;
import org.javier.browser.event.OutputListener;
import org.javier.browser.handlers.InputHandler;
import org.javier.browser.handlers.MSXMLHTTPNetworkHandler;
import org.javier.browser.handlers.StreamLogHandler;
import org.javier.jacob.SAPI.ISpeechObjectTokens;
import org.javier.jacob.SAPI.SpFileStream;
import org.javier.jacob.SAPI.SpVoice;

import com.orderlysoftware.orderlycalls.asterisk.agi.AGIConnection;
import com.orderlysoftware.orderlycalls.asterisk.agi.AGIReusableProcessor;

/**
 * AGI Input/Output handler
 */
public class AGIHandler 
	implements AGIReusableProcessor,
	  JavierListener,
	  InputHandler,
	  OutputListener
	  {

	private AGIConnection agi;
	private Javier javier;
	private Thread agiThread;
	private Thread javierThread;
	private volatile boolean javierIsWaiting;
	private boolean exitWaitLoop;
	private static int file_index;
	private SpVoice ttsVoice;
	private String buffer = "";

	private static synchronized int getFileIndex() {
		return ++file_index;
	}
	
	/* (non-Javadoc)
	 * @see com.orderlysoftware.orderlycalls.asterisk.agi.AGIProcessor#processCall(com.orderlysoftware.orderlycalls.asterisk.agi.AGIConnection)
	 */
	public void processCall(AGIConnection agi) throws IOException {
		this.agi = agi;
		//HashMap properties = agi.getAGIProperties();
		
		ttsVoice = (SpVoice) createActiveXObject(SpVoice.class);
		ISpeechObjectTokens voices = ttsVoice.GetVoices();
		
		for(int i = 0; i < voices.Count(); i++) {
			if(voices.Item(i).GetDescription().indexOf("Rosa") >= 0) {
				ttsVoice.setVoice(voices.Item(i));
				break;
			}
		}
		
		javier = new Javier(this,new MSXMLHTTPNetworkHandler());
		javier.addJavierListener(this);
		javier.addOutputListener(this);
		
		try {
			javier.addLogListener(new StreamLogHandler("Javier.log"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	
		agiThread = Thread.currentThread();
		javierThread = new Thread(new Runnable() {
				public void run() {
					javier.mainLoop("http://localhost/sictel.php");
				}
			});
		
		javierThread.start();
		for(;;) {
			if(javierIsWaiting) {
				javierThread.notify();
				try {
					agiThread.wait();
				} catch (InterruptedException e) {
					exitWaitLoop = true;
				}
			}
			if(exitWaitLoop) {
				break;
			}
			agi.exec("WAIT", "1");
		}
		System.out.println("Succesfully end!");
	}

	/**
	 * Excecution ended.
	 * 
	 * @param endCode
	 *            the end code
	 */
	public void excecutionEnded(int endCode) {
		exitWaitLoop = true;
	}

	/**
	 * Load state changed.
	 * 
	 * @param readyState
	 *            the ready state
	 */
	public void loadStateChanged(int readyState) {
		// TODO Auto-generated method stub
	}

	/**
	 * Url changed.
	 * 
	 * @param url
	 *            the url
	 */
	public void urlChanged(String url) {
		// TODO Auto-generated method stub
	}

	/**
	 * Gets the input.
	 * 
	 * @param text
	 *            the text
	 * @param value
	 *            the value
	 * 
	 * @return the input
	 */
	public String getInput(String text, String value) {
		String result = value;
		waitForAGI();
		try {
			buffer += agi.getData("beep", 60000);
			result = buffer;
			buffer = "";
		} catch (IOException e) {
			exitWaitLoop = true;
		}
		agiThread.notify();
		return result;
	}

	/**
	 * Wait for AGI thread to be ready.
	 */
	private void waitForAGI() {
		try {
			javierIsWaiting = true;
			javierThread.wait();
		} catch (InterruptedException e) {
			// do nothing
		}
		javierIsWaiting = false;
	}

	/**
	 * Adds the text.
	 * 
	 * @param text
	 *            the text
	 */
	public void addText(String text) {
		String file = textToWav(text);
		int result = 0;
		
		if(file == null) {
			exitWaitLoop = true;
		} else {
			waitForAGI();
			try {
				result = agi.streamFile(file,"0123456789");
			} catch (IOException e) {
				exitWaitLoop = true;
			}
			agiThread.notify();
		}
		
		if(result > 0) {
			buffer += new String(new char[] { (char)result });
		}
	}

	private String textToWav(String text) {
		SpFileStream spfs = (SpFileStream) createActiveXObject(SpFileStream.class);
		String fileName;
		String wavPath;
		String gsmPath;
		File wavFile;
		File gsmFile;
		
		/********************************************************
		Implement caching here
		*********************************************************/
		
		do {
			fileName = "javier" + getFileIndex();
			wavPath = "" + fileName + ".wav";
			gsmPath = "C:\\cygroot\\asterisk\\var\\lib\\sounds" + fileName + ".gsm";
			wavFile = new File(wavPath);
			gsmFile = new File(gsmPath);
		} while(wavFile.exists() || gsmFile.exists());
		
		spfs.getFormat().setType(SAFT8kHz8BitMono);
		spfs.Open(wavPath, SSFMCreateForWrite, false);
		
		ttsVoice.setAllowAudioOutputFormatChangesOnNextSet(false);
		ttsVoice.setAudioOutputStream(spfs);

		ttsVoice.Speak(text, SVSFDefault);
		
		spfs.Close();
		
		ProcessBuilder soxProc = new ProcessBuilder("C:\\cygroot\\asterisk\\var\\lib\\sounds\\sox",wavPath,gsmPath);
		try {
			soxProc.start();
		} catch (IOException e) {
			fileName = null;
			e.printStackTrace();
		}
		
		while(!gsmFile.exists()) {
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		wavFile.delete();
		
		return fileName;
	}

	/**
	 * Clear text.
	 */
	public void clearText() {
		// do nothing
	}

	/**
	 * Wait until done.
	 */
	public void waitUntilDone() {
		// do nothing
	}

}
