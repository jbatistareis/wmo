package com.jbatista.wmo.synthesis;

import java.util.LinkedList;

public class Algorithm {

    private int oscillatorId = 0;

    private final LinkedList<Oscillator> carriers = new LinkedList<>();
    private final boolean[][] activeCarriers = new boolean[144][36];
    private final long[] elapsed = new long[144];

    public boolean addCarrier(Oscillator carrier) {
        if (carriers.contains(carrier)) {
            return false;
        }

        return carriers.add(carrier);
    }

    public boolean removeCarrier(Oscillator carrier) {
        return carriers.remove(carrier);
    }

    public void clearCarriers() {
        carriers.clear();
    }

    public Oscillator[] getCarriers() {
        return carriers.toArray(new Oscillator[0]);
    }

    void fillFrame(int keyId, double[] sample) {
        sample[0] = 0;

        for (Oscillator oscillator : carriers) {
            activeCarriers[keyId][oscillator.getId()] = oscillator.fillFrame(keyId, sample, elapsed[keyId]);
        }

        sample[0] /= carriers.size();
        elapsed[keyId] += 1;
    }

    void start(int keyId, double frequency) {
        if (!hasActiveCarriers(keyId)) {
            elapsed[keyId] = 0;
        }

        for (Oscillator oscillator : carriers) {
            oscillator.start(keyId, frequency);
        }
    }

    void stop(int keyId) {
        for (Oscillator oscillator : carriers) {
            oscillator.stop(keyId);
        }
    }

    public boolean hasActiveCarriers(int keyId) {
        for (boolean value : activeCarriers[keyId]) {
            if (value) {
                return true;
            }
        }

        return false;
    }

    public Oscillator buildOscillator() {
        return new Oscillator(oscillatorId++, Instrument.getSampleRate());
    }

}
