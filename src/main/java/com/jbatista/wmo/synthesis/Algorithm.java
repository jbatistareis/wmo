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

    final Instrument instrument;

    private final boolean[][] activeCarriers = new boolean[132][6];
    private final long[] elapsed = new long[132];
    private double tempSample;

    final Oscillator[] oscillators = new Oscillator[6];

    Algorithm(int sampleRate, Instrument instrument) {
        this.instrument = instrument;

        for (int i = 0; i < 6; i++) {
            oscillators[i] = new Oscillator(i, sampleRate, instrument);
        }
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

        for (int i = 0; i < instrument.preset.getAlgorithm().getPattern()[0].length; i++) {
            tempSample += oscillators[instrument.preset.getAlgorithm().getPattern()[0][i]].getSample(keyId, 1, elapsed[keyId]);
            activeCarriers[keyId][oscillators[instrument.preset.getAlgorithm().getPattern()[0][i]].id] = oscillators[instrument.preset.getAlgorithm().getPattern()[0][i]].isActive(keyId);
        }

        elapsed[keyId] += 1;

        return tempSample / instrument.preset.getAlgorithm().getPattern()[0].length;
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

        for (int i = 0; i < instrument.preset.getAlgorithm().getPattern()[0].length; i++) {
            oscillators[instrument.preset.getAlgorithm().getPattern()[0][i]].start(keyId, frequency);
        }
    }

    /**
     * Puts the carriers in the <code>release</code> stage.
     *
     * @param keyId ID representing an unique key, in the range of 0 to 131.
     */
    void stop(int keyId) {
        for (int i = 0; i < instrument.preset.getAlgorithm().getPattern()[0].length; i++) {
            oscillators[instrument.preset.getAlgorithm().getPattern()[0][i]].stop(keyId);
        }
    }

    /**
     * Puts every carrier of every key in the <code>release</code> stage.
     */
    void stopAll() {
        for (int i = 0; i < 132; i++) {
            for (int j = 0; j < instrument.preset.getAlgorithm().getPattern()[0].length; j++) {
                oscillators[instrument.preset.getAlgorithm().getPattern()[0][j]].stop(i);
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
