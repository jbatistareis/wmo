package com.jbatista.wmo.components;

import com.jbatista.wmo.MathUtil;

public class Key {

    private final Instrument instrument;

    private double calculatedAmplitude = 0;

    private double frequency;

    private double attackFrames;
    private double attackStep;
    private double attackAmplitude;

    private double decayFrames;
    private double decayStep;
    private double decayAmplitude;

    private double sustainAmplitude;

    private double releaseFrames;
    private double releaseStep;
    private double releaseAmplitude;

    private long elapsed = 0;

    private KeyState keyState = KeyState.IDLE;
    private boolean wasActve = false;

    protected static enum KeyState {
        HIT, ATTACK, DECAY, SUSTAIN, RELEASE, IDLE
    }

    protected Key(double frequency, Instrument instrument) {
        this.frequency = frequency;
        this.instrument = instrument;
    }

    // <editor-fold defaultstate="collapsed" desc="getters/setters">
    public Instrument getInstrument() {
        return instrument;
    }

    public double getFrequency() {
        return frequency;
    }

    public void setFrequency(double frequency) {
        this.frequency = frequency;
    }

    public KeyState getKeyState() {
        return keyState;
    }
    // </editor-fold>

    // reacts to key press, apply envelope
    protected double getSample() {
        switch (keyState) {
            // setup
            case HIT:
                elapsed = wasActve ? elapsed : 0;

                sustainAmplitude = MathUtil.lerp(0, instrument.getEffectiveAmplitude(), instrument.getSustain());

                attackFrames = instrument.getSampleRate() * Math.max(instrument.getAttack(), 0.01);
                attackStep = (wasActve ? (instrument.getEffectiveAmplitude() - calculatedAmplitude) : instrument.getEffectiveAmplitude()) / attackFrames;
                attackAmplitude = wasActve ? calculatedAmplitude : 0;

                decayFrames = instrument.getSampleRate() * instrument.getDecay();
                decayStep = (instrument.getEffectiveAmplitude() - sustainAmplitude) / decayFrames;
                decayAmplitude = instrument.getEffectiveAmplitude();

                releaseFrames = instrument.getSampleRate() * Math.max(instrument.getRelease(), 0.01);
                releaseStep = sustainAmplitude / releaseFrames;
                releaseAmplitude = sustainAmplitude;

                keyState = KeyState.ATTACK;
                wasActve = false;

                break;

            case ATTACK:
                calculatedAmplitude = attackAmplitude += attackStep;
                elapsed++;

                keyState = (attackFrames-- > 0) ? KeyState.ATTACK : (decayFrames > 0) ? KeyState.DECAY : KeyState.SUSTAIN;

                break;

            case DECAY:
                calculatedAmplitude = decayAmplitude -= decayStep;
                elapsed++;

                keyState = (decayFrames-- > 0) ? KeyState.DECAY : KeyState.SUSTAIN;

                break;

            case SUSTAIN:
                calculatedAmplitude = sustainAmplitude;
                elapsed++;

                break;

            case RELEASE:
                calculatedAmplitude = releaseAmplitude -= releaseStep;
                elapsed++;

                keyState = (releaseFrames-- > 0) ? KeyState.RELEASE : KeyState.IDLE;

                break;

            case IDLE:
                return 0.0;
        }

        return calculatedAmplitude * MathUtil.oscillator(instrument.getWaveForm(), instrument.getSampleRate(), frequency, instrument.getModulation(elapsed), elapsed);
    }

    public void pressKey() {
        if (!keyState.equals(KeyState.IDLE)) {
            wasActve = true;
        }

        keyState = KeyState.HIT;
    }

    public void releaseKey() {
        keyState = KeyState.RELEASE;
    }

    @Override
    public int hashCode() {
        return ((Double) frequency).hashCode();
    }

}
