package com.jbatista.wmo.synthesis;

import com.jbatista.wmo.preset.AlgorithmPreset;

public class Algorithm {

    private final boolean[][] activeCarriers = new boolean[132][6];
    private final long[] elapsed = new long[132];
    private double tempSample;

    int[][] pattern = AlgorithmPreset.ALGO_1_OSC_1.getPattern();
    final Oscillator[] oscillators = new Oscillator[6];

    /**
     * <p>Supports the structure of the algorithm used by the {@link Instrument}.</p>
     *
     * @param sampleRate The sample rate that this algorithm is going to operate.
     * @see AlgorithmPreset
     * @see Oscillator
     */
    public Algorithm(int sampleRate) {
        for (int i = 0; i < 6; i++) {
            oscillators[i] = new Oscillator(i, this, sampleRate);
        }
    }

    public void setFeedback(int feedback) {
        for (int i = 0; i < 6; i++) {
            oscillators[i].setFeedback(0);
        }

        getOscillator(pattern[1][0]).setFeedback(feedback);
    }

    public void loadAlgorithmPreset(AlgorithmPreset algorithm) {
        this.pattern = algorithm.getPattern();
    }

    public Oscillator getOscillator(int id) {
        return oscillators[id];
    }

    /**
     * Sets off the chain of oscillators connected to the carriers of the {@link Algorithm#loadAlgorithmPreset defined algorithm}, resulting on an audio frame used by the {@link Instrument}.
     *
     * @param keyId ID representing an unique key, in the range of 0 to 131.
     * @return A single audio frame.
     * @see Oscillator#getSample
     * @see Instrument#getSample
     */
    double getSample(int keyId) {
        tempSample = 0;

        for (int i = 0; i < pattern[0].length; i++) {
            tempSample += oscillators[pattern[0][i]].getSample(keyId, 1, elapsed[keyId]);
            activeCarriers[keyId][oscillators[pattern[0][i]].getId()] = oscillators[pattern[0][i]].isActive(keyId);
        }

        elapsed[keyId] += 1;

        return tempSample / pattern[0].length;
    }

    /**
     * <p>Puts every carrier defined by the {@link Algorithm#loadAlgorithmPreset algorithm} in the <code>attack</code> stage, the envelope keeps progressing to <code>sustain</code> until the {@link Algorithm#stop(int) stop} method is called.</p>
     *
     * @param keyId     ID representing an unique key, in the range of 0 to 131.
     * @param frequency Indicates the frequency at which the oscillators are going to operate.
     */
    void start(int keyId, double frequency) {
        if (!hasActiveCarriers(keyId)) {
            elapsed[keyId] = 0;
        }

        for (int i = 0; i < pattern[0].length; i++) {
            oscillators[pattern[0][i]].start(keyId, frequency);
        }
    }

    /**
     * Puts the carriers in the <code>release</code> stage.
     *
     * @param keyId ID representing an unique key, in the range of 0 to 131.
     */
    void stop(int keyId) {
        for (int i = 0; i < pattern[0].length; i++) {
            oscillators[pattern[0][i]].stop(keyId);
        }
    }

    /**
     * Puts every carrier of every key in the <code>release</code> stage.
     */
    void stopAll() {
        for (int i = 0; i < 132; i++) {
            for (int j = 0; j < pattern[0].length; j++) {
                oscillators[pattern[0][j]].stop(i);
            }
        }
    }

    /**
     * Helper method used by {@link Instrument} to know if it needs to call {@link #getSample}.
     *
     * @param keyId ID representing an unique key, in the range of 0 to 131.
     * @return True if there are any oscillator in a state other than from <code>idle</code>.
     * @see Oscillator#isActive
     */
    boolean hasActiveCarriers(int keyId) {
        for (int i = 0; i < 6; i++) {
            if (activeCarriers[keyId][i]) {
                return true;
            }
        }

        return false;
    }

}
