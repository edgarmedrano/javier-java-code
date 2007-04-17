package org.javier.jacob.SAPI;

import annotations.OleInterface;
import annotations.OleMethod;
import annotations.OleProperty;

@OleInterface(name="Sapi.SpAudioFormat")
public interface SpAudioFormat {
	
	@OleProperty void setType(int type);
	
	@OleProperty int getType();
	
	@OleProperty void setGuid(String guid);
	
	@OleProperty String getGuid();
	
	@OleMethod SpWaveFormatEx GetWaveFormatEx();
	
	@OleMethod void SetWaveFormatEx(SpWaveFormatEx waveFormat);
}
