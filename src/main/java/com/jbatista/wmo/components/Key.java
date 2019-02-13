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
    private boolean wasAttackOrDecay = false;

    // L - R
    private final double[] sample = new double[2];
    private final double[] modulation = new double[2];

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
    protected double[] getSample() {
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

                calculateRelease(sustainAmplitude);

                keyState = KeyState.ATTACK;
                wasActve = false;
                wasAttackOrDecay = false;

                break;

            case ATTACK:
                calculatedAmplitude = attackAmplitude += attackStep;
                elapsed++;

                keyState = (attackFrames-- > 0) ? KeyState.ATTACK : (decayFrames > 0) ? KeyState.DECAY : KeyState.SUSTAIN;
                wasAttackOrDecay = true;

                break;

            case DECAY:
                calculatedAmplitude = decayAmplitude -= decayStep;
                elapsed++;

                keyState = (decayFrames-- > 0) ? KeyState.DECAY : KeyState.SUSTAIN;
                wasAttackOrDecay = true;

                break;

            case SUSTAIN:
                elapsed++;
                wasAttackOrDecay = false;

                break;

            case RELEASE:
                if (wasAttackOrDecay) {
                    calculateRelease(calculatedAmplitude);
                    wasAttackOrDecay = false;
                }

                calculatedAmplitude = releaseAmplitude -= releaseStep;
                elapsed++;

                keyState = (releaseFrames-- > 0) ? KeyState.RELEASE : KeyState.IDLE;

                break;

            case IDLE:
                sample[0] = 0.0;
                sample[1] = 0.0;

                return sample;
        }

        instrument.getModulation(modulation, elapsed);

        sample[0] = calculatedAmplitude * MathUtil.oscillator(
                instrument.getWaveForm(),
                instrument.getSampleRate(),
                frequency,
                instrument.getEffectivePhaseL(),
                modulation[0],
                elapsed);

        sample[1] = calculatedAmplitude * MathUtil.oscillator(
                instrument.getWaveForm(),
                instrument.getSampleRate(),
                frequency,
                instrument.getEffectivePhaseR(),
                modulation[1],
                elapsed);

        return sample;
    }

    private void calculateRelease(double baseAmplitude) {
        releaseFrames = instrument.getSampleRate() * Math.max(instrument.getRelease(), 0.01);
        releaseStep = baseAmplitude / releaseFrames;
        releaseAmplitude = baseAmplitude;
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
