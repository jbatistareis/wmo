package com.jbatista.wmo.synthesis;

import com.jbatista.wmo.EnvelopeCurve;
import com.jbatista.wmo.MathUtil;

public class EnvelopeGenerator {

    // parameters
    private double attackAmplitude = 0;
    private double decayAmplitude = 0;
    private double sustainAmplitude = 1;
    private double releaseAmplitude = 0;

    private double attackDuration = 0;
    private double decayDuration = 0;
    private double sustainDuration = 0;
    private double releaseDuration = 0;

    private final EnvelopeCurve[] envelopeCurves = new EnvelopeCurve[]{EnvelopeCurve.LINEAR, EnvelopeCurve.LINEAR, EnvelopeCurve.LINEAR, EnvelopeCurve.LINEAR};

    // envelope data
    private final EnvelopeState[] envelopeStates = new EnvelopeState[144];
    private final double[] envelopeAmplitude = new double[144];
    // attack -> decay -> sustain -> release
    private double[][] envelopes = new double[4][0];
    private final int[] envelopePosition = new int[144];
    private final long[] previousTime = new long[144];

    EnvelopeGenerator() {
        setDecayDuration(0);
        setSustainDuration(0);
    }

    // <editor-fold defaultstate="collapsed" desc="getters/setters">
    public double getAttackAmplitude() {
        return attackAmplitude;
    }

    public void setAttackAmplitude(double attackAmplitude) {
        this.attackAmplitude = Math.max(0, Math.min(attackAmplitude, 1));
    }

    public double getDecayAmplitude() {
        return decayAmplitude;
    }

    public void setDecayAmplitude(double decayAmplitude) {
        this.decayAmplitude = Math.max(0, Math.min(decayAmplitude, 1));
        calculateEnvelope(1, this.decayDuration, this.attackAmplitude, this.decayAmplitude);
    }

    public double getSustainAmplitude() {
        return sustainAmplitude;
    }

    public void setSustainAmplitude(double sustainAmplitude) {
        this.sustainAmplitude = Math.max(0, Math.min(sustainAmplitude, 1));
        calculateEnvelope(2, this.sustainDuration, this.decayAmplitude, this.sustainAmplitude);
    }

    public double getReleaseAmplitude() {
        return releaseAmplitude;
    }

    public void setReleaseAmplitude(double releaseAmplitude) {
        this.releaseAmplitude = Math.max(0, Math.min(releaseAmplitude, 1));
    }

    public double getAttackDuration() {
        return attackDuration;
    }

    public void setAttackDuration(double attackDuration) {
        this.attackDuration = Math.max(0, Math.min(attackDuration, 1));
    }

    public double getDecayDuration() {
        return decayDuration;
    }

    public void setDecayDuration(double decayDuration) {
        this.decayDuration = Math.max(0, Math.min(decayDuration, 1));
        calculateEnvelope(1, this.decayDuration, this.attackAmplitude, this.decayAmplitude);
    }

    public double getSustainDuration() {
        return sustainDuration;
    }

    public void setSustainDuration(double sustainDuration) {
        this.sustainDuration = Math.max(0, Math.min(sustainDuration, 1));
        calculateEnvelope(2, this.sustainDuration, this.decayAmplitude, this.sustainAmplitude);
    }

    public double getReleaseDuration() {
        return releaseDuration;
    }

    public void setReleaseDuration(double releaseDuration) {
        this.releaseDuration = Math.max(0, Math.min(releaseDuration, 1));
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
    void calculateEnvelope(int envelopeStateId, double duration, double startAmplitude, double endAmplitude) {
        final int samples = (int) (((duration == 0) ? 0.001 : duration) * Instrument.getSampleRate());
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

                case ACCELERATION:
                    envelopes[envelopeStateId][i] = MathUtil.accelerationInterpolation(startAmplitude, endAmplitude, accumulator);
                    break;

                case DECELERATION:
                    envelopes[envelopeStateId][i] = MathUtil.decelerationInterpolation(startAmplitude, endAmplitude, accumulator);
                    break;

                default:
                    break;
            }

            accumulator += factor;
        }
    }

}
