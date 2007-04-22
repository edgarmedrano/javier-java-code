package org.javier.jacob.SAPI;

import org.javier.jacob.OleInterface;
import org.javier.jacob.OleMethod;
import org.javier.jacob.OleProperty;

@OleInterface(name="Sapi.SpAudioFormat")
public interface SpAudioFormat {
	
	@OleProperty void setType(int type);
	
	@OleProperty int getType();
	
	@OleProperty void setGuid(String guid);
	
	@OleProperty String getGuid();
	
	@OleMethod SpWaveFormatEx GetWaveFormatEx();
	
	@OleMethod void SetWaveFormatEx(SpWaveFormatEx waveFormat);
}
