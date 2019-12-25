package com.jbatista.wmo.filters;

import com.jbatista.wmo.MathUtil;

/*
    Based on the 'Cookbook formulae for audio equalizer biquad filter coefficients' by Robert Bristow-Johnson
    https://www.w3.org/2011/audio/audio-eq-cookbook.html
*/

public class HighPass extends BiquadFilter {

    public HighPass(double sampleRate) {
        this.sampleRate = sampleRate;
        setCutoffFrequency(440);
        setResonance(1);
    }

    public void setCutoffFrequency(double cutoffFrequency) {
        this.frequency = cutoffFrequency;
        calculateCoefficients();
    }

    public double getCutoffFrequency() {
        return frequency;
    }

    public void setResonance(double resonance) {
        this.q = resonance;
        calculateCoefficients();
    }

    public double getResonance() {
        return q;
    }

    @Override
    protected void calculateCoefficients() {
        omega = (MathUtil.TAU * frequency) / sampleRate;
        sin = Math.sin(omega);
        cos = Math.cos(omega);
        alpha = sin / (2 * q);

        cB0 = (1 + cos) / 2;
        cB1 = 1 + cos;
        cB2 = (1 + cos) / 2;
        cA0 = 1 + alpha;
        cA1 = -2 * cos;
        cA2 = 1 - alpha;

        normalize();
    }

}
