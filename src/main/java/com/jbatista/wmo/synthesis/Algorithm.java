package com.jbatista.wmo.synthesis;

import java.util.LinkedHashSet;

public class Algorithm {

    private final LinkedHashSet<Oscillator> oscillators = new LinkedHashSet<>();

    public boolean addOscillator(Oscillator oscillator) {
        return oscillators.add(oscillator);
    }

    public boolean removeOscillator(Oscillator oscillator) {
        return oscillators.remove(oscillator);
    }

    public void clearOscillators() {
        oscillators.clear();
    }

    public Oscillator[] getOscillators() {
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

}
