package org.javier.jacob.SAPI;

public interface SpeechVoiceSpeakFlags {
    // SpVoice flags
    int SVSFDefault = 0;
    int SVSFlagsAsync = 1;
    int SVSFPurgeBeforeSpeak = 2;
    int SVSFIsFilename = 4; 
    int SVSFIsXML = 8;
    int SVSFIsNotXML = 16;
    int SVSFPersistXML = 32;

    // Normalizer flags
    int SVSFNLPSpeakPunc = 64;

    // Masks
    int SVSFNLPMask = 64;
    int SVSFVoiceMask = 127;
    int SVSFUnusedFlags = -128;   
}
