package com.jbatista.wmo.synthesis;

import com.jbatista.wmo.AudioFormat;
import com.jbatista.wmo.MathUtil;
import com.jbatista.wmo.filters.Filter;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

public class Instrument {

    private int keyId = 0;

    // parameters
    private static AudioFormat audioFormat;
    private double gain = 0.5;
    private Algorithm algorithm = new Algorithm();
    private final LinkedList<Filter> filterChain = new LinkedList<>();

    private final Map<Double, Key> keys = new LinkedHashMap<>();
    private final CopyOnWriteArrayList<Key> keysQueue = new CopyOnWriteArrayList<>();

    private final byte[] buffer16bit = new byte[]{0, 0, 0, 0};
    private final byte[] buffer32bit = new byte[]{0, 0, 0, 0, 0, 0, 0, 0};
    private final short[] shortBuffer = new short[]{0, 0};
    private final float[] floatBuffer = new float[]{0, 0};

    private double[] sample = new double[1];
    private double finalSample = 0;

    public Instrument(AudioFormat audioFormat) {
        setAudioFormat(audioFormat);
    }

    // <editor-fold defaultstate="collapsed" desc="getters/setters">
    public static AudioFormat getAudioFormat() {
        return audioFormat;
    }

    private static void setAudioFormat(AudioFormat _audioFormat) {
        audioFormat = _audioFormat;
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

    void addToQueue(Key key) {
        if (!keysQueue.contains(key)) {
            keysQueue.add(key);
        }
    }
    // </editor-fold>

    private void fillFrame() {
        sample[0] = 0;

        // TODO channel stuff
        for (Key key : keysQueue) {
            sample[0] += key.getSample();

            if (!key.hasActiveCarriers()) {
                keysQueue.remove(key);
            }
        }

        filterChain.forEach(filter -> filter.apply(sample));

        finalSample = sample[0] *= gain;
    }

    public byte[] getByteFrame(boolean bigEndian) {
        fillFrame();

        switch (audioFormat.getBitsPerSample()) {
            case 16:
                MathUtil.primitiveTo16bit(bigEndian, buffer16bit, 0, (int) finalSample);
                buffer16bit[2] = buffer16bit[0];
                buffer16bit[3] = buffer16bit[1];

                return buffer16bit;

            case 32:
                MathUtil.primitiveTo32bit(bigEndian, buffer32bit, 0, (long) finalSample);
                buffer16bit[3] = buffer16bit[0];
                buffer16bit[4] = buffer16bit[1];
                buffer16bit[5] = buffer16bit[2];

                return buffer32bit;

            default:
                throw new RuntimeException("Only 16 or 32 bits are supported");
        }
    }

    public short[] getShortFrame() {
        fillFrame();

        // L
        shortBuffer[0] = (short) finalSample;

        // R
        shortBuffer[1] = shortBuffer[0];

        return shortBuffer;
    }

    public float[] getFloatFrame() {
        fillFrame();

        // L
        floatBuffer[0] = (float) finalSample;

        // R
        floatBuffer[1] = floatBuffer[0];

        return floatBuffer;
    }

    public Key buildKey(double frequency) {
        if (!keys.containsKey(frequency)) {
            keys.put(frequency, new Key(keyId++, frequency, this));
        }

        return keys.get(frequency);
    }

}
