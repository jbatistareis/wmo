package com.jbatista.wmo.filters;

/*
    Based on the 'Cookbook formulae for audio equalizer biquad filter coefficients' by Robert Bristow-Johnson
    https://www.w3.org/2011/audio/audio-eq-cookbook.html
*/

public abstract class Filter {

    public enum FilterType {LOW_PASS, HIGH_PASS, BAND_PASS_CONSTANT_SKIRT_GAIN, BAND_PASS_CONSTANT_0_DB_PEAK_GAIN, NOTCH, ALL_PASS, PEAKING_EQ, LOW_SHELF, HIGH_SHELF}

    protected double frequency = 0;
    protected double sampleRate = 0;
    protected double dbGain = 0;
    protected double bandwidth = 0;
    protected double q = 0;
    protected double s = 0;

    protected double a = 0;
    protected double omega = 0;
    protected double sin = 0;
    protected double cos = 0;
    protected double alpha = 0;
    protected double beta = 0;

    protected final double[] cA = new double[]{0, 0, 0};
    protected final double[] cB = new double[]{0, 0, 0};

    private double y;

    private double b0a0;
    private double b1a0;
    private double b2a0;
    private double a1a0;
    private double a2a0;

    private final double[] in = new double[]{0, 0, 0};
    private final double[] out = new double[]{0, 0};

    protected Filter() {
    }

    protected void normalize() {
        cB[0] /= cA[0];
        cB[1] /= cA[0];
        cB[2] /= cA[0];
        cA[1] /= cA[0];
        cA[2] /= cA[0];

        b0a0 = (cB[0] / cA[0]);
        b1a0 = (cB[1] / cA[0]);
        b2a0 = (cB[2] / cA[0]);
        a1a0 = (cA[1] / cA[0]);
        a2a0 = (cA[2] / cA[0]);
    }

    public double apply(double sample) {
        in[0] = sample;

        y = b0a0 * in[0] + b1a0 * in[1] + b2a0 * in[2] - a1a0 * out[0] - a2a0 * out[1];

        in[2] = in[1];
        in[1] = in[0];
        out[1] = out[0];
        out[0] = y;

        return y;
    }

    protected abstract void calculateCoefficients();

}
