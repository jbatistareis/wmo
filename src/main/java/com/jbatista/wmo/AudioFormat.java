package com.jbatista.wmo;

public enum AudioFormat {

    _8000Hz_16bit(8000, 16),
    _11025Hz_16bit(11025, 16),
    _22050Hz_16bit(22050, 16),
    _32000Hz_16bit(32000, 16),
    _44100Hz_16bit(44100, 16),
    _48000Hz_16bit(48000, 16),

    _8000Hz_32bit(8000, 32),
    _11025Hz_32bit(11025, 32),
    _22050Hz_32bit(22050, 32),
    _32000Hz_32bit(32000, 32),
    _44100Hz_32bit(44100, 32),
    _48000Hz_32bit(48000, 32);

    private double sampleRate;
    private int bitsPerSample;

    AudioFormat(double sampleRate, int bitsPerSample) {
        this.sampleRate = sampleRate;
        this.bitsPerSample = bitsPerSample;
    }

    public double getSampleRate() {
        return sampleRate;
    }

    public int getBitsPerSample() {
        return bitsPerSample;
    }

}
