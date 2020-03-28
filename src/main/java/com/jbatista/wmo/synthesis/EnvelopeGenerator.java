package com.jbatista.wmo.synthesis;

import com.jbatista.wmo.preset.OscillatorPreset;
import com.jbatista.wmo.util.MathFunctions;

/**
 * Provides amplitude modulation for an {@link Oscillator}, based on <a href="https://en.wikipedia.org/wiki/Envelope_(music)">ADSR</a> parameters.
 * <p>Instances of this class are created by the {@link Oscillator} class.</p>
 *
 * @see Oscillator
 */
public class EnvelopeGenerator {

    private final int oscillatorId;
    private final int sampleRate;
    private final Instrument instrument;

    private final EnvelopeState[] state = new EnvelopeState[132];
    private final double[] startAmplitude = new double[132];
    private final double[] endAmplitude = new double[132];
    private final double[] currentAmplitude = new double[132];
    private final double[] position = new double[132];
    private final double[] progress = new double[132];
    private final double[] factor = new double[5];
    private final int[] size = new int[5];

    EnvelopeGenerator(int oscillatorId, int sampleRate, Instrument instrument) {
        this.oscillatorId = oscillatorId;
        this.sampleRate = sampleRate;
        this.instrument = instrument;

        this.size[EnvelopeState.PRE_IDLE.getId()] = sampleRate / 5;
        this.factor[EnvelopeState.PRE_IDLE.getId()] = 1d / size[EnvelopeState.PRE_IDLE.getId()];
    }

    EnvelopeState getEnvelopeState(int keyId) {
        return state[keyId];
    }

    void setEnvelopeState(int keyId, EnvelopeState envelopeState) {
        state[keyId] = envelopeState;

        if (envelopeState == EnvelopeState.RELEASE) {
            position[keyId] = 0;
            progress[keyId] = 0;
            startAmplitude[keyId] = currentAmplitude[keyId];
            endAmplitude[keyId] = Tables.ENV_EXP_INCREASE[oscillatorPreset().getReleaseLevel()];
        }
    }

    /**
     * @param keyId ID representing an unique key, in the range of 0 to 131.
     * @return The current value of the envelope shape.
     * @see #advanceEnvelope
     */
    double getEnvelopeAmplitude(int keyId) {
        switch (state[keyId]) {
            case ATTACK:
                if (applyEnvelope(keyId, EnvelopeState.ATTACK)) {
                    position[keyId] = 0;
                    progress[keyId] = 0;
                    startAmplitude[keyId] = Tables.ENV_EXP_INCREASE[oscillatorPreset().getAttackLevel()];
                    endAmplitude[keyId] = Tables.ENV_EXP_INCREASE[oscillatorPreset().getDecayLevel()];
                    state[keyId] = EnvelopeState.DECAY;
                }
                break;

            case DECAY:
                if (applyEnvelope(keyId, EnvelopeState.DECAY)) {
                    position[keyId] = 0;
                    progress[keyId] = 0;
                    startAmplitude[keyId] = Tables.ENV_EXP_INCREASE[oscillatorPreset().getDecayLevel()];
                    endAmplitude[keyId] = Tables.ENV_EXP_INCREASE[oscillatorPreset().getSustainLevel()];
                    state[keyId] = EnvelopeState.SUSTAIN;
                }
                break;

            case SUSTAIN:
                if (applyEnvelope(keyId, EnvelopeState.SUSTAIN)) {
                    state[keyId] = EnvelopeState.HOLD;
                }
                break;

            case HOLD:
                // do noting
                break;

            case RELEASE:
                if (applyEnvelope(keyId, EnvelopeState.RELEASE)) {
                    position[keyId] = 0;
                    progress[keyId] = 0;
                    startAmplitude[keyId] = currentAmplitude[keyId];
                    endAmplitude[keyId] = 0;
                    state[keyId] = EnvelopeState.PRE_IDLE;
                }
                break;

            case PRE_IDLE:
                if (applyEnvelope(keyId, EnvelopeState.PRE_IDLE)) {
                    reset(keyId);
                }
                break;

            case IDLE:
                // do nothing
                break;
        }

        return currentAmplitude[keyId];
    }

    boolean applyEnvelope(int keyId, EnvelopeState envelopeState) {
        if (position[keyId] < size[envelopeState.getId()]) {
            currentAmplitude[keyId] = MathFunctions.linearInterpolation(startAmplitude[keyId], endAmplitude[keyId], progress[keyId]);

            return false;
        } else {
            return true;
        }
    }

    /**
     * Sets the required parameters for the next value of the envelope shape.
     *
     * @param keyId ID representing an unique key, in the range of 0 to 131.
     */
    void advanceEnvelope(int keyId) {
        if (state[keyId].getId() <= 4) {
            position[keyId] += 1;
            progress[keyId] += factor[state[keyId].getId()];
        }
    }

    /**
     * Puts the envelope on the {@link EnvelopeState#ATTACK} position.
     *
     * @param keyId ID representing an unique key, in the range of 0 to 131.
     */
    void initialize(int keyId) {
        size[EnvelopeState.ATTACK.getId()] = (int) (Tables.ENV_SPEED[oscillatorPreset().getAttackSpeed()] * sampleRate);
        factor[EnvelopeState.ATTACK.getId()] = 1d / size[EnvelopeState.ATTACK.getId()];

        size[EnvelopeState.DECAY.getId()] = (int) (Tables.ENV_SPEED[oscillatorPreset().getDecaySpeed()] * sampleRate);
        factor[EnvelopeState.DECAY.getId()] = 1d / size[EnvelopeState.DECAY.getId()];

        size[EnvelopeState.SUSTAIN.getId()] = (int) (Tables.ENV_SPEED[oscillatorPreset().getSustainSpeed()] * sampleRate);
        factor[EnvelopeState.SUSTAIN.getId()] = 1d / size[EnvelopeState.SUSTAIN.getId()];

        size[EnvelopeState.RELEASE.getId()] = (int) (Tables.ENV_SPEED[oscillatorPreset().getReleaseSpeed()] * sampleRate);
        factor[EnvelopeState.RELEASE.getId()] = 1d / size[EnvelopeState.RELEASE.getId()];

        position[keyId] = 0;
        progress[keyId] = 0;

        startAmplitude[keyId] = 0;
        endAmplitude[keyId] = Tables.ENV_EXP_INCREASE[oscillatorPreset().getAttackLevel()];

        state[keyId] = EnvelopeState.ATTACK;
    }

    /**
     * * Puts the envelope on the {@link EnvelopeState#IDLE} position.
     *
     * @param keyId ID representing an unique key, in the range of 0 to 131.
     */
    void reset(int keyId) {
        position[keyId] = 0;
        progress[keyId] = 0;

        startAmplitude[keyId] = 0;
        endAmplitude[keyId] = 0;
        currentAmplitude[keyId] = 0;

        state[keyId] = EnvelopeState.IDLE;
    }

    private OscillatorPreset oscillatorPreset() {
        return instrument.preset.getOscillatorPresets()[oscillatorId];
    }

}
