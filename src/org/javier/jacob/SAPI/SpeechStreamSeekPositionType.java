/**
 * File:        SpeechStreamSeekPositionType.java
 * Description: SpeechStreamSeekPositionType from MS SAPI
 * Author:      Edgar Medrano Pérez
 *              edgarmedrano at gmail dot com
 * Created:     2007.04.21
 * Company:     JAVIER project
 *              http://javier.sourceforge.net
 * Notes:        
 */
package org.javier.jacob.SAPI;

public interface SpeechStreamSeekPositionType {
    int SSSPTRelativeToStart = 0;
    int SSSPTRelativeToCurrentPosition = 1;
    int SSSPTRelativeToEnd = 2;
}
