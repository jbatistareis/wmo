package com.jbatista.wmo.filters;

import com.jbatista.wmo.synthesis.Instrument;

public class LowPass extends Filter {

    public LowPass() {
        setCutoffFrequency(440);
        setResonance(0);
    }

    public void setCutoffFrequency(double cutoffFrequency) {
        this.frequency = cutoffFrequency;

        calculateCoefficients();
        normalize();
    }

    public double getCutoffFrequency() {
        return frequency;
    }

    public void setResonance(double resonance) {
        this.q = resonance;

        calculateCoefficients();
        normalize();
    }

    public double getResonance() {
        return q;
    }

    @Override
    protected void calculateCoefficients() {
        omega = (2 * Math.PI * frequency) / Instrument.getSampleRate();
        sin = Math.sin(omega);
        cos = Math.cos(omega);
        alpha = sin / (2 * q);

        cB[0] = (1 - cos) / 2;
        cB[1] = 1 - cos;
        cB[2] = (1 - cos) / 2;
        cA[0] = 1 + alpha;
        cA[1] = -2 * cos;
        cA[2] = 1 - alpha;
    }

}
