package com.jbatista.wmo.synthesis;

import com.jbatista.wmo.preset.OscillatorPreset;
import com.jbatista.wmo.util.MathFunctions;

/**
 * <p>Provides amplitude modulation for an {@link Oscillator}, based on <a href="https://en.wikipedia.org/wiki/Envelope_(music)">ADSR</a> parameters.</p>
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
    private final double[][] factor = new double[132][5];
    private final int[][] size = new int[132][5];

    private int currentAttackSpeed = -1;
    private int currentDecaySpeed = -1;
    private int currentSustainSpeed = -1;
    private int currentReleaseSpeed = -1;
    private int currentSpeedScale = -1;

    EnvelopeGenerator(int oscillatorId, int sampleRate, Instrument instrument) {
        this.oscillatorId = oscillatorId;
        this.sampleRate = sampleRate;
        this.instrument = instrument;

        for (int i = 0; i < 132; i++) {
            this.size[i][EnvelopeState.PRE_IDLE.getId()] = sampleRate / 3;
            this.factor[i][EnvelopeState.PRE_IDLE.getId()] = 1d / size[i][EnvelopeState.PRE_IDLE.getId()];
        }

        checkParameters();
    }

    EnvelopeState getEnvelopeState(int keyId) {
        return state[keyId];
    }

    private OscillatorPreset oscillatorPreset() {
        return instrument.preset.getOscillatorPresets()[oscillatorId];
    }

    /**
     * <p>Helper method that only recalculate envelope duration if speed changes.</p>
     */
    private void checkParameters() {
        boolean scaleChange = false;

        if (currentSpeedScale != oscillatorPreset().getSpeedScaling()) {
            currentSpeedScale = oscillatorPreset().getSpeedScaling();

            scaleChange = true;
        }

        if ((currentAttackSpeed != oscillatorPreset().getAttackSpeed()) || scaleChange) {
            changeParameters(EnvelopeState.ATTACK, oscillatorPreset().getAttackSpeed());
            currentAttackSpeed = oscillatorPreset().getAttackSpeed();
        }

        if ((currentDecaySpeed != oscillatorPreset().getDecaySpeed()) || scaleChange) {
            changeParameters(EnvelopeState.DECAY, oscillatorPreset().getDecaySpeed());
            currentDecaySpeed = oscillatorPreset().getDecaySpeed();
        }

        if ((currentSustainSpeed != oscillatorPreset().getSustainSpeed()) || scaleChange) {
            changeParameters(EnvelopeState.SUSTAIN, oscillatorPreset().getSustainSpeed());
            currentSustainSpeed = oscillatorPreset().getSustainSpeed();
        }

        if ((currentReleaseSpeed != oscillatorPreset().getReleaseSpeed()) || scaleChange) {
            changeParameters(EnvelopeState.RELEASE, oscillatorPreset().getReleaseSpeed());
            currentReleaseSpeed = oscillatorPreset().getReleaseSpeed();
        }
    }

    private void changeParameters(EnvelopeState envelopeState, int speed) {
        for (int i = 0; i < 132; i++) {
            size[i][envelopeState.getId()]
                    = (int) (Tables.ENV_SPEED[Math.max(0, Math.min(speed + Tables.SPEED_SCALE[oscillatorPreset().getSpeedScaling()][i], 99))] * sampleRate);
            factor[i][envelopeState.getId()] = 1d / size[i][envelopeState.getId()];
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
                    silence(keyId);
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
        if (position[keyId] < size[keyId][envelopeState.getId()]) {
            currentAmplitude[keyId] = MathFunctions.linearInterpolation(startAmplitude[keyId], endAmplitude[keyId], progress[keyId]);

            return false;
        } else {
            return true;
        }
    }

    /**
     * <p>Sets the required parameters for the next value of the envelope shape.</p>
     *
     * @param keyId ID representing an unique key, in the range of 0 to 131.
     */
    void advanceEnvelope(int keyId) {
        if (state[keyId].getId() <= 4) {
            position[keyId] += 1;
            progress[keyId] += factor[keyId][state[keyId].getId()];
        }
    }

    /**
     * <p>Puts the envelope on the {@link EnvelopeState#ATTACK} position.</p>
     *
     * @param keyId ID representing an unique key, in the range of 0 to 131.
     */
    void initialize(int keyId) {
        checkParameters();

        position[keyId] = 0;
        progress[keyId] = 0;

        startAmplitude[keyId] = 0;
        endAmplitude[keyId] = Tables.ENV_EXP_INCREASE[oscillatorPreset().getAttackLevel()];

        state[keyId] = EnvelopeState.ATTACK;
    }

    /**
     * <p>Puts the envelope on the {@link EnvelopeState#RELEASE} position.</p>
     *
     * @param keyId ID representing an unique key, in the range of 0 to 131.
     */
    void stop(int keyId) {
        if (state[keyId] != EnvelopeState.RELEASE) {
            position[keyId] = 0;
            progress[keyId] = 0;

            startAmplitude[keyId] = currentAmplitude[keyId];
            endAmplitude[keyId] = Tables.ENV_EXP_INCREASE[oscillatorPreset().getReleaseLevel()];

            state[keyId] = EnvelopeState.RELEASE;
        }
    }

    /**
     * <p>Puts the envelope <b>immediately</b> on the {@link EnvelopeState#IDLE} position.</p>
     *
     * @param keyId ID representing an unique key, in the range of 0 to 131.
     */
    void reset(int keyId) {
        if (state[keyId] != EnvelopeState.IDLE) {
            position[keyId] = 0;
            progress[keyId] = 0;

            startAmplitude[keyId] = 0;
            endAmplitude[keyId] = 0;
            currentAmplitude[keyId] = 0;

            state[keyId] = EnvelopeState.IDLE;
        }
    }

    /**
     * <p>Puts the envelope <b>gradually</b> on the {@link EnvelopeState#IDLE} position.</p>
     *
     * @param keyId ID representing an unique key, in the range of 0 to 131.
     */
    void silence(int keyId) {
        if (state[keyId] != EnvelopeState.PRE_IDLE) {
            position[keyId] = 0;
            progress[keyId] = 0;

            startAmplitude[keyId] = currentAmplitude[keyId];
            endAmplitude[keyId] = 0;

            state[keyId] = EnvelopeState.PRE_IDLE;
        }
    }

}
