package com.jbatista.wmo.synthesis;

import java.util.HashMap;
import java.util.Map;

public class Key {

    private final int hash;

    private final Instrument instrument;
    private final double frequency;
    private final Map<Integer, Boolean> activeOscillators = new HashMap<>();

    // L - R
    private final double[] sample = new double[2];
    private final double[] tempSample = new double[2];

    private long elapsed = 0;
    private boolean pressed = false;

    Key(double frequency, Instrument instrument) {
        this.frequency = frequency;
        this.instrument = instrument;
        this.hash = ((Double) this.frequency).hashCode();
    }

    public double getFrequency() {
        return frequency;
    }

    public boolean isPressed() {
        return pressed;
    }

    double[] getSample() {
        instrument.getAlgorithm().fillFrame(this, sample, tempSample, elapsed++);

        return sample;
    }

    public void press() {
        if (!hasActiveOscillators()) {
            elapsed = 0;
        }

        pressed = true;
        instrument.getAlgorithm().start(this);

        if (!instrument.getKeysQueue().contains(this)) {
            instrument.getKeysQueue().offer(this);
        }
    }

    public void release() {
        pressed = false;
        instrument.getAlgorithm().stop(this);
    }

    public boolean hasActiveOscillators() {
        for (boolean value : activeOscillators.values()) {
            if (value) {
                return true;
            }
        }

        return false;
    }

    boolean isOscillatorActive(int id) {
        return activeOscillators.getOrDefault(id, false);
    }

    void setActiveOscillator(int oscillatorId, boolean value) {
        activeOscillators.put(oscillatorId, value);
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof Key) && (obj.hashCode() == this.hash);
    }

    @Override
    public int hashCode() {
        return hash;
    }

}
