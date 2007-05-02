/**
 * File:        SpAudioFormat.java
 * Description: SpAudioFormat from MS SAPI
 * Author:      Edgar Medrano Pérez
 *              edgarmedrano at gmail dot com
 * Created:     2007.04.21
 * Company:     JAVIER project
 *              http://javier.sourceforge.net
 * Notes:        
 */
package org.javier.jacob.SAPI;

import org.javier.jacob.OleInterface;
import org.javier.jacob.OleMethod;
import org.javier.jacob.OleProperty;

/**
 * The SpAudioFormat automation object represents an audio format.
 * <p>Most applications using standard audio formats will use the Type 
 * property to set and retrieve formats. Non-standard formats using wav 
 * files will use SetWavFormatEx and GetWaveFormatEx to set and retrieve 
 * formats, respectively. Non-standard formats using sources other than 
 * wav files use Guid.</p>
 */
@OleInterface(name="Sapi.SpAudioFormat")
public interface SpAudioFormat {
	
	/**
	 * Sets the speech audio format as a SpeechAudioFormatType.
	 * 
	 * @param type the speech audio format
	 * 
	 * @see SpeechAudioFormatType
	 */
	@OleProperty void setType(int type);
	
	/**
	 * Gets the speech audio format as a SpeechAudioFormatType.
	 * 
	 * @return the speech audio format
	 * 
	 * @see SpeechAudioFormatType
	 */
	@OleProperty int getType();
	
	/**
	 * Sets the GUID of the default audio format.
	 * 
	 * @param guid the GUID
	 */
	@OleProperty void setGuid(String guid);
	
	/**
	 * Gets the GUID of the default audio format.
	 * 
	 * @return the GUID
	 */
	@OleProperty String getGuid();
	
	/**
	 * Gets the audio format as an SpWaveFormatEx object.
	 * 
	 * @return the audio format as an SpWaveFormatEx
	 * 
	 * @see SpWaveFormatEx
	 */
	@OleMethod SpWaveFormatEx GetWaveFormatEx();
	
	/**
	 * Sets the audio format as an SpWaveFormatEx object.
	 * 
	 * @param waveFormat the audio format as an SpWaveFormatEx
	 * 
	 * @see SpWaveFormatEx
	 */
	@OleMethod void SetWaveFormatEx(SpWaveFormatEx waveFormat);
}
