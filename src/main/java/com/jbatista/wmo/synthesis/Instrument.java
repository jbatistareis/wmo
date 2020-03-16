package com.jbatista.wmo.synthesis;

import com.jbatista.wmo.AudioFormat;
import com.jbatista.wmo.preset.InstrumentPreset;
import com.jbatista.wmo.preset.OscillatorPreset;
import com.jbatista.wmo.util.MathFunctions;

public class Instrument {

    private int keyId = 0;

    // parameters
    private AudioFormat audioFormat;
    private double gain = 0.5;
    private final Algorithm algorithm;
    private final FilterChain filterChain = new FilterChain();

    private final boolean[] keysQueue = new boolean[132];

    private double tempSample;
    private final byte[] buffer16bit = new byte[]{0, 0, 0, 0};
    private final byte[] buffer32bit = new byte[]{0, 0, 0, 0, 0, 0, 0, 0};
    private final short[] shortBuffer = new short[]{0, 0};
    private final float[] floatBuffer = new float[]{0, 0};

    public Instrument(AudioFormat audioFormat) {
        this.audioFormat = audioFormat;
        this.algorithm = new Algorithm(audioFormat.getSampleRate());

        loadInstrumentPreset(new InstrumentPreset());
    }

    // <editor-fold defaultstate="collapsed" desc="getters/setters">
    public AudioFormat getAudioFormat() {
        return audioFormat;
    }

    public double getSampleRate() {
        return audioFormat.getSampleRate();
    }

    public int getBitsPerSample() {
        return audioFormat.getBitsPerSample();
    }

    public double getGain() {
        return gain;
    }

    public void setGain(double gain) {
        this.gain = Math.max(0, Math.min(gain, 10));
    }

    public Algorithm getAlgorithm() {
        return algorithm;
    }

    public FilterChain getFilterChain() {
        return filterChain;
    }
    // </editor-fold>

    public double getSample() {
        tempSample = 0;

        for (keyId = 0; keyId < 132; keyId++) {
            if (keysQueue[keyId]) {
                tempSample += algorithm.getSample(keyId);

                if (!algorithm.hasActiveCarriers(keyId)) {
                    keysQueue[keyId] = false;
                }
            }
        }

        return gain * filterChain.getResult(tempSample);
    }

    public byte[] getByteFrame(boolean bigEndian) {
        final double sample = getSample();

        // TODO channel stuff, [L][R]
        switch (audioFormat.getBitsPerSample()) {
            case 16:
                MathFunctions.primitiveTo16bit(bigEndian, buffer16bit, 0, (int) sample);
                MathFunctions.primitiveTo16bit(bigEndian, buffer16bit, 2, (int) sample);

                return buffer16bit;

            case 32:
                MathFunctions.primitiveTo32bit(bigEndian, buffer32bit, 0, (long) sample);
                MathFunctions.primitiveTo32bit(bigEndian, buffer32bit, 3, (long) sample);

                return buffer32bit;

            default:
                throw new RuntimeException("Only 16 or 32 bits are supported");
        }
    }

    public short[] getShortFrame() {
        final double sample = getSample();

        // TODO channel stuff, [L][R]
        shortBuffer[0] = (short) sample;
        shortBuffer[1] = (short) sample;

        return shortBuffer;
    }

    public float[] getFloatFrame() {
        final double sample = getSample();

        // TODO channel stuff, [L][R]
        floatBuffer[0] = (float) sample;
        floatBuffer[1] = (float) sample;

        return floatBuffer;
    }

    public void pressKey(int keyId, double frequency) {
        algorithm.start(keyId, frequency);
        keysQueue[keyId] = true;
    }

    public void releaseKey(int keyId, double frequency) {
        algorithm.stop(keyId);
    }

    public void releaseAllKeys() {
        algorithm.stopAll();
    }

    public void loadInstrumentPreset(InstrumentPreset instrumentPreset) {
        algorithm.loadAlgorithmPreset(instrumentPreset.getAlgorithm());

        for (OscillatorPreset oscillatorPreset : instrumentPreset.getOscillatorPresets()) {
            algorithm.getOscillator(oscillatorPreset.getId()).loadOscillatorPreset(oscillatorPreset);
        }

        filterChain.clear();
    }

}
