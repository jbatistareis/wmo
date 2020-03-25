package com.jbatista.wmo.synthesis;

import com.jbatista.wmo.util.MathFunctions;

public class EnvelopeGenerator {

    private final int sampleRate;

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

    EnvelopeGenerator(int sampleRate) {
        this.sampleRate = sampleRate;
        this.size[EnvelopeState.PRE_IDLE.getId()] = sampleRate / 5;
        this.factor[EnvelopeState.PRE_IDLE.getId()] = 1d / size[EnvelopeState.PRE_IDLE.getId()];

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
        this.size[EnvelopeState.ATTACK.getId()] = (int) (Tables.ENV_SPEED[this.attackSpeed] * sampleRate);
        this.factor[EnvelopeState.ATTACK.getId()] = 1d / size[EnvelopeState.ATTACK.getId()];
    }

    public int getDecaySpeed() {
        return decaySpeed;
    }

    public void setDecaySpeed(int decaySpeed) {
        this.decaySpeed = Math.max(0, Math.min(decaySpeed, 99));
        this.size[EnvelopeState.DECAY.getId()] = (int) (Tables.ENV_SPEED[this.decaySpeed] * sampleRate);
        this.factor[EnvelopeState.DECAY.getId()] = 1d / size[EnvelopeState.DECAY.getId()];
    }

    public int getSustainSpeed() {
        return sustainSpeed;
    }

    public void setSustainSpeed(int sustainSpeed) {
        this.sustainSpeed = Math.max(0, Math.min(sustainSpeed, 99));
        this.size[EnvelopeState.SUSTAIN.getId()] = (int) (Tables.ENV_SPEED[this.sustainSpeed] * sampleRate);
        this.factor[EnvelopeState.SUSTAIN.getId()] = 1d / size[EnvelopeState.SUSTAIN.getId()];
    }

    public int getReleaseSpeed() {
        return releaseSpeed;
    }

    public void setReleaseSpeed(int releaseSpeed) {
        this.releaseSpeed = Math.max(0, Math.min(releaseSpeed, 99));
        this.size[EnvelopeState.RELEASE.getId()] = (int) (Tables.ENV_SPEED[this.releaseSpeed] * sampleRate);
        this.factor[EnvelopeState.RELEASE.getId()] = 1d / size[EnvelopeState.RELEASE.getId()];
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
            endAmplitude[keyId] = releaseAmplitude;
        }
    }
    // </editor-fold>

    double getEnvelopeAmplitude(int keyId) {
        switch (state[keyId]) {
            case ATTACK:
                if (applyEnvelope(keyId, EnvelopeState.ATTACK)) {
                    position[keyId] = 0;
                    progress[keyId] = 0;
                    startAmplitude[keyId] = attackAmplitude;
                    endAmplitude[keyId] = decayAmplitude;
                    state[keyId] = EnvelopeState.DECAY;
                }
                break;

            case DECAY:
                if (applyEnvelope(keyId, EnvelopeState.DECAY)) {
                    position[keyId] = 0;
                    progress[keyId] = 0;
                    startAmplitude[keyId] = decayAmplitude;
                    endAmplitude[keyId] = sustainAmplitude;
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

    void advanceEnvelope(int keyId) {
        if (state[keyId].getId() <= 4) {
            position[keyId] += 1;
            progress[keyId] += factor[state[keyId].getId()];
        }
    }

    void initialize(int keyId) {
        position[keyId] = 0;
        progress[keyId] = 0;

        startAmplitude[keyId] = currentAmplitude[keyId];
        endAmplitude[keyId] = attackAmplitude;

        state[keyId] = EnvelopeState.ATTACK;
    }

    void reset(int keyId) {
        position[keyId] = 0;
        progress[keyId] = 0;

        startAmplitude[keyId] = 0;
        endAmplitude[keyId] = 0;
        currentAmplitude[keyId] = 0;

        state[keyId] = EnvelopeState.IDLE;
    }

}
