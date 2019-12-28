package com.jbatista.wmo.synthesis;

import com.jbatista.wmo.EnvelopeCurve;
import com.jbatista.wmo.MathUtil;

public class EnvelopeGenerator {

    // parameters
    private int attackLevel;
    private int decayLevel;
    private int sustainLevel;
    private int releaseLevel;

    private int attackSpeed = 99;
    private int decaySpeed = 99;
    private int sustainSpeed = 99;
    private int releaseSpeed = 99;

    private final EnvelopeCurve[] envelopeCurves = new EnvelopeCurve[]{EnvelopeCurve.LINEAR, EnvelopeCurve.LINEAR, EnvelopeCurve.LINEAR, EnvelopeCurve.LINEAR};

    // envelope data
    private double attackAmplitude;
    private double decayAmplitude;
    private double sustainAmplitude;
    private double releaseAmplitude;

    private final EnvelopeState[] envelopeStates = new EnvelopeState[144];
    private final double[] envelopeAmplitude = new double[144];
    // attack -> decay -> sustain -> release
    private double[][] envelopes = new double[3][0];
    private double[][] releaseEnvelopes = new double[144][0];
    private final int[] envelopePosition = new int[144];
    private final long[] previousTime = new long[144];

    EnvelopeGenerator() {
        // initialize envelope shape
        setAttackLevel(0);
        setDecayLevel(0);
        setSustainLevel(99);
        setReleaseLevel(0);
    }

    // <editor-fold defaultstate="collapsed" desc="getters/setters">
    public int getAttackLevel() {
        return attackLevel;
    }

    public void setAttackLevel(int attackLevel) {
        this.attackLevel = Math.max(0, Math.min(attackLevel, 99));
        this.attackAmplitude = Tables.ENV_EXP_INCREASE[this.attackLevel];
        calculateEnvelope(0, -1, this.attackSpeed, 0.0000, this.attackAmplitude);
    }

    public int getDecayLevel() {
        return decayLevel;
    }

    public void setDecayLevel(int decayLevel) {
        this.decayLevel = Math.max(0, Math.min(decayLevel, 99));
        this.decayAmplitude = Tables.ENV_EXP_INCREASE[this.decayLevel];
        calculateEnvelope(1, -1, this.decaySpeed, this.attackAmplitude, this.decayAmplitude);
    }

    public int getSustainLevel() {
        return sustainLevel;
    }

    public void setSustainLevel(int sustainLevel) {
        this.sustainLevel = Math.max(0, Math.min(sustainLevel, 99));
        this.sustainAmplitude = Tables.ENV_EXP_INCREASE[this.sustainLevel];
        calculateEnvelope(2, -1, this.sustainSpeed, this.decayAmplitude, this.sustainAmplitude);
    }

    public int getReleaseLevel() {
        return releaseLevel;
    }

    public void setReleaseLevel(int releaseLevel) {
        this.releaseLevel = Math.max(0, Math.min(releaseLevel, 99));
        this.releaseAmplitude = Tables.ENV_EXP_INCREASE[this.releaseLevel];
    }

    public int getAttackSpeed() {
        return attackSpeed;
    }

    public void setAttackSpeed(int attackSpeed) {
        this.attackSpeed = Math.max(0, Math.min(attackSpeed, 99));
        calculateEnvelope(0, -1, this.attackSpeed, 0.0000, this.attackAmplitude);
    }

    public int getDecaySpeed() {
        return decaySpeed;
    }

    public void setDecaySpeed(int decaySpeed) {
        this.decaySpeed = Math.max(0, Math.min(decaySpeed, 99));
        calculateEnvelope(1, -1, this.decaySpeed, this.attackAmplitude, this.decayAmplitude);
    }

    public double getSustainSpeed() {
        return sustainSpeed;
    }

    public void setSustainSpeed(int sustainSpeed) {
        this.sustainSpeed = Math.max(0, Math.min(sustainSpeed, 99));
        calculateEnvelope(2, -1, this.sustainSpeed, this.decayAmplitude, this.sustainAmplitude);
    }

    public int getReleaseSpeed() {
        return releaseSpeed;
    }

    public void setReleaseSpeed(int releaseSpeed) {
        this.releaseSpeed = Math.max(0, Math.min(releaseSpeed, 99));
    }

    public EnvelopeCurve getAttackCurve() {
        return envelopeCurves[0];
    }

    public void setAttackCurve(EnvelopeCurve attackCurve) {
        envelopeCurves[0] = attackCurve;
    }

    public EnvelopeCurve getDecayCurve() {
        return envelopeCurves[1];
    }

    public void setDecayCurve(EnvelopeCurve decayCurve) {
        envelopeCurves[1] = decayCurve;
    }

    public EnvelopeCurve getSustainCurve() {
        return envelopeCurves[2];
    }

