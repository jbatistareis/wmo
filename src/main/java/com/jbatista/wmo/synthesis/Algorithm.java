package com.jbatista.wmo.synthesis;

import com.jbatista.wmo.preset.AlgorithmPreset;

/**
 * Supports the structure of the algorithm used by the {@link Instrument}, creates instances of {@link Oscillator}, obtains PCM data from then.
 * <p>Instances of this class are created by {@link Instrument}.</p>
 *
 * @see Instrument
 * @see AlgorithmPreset
 * @see Oscillator
 */
public class Algorithm {

    private final Instrument instrument;
    private final boolean[][] activeCarriers = new boolean[132][6];
    private final long[] elapsed = new long[132];
    private double tempSample;
    private double pitchOffset = 1;

    final Oscillator[] oscillators = new Oscillator[6];

    Algorithm(int sampleRate, Instrument instrument) {
        this.instrument = instrument;

        for (int i = 0; i < 6; i++) {
            oscillators[i] = new Oscillator(i, sampleRate, instrument);
        }
    }

    private int[][] getPattern() {
        return instrument.preset.getAlgorithm().getPattern();
    }

    /**
     * Sets off the chain of oscillators defined as carriers on the algorithm.
     *
     * @param keyId ID representing an unique key, in the range of 0 to 131.
     * @return A single audio frame.
     * @see Oscillator#getSample
     * @see Instrument#getSample
     */
    double getSample(int keyId) {
        tempSample = 0;

        for (int i = 0; i < getPattern()[0].length; i++) {
            tempSample += oscillators[getPattern()[0][i]]
                    .getSample(keyId, pitchOffset, getModulation(keyId, getPattern()[0][i], 0, true), elapsed[keyId]) / 13;

            activeCarriers[keyId][oscillators[getPattern()[0][i]].id] = oscillators[getPattern()[0][i]].isActive(keyId);
        }

        elapsed[keyId] += 1;

        return tempSample / (getPattern()[0].length * 13);
    }

    // obtain the modulation sample using recursion
    private double getModulation(int keyId, int oscillator, double modulation, boolean feedbackOn) {
        for (int i = 2; i < getPattern().length; i++) {
            if (getPattern()[i][0] == oscillator) {
                modulation += oscillators[getPattern()[i][1]].getSample(
                        keyId,
                        pitchOffset,
                        getModulation(keyId, getPattern()[i][1], 0, feedbackOn),
                        elapsed[keyId]);
            }
        }

        // checks if this oscillator receives feedback or not
        if (feedbackOn && (oscillator == getPattern()[1][0])) {
            modulation += Math.pow(2, instrument.preset.getFeedback() - 7) * oscillators[getPattern()[1][0]].getSample(
                    keyId,
                    pitchOffset,
                    getModulation(keyId, getPattern()[1][1], 0, false),
                    elapsed[keyId]);
        }

        return modulation;
    }

    /**
     * Puts every carrier defined by the algorithm in the <code>attack</code> stage, the envelope keeps progressing to <code>sustain</code> until the {@link #stop(int)} method is called.
     *
     * @param keyId     ID representing an unique key, in the range of 0 to 131.
     * @param frequency Indicates the frequency at which the oscillators are going to operate.
     */
    void start(int keyId, double frequency) {
        if (!hasActiveCarriers(keyId)) {
            elapsed[keyId] = 0;
        }

        for (int i = 0; i < oscillators.length; i++) {
            oscillators[i].start(keyId, frequency);
        }
    }

    /**
     * Puts the carriers in the <code>release</code> stage.
     *
     * @param keyId ID representing an unique key, in the range of 0 to 131.
     */
    void stop(int keyId) {
        for (int i = 0; i < oscillators.length; i++) {
            oscillators[i].stop(keyId);
        }
    }

    /**
     * Puts every carrier of every key in the <code>release</code> stage.
     */
    void stopAll() {
        for (int i = 0; i < 132; i++) {
            for (int j = 0; j < getPattern()[0].length; j++) {
                oscillators[getPattern()[0][j]].silence(i);
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
