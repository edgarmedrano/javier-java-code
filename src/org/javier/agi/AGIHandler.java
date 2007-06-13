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
package org.javier.agi;

import static org.javier.jacob.OleAutomation.createActiveXObject;
import static org.javier.jacob.SAPI.SpeechAudioFormatType.*;
import static org.javier.jacob.SAPI.SpeechStreamFileMode.*;
import static org.javier.jacob.SAPI.SpeechVoiceSpeakFlags.*;

import java.io.File;
import java.io.IOException;
import org.javier.browser.DataType;
import org.javier.browser.Javier;
import org.javier.browser.event.JavierListener;
import org.javier.browser.event.OutputListener;
import org.javier.browser.handlers.ConsoleLogHandler;
import org.javier.browser.handlers.InputHandler;
import org.javier.browser.handlers.MSXMLHTTPNetworkHandler;
import org.javier.jacob.SAPI.ISpeechObjectTokens;
import org.javier.jacob.SAPI.SpFileStream;
import org.javier.jacob.SAPI.SpVoice;

import com.jacob.com.ComThread;

/**
 * AGI Input/Output handler
 */
public class AGIHandler 
	implements AGIScript,
	  JavierListener,
	  InputHandler,
	  OutputListener,
	  Thread.UncaughtExceptionHandler
	  {

	private AGIConnection agi;
	private static int file_index;
	private SpVoice ttsVoice;
	private String buffer = "";
	private Javier javier;
	
	static {
		ComThread.startMainSTA();		
	}
	
	private static synchronized int getFileIndex() {
		return ++file_index;
	}
	

	public void execute(AGIConnection agi) {
		this.agi = agi;
		
		ComThread.InitMTA();		
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
		
		javier.addLogListener(new ConsoleLogHandler());
		/*
		try {
			javier.addLogListener(new StreamLogHandler("Javier.log"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		*/
		
		// Translate AGI properties to VoiceXML properties
		try {
			String value = agi.get_variable("TIMEOUT(digit)");
			if(!value.equals("")) {
				javier.setProperty("timeout", value + "s");				
			}
		} catch (AGIException e1) {
			e1.printStackTrace();
		}
		
		try {
			javier.mainLoop("http://localhost/sictel.php");
		} catch (IOException e) {
			e.printStackTrace();
		}
		ComThread.Release(); 				
	}

	/**
	 * Excecution ended.
	 * 
	 * @param endCode
	 *            the end code
	 */
	public void excecutionEnded(int endCode) {
	}

	/**
	 * Load state changed.
	 * 
	 * @param readyState
	 *            the ready state
	 */
	public void loadStateChanged(int readyState) {
		// do nothing
	}

	/**
	 * Url changed.
	 * 
	 * @param url
	 *            the url
	 */
	public void urlChanged(String url) {
		// do nothing
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

	public String getInput(String text, String value, String type, String slot, boolean modal) throws IOException {
		String result = value;
		String timeout = javier.getProperty("timeout");
		long time = 0;
		DataType datatype = DataType.getType(type);
		int digits = datatype.getMax() - buffer.length();
		
		if(timeout.indexOf("ms") >= 0) {
			timeout = timeout.replaceFirst("ms", "");
			time = Long.parseLong(timeout);
		} else {
			if(timeout.indexOf("s") > 0) {
				timeout = timeout.replaceFirst("s", "");
				time = Long.parseLong(timeout) * 1000;
			}
		}		
		
		try {
			if(digits > 0) {
				buffer += agi.get_data("silence", time, digits);
			}
			result = datatype.parse(buffer);
			buffer = "";
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}

	/**
	 * Adds the text.
	 * 
	 * @param text
	 *            the text
	 * @throws IOException 
	 */
	public void addText(String text) throws IOException {
		String file = textToWav(text);
		
		if(file == null) {
		} else {
			try {
				buffer += agi.stream_file(file,"0123456789");
			} catch (Exception e) {
				throw(new IOException(e.getMessage(),e.getCause()));
			}
		}
	}

	private String textToWav(String text) {
		SpFileStream spfs = (SpFileStream) createActiveXObject(SpFileStream.class);
		String fileName;
		String wavPath;
		File wavFile;
		
		/********************************************************
		Implement caching here
		*********************************************************/
		
		do {
			fileName = "javier" + getFileIndex();
			wavPath = "C:\\cygroot\\asterisk\\var\\lib\\sounds\\" + fileName + ".WAV";
			wavFile = new File(wavPath);
		} while(wavFile.exists());
		
		
		spfs.getFormat().setType(SAFT8kHz16BitMono);
		spfs.Open(wavPath, SSFMCreateForWrite, false);
		
		ttsVoice.setAllowAudioOutputFormatChangesOnNextSet(false);
		ttsVoice.setAudioOutputStream(spfs);

		ttsVoice.Speak(text, SVSFDefault);
		
		spfs.Close();
				
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

	public void uncaughtException(Thread t, Throwable e) {
		e.printStackTrace();	
	}
}
