package com.jbatista.wmo.synthesis;

public class Key {

    private final int id;
    private final int hash;

    private final Instrument instrument;
    private final double frequency;
    private final boolean[] activeOscillators = new boolean[36];

    // L - R
    private final double[] sample = new double[2];
    private final double[] tempSample = new double[2];

    private long elapsed = 0;
    private boolean pressed = false;

    Key(int id, double frequency, Instrument instrument) {
        this.frequency = frequency;
        this.instrument = instrument;

        this.id = id;
        this.hash = ((Double) this.frequency).hashCode();
    }

    int getId() {
        return id;
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
        for (boolean value : activeOscillators) {
            if (value) {
                return true;
            }
        }

        return false;
    }

    boolean isOscillatorActive(int id) {
        return activeOscillators[id];
    }

    void setActiveOscillator(int id, boolean value) {
        activeOscillators[id] = value;
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
