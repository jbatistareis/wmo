package com.jbatista.wmo.synthesis;

public class Algorithm {

    private final double sampleRate;

    private int[][] algorithm = new int[0][0];
    private final Oscillator[] oscillators = new Oscillator[36];
    private final boolean[][] activeCarriers = new boolean[132][36];
    private final long[] elapsed = new long[132];
    private double tempSample;

    public Algorithm(double sampleRate) {
        this.sampleRate = sampleRate;
    }

    public void loadAlgorithmPreset(int[][] algorithm) {
        this.algorithm = algorithm;
    }

    public Oscillator getOscillator(int id) {
        if (oscillators[id] == null) {
            oscillators[id] = new Oscillator(id, sampleRate, this);
        }

        return oscillators[id];
    }

    int[][] getAlgorithm() {
        return algorithm;
    }

    double getSample(int keyId) {
        tempSample = 0;

        for (int i = 0; i < algorithm[0].length; i++) {
            tempSample += oscillators[algorithm[0][i]].getFrame(keyId, 1, elapsed[keyId]);
            activeCarriers[keyId][oscillators[algorithm[0][i]].getId()]
                    = oscillators[algorithm[0][i]].isActive(keyId);
        }

        elapsed[keyId] += 1;

        return tempSample / algorithm[0].length;
    }

    void start(int keyId, double frequency) {
        if (!hasActiveCarriers(keyId)) {
            elapsed[keyId] = 0;
        }

        for (int i = 0; i < algorithm[0].length; i++) {
            oscillators[algorithm[0][i]].start(keyId, frequency);
        }
    }

    void stop(int keyId) {
        for (int i = 0; i < algorithm[0].length; i++) {
            oscillators[algorithm[0][i]].stop(keyId);
        }
    }

    void stopAll() {
        for (int i = 0; i < 144; i++) {
            for (int j = 0; j < algorithm[0].length; i++) {
                oscillators[algorithm[0][i]].stop(i);
            }
        }
    }

    boolean hasActiveCarriers(int keyId) {
        for (int i = 0; i < 36; i++) {
            if (activeCarriers[keyId][i]) {
                return true;
            }
        }

        return false;
    }

}
