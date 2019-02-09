package com.jbatista.wmo.components;

import com.jbatista.wmo.MathUtil;
import com.jbatista.wmo.Note;
import com.jbatista.wmo.WaveForm;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;

public class Instrument {

    private final byte[] buffer = new byte[]{0, 0, 0, 0};

    private final Map<Double, Key> keys = new HashMap<>();
    private final LinkedList<Modulator> modulators = new LinkedList<>();

    private WaveForm waveForm = WaveForm.SINE;
    private double sampleRate = 44100;

    private double amplitude = 0.5;
    private double attack = 0.1;
    private double decay = 0;
    private double sustain = 1;
    private double release = 0.1;

    private double effectiveAmplitude = 16384;
    private double modulation = 0;
    private short frameData = 0;

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
        this.amplitude = amplitude;
        effectiveAmplitude = MathUtil.lerp(0, 32768, amplitude);
    }

    public double getAttack() {
        return attack;
    }

    public void setAttack(double attack) {
        this.attack = attack;
    }

    public double getDecay() {
        return decay;
    }

    public void setDecay(double decay) {
        this.decay = decay;
    }

    public double getSustain() {
        return sustain;
    }

    public void setSustain(double sustain) {
        this.sustain = sustain;
    }

    public double getRelease() {
        return release;
    }

    public void setRelease(double release) {
        this.release = release;
    }

    protected double getEffectiveAmplitude() {
        return effectiveAmplitude / keys.size();
    }
    // </editor-fold>

    public synchronized byte[] getFrame() {
        for (Entry<Double, Key> entry : keys.entrySet()) {
            frameData += entry.getValue().getSample();
        }

        // TODO channel stuff
        // L
        buffer[0] = (byte) (frameData >> 8);
        buffer[1] = (byte) frameData;

        // R
        buffer[2] = (byte) (frameData >> 8);
        buffer[3] = (byte) frameData;

        frameData = 0;

        return buffer;
    }

    protected double getModulation(long time) {
        modulation = 0;

        for (Modulator modulator : modulators) {
            modulation += modulator.calculate(time);
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

    public Key buildKey(Note note) {
        if (!keys.containsKey(note.getFrequency())) {
            final Key key = new Key(note.getFrequency(), this);
            keys.put(note.getFrequency(), key);
        }

        return keys.get(note.getFrequency());
    }

    public synchronized Modulator buildModulator() {
        modulators.add(new Modulator(this));

        return modulators.peekLast();
    }

}
