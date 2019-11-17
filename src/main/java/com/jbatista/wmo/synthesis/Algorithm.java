package com.jbatista.wmo.synthesis;

import java.util.LinkedHashSet;

public class Algorithm {

    private final LinkedHashSet<Oscillator> oscillators = new LinkedHashSet<>();

    public LinkedHashSet<Oscillator> getOscillators() {
        return oscillators;
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
