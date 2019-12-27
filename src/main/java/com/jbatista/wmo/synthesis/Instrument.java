package com.jbatista.wmo.synthesis;

import com.jbatista.wmo.AudioFormat;
import com.jbatista.wmo.MathUtil;
import com.jbatista.wmo.filters.Filter;
import com.jbatista.wmo.preset.InstrumentPreset;
import com.jbatista.wmo.preset.OscillatorPreset;

import java.util.LinkedList;

public class Instrument {

    private int keyCounter = 0;
    private int filterCounter = 0;

    // parameters
    private static AudioFormat audioFormat;
    private double gain = 0.5;
    private final Algorithm algorithm = new Algorithm();
    private final LinkedList<Filter> filterChain = new LinkedList<>();

    private final boolean[] keysQueue = new boolean[144];

    private final byte[] buffer16bit = new byte[]{0, 0, 0, 0};
    private final byte[] buffer32bit = new byte[]{0, 0, 0, 0, 0, 0, 0, 0};
    private final short[] shortBuffer = new short[]{0, 0};
    private final float[] floatBuffer = new float[]{0, 0};

    public Instrument(AudioFormat audioFormat) {
        Instrument.audioFormat = audioFormat;
    }

    // <editor-fold defaultstate="collapsed" desc="getters/setters">
    public static AudioFormat getAudioFormat() {
        return audioFormat;
    }

    public static double getSampleRate() {
        return audioFormat.getSampleRate();
    }

    public static int getBitsPerSample() {
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

    public LinkedList<Filter> getFilterChain() {
        return filterChain;
    }
    // </editor-fold>

    public double getFrame() {
        double sample = 0;

        for (keyCounter = 0; keyCounter < 144; keyCounter++) {
            if (keysQueue[keyCounter]) {
                sample += algorithm.getFrame(keyCounter);

                if (!algorithm.hasActiveCarriers(keyCounter)) {
                    keysQueue[keyCounter] = false;
                }
            }
        }

        for (filterCounter = 0; filterCounter < filterChain.size(); filterCounter++) {
            sample = filterChain.get(filterCounter).apply(sample);
        }

        return gain * sample;
    }

    public byte[] getByteFrame(boolean bigEndian) {
        double sample = getFrame();

        // TODO channel stuff, [L][R]
        switch (audioFormat.getBitsPerSample()) {
            case 16:
                MathUtil.primitiveTo16bit(bigEndian, buffer16bit, 0, (int) sample);
                MathUtil.primitiveTo16bit(bigEndian, buffer16bit, 2, (int) sample);

                return buffer16bit;

            case 32:
                MathUtil.primitiveTo32bit(bigEndian, buffer32bit, 0, (long) sample);
                MathUtil.primitiveTo32bit(bigEndian, buffer32bit, 3, (long) sample);

                return buffer32bit;

            default:
                throw new RuntimeException("Only 16 or 32 bits are supported");
        }
    }

    public short[] getShortFrame() {
        double sample = getFrame();

        // TODO channel stuff, [L][R]
        shortBuffer[0] = (short) sample;
        shortBuffer[1] = (short) sample;

        return shortBuffer;
    }

    public float[] getFloatFrame() {
        double sample = getFrame();

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
        setGain(instrumentPreset.getGain());
        algorithm.loadAlgorithmPreset(instrumentPreset.getAlgorithm());

        for (OscillatorPreset oscillatorPreset : instrumentPreset.getOscillatorPresets()) {
            algorithm.getOscillator(oscillatorPreset.getId()).loadOscillatorPreset(oscillatorPreset);
        }

        filterChain.clear();
        for (Filter filter : instrumentPreset.getFilterChain()) {
            filterChain.add(filter);
        }
    }

}
