/**
 * File:        SpeechStreamFileMode.java
 * Description: SpeechStreamFileMode from MS SAPI
 * Author:      Edgar Medrano Pérez
 *              edgarmedrano at gmail dot com
 * Created:     2007.04.21
 * Company:     JAVIER project
 *              http://javier.sourceforge.net
 * Notes:        
 */
package org.javier.jacob.SAPI;

public interface SpeechStreamFileMode {
    int SSFMOpenForRead = 0;
    //[hidden] int SSFMOpenReadWrite = 1;
    //[hidden] int SSFMCreate = 2;
    int SSFMCreateForWrite = 3;
}
