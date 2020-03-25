package com.jbatista.wmo.synthesis;

import com.jbatista.wmo.KeyboardNote;
import com.jbatista.wmo.preset.InstrumentPreset;
import com.jbatista.wmo.preset.OscillatorPreset;
import com.jbatista.wmo.util.MathFunctions;

public class Instrument {

    private final static KeyboardNote[] NOTES = KeyboardNote.values();

    private int keyId = 0;

    // parameters
    private int sampleRate = 44100;
    private double gain = 0.01;
    private int transpose = 0;
    private final Algorithm algorithm;
    private final FilterChain filterChain = new FilterChain();

    private final boolean[] keysQueue = new boolean[132];

    private double frameSample;
    private final byte[] buffer16bit = new byte[]{0, 0, 0, 0};
    private final short[] shortBuffer = new short[]{0, 0};
    private final float[] floatBuffer = new float[]{0, 0};

    public Instrument() {
        this.algorithm = new Algorithm();

        loadInstrumentPreset(new InstrumentPreset());
    }

    // <editor-fold defaultstate="collapsed" desc="getters/setters">

    public int getSampleRate() {
        return sampleRate;
    }

    public void setSampleRate(int sampleRate) {
        this.sampleRate = sampleRate;
        this.algorithm.setSampleRate(sampleRate);
    }

    public double getGain() {
        return gain;
    }

    public void setGain(double gain) {
        this.gain = Math.max(0, Math.min(gain, 1));
    }

    public int getTranspose() {
        return transpose;
    }

    public void setTranspose(int transpose) {
        this.transpose = Math.max(-24, Math.min(transpose, 24));
    }

    public Algorithm getAlgorithm() {
        return algorithm;
    }

    public FilterChain getFilterChain() {
        return filterChain;
    }
    // </editor-fold>

    public double getSample() {
        frameSample = 0;

        for (keyId = 0; keyId < 132; keyId++) {
            if (keysQueue[keyId]) {
                frameSample += algorithm.getSample(keyId);

                if (!algorithm.hasActiveCarriers(keyId)) {
                    keysQueue[keyId] = false;
                }
            }
        }

        frameSample = gain * filterChain.getResult(frameSample);

        return frameSample;
    }

    public byte[] getByteFrame(boolean bigEndian) {
        getSample();
        frameSample *= 32768;

        // TODO channel stuff, [L][R]
        MathFunctions.primitiveTo16bit(bigEndian, buffer16bit, 0, (int) frameSample);
        MathFunctions.primitiveTo16bit(bigEndian, buffer16bit, 2, (int) frameSample);

        return buffer16bit;
    }

    public short[] getShortFrame() {
        getSample();

        // TODO channel stuff, [L][R]
        shortBuffer[0] = (short) frameSample;
        shortBuffer[1] = (short) frameSample;

        return shortBuffer;
    }

    public float[] getFloatFrame() {
        getSample();

        // TODO channel stuff, [L][R]
        floatBuffer[0] = (float) frameSample;
        floatBuffer[1] = (float) frameSample;

        return floatBuffer;
    }

    public void pressKey(KeyboardNote key) {
        pressKey(key.getId());
    }

    public void pressKey(int keyId) {
        keyId += transpose;

        if ((keyId >= 0) || (keyId <= 131)) {
            algorithm.start(keyId, NOTES[keyId].getFrequency());
            keysQueue[keyId] = true;
        }
    }

    public void releaseKey(KeyboardNote key) {
        releaseKey(key.getId());
    }

    public void releaseKey(int keyId) {
        keyId += transpose;

        if ((keyId >= 0) || (keyId <= 131)) {
            algorithm.stop(keyId);
        }
    }

    public void releaseAllKeys() {
        algorithm.stopAll();
    }

    public void loadInstrumentPreset(InstrumentPreset instrumentPreset) {
        setGain(instrumentPreset.getGain());
        setTranspose(instrumentPreset.getTranspose());

        algorithm.loadAlgorithmPreset(instrumentPreset.getAlgorithm());
        algorithm.setFeedback(instrumentPreset.getFeedback());

        for (OscillatorPreset oscillatorPreset : instrumentPreset.getOscillatorPresets()) {
            algorithm.getOscillator(oscillatorPreset.getId()).loadOscillatorPreset(oscillatorPreset);
        }

        filterChain.clear();
    }

}
