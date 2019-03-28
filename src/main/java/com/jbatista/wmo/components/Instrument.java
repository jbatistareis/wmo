package com.jbatista.wmo.components;

import com.jbatista.wmo.AudioFormat;
import com.jbatista.wmo.MathUtil;
import com.jbatista.wmo.WaveForm;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;

public class Instrument {

    private final Map<Double, Key> keys = new LinkedHashMap<>();
    private final LinkedList<Modulator> modulators = new LinkedList<>();

    private WaveForm waveForm;
    private AudioFormat audioFormat;
    private double sampleRate;
    private int bitsPerSample;

    private double amplitude = 0.5;
    private double attack = 0;
    private double decay = 0;
    private double sustain = 1;
    private double release = 0;

    private double phaseL = 0;
    private double phaseR = 0;

    private int pressedKeys = 0;
    private double effectiveAmplitude = 0;
    private double effectivePhaseL = 0;
    private double effectivePhaseR = 0;
    private double[] tempModulation;

    private double[] tempFrameData;
    private final double[] frameData = new double[]{0, 0};

    // 16 bits
    private final byte[] byte16Buffer = new byte[]{0, 0, 0, 0};
    private final short[] shortBuffer = new short[]{0, 0};
    // 32 bits
    private final byte[] byte32Buffer = new byte[]{0, 0, 0, 0, 0, 0, 0, 0};
    private final float[] floatBuffer = new float[]{0, 0};

    public Instrument(WaveForm waveForm, AudioFormat audioFormat) {
        this.waveForm = waveForm;
        this.audioFormat = audioFormat;

        this.sampleRate = audioFormat.getSampleRate();
        this.bitsPerSample = audioFormat.getBitsPerSample();

        setEffectiveAmplitude();
    }

    // <editor-fold defaultstate="collapsed" desc="getters/setters">
    public WaveForm getWaveForm() {
        return waveForm;
    }

    public void setWaveForm(WaveForm waveForm) {
        this.waveForm = waveForm;
    }

    public double getSampleRate() {
        return sampleRate;
    }

    public int getBitsPerSample() {
        return bitsPerSample;
    }

    public AudioFormat getAudioFormat() {
        return audioFormat;
    }

    public void setAudioFormat(AudioFormat audioFormat) {
        this.audioFormat = audioFormat;
        setEffectiveAmplitude();
    }

    public double getAmplitude() {
        return amplitude;
    }

    public void setAmplitude(double amplitude) {
        this.amplitude = Math.max(0, Math.min(amplitude, 1));
        setEffectiveAmplitude();
    }

    private void setEffectiveAmplitude() {
        effectiveAmplitude = MathUtil.lerp(0, Math.pow(2, bitsPerSample) / 2, amplitude);
    }

    protected double getEffectiveAmplitude() {
        return effectiveAmplitude / pressedKeys;
    }

    public double getAttack() {
        return attack;
    }

    public void setAttack(double attack) {
        this.attack = Math.max(0, attack);
    }

    public double getDecay() {
        return decay;
    }

    public void setDecay(double decay) {
        this.decay = Math.max(0, decay);
    }

    public double getSustain() {
        return sustain;
    }

    public void setSustain(double sustain) {
        this.sustain = Math.max(0, sustain);
    }

    public double getRelease() {
        return release;
    }

    public void setRelease(double release) {
        this.release = Math.max(0, release);
    }

    public double getPhaseL() {
        return phaseL;
    }

    public void setPhaseL(double phaseL) {
        this.phaseL = Math.max(0, Math.min(phaseL, 1));
        this.effectivePhaseL = MathUtil.lerp(0, Math.PI * 2, this.phaseL);
    }

    public double getPhaseR() {
        return phaseR;
    }

    public void setPhaseR(double phaseR) {
        this.phaseR = Math.max(0, Math.min(phaseR, 1));
        this.effectivePhaseR = MathUtil.lerp(0, Math.PI * 2, this.phaseR);
    }

    public void pressKey(double frequency) {
        keys.get(frequency).press();
    }

    public void releaseKey(double frequency) {
        keys.get(frequency).press();
    }

    public Key getKey(double frequency) {
        return keys.get(frequency);
    }

    protected double getEffectivePhaseL() {
        return effectivePhaseL;
    }

    protected double getEffectivePhaseR() {
        return effectivePhaseR;
    }

    protected void incrementKeyCount() {
        pressedKeys++;
    }

    protected void decrementKeyCount() {
        pressedKeys--;
    }
    // </editor-fold>

