package com.jbatista.wmo.components;

import com.jbatista.wmo.MathUtil;
import com.jbatista.wmo.KeyboardNote;
import com.jbatista.wmo.WaveForm;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;

public class Instrument {

    private final Map<Double, Key> keys = new HashMap<>();
    private final LinkedList<Modulator> modulators = new LinkedList<>();

    private WaveForm waveForm = WaveForm.SINE;
    private double sampleRate = 44100;

    private double amplitude = 0.5;
    private double attack = 0;
    private double decay = 0;
    private double sustain = 1;
    private double release = 0.1;

    private double phaseL = 0;
    private double phaseR = 0;

    private double effectiveAmplitude = 16384;
    private double effectivePhaseL = 0;
    private double effectivePhaseR = 0;
    private double[] tempModulation;
    private final double[] modulation = new double[2];

    private double[] tempFrameData;
    private final double[] frameData = new double[]{0, 0};
    private final byte[] buffer = new byte[]{0, 0, 0, 0};

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

    public void setSampleRate(double sampleRate) {
        this.sampleRate = sampleRate;
    }

    public double getAmplitude() {
        return amplitude;
    }

    public void setAmplitude(double amplitude) {
        this.amplitude = Math.max(0, Math.min(amplitude, 1));
        effectiveAmplitude = MathUtil.lerp(0, 32768, this.amplitude);
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
        this.effectivePhaseL = MathUtil.lerp(0, MathUtil.PIx2, this.phaseL);
    }

    public double getPhaseR() {
        return phaseR;
    }

    public void setPhaseR(double phaseR) {
        this.phaseR = Math.max(0, Math.min(phaseR, 1));
        this.effectivePhaseR = MathUtil.lerp(0, MathUtil.PIx2, this.phaseR);
    }

    protected double getEffectiveAmplitude() {
        return effectiveAmplitude / keys.size();
    }

    protected double getEffectivePhaseL() {
        return effectivePhaseL;
    }

    protected double getEffectivePhaseR() {
        return effectivePhaseR;
    }
    // </editor-fold>

    public synchronized byte[] getFrame() {
        for (Entry<Double, Key> entry : keys.entrySet()) {
            tempFrameData = entry.getValue().getSample();

            frameData[0] += tempFrameData[0];
            frameData[1] += tempFrameData[1];
        }

        // TODO channel stuff
        // L
        buffer[0] = (byte) ((int) frameData[0] >> 8);
        buffer[1] = (byte) frameData[0];

        // R
        buffer[2] = (byte) ((int) frameData[1] >> 8);
        buffer[3] = (byte) frameData[1];

        frameData[0] = 0.0;
        frameData[1] = 0.0;

        return buffer;
    }

    protected double[] getModulation(long time) {
        modulation[0] = 0;
        modulation[1] = 0;

        for (Modulator modulator : modulators) {
            tempModulation = modulator.calculate(time);

            modulation[0] += tempModulation[0];
            modulation[1] += tempModulation[1];
        }

        return modulation;
    }

    public Key buildKey(double frequency) {
        if (!keys.containsKey(frequency)) {
            final Key key = new Key(frequency, this);
            keys.put(frequency, key);
        }

        return keys.get(frequency);
    }

    public Key buildKey(KeyboardNote keyboardNote) {
        if (!keys.containsKey(keyboardNote.getFrequency())) {
            final Key key = new Key(keyboardNote.getFrequency(), this);
            keys.put(keyboardNote.getFrequency(), key);
        }

        return keys.get(keyboardNote.getFrequency());
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
