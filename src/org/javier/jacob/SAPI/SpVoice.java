package org.javier.jacob.SAPI;

import org.javier.jacob.Dispatchable;
import org.javier.jacob.OleInterface;
import org.javier.jacob.OleMethod;
import org.javier.jacob.OleProperty;


@OleInterface(name="Sapi.SpVoice")
public interface SpVoice extends Dispatchable {

	@OleProperty void setVoice(SpObjectToken voice);

	@OleMethod long Speak(String text,int options);

	@OleProperty void setAllowAudioOutputFormatChangesOnNextSet(
			boolean allowChanges);

	@OleProperty void setAudioOutputStream(SpFileStream stream);
	
	@OleMethod ISpeechObjectTokens GetVoices();
	
	@OleMethod ISpeechObjectTokens GetVoices(
		     String RequiredAttributes);
	
	@OleMethod ISpeechObjectTokens GetVoices(
		     String RequiredAttributes,
		     String OptionalAttributes);
	
	@OleMethod boolean WaitUntilDone(long msTimeout);
}
