package com.jbatista.wmo.synthesis;

import com.jbatista.wmo.AudioFormat;
import com.jbatista.wmo.MathUtil;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Instrument {

    private Algorithm algorithm = new Algorithm();

    private final Map<Double, Key> keys = new LinkedHashMap<>();
    private final Queue<Key> keysQueue = new ConcurrentLinkedQueue<>();
    private short keyIndex;
    private Key tempKey;

    private static AudioFormat audioFormat;
    private static double sampleRate;
    private static int bitsPerSample;

    private double amplitude = 0.1;
    private double attack = 0.01;
    private double decay = 0.01;
    private double sustain = 1;
    private double release = 0.01;

    private double phaseL = 0;
    private double phaseR = 0;

    private double effectiveAmplitude = 0;

    private double[] tempWaveSample;
    private final double[] waveSample = new double[]{0, 0};
    private final double[] finalWaveSample = new double[]{0, 0};

    // 16 bits
    private final byte[] buffer16bit = new byte[]{0, 0, 0, 0};
    private final short[] shortBuffer = new short[]{0, 0};
    // 32 bits
    private final byte[] buffer32bit = new byte[]{0, 0, 0, 0, 0, 0, 0, 0};
    private final float[] floatBuffer = new float[]{0, 0};

    public Instrument(AudioFormat audioFormat) {
        this.audioFormat = audioFormat;

        this.sampleRate = audioFormat.getSampleRate();
        this.bitsPerSample = audioFormat.getBitsPerSample();

        setEffectiveAmplitude();
    }

    // <editor-fold defaultstate="collapsed" desc="getters/setters">
    Queue<Key> getKeysQueue() {
        return keysQueue;
    }

    public Algorithm getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(Algorithm algorithm) {
        this.algorithm = algorithm;
    }

    public static double getSampleRate() {
        return sampleRate;
    }

    public static int getBitsPerSample() {
        return bitsPerSample;
    }

    public static AudioFormat getAudioFormat() {
        return audioFormat;
    }

    public void setAudioFormat(AudioFormat audioFormat) {
        this.audioFormat = audioFormat;
        this.sampleRate = audioFormat.getSampleRate();
        this.bitsPerSample = audioFormat.getBitsPerSample();

        setEffectiveAmplitude();
    }

    public double getAmplitude() {
        return amplitude;
    }

    public void setAmplitude(double amplitude) {
        this.amplitude = Math.max(0.01, Math.min(amplitude, 1));
        setEffectiveAmplitude();
    }

    private void setEffectiveAmplitude() {
        effectiveAmplitude = MathUtil.lerp(0, Math.pow(2, bitsPerSample - 1), amplitude);
    }

    public double getAttack() {
        return attack;
    }

    public void setAttack(double attack) {
        this.attack = Math.max(0.01, Math.min(attack, 1));
        ;
    }

    public double getDecay() {
        return decay;
    }

    public void setDecay(double decay) {
        this.decay = Math.max(0.01, Math.min(decay, 1));
        ;
    }

    public double getSustain() {
        return sustain;
    }

    public void setSustain(double sustain) {
        this.sustain = Math.max(0.01, Math.min(sustain, 1));
        ;
    }

    public double getRelease() {
        return release;
    }

    public void setRelease(double release) {
        this.release = Math.max(0.01, Math.min(release, 1));
        ;
    }

    public double getPhaseL() {
        return phaseL;
    }

    public void setPhaseL(double phaseL) {
        this.phaseL = Math.max(0, Math.min(phaseL, 1));
    }

    public double getPhaseR() {
        return phaseR;
    }

    public void setPhaseR(double phaseR) {
        this.phaseR = Math.max(0, Math.min(phaseR, 1));
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

            if (tempKey.activeOscillators()) {
                keysQueue.offer(tempKey);
            }
        }

        finalWaveSample[0] = waveSample[0] *= effectiveAmplitude;
        finalWaveSample[1] = waveSample[1] *= effectiveAmplitude;
    }

    public synchronized byte[] getByteFrame(boolean bigEndian) {
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

    public synchronized short[] getShortFrame() {
        fillFrame();

        // L
        shortBuffer[0] = (short) finalWaveSample[0];

        // R
        shortBuffer[1] = (short) finalWaveSample[1];

        return shortBuffer;
    }

    public synchronized float[] getFloatFrame() {
        fillFrame();

        // L
        floatBuffer[0] = (float) finalWaveSample[0];

        // R
        floatBuffer[1] = (float) finalWaveSample[1];

        return floatBuffer;
    }

    public synchronized Key buildKey(double frequency) {
        if (!keys.containsKey(frequency)) {
            keys.put(frequency, new Key(frequency, this));
        }

        return keys.get(frequency);
    }

}
