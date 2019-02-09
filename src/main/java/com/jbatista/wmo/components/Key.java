package com.jbatista.wmo.components;

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
    protected double calculateFrame() {
        switch (keyState) {
            // setup
            case HIT:
                elapsed = 0;

                sustainAmplitude = Util.lerp(0, instrument.getEffectiveAmplitude(), instrument.getSustain());

                attackFrames = instrument.getSampleRate() * instrument.getAttack();
                attackStep = instrument.getEffectiveAmplitude() / attackFrames;
                attackAmplitude = 0;

                decayFrames = instrument.getSampleRate() * instrument.getDecay();
                decayStep = (instrument.getEffectiveAmplitude() - sustainAmplitude) / decayFrames;
                decayAmplitude = instrument.getEffectiveAmplitude();

                releaseFrames = instrument.getSampleRate() * instrument.getRelease();
                releaseStep = sustainAmplitude / releaseFrames;
                releaseAmplitude = sustainAmplitude;

                keyState = (attackFrames > 0) ? KeyState.ATTACK : KeyState.SUSTAIN;

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

            // reset
            case IDLE:
                calculatedAmplitude = 0;
                elapsed = 0;

                break;
        }

        return Util.oscillator(instrument.getWaveForm(), calculatedAmplitude, instrument.getSampleRate(), frequency, elapsed);
    }

    public void pressKey() {
        instrument.addKey(this);
        keyState = KeyState.HIT;
    }

    public void releaseKey() {
        keyState = KeyState.RELEASE;
    }

}
