package com.jbatista.wmo.filters;

// uses direct-form I

abstract class BiquadFilter implements Filter {

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

    protected double cA0 = 0;
    protected double cA1 = 0;
    protected double cA2 = 0;
    protected double cB0 = 0;
    protected double cB1 = 0;
    protected double cB2 = 0;

    private double b0 = 0;
    private double b1 = 0;
    private double b2 = 0;
    private double a1 = 0;
    private double a2 = 0;

    private double y = 0;

    private double in0 = 0;
    private double in1 = 0;
    private double in2 = 0;

    private double out0 = 0;
    private double out1 = 0;

    protected void normalize() {
        b0 = cB0 / cA0;
        b1 = cB1 / cA0;
        b2 = cB2 / cA0;
        a1 = cA1 / cA0;
        a2 = cA2 / cA0;
    }

    @Override
    public double apply(double sample) {
        in0 = sample;

        y = b0 * in0 + b1 * in1 + b2 * in2 - a1 * out0 - a2 * out1;

        in2 = in1;
        in1 = in0;
        out1 = out0;
        out0 = y;

        return y;
    }

    protected abstract void calculateCoefficients();
}
