/**
 * File:        SpeechAudioState.java
 * Description: SpeechAudioState from MS SAPI
 * Author:      Edgar Medrano Pérez
 *              edgarmedrano at gmail dot com
 * Created:     2007.04.21
 * Company:     JAVIER project
 *              http://javier.sourceforge.net
 * Notes:        
 */
package org.javier.jacob.SAPI;

public interface SpeechAudioState {
	int SASClosed = 0;
    int SASStop = 1;
    int SASPause = 2;
    int SASRun = 3;
}
