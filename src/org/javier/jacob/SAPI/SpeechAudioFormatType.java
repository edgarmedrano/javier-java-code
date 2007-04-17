package org.javier.jacob.SAPI;

public interface SpeechAudioFormatType {
    final int SAFTDefault = -1;
    final int SAFTNoAssignedFormat = 0;
    final int SAFTText = 1;
    final int SAFTNonStandardFormat = 2;
    final int SAFTExtendedAudioFormat = 3;

    // Standard PCM wave formats
    final int SAFT8kHz8BitMono = 4;
    final int SAFT8kHz8BitStereo = 5;
    final int SAFT8kHz16BitMono = 6;
    final int SAFT8kHz16BitStereo = 7;
    final int SAFT11kHz8BitMono = 8;
    final int SAFT11kHz8BitStereo = 9;
    final int SAFT11kHz16BitMono = 10;
    final int SAFT11kHz16BitStereo = 11;
    final int SAFT12kHz8BitMono = 12;
    final int SAFT12kHz8BitStereo = 13;
    final int SAFT12kHz16BitMono = 14;
    final int SAFT12kHz16BitStereo = 15;
    final int SAFT16kHz8BitMono = 16;
    final int SAFT16kHz8BitStereo = 17;
    final int SAFT16kHz16BitMono = 18;
    final int SAFT16kHz16BitStereo = 19;
    final int SAFT22kHz8BitMono = 20;
    final int SAFT22kHz8BitStereo = 21;
    final int SAFT22kHz16BitMono = 22;
    final int SAFT22kHz16BitStereo = 23;
    final int SAFT24kHz8BitMono = 24;
    final int SAFT24kHz8BitStereo = 25;
    final int SAFT24kHz16BitMono = 26;
    final int SAFT24kHz16BitStereo = 27;
    final int SAFT32kHz8BitMono = 28;
    final int SAFT32kHz8BitStereo = 29;
    final int SAFT32kHz16BitMono = 30;
    final int SAFT32kHz16BitStereo = 31;
    final int SAFT44kHz8BitMono = 32;
    final int SAFT44kHz8BitStereo = 33;
    final int SAFT44kHz16BitMono = 34;
    final int SAFT44kHz16BitStereo = 35;
    final int SAFT48kHz8BitMono = 36;
    final int SAFT48kHz8BitStereo = 37;
    final int SAFT48kHz16BitMono = 38;
    final int SAFT48kHz16BitStereo = 39;

    // TrueSpeech format
    final int SAFTTrueSpeech_8kHz1BitMono = 40;

    // A-Law formats
    final int SAFTCCITT_ALaw_8kHzMono = 41;
    final int SAFTCCITT_ALaw_8kHzStereo = 42;
    final int SAFTCCITT_ALaw_11kHzMono = 43;
    final int SAFTCCITT_ALaw_11kHzStereo = 4;
    final int SAFTCCITT_ALaw_22kHzMono = 44;
    final int SAFTCCITT_ALaw_22kHzStereo = 45;
    final int SAFTCCITT_ALaw_44kHzMono = 46;
    final int SAFTCCITT_ALaw_44kHzStereo = 47;

    // u-Law formats
    final int SAFTCCITT_uLaw_8kHzMono = 48;
    final int SAFTCCITT_uLaw_8kHzStereo = 49;
    final int SAFTCCITT_uLaw_11kHzMono = 50;
    final int SAFTCCITT_uLaw_11kHzStereo = 51;
    final int SAFTCCITT_uLaw_22kHzMono = 52;
    final int SAFTCCITT_uLaw_22kHzStereo = 53;
    final int SAFTCCITT_uLaw_44kHzMono = 54;
    final int SAFTCCITT_uLaw_44kHzStereo = 55;
    final int SAFTADPCM_8kHzMono = 56;
    final int SAFTADPCM_8kHzStereo = 57;
    final int SAFTADPCM_11kHzMono = 58;
    final int SAFTADPCM_11kHzStereo = 59;
    final int SAFTADPCM_22kHzMono = 60;
    final int SAFTADPCM_22kHzStereo = 61;
    final int SAFTADPCM_44kHzMono = 62;
    final int SAFTADPCM_44kHzStereo = 63;

    // GSM 6.10 formats
    final int SAFTGSM610_8kHzMono = 64;
    final int SAFTGSM610_11kHzMono = 65;
    final int SAFTGSM610_22kHzMono = 66;
    final int SAFTGSM610_44kHzMono = 67;

    // Other formats
    final int SAFTNUM_FORMATS = 68;
}
