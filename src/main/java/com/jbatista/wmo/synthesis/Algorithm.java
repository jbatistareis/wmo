package com.jbatista.wmo.synthesis;

public class Algorithm {

    private final double sampleRate;

    private Oscillator[] carriers = new Oscillator[1];
    private final Oscillator[] oscillators = new Oscillator[36];
    private final boolean[][] activeCarriers = new boolean[168][36];
    private final long[] elapsed = new long[168];

    public Algorithm(double sampleRate) {
        this.sampleRate = sampleRate;
    }

    public void loadAlgorithmPreset(int[][] algorithm) {
        carriers = new Oscillator[algorithm[0].length];

        for (int i = 0; i < algorithm[0].length; i++) {
            carriers[i] = getOscillator(algorithm[0][i]);
        }

        for (int i = 1; i < algorithm.length; i++) {
            getOscillator(algorithm[i][0]).getModulators().add(getOscillator(algorithm[i][1]));
        }
    }

    public Oscillator getOscillator(int id) {
        if (oscillators[id] == null) {
            oscillators[id] = new Oscillator(id, sampleRate);
        }

        return oscillators[id];
    }

    double getFrame(int keyId) {
        double sample = 0;

        for (int i = 0; i < carriers.length; i++) {
            sample += carriers[i].getFrame(keyId, 1, elapsed[keyId]);
            activeCarriers[keyId][carriers[i].getId()] = carriers[i].isActive(keyId);
        }

        elapsed[keyId] += 1;

        return sample / carriers.length;
    }

    void start(int keyId, double frequency) {
        if (!hasActiveCarriers(keyId)) {
            elapsed[keyId] = 0;
        }

        for (int i = 0; i < carriers.length; i++) {
            carriers[i].start(keyId, frequency);
        }
    }

    void stop(int keyId) {
        for (int i = 0; i < carriers.length; i++) {
            carriers[i].stop(keyId);
        }
    }

    void stopAll() {
        for (int i = 0; i < 144; i++) {
            for (int j = 0; j < carriers.length; i++) {
                carriers[j].stop(i);
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
