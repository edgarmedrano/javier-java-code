/**
 * File:        SpFileStream.java
 * Description: SpFileStream from MS SAPI
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
 * The SpFileStream automation object enables data streams to be read and 
 * written as files.
 * <p>SpFileStream objects normally contain audio data, but may also be 
 * used for text data.</p>
 */
@OleInterface(name="Sapi.SpFileStream") 	
public interface SpFileStream extends Dispatchable {
	
	/**
	 * Gets the cached wave format of the stream as an SpAudioFormat object.
	 * 
	 * @return the cached wave format
	 */
	@OleProperty SpAudioFormat getFormat();
	
	/**
	 * Sets the cached wave format of the stream as an SpAudioFormat object.
	 * 
	 * @param audioFormat the cached wave format
	 */
	@OleProperty void setFormat(SpAudioFormat audioFormat);
	
	/**
	 * Opens a filestream object for reading or writing.
	 * 
	 * @param fileName the FileName. 
	 * @param fileMode the FileMode. Default value is SSFMOpenForRead. 
	 * @param doEvents When FileMode is SSFMCreateForWrite, DoEvents 
	 *        specifies whether playback of the resulting sound file 
	 *        will generate voice events. <br>
	 *        Default value is <code>false</code>.
	 * 
	 * @see SpeechStreamFileMode
	 */
	@OleMethod void Open(String fileName
			, int fileMode
			, boolean doEvents);
	
	/**
	 * Opens a filestream object for reading or writing.
	 * 
	 * @param fileName the FileName. 
	 * @param fileMode the FileMode. Default value is SSFMOpenForRead. 
	 * 
	 * @see SpeechStreamFileMode
	 */
	@OleMethod void Open(String fileName
			, int fileMode);
	
	/**
	 * Opens a filestream object for reading or writing.
	 * 
	 * @param fileName the FileName. 
	 */
	@OleMethod void Open(String fileName);

	/**
	 * Reads data from a stream object.
	 * 
	 * @param Buffer        a Variant variable to receive the data.
	 * @param NumberOfBytes the number of bytes of data to attempt to read 
	 *                      from the audio stream. 
	 * 
	 * @return the long
	 */
	@OleMethod long Read(
		     Object Buffer,
		     long NumberOfBytes);
	
	/**
	 * returns the current read position of the stream in bytes.
	 * 
	 * @param Position the number of bytes to move the Seek pointer forward 
	 *                 in the stream. Negative values move the pointer 
	 *                 backward. 
	 * 
	 * @return the current read position
	 */
	@OleMethod Object Seek(Object Position);
	
	/**
	 * returns the current read position of the stream in bytes.
	 * 
	 * @param Position the number of bytes to move the Seek pointer forward 
	 *                 in the stream. Negative values move the pointer 
	 *                 backward. 
	 * @param Origin   the Origin. Default value is SSSPTRelativeToStart. 
	 * 
	 * @return the current read position
	 * @see SpeechStreamSeekPositionType
	 */
	@OleMethod Object Seek(
		     Object Position,
		     int Origin);
	
	/**
	 * closes the filestream object.
	 */
	@OleMethod void Close();
}