    public void setSustainCurve(EnvelopeCurve sustainCurve) {
        envelopeCurves[2] = sustainCurve;
    }

    public EnvelopeCurve getReleaseCurve() {
        return envelopeCurves[3];
    }

    public void setReleaseCurve(EnvelopeCurve releaseCurve) {
        envelopeCurves[3] = releaseCurve;
    }

    void setPreviousTime(int keyId, long time) {
        previousTime[keyId] = time;
    }

    double getEnvelopeAmplitude(int keyId) {
        return envelopeAmplitude[keyId];
    }

    EnvelopeState getEnvelopeState(int keyId) {
        return envelopeStates[keyId];
    }

    void setEnvelopeState(int keyId, EnvelopeState envelopeState) {
        envelopeStates[keyId] = envelopeState;
    }
    // </editor-fold>

    // envelopeStateId: 0 = attack, 1 = decay, 2 = sustain, 3 = release
    void setEnvelopePosition(int keyId, int envelopeStateId) {
        envelopePosition[keyId] = envelopeStateId;
    }

    void defineEnvelopeAmplitude(int keyId, long time) {
        switch (envelopeStates[keyId]) {
            case ATTACK:
                if (applyEnvelope(keyId, 0, time)) {
                    envelopePosition[keyId] = 0;
                    envelopeStates[keyId] = EnvelopeState.DECAY;
                }
                break;

            case DECAY:
                if (applyEnvelope(keyId, 1, time)) {
                    envelopePosition[keyId] = 0;
                    envelopeStates[keyId] = EnvelopeState.SUSTAIN;
                }
                break;

            case SUSTAIN:
                if (applyEnvelope(keyId, 2, time)) {
                    envelopeStates[keyId] = EnvelopeState.HOLD;
                }
                break;

            case HOLD:
                // do noting
                break;

            case PRE_RELEASE:
                calculateEnvelope(3, keyId, releaseSpeed, envelopeAmplitude[keyId], releaseAmplitude);
                envelopePosition[keyId] = 0;
                envelopeStates[keyId] = EnvelopeState.RELEASE;
                break;

            case RELEASE:
                if (applyEnvelope(keyId, 3, time)) {
                    envelopeStates[keyId] = EnvelopeState.RELEASE_END;
                }
                break;

            case RELEASE_END:
                if (envelopeAmplitude[keyId] > 0.005) {
                    envelopeAmplitude[keyId] -= 0.005;
                } else {
                    envelopeAmplitude[keyId] = 0.0;
                    envelopeStates[keyId] = EnvelopeState.IDLE;
                }
                break;

            case IDLE:
                // do nothing
                break;
        }
    }

    /*
        returns true if finished
        envelopeStateId: 0 = attack, 1 = decay, 2 = sustain, 3 = release
     */
    boolean applyEnvelope(int keyId, int envelopeStateId, long time) {
        final boolean isRelease = (envelopeStateId == 3);

        if (envelopePosition[keyId] < (isRelease ? releaseEnvelopes[keyId].length : envelopes[envelopeStateId].length)) {
            envelopeAmplitude[keyId] = isRelease ? releaseEnvelopes[keyId][envelopePosition[keyId]] : envelopes[envelopeStateId][envelopePosition[keyId]];

            if (previousTime[keyId] != time) {
                envelopePosition[keyId] += 1;
            }

            return false;
        } else {
            return true;
        }
    }

    // envelopeStateId: 0 = attack, 1 = decay, 2 = sustain, 3 = release
    void calculateEnvelope(int envelopeStateId, int keyId, int speed, double startAmplitude, double endAmplitude) {
        final double[][] envValues;
        final int index;
        final int samples = (int) (Tables.ENV_EXP_INCREASE[99 - speed] * Instrument.getSampleRate());
        final double factor = 1d / samples;
        double accumulator = 0;

        if (envelopeStateId == 3) {
            envValues = releaseEnvelopes;
            index = keyId;
        } else {
            envValues = envelopes;
            index = envelopeStateId;
        }

        if (envValues[index].length != samples) {
            envValues[index] = new double[samples];
        }

        for (int i = 0; i < samples; i++) {
            switch (envelopeCurves[envelopeStateId]) {
                case LINEAR:
                    envValues[index][i] = MathUtil.linearInterpolation(startAmplitude, endAmplitude, accumulator);
                    break;

                case SMOOTH:
                    envValues[index][i] = MathUtil.smoothInterpolation(startAmplitude, endAmplitude, accumulator);
                    break;

                case EXP_INCREASE:
                    envValues[index][i] = MathUtil.expIncreaseInterpolation(startAmplitude, endAmplitude, accumulator);
                    break;

                case EXP_DECREASE:
                    envValues[index][i] = MathUtil.expDecreaseInterpolation(startAmplitude, endAmplitude, accumulator);
                    break;

                default:
                    break;
            }

            accumulator += factor;
        }
    }

}
