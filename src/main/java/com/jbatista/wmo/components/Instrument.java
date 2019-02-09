package com.jbatista.wmo.components;

import com.jbatista.wmo.MathUtil;
import com.jbatista.wmo.Note;
import com.jbatista.wmo.WaveForm;
import com.jbatista.wmo.components.Key.KeyState;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class Instrument {

    private final byte[] buffer = new byte[]{0, 0, 0, 0};

    private final Set<Key> keys = new HashSet<>();
    private Iterator<Key> keysIterator;
    private Key key;

    private WaveForm waveForm = WaveForm.SINE;
    private double sampleRate = 44100;

    private double amplitude = 0.5;
    private double attack = 0.1;
    private double decay = 0;
    private double sustain = 1;
    private double release = 0.1;

    private double effectiveAmplitude = 16384;
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

    public byte[] getFrame() {
        keysIterator = keys.iterator();
        while (keysIterator.hasNext()) {
            key = keysIterator.next();

            frameData += key.getAmplitude() * key.getSample();

            if (key.getKeyState().equals(KeyState.IDLE)) {
                keysIterator.remove();
            }
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

    // TODO
    protected double getModulation(long time) {
        return 0;
    }

    protected void addKey(Key key) {
        keys.add(key);
    }

    public Key buildKey(double frequency) {
        return new Key(frequency, this);
    }

    public Key buildKey(Note note) {
        return new Key(note.getFrequency(), this);
    }

}
