package com.jbatista.wmo.components.play;

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
    private boolean wasActive = false;
    private boolean initialRelease = true;
    private double effectiveAmplitude;

    // L - R
    private final double[] sample = new double[2];
    private final double[] modulation = new double[2];

    protected static enum KeyState {
        ATTACK, DECAY, SUSTAIN, RELEASE, IDLE
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
                elapsed++;
                calculatedAmplitude = sustainAmplitude;

                break;

            case RELEASE:
                if (initialRelease) {
                    calculateRelease(calculatedAmplitude);
                    initialRelease = false;
                }

                calculatedAmplitude = releaseAmplitude -= releaseStep;
                elapsed++;

                if (releaseFrames-- > 0) {
                    keyState = KeyState.RELEASE;
                } else {
                    keyState = KeyState.IDLE;
                    instrument.decrementKeyCount();
                }

                break;

            case IDLE:
                sample[0] = 0.0;
                sample[1] = 0.0;

                return sample;
        }

        instrument.getModulation(modulation, elapsed, frequency);

        sample[0] = MathUtil.oscillator(
                instrument.getWaveForm(),
                calculatedAmplitude,
                instrument.getSampleRate(),
                frequency,
                instrument.getEffectivePhaseL(),
                modulation[0],
                elapsed);

        sample[1] = MathUtil.oscillator(
                instrument.getWaveForm(),
                calculatedAmplitude,
                instrument.getSampleRate(),
                frequency,
                instrument.getEffectivePhaseR(),
                modulation[1],
                elapsed);

        return sample;
    }

    public void press() {
        wasActive = !keyState.equals(KeyState.IDLE);
        initialRelease = true;

        if (!wasActive) {
            instrument.incrementKeyCount();
            elapsed = wasActive ? elapsed : 0;
        }

        effectiveAmplitude = instrument.getEffectiveAmplitude();

        sustainAmplitude = MathUtil.lerp(0, effectiveAmplitude, instrument.getSustain());

        decayFrames = instrument.getSampleRate() * instrument.getDecay();
        decayStep = (effectiveAmplitude - sustainAmplitude) / decayFrames;
        decayAmplitude = effectiveAmplitude;

        if (decayFrames == 0) {
            effectiveAmplitude = sustainAmplitude;
        }

        attackFrames = instrument.getSampleRate() * Math.max(instrument.getAttack(), 0.01);
        attackStep = (wasActive ? (effectiveAmplitude - calculatedAmplitude) : effectiveAmplitude) / attackFrames;
        attackAmplitude = wasActive ? calculatedAmplitude : 0;

        keyState = KeyState.ATTACK;
    }

    public void release() {
        keyState = KeyState.RELEASE;
    }

    private void calculateRelease(double baseAmplitude) {
        releaseFrames = instrument.getSampleRate() * Math.max(instrument.getRelease(), 0.01);
        releaseStep = baseAmplitude / releaseFrames;
        releaseAmplitude = baseAmplitude;
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof Key) ? ((Key) obj).getFrequency() == this.frequency : false;
    }

    @Override
    public int hashCode() {
        return ((Double) frequency).hashCode();
    }

}
