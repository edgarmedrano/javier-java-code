package org.javier.jacob.SAPI;

import org.javier.jacob.Dispatchable;

import annotations.OleInterface;
import annotations.OleMethod;
import annotations.OleProperty;

@OleInterface(name="Sapi.SpFileStream") 	
public interface SpFileStream extends Dispatchable {
	
	@OleProperty SpAudioFormat getFormat();
	
	@OleProperty void setFormat(SpAudioFormat audioFormat);
	
	@OleMethod void Open(String fileName
			, int fileMode
			, boolean doEvents);

	@OleMethod long Read(
		     Object Buffer,
		     long NumberOfBytes);
	
	@OleMethod Object Seek(Object Position);
	
	@OleMethod Object Seek(
		     Object Position,
		     int Origin);
	@OleMethod void SetState(int State);
	
	@OleMethod void Close();
}
