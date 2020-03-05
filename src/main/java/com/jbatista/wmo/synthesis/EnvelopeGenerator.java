package com.jbatista.wmo.synthesis;

import com.jbatista.wmo.EnvelopeState;
import com.jbatista.wmo.util.MathFunctions;

public class EnvelopeGenerator {

    private final double sampleRate;

    // parameters
    private int attackLevel = 0;
    private int decayLevel = 0;
    private int sustainLevel = 99;
    private int releaseLevel = 0;

    private int attackSpeed = 99;
    private int decaySpeed = 99;
    private int sustainSpeed = 99;
    private int releaseSpeed = 99;

    private double attackAmplitude;
    private double decayAmplitude;
    private double sustainAmplitude;
    private double releaseAmplitude;

    private final EnvelopeState[] state = new EnvelopeState[132];
    private final double[] startAmplitude = new double[132];
    private final double[] endAmplitude = new double[132];
    private final double[] currentAmplitude = new double[132];
    private final double[] position = new double[132];
    private final double[] progress = new double[132];
    private final double[] factor = new double[5];
    private final int[] size = new int[5];
    private final long[] previousTime = new long[132];

    EnvelopeGenerator(double sampleRate) {
        this.sampleRate = sampleRate;
        this.size[4] = (int) (sampleRate / 10);
        this.factor[4] = 1d / size[4];

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
    }

    public int getSustainLevel() {
        return sustainLevel;
    }

    public void setSustainLevel(int sustainLevel) {
        this.sustainLevel = Math.max(0, Math.min(sustainLevel, 99));
        this.sustainAmplitude = Tables.ENV_EXP_INCREASE[this.sustainLevel];
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
        this.size[0] = (int) (Tables.ENV_EXP_INCREASE[99 - this.attackSpeed] * sampleRate);
        this.factor[0] = 1d / size[0];
    }

    public int getDecaySpeed() {
        return decaySpeed;
    }

    public void setDecaySpeed(int decaySpeed) {
        this.decaySpeed = Math.max(0, Math.min(decaySpeed, 99));
        this.size[1] = (int) (Tables.ENV_EXP_INCREASE[99 - this.decaySpeed] * sampleRate);
        this.factor[1] = 1d / size[1];
    }

    public double getSustainSpeed() {
        return sustainSpeed;
    }

    public void setSustainSpeed(int sustainSpeed) {
        this.sustainSpeed = Math.max(0, Math.min(sustainSpeed, 99));
        this.size[2] = (int) (Tables.ENV_EXP_INCREASE[99 - this.sustainSpeed] * sampleRate);
        this.factor[2] = 1d / size[2];
    }

    public int getReleaseSpeed() {
        return releaseSpeed;
    }

    public void setReleaseSpeed(int releaseSpeed) {
        this.releaseSpeed = Math.max(0, Math.min(releaseSpeed, 99));
        this.size[3] = (int) (Tables.ENV_EXP_INCREASE[99 - this.releaseSpeed] * sampleRate);
        this.factor[3] = 1d / size[3];
    }

    void setPreviousTime(int keyId, long time) {
        previousTime[keyId] = time;
    }

    double getEnvelopeAmplitude(int keyId) {
        return currentAmplitude[keyId];
    }

    EnvelopeState getEnvelopeState(int keyId) {
        return state[keyId];
    }

    void setEnvelopeState(int keyId, EnvelopeState envelopeState) {
        state[keyId] = envelopeState;
    }
    // </editor-fold>

    // envelopeStateId: 0 = attack, 1 = decay, 2 = sustain, 3 = release
    void setEnvelopePosition(int keyId, int envelopeStateId) {
        position[keyId] = envelopeStateId;
    }

    void defineEnvelopeAmplitude(int keyId, long time) {
        switch (state[keyId]) {
            case ATTACK:
                if (applyEnvelope(keyId, 0, time)) {
                    position[keyId] = 0;
                    progress[keyId] = 0;
                    startAmplitude[keyId] = attackAmplitude;
                    endAmplitude[keyId] = decayAmplitude;
                    state[keyId] = EnvelopeState.DECAY;
                }
                break;

            case DECAY:
                if (applyEnvelope(keyId, 1, time)) {
                    position[keyId] = 0;
                    progress[keyId] = 0;
                    startAmplitude[keyId] = decayAmplitude;
                    endAmplitude[keyId] = sustainAmplitude;
                    state[keyId] = EnvelopeState.SUSTAIN;
                }
                break;

            case SUSTAIN:
                if (applyEnvelope(keyId, 2, time)) {
                    state[keyId] = EnvelopeState.HOLD;
                }
                break;

            case HOLD:
                // do noting
                break;

            case PRE_RELEASE:
                position[keyId] = 0;
                progress[keyId] = 0;
                startAmplitude[keyId] = currentAmplitude[keyId];
                endAmplitude[keyId] = releaseAmplitude;
                state[keyId] = EnvelopeState.RELEASE;
                break;

            case RELEASE:
                if (applyEnvelope(keyId, 3, time)) {
                    position[keyId] = 0;
                    progress[keyId] = 0;
                    startAmplitude[keyId] = currentAmplitude[keyId];
                    endAmplitude[keyId] = 0;
                    state[keyId] = EnvelopeState.RELEASE_END;
                }
                break;

            case RELEASE_END:
                if (applyEnvelope(keyId, 4, time)) {
                    currentAmplitude[keyId] = 0.0;
                    state[keyId] = EnvelopeState.IDLE;
                }
                break;

            case IDLE:
                // do nothing
                break;
        }
    }

    /*
        returns true if finished
        envelopeStateId: 0 = attack, 1 = decay, 2 = sustain, 3 = release, 4 = release end
     */
    boolean applyEnvelope(int keyId, int envelopeStateId, long time) {
        if (position[keyId] < size[envelopeStateId]) {
            currentAmplitude[keyId] = MathFunctions.smoothInterpolation(startAmplitude[keyId], endAmplitude[keyId], progress[keyId]);

            if (previousTime[keyId] != time) {
                position[keyId] += 1;
                progress[keyId] += factor[envelopeStateId];
            }

            return false;
        } else {
            return true;
        }
    }

    void reset(int keyId) {
        setPreviousTime(keyId, -1);
        setEnvelopePosition(keyId, 0);
        setEnvelopeState(keyId, EnvelopeState.ATTACK);

        startAmplitude[keyId] = 0;
        endAmplitude[keyId] = attackAmplitude;
        progress[keyId] = 0;
    }

}
