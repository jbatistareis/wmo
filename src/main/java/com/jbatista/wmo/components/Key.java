package com.jbatista.wmo.components;

public class Key {

    private String name;
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

    protected static enum KeyState {
        HIT, ATTACK, DECAY, SUSTAIN, RELEASE, IDLE
    }
    private KeyState keyState = KeyState.IDLE;

    public Key(String name, double frequency, Instrument instrument) {
        this.name = name;
        this.frequency = frequency;
        this.instrument = instrument;
    }

    // <editor-fold defaultstate="collapsed" desc="getters/setters">
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Instrument getInstrument() {
        return instrument;
    }

    public double getFrequency() {
        return frequency;
    }

    public void setFrequency(double frequency) {
        this.frequency = frequency;
    }

    public double getCalculatedAmplitude() {
        return calculatedAmplitude;
    }

    public long getElapsed() {
        return elapsed;
    }

    public KeyState getKeyState() {
        return keyState;
    }
    // </editor-fold>

    // reacts to key press, apply envelope
    protected void calculate() {
        switch (keyState) {
            // setup
            case HIT:
                elapsed = 0;

                sustainAmplitude = lerp(0, instrument.getAmplitude(), instrument.getSustain());

                attackFrames = instrument.getSampleRate() * instrument.getAttack();
                attackStep = instrument.getAmplitude() / attackFrames;
                attackAmplitude = 0;

                decayFrames = instrument.getSampleRate() * instrument.getDecay();
                decayStep = (instrument.getAmplitude() - sustainAmplitude) / decayFrames;
                decayAmplitude = instrument.getAmplitude();

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

                break;
        }
    }

    public void pressKey() {
        instrument.addKey(this);
        keyState = KeyState.HIT;
    }

    public void releaseKey() {
        keyState = KeyState.RELEASE;
    }

    private double lerp(double start, double end, double factor) {
        return start + factor * (end - start);
    }

}
