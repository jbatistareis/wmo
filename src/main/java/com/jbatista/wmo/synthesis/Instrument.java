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

    private double sample;
    private final double[] finalSample = new double[2];

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
    // </editor-fold>

    private void getFrame() {
        sample = 0;

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
        sample *= gain;

        // TODO channel stuff, [L][R]
        finalSample[0] = sample;
        finalSample[1] = sample;
    }

    public byte[] getByteFrame(boolean bigEndian) {
        getFrame();

        switch (audioFormat.getBitsPerSample()) {
            case 16:
                MathUtil.primitiveTo16bit(bigEndian, buffer16bit, 0, (int) finalSample[0]);
                MathUtil.primitiveTo16bit(bigEndian, buffer16bit, 2, (int) finalSample[1]);

                return buffer16bit;

            case 32:
                MathUtil.primitiveTo32bit(bigEndian, buffer32bit, 0, (long) finalSample[0]);
                MathUtil.primitiveTo32bit(bigEndian, buffer32bit, 3, (long) finalSample[1]);

                return buffer32bit;

            default:
                throw new RuntimeException("Only 16 or 32 bits are supported");
        }
    }

    public short[] getShortFrame() {
        getFrame();

        shortBuffer[0] = (short) finalSample[0];
        shortBuffer[1] = (short) finalSample[1];

        return shortBuffer;
    }

    public float[] getFloatFrame() {
        getFrame();

        floatBuffer[0] = (float) finalSample[0];
        floatBuffer[1] = (float) finalSample[1];

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

    public boolean addFilter(Filter filter) {
        return filterChain.add(filter);
    }

    public boolean removeFilter(Filter filter) {
        return filterChain.remove(filter);
    }

    public void clearFilters() {
        filterChain.clear();
    }

    public void loadInstrumentPreset(InstrumentPreset instrumentPreset) {
        setGain(instrumentPreset.getGain());
        algorithm.loadAlgorithmPreset(instrumentPreset.getAlgorithm());

        for (OscillatorPreset oscillatorPreset : instrumentPreset.getOscillatorPresets()) {
            algorithm.getOscillator(oscillatorPreset.getId()).loadOscillatorPreset(oscillatorPreset);
        }

        clearFilters();
        for (Filter filter : instrumentPreset.getFilterChain()) {
            addFilter(filter);
        }
    }

}
