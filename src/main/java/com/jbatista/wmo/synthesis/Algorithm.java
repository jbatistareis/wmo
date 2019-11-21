package com.jbatista.wmo.synthesis;

import java.util.LinkedList;

public class Algorithm {

    private int oscillatorId = 0;

    private final LinkedList<Oscillator> carriers = new LinkedList<>();

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

    void fillFrame(Key key, double[] sample, long time) {
        sample[0] = 0;

        for (Oscillator oscillator : carriers) {
            key.setActiveCarrier(oscillator.getId(), oscillator.fillFrame(key.getId(), sample, time));
        }

        sample[0] /= carriers.size();
    }

    void start(int keyId, double frequency) {
        for (Oscillator oscillator : carriers) {
            oscillator.start(keyId, frequency);
        }
    }

    void stop(int keyId) {
        for (Oscillator oscillator : carriers) {
            oscillator.stop(keyId);
        }
    }

    public Oscillator buildOscillator() {
        return new Oscillator(oscillatorId++);
    }

}
