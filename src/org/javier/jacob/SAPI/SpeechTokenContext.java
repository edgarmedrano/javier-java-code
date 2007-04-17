package org.javier.jacob.SAPI;

public interface SpeechTokenContext {
	int STCInprocServer = 1;
	int STCInprocHandler = 2;
	int STCLocalServer = 4;
	int STCRemoteServer = 16;
	int STCAll = 23;
}
