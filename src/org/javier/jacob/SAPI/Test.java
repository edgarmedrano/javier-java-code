package org.javier.jacob.SAPI;

import static proxies.OleAutomationFactory.getActiveXComponet;
import static org.javier.jacob.SAPI.SpeechStreamFileMode.*; 
import static org.javier.jacob.SAPI.SpeechAudioFormatType.*; 
import static org.javier.jacob.SAPI.SpeechVoiceSpeakFlags.*;

public class Test {

	public static void main(String[] args) {
		SpVoice ttsHandler = (SpVoice) getActiveXComponet(SpVoice.class);
		SpFileStream spfs = (SpFileStream) getActiveXComponet(SpFileStream.class);
		
		ISpeechObjectTokens TTSVoices = ttsHandler.GetVoices();
		
		for(int i = 0; i < TTSVoices.Count(); i++) {
			if(TTSVoices.Item(i).GetDescription().indexOf("Rosa") >= 0) {
				ttsHandler.setVoice(TTSVoices.Item(i));
				break;
			}
		}
		
		
		spfs.getFormat().setType(SAFT8kHz8BitMono);
		spfs.Open("C:\\test.wav", SSFMCreateForWrite, false);

		//ttsHandler.setAllowAudioOutputFormatChangesOnNextSet(false);
		//ttsHandler.setAudioOutputStream(spfs);
		
		try {
			ttsHandler.Speak("Hola mi nombre es Rosa", SVSFDefault);
		} catch(Exception e) {
			//e.printStackTrace();
		}
		
		try {
			ttsHandler.Speak(" adios!", SVSFDefault);
		} catch(Exception e) {
			//e.printStackTrace();
		}
		
		spfs.Close();
		spfs = null;
	}

}
