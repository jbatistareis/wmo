package com.jbatista.wmo.synthesis;

import java.util.HashMap;
import java.util.Map;

public class Key {

    private final Instrument instrument;

    private final double frequency;
    private final int hash;

    private long elapsed = 0;
    private boolean pressed = false;

    private final Map<Integer, Boolean> activeOscillators = new HashMap<>();

    // L - R
    private final double[] sample = new double[2];
    private final double[] tempSample = new double[2];

    protected Key(double frequency, Instrument instrument) {
        this.frequency = frequency;
        this.instrument = instrument;
        this.hash = ((Double) this.frequency).hashCode();
    }

    // <editor-fold defaultstate="collapsed" desc="getters/setters">
    public double getFrequency() {
        return frequency;
    }

    boolean isOscillatorActive(int oscillatorId) {
        return activeOscillators.getOrDefault(oscillatorId, false);
    }

    void setOscillatorActive(int oscillatorId, boolean value) {
        activeOscillators.put(oscillatorId, value);
    }
    // </editor-fold>

    protected double[] getSample() {
        instrument.getAlgorithm().fillFrame(this, sample, tempSample, elapsed++);

        return sample;
    }

    public void press() {
        if (!activeOscillators()) {
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

    public boolean activeOscillators() {
        for (boolean value : activeOscillators.values()) {
            if (value) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof Key) && ((Key) obj).getFrequency() == this.frequency;
    }

    @Override
    public int hashCode() {
        return hash;
    }

}
