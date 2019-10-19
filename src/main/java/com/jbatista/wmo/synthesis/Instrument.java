package com.jbatista.wmo.synthesis;

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

    private double amplitude = 0.1;
    private double attack = 0;
    private double decay = 0;
    private double sustain = 1;
    private double release = 0;

    private double phaseL = 0;
    private double phaseR = 0;

    private double effectiveAmplitude = 0;
    private double[] tempModulation;

    private double frameData0;
    private double frameData1;
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
        this.sampleRate = audioFormat.getSampleRate();
        this.bitsPerSample = audioFormat.getBitsPerSample();

        setEffectiveAmplitude();
    }

    public double getAmplitude() {
        return amplitude;
    }

    public void setAmplitude(double amplitude) {
        this.amplitude = Math.max(0.1, Math.min(amplitude, 1));
        setEffectiveAmplitude();
    }

    private void setEffectiveAmplitude() {
        effectiveAmplitude = MathUtil.lerp(0, Math.pow(2, bitsPerSample - 1), amplitude);
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
        frameData0 = 0.0;
        frameData1 = 0.0;

        // TODO channel stuff
        for (Entry<Double, Key> entry : keys.entrySet()) {
            if (!entry.getValue().getKeyState().equals(Key.KeyState.IDLE)) {
                tempFrameData = entry.getValue().getSample();

                // L
                frameData0 += tempFrameData[0];

                // R
                frameData1 += tempFrameData[1];
            }
        }

        frameData[0] = effectiveAmplitude * frameData0;
        frameData[1] = effectiveAmplitude * frameData1;
    }

    public synchronized byte[] getByteFrame(boolean bigEndian) {
        fillFrame();

        switch (audioFormat.getBitsPerSample()) {
            case 16:
                // L
                MathUtil.primitiveTo16bit(bigEndian, byte16Buffer, 0, (int) frameData[0]);

                // R
                MathUtil.primitiveTo16bit(bigEndian, byte16Buffer, 2, (int) frameData[1]);

                return byte16Buffer;

            case 32:
                // L
                MathUtil.primitiveTo32bit(bigEndian, byte32Buffer, 0, (long) frameData[0]);

                // R
                MathUtil.primitiveTo32bit(bigEndian, byte32Buffer, 4, (long) frameData[1]);

                return byte32Buffer;

            default:
                throw new RuntimeException("Only 16 or 32 bits per sample are supported");
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

    protected void getModulation(double[] buffer, long time) {
        buffer[0] = 0;
        buffer[1] = 0;

        for (Modulator modulator : modulators) {
            tempModulation = modulator.calculate(time);

            buffer[0] += tempModulation[0] / modulators.size();
            buffer[1] += tempModulation[1] / modulators.size();
        }
    }

    public synchronized Key buildKey(double frequency) {
        if (!keys.containsKey(frequency)) {
            keys.put(frequency, new Key(frequency, this));
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

}
