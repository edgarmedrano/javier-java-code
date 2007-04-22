package org.javier.jacob.SAPI;

import org.javier.jacob.Dispatchable;
import org.javier.jacob.OleInterface;
import org.javier.jacob.OleProperty;


@OleInterface(name="Sapi.SpWaveFormatEx")
public interface SpWaveFormatEx extends Dispatchable {
	@OleProperty public long getAvgBytesPerSec();
	@OleProperty public void setAvgBytesPerSec(long avgBytesPerSec);
	@OleProperty public int getBitsPerSample();
	@OleProperty public void setBitsPerSample(int bitsPerSample);
	@OleProperty public int getBlockAlign();
	@OleProperty public void setBlockAlign(int blockAlign);
	@OleProperty public int getChannels();
	@OleProperty public void setChannels(int channels);
	@OleProperty public Object getExtraData();
	@OleProperty public void setExtraData(Object extraData);
	@OleProperty public int getFormatTag();
	@OleProperty public void setFormatTag(int formatTag);
	@OleProperty public long getSamplesPerSec();
	@OleProperty public void setSamplesPerSec(long samplesPerSec);
}
