package com.jbatista.wmo.synthesis;

import com.jbatista.wmo.AudioFormat;
import com.jbatista.wmo.MathUtil;
import com.jbatista.wmo.filters.Filter;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Instrument {

    // parameters
    private static AudioFormat audioFormat;
    private double gain = 0.5;
    private Algorithm algorithm = new Algorithm();
    private final LinkedList<Filter> filterChain = new LinkedList<>();

    private final Map<Double, Key> keys = new LinkedHashMap<>();
    private final Queue<Key> keysQueue = new ConcurrentLinkedQueue<>();
    private short keyIndex;
    private Key tempKey;

    private final byte[] buffer16bit = new byte[]{0, 0, 0, 0};
    private final short[] shortBuffer = new short[]{0, 0};
    private final byte[] buffer32bit = new byte[]{0, 0, 0, 0, 0, 0, 0, 0};
    private final float[] floatBuffer = new float[]{0, 0};

    private final double[] waveSample = new double[]{0, 0};
    private final double[] finalWaveSample = new double[]{0, 0};
    private double[] tempWaveSample;

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

    public boolean addFilter(Filter filter) {
        return filterChain.add(filter);
    }

    public boolean removeFilter(Filter filter) {
        return filterChain.remove(filter);
    }

    public void clearFilters() {
        filterChain.clear();
    }

    public void setAlgorithm(Algorithm algorithm) {
        this.algorithm = algorithm;
    }

    public void pressKey(double frequency) {
        keys.get(frequency).press();
    }

    public void releaseKey(double frequency) {
        keys.get(frequency).release();
    }

    public Key getKey(double frequency) {
        return keys.get(frequency);
    }

    Queue<Key> getKeysQueue() {
        return keysQueue;
    }

    // </editor-fold>
    private void fillFrame() {
        waveSample[0] = 0.0;
        waveSample[1] = 0.0;

        // TODO channel stuff
        for (keyIndex = 0; keyIndex < keysQueue.size(); keyIndex++) {
            tempKey = keysQueue.poll();
            tempWaveSample = tempKey.getSample();

            // L
            waveSample[0] += tempWaveSample[0];

            // R
            waveSample[1] += tempWaveSample[1];

            if (tempKey.hasActiveOscillators()) {
                keysQueue.offer(tempKey);
            }
        }

        filterChain.forEach(filter -> filter.apply(waveSample));

        finalWaveSample[0] = waveSample[0] *= gain;
        finalWaveSample[1] = waveSample[1] *= gain;
    }

    public byte[] getByteFrame(boolean bigEndian) {
        fillFrame();

        switch (audioFormat.getBitsPerSample()) {
            case 16:
                // L
                MathUtil.primitiveTo16bit(bigEndian, buffer16bit, 0, (int) finalWaveSample[0]);

                // R
                MathUtil.primitiveTo16bit(bigEndian, buffer16bit, 2, (int) finalWaveSample[1]);

                return buffer16bit;

            case 32:
                // L
                MathUtil.primitiveTo32bit(bigEndian, buffer32bit, 0, (long) finalWaveSample[0]);

                // R
                MathUtil.primitiveTo32bit(bigEndian, buffer32bit, 4, (long) finalWaveSample[1]);

                return buffer32bit;

            default:
                throw new RuntimeException("Only 16 or 32 bits are supported");
        }
    }

    public short[] getShortFrame() {
        fillFrame();

        // L
        shortBuffer[0] = (short) finalWaveSample[0];

        // R
        shortBuffer[1] = (short) finalWaveSample[1];

        return shortBuffer;
    }

    public float[] getFloatFrame() {
        fillFrame();

        // L
        floatBuffer[0] = (float) finalWaveSample[0];

        // R
        floatBuffer[1] = (float) finalWaveSample[1];

        return floatBuffer;
    }

    public Key buildKey(double frequency) {
        if (!keys.containsKey(frequency)) {
            keys.put(frequency, new Key(frequency, this));
        }

        return keys.get(frequency);
    }

}
