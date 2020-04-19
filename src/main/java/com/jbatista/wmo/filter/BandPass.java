package com.jbatista.wmo.filter;

import com.jbatista.wmo.util.MathFunctions;

/**
 * <p>Based on the <i>'Cookbook formulae for audio equalizer biquad filter coefficients'</i> by Robert Bristow-Johnson.</p>
 *
 * @see <a href="https://www.w3.org/2011/audio/audio-eq-cookbook.html">Audio EQ Cookbook</a>
 */
public class BandPass extends BiquadFilter {

    public BandPass(int sampleRate) {
        this.sampleRate = sampleRate;
        setCenterFrequency(440);
        setQ(1);
    }

    public void setCenterFrequency(double centerFrequency) {
        this.frequency = centerFrequency;
        calculateCoefficients();
    }

    public double getCenterFrequency() {
        return frequency;
    }

    public void setQ(double q) {
        this.q = q;
        calculateCoefficients();
    }

    public double getQ() {
        return q;
    }

    @Override
    protected void calculateCoefficients() {
        omega = MathFunctions.TAU * frequency / sampleRate;
        sin = Math.sin(omega);
        cos = Math.cos(omega);
        alpha = sin / (2 * q);

        cB0 = alpha;
        cB1 = 0;
        cB2 = -alpha;
        cA0 = 1 + alpha;
        cA1 = -2 * cos;
        cA2 = 1 - alpha;

        normalize();
    }

}
