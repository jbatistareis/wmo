package com.jbatista.wmo.synthesis;

import com.jbatista.wmo.AudioFormat;
import com.jbatista.wmo.MathUtil;
import com.jbatista.wmo.filters.Filter;

import java.util.LinkedList;

public class Instrument {

    private int keyCounter = 0;

    // parameters
    private static AudioFormat audioFormat;
    private double gain = 0.5;
    private Algorithm algorithm = new Algorithm();
    private final LinkedList<Filter> filterChain = new LinkedList<>();

    private final boolean[] keysQueue = new boolean[144];

    private final byte[] buffer16bit = new byte[]{0, 0, 0, 0};
    private final byte[] buffer32bit = new byte[]{0, 0, 0, 0, 0, 0, 0, 0};
    private final short[] shortBuffer = new short[]{0, 0};
    private final float[] floatBuffer = new float[]{0, 0};

    private final double[][] keySample = new double[144][1];
    private final double[] algorithmSample = new double[1];
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
        this.gain = Math.max(0, Math.min(gain, 2));
    }

    public Algorithm getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(Algorithm algorithm) {
        this.algorithm = algorithm;
    }
    // </editor-fold>

    private void fillFrame() {
        algorithmSample[0] = 0;

        for (keyCounter = 0; keyCounter < 144; keyCounter++) {
            if (keysQueue[keyCounter]) {
                algorithm.fillFrame(keyCounter, keySample[keyCounter]);
                algorithmSample[0] += keySample[keyCounter][0];

                if (!algorithm.hasActiveCarriers(keyCounter)) {
                    keysQueue[keyCounter] = false;
                }
            }
        }

        filterChain.forEach(filter -> filter.apply(algorithmSample));
        algorithmSample[0] *= gain;

        // TODO channel stuff, [L][R]
        finalSample[0] = algorithmSample[0];
        finalSample[1] = algorithmSample[0];
    }

    public byte[] getByteFrame(boolean bigEndian) {
        fillFrame();

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
        fillFrame();

        shortBuffer[0] = (short) finalSample[0];
        shortBuffer[1] = (short) finalSample[1];

        return shortBuffer;
    }

    public float[] getFloatFrame() {
        fillFrame();

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

    public boolean addFilter(Filter filter) {
        return filterChain.add(filter);
    }

    public boolean removeFilter(Filter filter) {
        return filterChain.remove(filter);
    }

    public void clearFilters() {
        filterChain.clear();
    }

}
