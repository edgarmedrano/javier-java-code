/**
 * File:        SAPIOutputHandler.java
 * Description: An output handler that uses MS SAPI
 * Author:      Edgar Medrano P�rez
 *              edgarmedrano at gmail dot com
 * Created:     2007.04.17
 * Company:     JAVIER project
 *              http://javier.sourceforge.net
 * Notes:       WARNING!
 *              This class runs only on MS Windows, because
 *              it relies on MS SAPI in the end  
 */
package org.javier.browser.handlers;

import static org.javier.jacob.OleAutomation.createActiveXObject;

import static org.javier.jacob.SAPI.SpeechAudioFormatType.*;
import static org.javier.jacob.SAPI.SpeechStreamFileMode.*;
import static org.javier.jacob.SAPI.SpeechVoiceSpeakFlags.*;

import org.javier.browser.event.OutputListener;
import org.javier.jacob.SAPI.ISpeechObjectTokens;
import org.javier.jacob.SAPI.SpFileStream;
import org.javier.jacob.SAPI.SpVoice;

/**
 * An output handler that uses MS SAPI.
 * <p><strong>WARNING!</strong><br>
 * This class runs only on MS Windows, because it relies on MS SAPI in 
 * the end.</p>
 */
public class SAPIOutputHandler implements OutputListener {
	protected SpVoice spVoice;
	
	public SAPIOutputHandler(String voice) {
		spVoice = (SpVoice) createActiveXObject(SpVoice.class);
		
		if(voice != null && !voice.trim().equals("")) {
			ISpeechObjectTokens isot = spVoice.GetVoices();
			for(int i = 0; i < isot.Count(); i++) {
				if(isot.Item(i).GetDescription().indexOf(voice) >= 0) {
					spVoice.setVoice(isot.Item(i));
					break;
				}
			}
		}
	}

	public SAPIOutputHandler() {
		this("");
	}
	
	public void addText(String text) {
		try {
			spVoice.Speak(text, SVSFlagsAsync);
		} catch(Exception e) {
			//e.printStackTrace();
		}
	}

	public void clearText() {
		try {
			spVoice.Speak("", SVSFPurgeBeforeSpeak);
		} catch(Exception e) {
			//e.printStackTrace();
		}
	}

	public void waitUntilDone() {
		spVoice.WaitUntilDone(-1);
	}

	public void file(String path, String text) {
		SpFileStream spfs = (SpFileStream) createActiveXObject(SpFileStream.class);
		
		spfs.getFormat().setType(SAFT8kHz8BitMono);
		spfs.Open(path, SSFMCreateForWrite, false);

		spVoice.setAllowAudioOutputFormatChangesOnNextSet(false);
		spVoice.setAudioOutputStream(spfs);
		
		spVoice.Speak(text, SVSFDefault);
		
		spfs.Close();
		spfs = null;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		SAPIOutputHandler soh = new SAPIOutputHandler("Rosa");
		String strText = "Hola mi nombre es Rosa!";
		
		soh.addText(strText);
		soh.file("c:\\test.wav",strText);
	}

}
