/**
 * File:        SpVoice.java
 * Description: SpVoice from MS SAPI
 * Author:      Edgar Medrano Pérez
 *              edgarmedrano at gmail dot com
 * Created:     2007.04.21
 * Company:     JAVIER project
 *              http://javier.sourceforge.net
 * Notes:        
 */
package org.javier.jacob.SAPI;

import org.javier.jacob.Dispatchable;
import org.javier.jacob.OleInterface;
import org.javier.jacob.OleMethod;
import org.javier.jacob.OleProperty;

/**
 * The SpVoice object brings the text-to-speech (TTS) engine capabilities 
 * to applications using SAPI automation. An application can create numerous 
 * SpVoice objects, each independent of and capable of interacting with the 
 * others. An SpVoice object, usually referred to simply as a voice, is 
 * created with default property settings so that it is ready to speak 
 * immediately.
 */
@OleInterface(name="Sapi.SpVoice")
public interface SpVoice extends Dispatchable {

	/**
	 * Sets the currently active member of the Voices collection.
	 * 
	 * @param voice the voice
	 */
	@OleProperty void setVoice(SpObjectToken voice);

	/**
	 * Initiates the speaking of a text string, a text file, an XML file, 
	 * or a wave file by the voice.
	 * 
	 * <p>The Speak method can be called synchronously or asynchronously. 
	 * When called synchronously, the method does not return until the 
	 * text has been spoken; when called asynchronously, it returns 
	 * immediately, and the voice speaks as a background process.</p>
	 * 
	 * <p>When synchronous speech is used in an application, the 
	 * application's execution is blocked while the voice speaks, and the 
	 * user is effectively locked out. This may be acceptable for simple 
	 * applications, or those with no graphical user interface (GUI), but 
	 * when sophisticated user interaction is intended, asynchronous speaking 
	 * will generally be more appropriate.</p>
	 * 
	 * <p>The WaitUntilDone and SpeakCompleteEvent methods can be used to 
	 * block an application's forward progress while allowing user interaction
	 * with the mouse or keyboard.</p>
	 * 
	 * @param text    The text to be spoken, or if the SVSFIsFilename flag is 
	 *                included in the Flags parameter, the path of the file to 
	 *                be spoken. 
	 * @param options Flags. Default value is SVSFDefault.
	 * 
	 * @return the stream number. When a voice enqueues more than one stream 
	 *         by speaking asynchronously, the stream number is necessary to 
	 *         associate events with the appropriate stream. 
	 * @see SpeechVoiceSpeakFlags
	 */
	@OleMethod long Speak(String text,int options);

	/**
	 * Sets the flag that specifies whether SAPI will adjust the format of a 
	 * voice object's new audio output device automatically.
	 * 
	 * <p>By default, when an application sets a voice object's AudioOutput 
	 * property to an audio device, SAPI will change the format of that 
	 * device to match the engine's preferred format. In cases where a 
	 * specific audio format is required, such as telephony applications, 
	 * the AllowOutputFormatChangesOnNextSet property can be used to prevent 
	 * this format change.</p>
	 * 
	 * @param allowChanges When <code>true</code>, SAPI adjusts the format 
	 *                     of the audio output object to the engine's 
	 *                     preferred format. <br>
	 *                     When <code>false</code>, SAPI uses the audio 
	 *                     output object's format.<br>
	 *                     If the output is set to a stream object, SAPI 
	 *                     will convert the output to the format of the 
	 *                     stream.
	 */
	@OleProperty void setAllowAudioOutputFormatChangesOnNextSet(
			boolean allowChanges);

	/**
	 * Sets the current audio output object used by the voice.
	 * 
	 * @param stream the current audio output object used by the voice.
	 */
	@OleProperty void setAudioOutputStream(SpFileStream stream);
	
	/**
	 * Returns all the voices available to the voice.
	 * 
	 * @return all the voices available to the voice.
	 */
	@OleMethod ISpeechObjectTokens GetVoices();
	
	/**
	 * Returns a selection of voices available to the voice.
	 * 
	 * <p>Selection criteria may be applied optionally. In the absence 
	 * of selection criteria, all voices are returned in the selection, 
	 * ordered alphabetically by the voice name. If no voices match the 
	 * criteria, GetVoices returns an empty selection, that is, an 
	 * ISpeechObjectTokens collection with a Count of zero.</p>
	 *
	 * @param RequiredAttributes 
	 *          the RequiredAttributes. All voices selected will match 
	 *          these specifications. If no voices match the selection, 
	 *          the selection returned will contain no voices. 
	 *          By default, no attributes are required and so the list 
	 *          returns all the tokens discovered. 
	 * 
	 * @return a selection of voices available to the voice.
	 */
	@OleMethod ISpeechObjectTokens GetVoices(
		     String RequiredAttributes);
	
	/**
	 * Returns a selection of voices available to the voice.
	 * 
	 * <p>Selection criteria may be applied optionally. In the absence 
	 * of selection criteria, all voices are returned in the selection, 
	 * ordered alphabetically by the voice name. If no voices match the 
	 * criteria, GetVoices returns an empty selection, that is, an 
	 * ISpeechObjectTokens collection with a Count of zero.</p>
	 *
	 * @param RequiredAttributes 
	 *          the RequiredAttributes. All voices selected will match 
	 *          these specifications. If no voices match the selection, 
	 *          the selection returned will contain no voices. 
	 *          By default, no attributes are required and so the list 
	 *          returns all the tokens discovered. 
	 * 
	 * @param OptionalAttributes 
	 *          the OptionalAttributes. Voices which match these 
	 *          specifications will be returned at the front of the 
	 *          selection. By default, no attribute is specified and the 
	 *          list returned from the speech configuration database is 
	 *          in the order that attributes were discovered. 
	 * 
	 * @return a selection of voices available to the voice.
	 */
	@OleMethod ISpeechObjectTokens GetVoices(
		     String RequiredAttributes,
		     String OptionalAttributes);
	
	/**
	 * Blocks the caller until either the voice has finished speaking or the 
	 * specified time interval has elapsed.
	 * 
	 * @param msTimeout the timeout in milliseconds. If -1, the time interval 
	 *                  is ignored and the method simply waits for the voice 
	 *                  to finish speaking. 
	 * 
	 * @return <code>true</code>, if the voice finished speaking.<br>
	 *         <code>false</code>, if the time interval elapsed.
	 */
	@OleMethod boolean WaitUntilDone(long msTimeout);
}
