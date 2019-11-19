package com.jbatista.wmo.synthesis;

import java.util.LinkedHashSet;

public class Algorithm {

    private int oscillatorId = 0;

    private final LinkedHashSet<Oscillator> oscillators = new LinkedHashSet<>();

    public boolean addCarrier(Oscillator carrier) {
        return oscillators.add(carrier);
    }

    public boolean removeCarrier(Oscillator carrier) {
        return oscillators.remove(carrier);
    }

    public void clearCarriers() {
        oscillators.clear();
    }

    public Oscillator[] getCarriers() {
        return oscillators.toArray(new Oscillator[0]);
    }

    void fillFrame(Key key, double[] sample, double[] tempSample, long time) {
        tempSample[0] = 0;
        tempSample[1] = 0;

        for (Oscillator oscillator : oscillators) {
            oscillator.fillFrame(key, tempSample, time);
        }

        sample[0] = tempSample[0] / oscillators.size();
        sample[1] = tempSample[1] / oscillators.size();
    }

    void start(Key key) {
        for (Oscillator oscillator : oscillators) {
            oscillator.start(key);
        }
    }

    void stop(Key key) {
        for (Oscillator oscillator : oscillators) {
            oscillator.stop(key);
        }
    }

    public Oscillator buildOscillator() {
        return new Oscillator(oscillatorId++);
    }

}