    private void fillFrame() {
        frameData[0] = 0.0;
        frameData[1] = 0.0;

        // TODO channel stuff
        for (Entry<Double, Key> entry : keys.entrySet()) {
            tempFrameData = entry.getValue().getSample();

            // L
            frameData[0] += tempFrameData[0];

            // R
            frameData[1] += tempFrameData[1];
        }
    }

    public synchronized byte[] getByteFrame(boolean bigEndian) {
        fillFrame();

        if (bigEndian) {
            switch (audioFormat.getBitsPerSample()) {
                case 16:
                    // L
                    byte16Buffer[0] = (byte) ((int) frameData[0] >> 8);
                    byte16Buffer[1] = (byte) frameData[0];

                    // R
                    byte16Buffer[2] = (byte) ((int) frameData[1] >> 8);
                    byte16Buffer[3] = (byte) frameData[1];

                    return byte16Buffer;

                case 32:
                    // L
                    byte32Buffer[0] = (byte) ((int) frameData[0] >> 24);
                    byte32Buffer[1] = (byte) ((int) frameData[0] >> 16);
                    byte32Buffer[2] = (byte) ((int) frameData[0] >> 8);
                    byte32Buffer[3] = (byte) frameData[0];

                    // R
                    byte32Buffer[4] = (byte) ((int) frameData[1] >> 24);
                    byte32Buffer[5] = (byte) ((int) frameData[1] >> 16);
                    byte32Buffer[6] = (byte) ((int) frameData[1] >> 8);
                    byte32Buffer[7] = (byte) frameData[1];

                    return byte32Buffer;

                default:
                    throw new RuntimeException("Only 16 or 32 bits per sample are supported");
            }
        } else {
            switch (audioFormat.getBitsPerSample()) {
                case 16:
                    // L
                    byte16Buffer[0] = (byte) frameData[0];
                    byte16Buffer[1] = (byte) ((int) frameData[0] >> 8);

                    // R
                    byte16Buffer[2] = (byte) frameData[1];
                    byte16Buffer[3] = (byte) ((int) frameData[1] >> 8);

                    return byte16Buffer;

                case 32:
                    // L
                    byte32Buffer[0] = (byte) frameData[0];
                    byte32Buffer[1] = (byte) ((int) frameData[0] >> 8);
                    byte32Buffer[2] = (byte) ((int) frameData[0] >> 16);
                    byte32Buffer[3] = (byte) ((int) frameData[0] >> 24);

                    // R
                    byte32Buffer[4] = (byte) frameData[1];
                    byte32Buffer[5] = (byte) ((int) frameData[1] >> 8);
                    byte32Buffer[6] = (byte) ((int) frameData[1] >> 16);
                    byte32Buffer[7] = (byte) ((int) frameData[1] >> 24);

                    return byte32Buffer;

                default:
                    throw new RuntimeException("Only 16 or 32 bits per sample are supported");
            }
        }
    }

    public synchronized short[] getShortFrame() {
        fillFrame();

        // L
        shortBuffer[0] = (short) frameData[0];

        // R
        shortBuffer[1] = (short) frameData[1];

        return shortBuffer;
    }

    public synchronized float[] getFloatFrame() {
        fillFrame();

        // L
        floatBuffer[0] = (float) frameData[0];

        // R
        floatBuffer[1] = (float) frameData[1];

        return floatBuffer;
    }

    protected void getModulation(double[] buffer, long time, double frequency) {
        buffer[0] = 0;
        buffer[1] = 0;

        for (Modulator modulator : modulators) {
            tempModulation = modulator.calculate(time, frequency);

            buffer[0] += tempModulation[0];
            buffer[1] += tempModulation[1];
        }
    }

    public synchronized Key buildKey(double frequency) {
        if (!keys.containsKey(frequency)) {
            final Key key = new Key(frequency, this);
            keys.put(frequency, key);
        }

        return keys.get(frequency);
    }

    public synchronized Modulator buildModulator() {
        modulators.add(new Modulator(this));

        return modulators.peekLast();
    }

    public synchronized void removeModulator(Modulator modulator) {
        modulators.remove(modulator);
    }

    public synchronized void shiftModulators(Modulator modulator1, Modulator modulator2) {
        final int index1 = modulators.indexOf(modulator1);
        final int index2 = modulators.indexOf(modulator2);

        removeModulator(modulator1);
        removeModulator(modulator2);

        modulators.add(index2, modulator1);
        modulators.add(index1, modulator2);
    }

}
