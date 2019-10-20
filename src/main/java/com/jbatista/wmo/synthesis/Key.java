package com.jbatista.wmo.synthesis;

import com.jbatista.wmo.DspUtil;
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
    private double samplePositionFactor = 0;

    private KeyState keyState = KeyState.IDLE;
    private boolean wasActive = false;
    private boolean initialRelease = true;
    private double effectiveAmplitude;

    // L - R
    private final double[] sample = new double[2];
    private double modulationSample = 0;

    protected enum KeyState {ATTACK, DECAY, SUSTAIN, RELEASE, IDLE}

    protected Key(double frequency, Instrument instrument) {
        this.frequency = frequency;
        this.instrument = instrument;
    }

    // <editor-fold defaultstate="collapsed" desc="getters/setters">
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

                keyState = (attackFrames-- > 0) ? KeyState.ATTACK : (decayFrames > 0) ? KeyState.DECAY : KeyState.SUSTAIN;

                break;

            case DECAY:
                calculatedAmplitude = decayAmplitude -= decayStep;

                keyState = (decayFrames-- > 0) ? KeyState.DECAY : KeyState.SUSTAIN;

                break;

            case SUSTAIN:
                calculatedAmplitude = sustainAmplitude;

                break;

            case RELEASE:
                if (initialRelease) {
                    calculateRelease(calculatedAmplitude);
                    initialRelease = false;
                }

                calculatedAmplitude = releaseAmplitude -= releaseStep;

                if (releaseFrames-- > 0) {
                    keyState = KeyState.RELEASE;
                } else {
                    keyState = KeyState.IDLE;
                }

                break;

            case IDLE:
                sample[0] = 0.0;
                sample[1] = 0.0;

                return sample;
        }

        elapsed++;

        if (!instrument.getModulators().isEmpty()) {
            modulationSample = 0;
            for (Modulator modulator : instrument.getModulators()) {
                modulationSample += modulator.getSample(frequency, elapsed);
            }
            modulationSample /= instrument.getModulators().size();
        }

        sample[0] = calculatedAmplitude * produceSample(modulationSample, instrument.getPhaseL(), elapsed);
        sample[1] = calculatedAmplitude * produceSample(modulationSample, instrument.getPhaseR(), elapsed);

        return sample;
    }

    public void press() {
        wasActive = !keyState.equals(KeyState.IDLE);
        initialRelease = true;

        if (!wasActive) {
            elapsed = 0;
        }

        effectiveAmplitude = 1;

        sustainAmplitude = MathUtil.lerp(0, effectiveAmplitude, instrument.getSustain());

        decayFrames = instrument.getSampleRate() * instrument.getDecay();
        decayStep = (effectiveAmplitude - sustainAmplitude) / decayFrames;
        decayAmplitude = effectiveAmplitude;

        if (decayFrames == 0) {
            effectiveAmplitude = sustainAmplitude;
        }

        attackFrames = instrument.getSampleRate() * instrument.getAttack();
        attackStep = (wasActive ? (effectiveAmplitude - calculatedAmplitude) : effectiveAmplitude) / attackFrames;
        attackAmplitude = wasActive ? calculatedAmplitude : 0;

        keyState = KeyState.ATTACK;

        if (!instrument.getKeysQueue().contains(this)) {
            instrument.getKeysQueue().offer(this);
        }
    }

    public void release() {
        keyState = KeyState.RELEASE;
    }

    private void calculateRelease(double baseAmplitude) {
        releaseFrames = instrument.getSampleRate() * instrument.getRelease();
        releaseStep = baseAmplitude / releaseFrames;
        releaseAmplitude = baseAmplitude;
    }

    private double produceSample(double modulation, double phase, long time) {
        return DspUtil.oscillator(
                instrument.getWaveForm(),
                instrument.getSampleRate(),
                frequency,
                modulation,
                phase,
                time);
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof Key) && ((Key) obj).getFrequency() == this.frequency;
    }

    @Override
    public int hashCode() {
        return ((Double) frequency).hashCode();
    }

}
