/**
 * File:        SpeechTokenContext.java
 * Description: SpeechTokenContext from MS SAPI
 * Author:      Edgar Medrano P�rez
 *              edgarmedrano at gmail dot com
 * Created:     2007.04.21
 * Company:     JAVIER project
 *              http://javier.sourceforge.net
 * Notes:        
 */
package org.javier.jacob.SAPI;

public interface SpeechTokenContext {
	int STCInprocServer = 1;
	int STCInprocHandler = 2;
	int STCLocalServer = 4;
	int STCRemoteServer = 16;
	int STCAll = 23;
}
