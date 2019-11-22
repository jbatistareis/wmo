package com.jbatista.wmo.filters;

import com.jbatista.wmo.MathUtil;

public class LowPass extends Filter {

    public LowPass(double sampleRate) {
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

        cB[0] = (1 - cos) / 2;
        cB[1] = 1 - cos;
        cB[2] = (1 - cos) / 2;
        cA[0] = 1 + alpha;
        cA[1] = -2 * cos;
        cA[2] = 1 - alpha;

        normalize();
    }

}
