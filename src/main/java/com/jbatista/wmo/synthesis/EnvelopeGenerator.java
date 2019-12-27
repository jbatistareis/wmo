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
    private double[][] envelopes = new double[4][0];
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
    }

    public int getDecayLevel() {
        return decayLevel;
    }

    public void setDecayLevel(int decayLevel) {
        this.decayLevel = Math.max(0, Math.min(decayLevel, 99));
        this.decayAmplitude = Tables.ENV_EXP_INCREASE[this.decayLevel];
        calculateEnvelope(1, this.decaySpeed, this.attackAmplitude, this.decayAmplitude);
    }

    public int getSustainLevel() {
        return sustainLevel;
    }

    public void setSustainLevel(int sustainLevel) {
        this.sustainLevel = Math.max(0, Math.min(sustainLevel, 99));
        this.sustainAmplitude = Tables.ENV_EXP_INCREASE[this.sustainLevel];
        calculateEnvelope(2, this.sustainSpeed, this.decayAmplitude, this.sustainAmplitude);
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
    }

    public int getDecaySpeed() {
        return decaySpeed;
    }

    public void setDecaySpeed(int decaySpeed) {
        this.decaySpeed = Math.max(0, Math.min(decaySpeed, 99));
        calculateEnvelope(1, this.decaySpeed, this.attackAmplitude, this.decayAmplitude);
    }

    public double getSustainSpeed() {
        return sustainSpeed;
    }

    public void setSustainSpeed(int sustainSpeed) {
        this.sustainSpeed = Math.max(0, Math.min(sustainSpeed, 99));
        calculateEnvelope(2, this.sustainSpeed, this.decayAmplitude, this.sustainAmplitude);
    }

    public int getReleaseSpeed() {
        return releaseSpeed;
    }

    public void setReleaseSpeed(int releaseSpeed) {
        this.releaseSpeed = Math.max(0, Math.min(releaseSpeed, 99));
    }

    public double getAttackAmplitude() {
        return attackAmplitude;
    }

    public double getDecayAmplitude() {
        return decayAmplitude;
    }

    public double getSustainAmplitude() {
        return sustainAmplitude;
    }

    public double getReleaseAmplitude() {
        return releaseAmplitude;
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
        final boolean advancePosition = previousTime[keyId] != time;

        switch (envelopeStates[keyId]) {
            case ATTACK:
                if (applyEnvelope(keyId, 0, advancePosition)) {
                    envelopePosition[keyId] = 0;
                    envelopeStates[keyId] = EnvelopeState.DECAY;
                }
                break;

            case DECAY:
                if (applyEnvelope(keyId, 1, advancePosition)) {
                    envelopePosition[keyId] = 0;
                    envelopeStates[keyId] = EnvelopeState.SUSTAIN;
                }
                break;

            case SUSTAIN:
                if (applyEnvelope(keyId, 2, advancePosition)) {
                    envelopePosition[keyId] = 0;
                    envelopeStates[keyId] = EnvelopeState.HOLD;
                }
                break;

            case HOLD:
                // do noting
                break;

            case RELEASE:
                if (applyEnvelope(keyId, 3, advancePosition)) {
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
    boolean applyEnvelope(int keyId, int envelopeStateId, boolean advancePosition) {
        if (envelopePosition[keyId] < envelopes[envelopeStateId].length) {
            envelopeAmplitude[keyId] = envelopes[envelopeStateId][envelopePosition[keyId]];

            if (advancePosition) {
                envelopePosition[keyId] += 1;
            }

            return false;
        } else {
            return true;
        }
    }

    // envelopeStateId: 0 = attack, 1 = decay, 2 = sustain, 3 = release
    void calculateEnvelope(int envelopeStateId, int speed, double startAmplitude, double endAmplitude) {
        final int samples = (int) (Tables.ENV_EXP_INCREASE[99 - speed] * Instrument.getSampleRate());
        final double factor = 1d / samples;
        double accumulator = 0;

        if (envelopes[envelopeStateId].length != samples) {
            envelopes[envelopeStateId] = new double[samples];
        }

        for (int i = 0; i < samples; i++) {
            switch (envelopeCurves[envelopeStateId]) {
                case LINEAR:
                    envelopes[envelopeStateId][i] = MathUtil.linearInterpolation(startAmplitude, endAmplitude, accumulator);
                    break;

                case SMOOTH:
                    envelopes[envelopeStateId][i] = MathUtil.smoothInterpolation(startAmplitude, endAmplitude, accumulator);
                    break;

                case EXP_INCREASE:
                    envelopes[envelopeStateId][i] = MathUtil.expIncreaseInterpolation(startAmplitude, endAmplitude, accumulator);
                    break;

                case EXP_DECREASE:
                    envelopes[envelopeStateId][i] = MathUtil.expDecreaseInterpolation(startAmplitude, endAmplitude, accumulator);
                    break;

                default:
                    break;
            }

            accumulator += factor;
        }
    }

}
