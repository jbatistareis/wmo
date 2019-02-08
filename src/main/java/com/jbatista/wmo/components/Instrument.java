package com.jbatista.wmo.components;

import com.jbatista.wmo.WaveForm;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class Instrument {

    private static final double _2xPI = 2 * Math.PI;
    private static final double _2dPI = 2 / Math.PI;

    private String name;

    private final byte[] buffer = new byte[]{0, 0, 0, 0};

    private final Set<Key> keys = new HashSet<>();
    private final Iterator<Key> keysIterator = keys.iterator();
    private Key key;

    private WaveForm waveForm = WaveForm.SINE;
    private double sampleRate = 44100;

    private double amplitude = 0.5;
    private double attack = 0.1;
    private double decay = 0;
    private double sustain = 1;
    private double release = 0.1;

    private double amplitudeValue = 16384;
    private short frameData = 0;

    public Instrument(String name) {
        this.name = name;
    }

    // <editor-fold defaultstate="collapsed" desc="getters/setters">
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

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
        amplitudeValue = Util.lerp(0, 32768, amplitude);
    }

    protected double getAmplitudeValue() {
        return amplitudeValue;
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
    // </editor-fold>

    public byte[] getFrame() {
        keys.forEach(key -> {
            key.calculate();
            frameData += oscilator(key.getCalculatedAmplitude(), sampleRate, key.getFrequency(), key.getElapsed());
            frameData /= keys.size();
        });

        // L
        buffer[0] = (byte) (frameData >> 8);
        buffer[1] = (byte) frameData;

        // R
        buffer[2] = (byte) (frameData >> 8);
        buffer[3] = (byte) frameData;

        frameData = 0;

        while (keysIterator.hasNext()) {
            key = keysIterator.next();
            if (key.getKeyState().equals(Key.KeyState.IDLE)) {
                keysIterator.remove();
            }
        }

        return buffer;
    }

    protected void addKey(Key key) {
        keys.add(key);
    }

    protected void removeKey(Key key) {
        keys.remove(key);
    }

    private double oscilator(double amplitude, double sampleRate, double frequency, long frame) {
        switch (getWaveForm()) {
            case SINE:
                return amplitude * Math.sin(_2xPI * (frequency / sampleRate) * frame);
            case SQUARE:
                return amplitude * Math.signum(Math.sin(_2xPI * (frequency / sampleRate) * frame));
            case TRIANGLE:
                return amplitude * _2dPI * Math.asin(Math.sin(_2xPI * (frequency / sampleRate) * frame));
            case SAWTOOTH:
                return amplitude * ((frame + sampleRate / frequency * 2) % (sampleRate / frequency)) / (sampleRate / frequency) - amplitude / 2;
            default:
                throw new AssertionError(getWaveForm().name());
        }
    }

}
