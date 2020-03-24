package com.jbatista.wmo.synthesis;

import com.jbatista.wmo.preset.AlgorithmPreset;

public class Algorithm {

    private final int sampleRate;
    private int feedback = 0;

    private final boolean[][] activeCarriers = new boolean[132][6];
    private final long[] elapsed = new long[132];
    private double tempSample;

    int[][] pattern = AlgorithmPreset.ALGO_4_OSC_1.getPattern();
    final Oscillator[] oscillators = new Oscillator[6];

    public Algorithm(int sampleRate) {
        this.sampleRate = sampleRate;

        for (int i = 0; i < oscillators.length; i++) {
            oscillators[i] = new Oscillator(i, sampleRate, this);
        }
    }

    public int getFeedback() {
        return feedback;
    }

    public void setFeedback(int feedback) {
        getOscillator(pattern[1][0]).setFeedback(feedback);
        this.feedback = getOscillator(pattern[1][0]).getFeedback();
    }

    public void loadAlgorithmPreset(AlgorithmPreset algorithm) {
        this.pattern = algorithm.getPattern();
    }

    public Oscillator getOscillator(int id) {
        if (oscillators[id] == null) {
            oscillators[id] = new Oscillator(id, sampleRate, this);
        }

        return oscillators[id];
    }

    double getSample(int keyId) {
        tempSample = 0;

        for (int i = 0; i < pattern[0].length; i++) {
            tempSample += oscillators[pattern[0][i]].getFrame(keyId, 1, elapsed[keyId]);
            activeCarriers[keyId][oscillators[pattern[0][i]].getId()] = oscillators[pattern[0][i]].isActive(keyId);
        }

        elapsed[keyId] += 1;

        return tempSample / pattern[0].length;
    }

    void start(int keyId, double frequency) {
        if (!hasActiveCarriers(keyId)) {
            elapsed[keyId] = 0;
        }

        for (int i = 0; i < pattern[0].length; i++) {
            oscillators[pattern[0][i]].start(keyId, frequency);
        }
    }

    void stop(int keyId) {
        for (int i = 0; i < pattern[0].length; i++) {
            oscillators[pattern[0][i]].stop(keyId);
        }
    }

    void stopAll() {
        for (int i = 0; i < 132; i++) {
            for (int j = 0; j < pattern[0].length; j++) {
                oscillators[pattern[0][j]].stop(i);
            }
        }
    }

    boolean hasActiveCarriers(int keyId) {
        for (int i = 0; i < 6; i++) {
            if (activeCarriers[keyId][i]) {
                return true;
            }
        }

        return false;
    }

}
