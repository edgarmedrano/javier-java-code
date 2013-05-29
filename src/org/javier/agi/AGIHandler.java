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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.javier.browser.Javier;
import org.javier.browser.event.JavierListener;
import org.javier.browser.event.OutputListener;
import org.javier.browser.handlers.InputHandler;
import org.javier.browser.handlers.MSXMLHTTPNetworkHandler;
import org.javier.browser.handlers.StreamLogHandler;
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
	private String soundsDir = "C:\\cygroot\\asterisk\\var\\lib\\sounds\\";
	
	static {
		ComThread.startMainSTA();		
	}
	
	private static synchronized int getFileIndex() {
		file_index = (file_index + 1) & 0xffff; // resets index after 32,767
		return file_index;
	}
	

	public void execute(AGIConnection agi) {
		this.agi = agi;
		
	    String ttsProvider = "org.javier.browser.handlers.SAPIOutputHandler";
	    String voiceName = "";
	    String homeAddress = "http://localhost/javier/default.vxml";
	    String logFile = "Javier.log";
		
	    try {
		    Properties properties = new Properties();
		    File file = new File("AGIService.conf");
		    
	        properties.load(new FileInputStream(file));
	        ttsProvider = properties.getProperty("tts_class", ttsProvider);
		    voiceName = properties.getProperty("tts_voice", voiceName);
	    	homeAddress = properties.getProperty("home_address", homeAddress);
	    	logFile = properties.getProperty("log_file", logFile);
	    	soundsDir = properties.getProperty("sounds_dir", soundsDir);
	    } catch (IOException e) {
			e.printStackTrace();
	    }
		
		ComThread.InitMTA();
		try {
			ttsVoice = (SpVoice) createActiveXObject(SpVoice.class);
			ISpeechObjectTokens voices = ttsVoice.GetVoices();
			
			for(int i = 0; i < voices.Count(); i++) {
				if(voices.Item(i).GetDescription().indexOf(voiceName) >= 0) {
					ttsVoice.setVoice(voices.Item(i));
					break;
				}
			}
			javier = new Javier(this,new MSXMLHTTPNetworkHandler());
			javier.addJavierListener(this);
			javier.addOutputListener(this);
			/*
			javier.addLogListener(new ConsoleLogHandler());
			*/
			try {
				javier.addLogListener(new StreamLogHandler(logFile));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}		
			
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
				javier.mainLoop(homeAddress);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} finally {
			ComThread.Release();
		}
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
	 * @param text  the text
	 * 
	 * @return the input
	 */
	public String getInput(String text) throws IOException {
		return getInput(text,"");
	}

	/**
	 * Gets the input.
	 * 
	 * @param text  the text
	 * @param value the default value
	 * 
	 * @return the input
	 */
	public String getInput(String text, String value) throws IOException {
		return getInput(text, value, 0, 32767);		
	}

	/**
	 * Gets the input.
	 * 
	 * @param text  the text
	 * @param value the default value
	 * @param min   the minimum value's length
	 * @param max   the maximum value's length
	 * 
	 * @return the input
	 */
	public String getInput(String text, String value, int min, int max) throws IOException {
		String result = value;
		String timeout = javier.getProperty("timeout");
		long time = 0;
		int digits = max - buffer.length();
		
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
			result = buffer;
			buffer = "";
		} catch (Exception e) {
			throw(new IOException(e.getMessage(),e.getCause()));
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
		File file = textToWav(text);
		
		if(file != null) {
			String fileName = file.getName();
			fileName = fileName.substring(0,fileName.lastIndexOf(".WAV"));
			try {
				buffer += agi.stream_file(fileName,"0123456789");
			} catch (Exception e) {
				throw(new IOException(e.getMessage(),e.getCause()));
			} finally {
				file.delete();
			}
		}
	}

	private File textToWav(String text) {
		SpFileStream spfs = (SpFileStream) createActiveXObject(SpFileStream.class);
		String fileName;
		String wavPath;
		File wavFile;
		
		/********************************************************
		Implement caching here
		*********************************************************/
		
		do {
			fileName = "javier" + getFileIndex();
			wavPath = soundsDir + fileName + ".WAV";
			wavFile = new File(wavPath);
		} while(wavFile.exists());
		
		
		spfs.getFormat().setType(SAFT8kHz16BitMono);
		spfs.Open(wavPath, SSFMCreateForWrite, false);
		
		ttsVoice.setAllowAudioOutputFormatChangesOnNextSet(false);
		ttsVoice.setAudioOutputStream(spfs);

		ttsVoice.Speak(text, SVSFDefault);
		
		spfs.Close();
				
		return wavFile;
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
