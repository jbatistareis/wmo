package com.jbatista.wmo.synthesis;

public class Key {

    private final int id;
    private final int hash;

    private final Instrument instrument;
    private final double frequency;
    private final boolean[] activeCarriers = new boolean[36];

    private final double[] sample = new double[1];

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

    double getSample() {
        instrument.getAlgorithm().fillFrame(this, sample, elapsed++);
        return sample[0];
    }

    public void press() {
        if (!hasActiveCarriers()) {
            elapsed = 0;
        }

        pressed = true;
        instrument.getAlgorithm().start(id, frequency);

        instrument.addKeyToQueue(this);
    }

    public void release() {
        pressed = false;
        instrument.getAlgorithm().stop(id);
    }

    public boolean hasActiveCarriers() {
        for (boolean value : activeCarriers) {
            if (value) {
                return true;
            }
        }

        return false;
    }

    void setActiveCarrier(int id, boolean value) {
        activeCarriers[id] = value;
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
