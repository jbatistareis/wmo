package com.jbatista.wmo.synthesis;

import java.util.LinkedList;

public class Algorithm {

    private int oscillatorsCounter = 0;

    private final LinkedList<Oscillator> carriers = new LinkedList<>();
    private final boolean[][] activeCarriers = new boolean[144][36];
    private final long[] elapsed = new long[144];
    private double sample = 0;

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

    double getFrame(int keyId) {
        sample = 0;

        for (Oscillator oscillator : carriers) {
            sample += oscillator.getFrame(keyId, elapsed[keyId]);
            activeCarriers[keyId][oscillator.getId()] = oscillator.isActive(keyId);
        }

        elapsed[keyId] += 1;

        return sample / carriers.size();
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
        return new Oscillator(oscillatorsCounter++);
    }

}
