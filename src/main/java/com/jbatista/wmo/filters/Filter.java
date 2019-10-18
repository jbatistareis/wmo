package com.jbatista.wmo.filters;

public abstract class Filter {

    private double y;

    private int bufferSize;

    protected double sampleRate;
    protected double frequency;
    protected double dBgain;
    protected double bandwidth;
    protected double q;
    protected double s;

    protected double a;
    protected double omega;
    protected double sin;
    protected double cos;
    protected double alpha;
    protected double beta;

    protected final double[] cA = new double[]{0, 0, 0};
    protected final double[] cB = new double[]{0, 0, 0};

    private double b0a0;
    private double b1a0;
    private double b2a0;
    private double a1a0;
    private double a2a0;

    private final double[] in = new double[]{0, 0, 0};
    private final double[] out = new double[]{0, 0};

    public Filter(int bufferSize, double sampleRate, double frequency, double dBgain, double bandwidth, double q, double s) {
        this.bufferSize = bufferSize;
        this.sampleRate = sampleRate;
        this.frequency = frequency;
        this.dBgain = dBgain;
        this.bandwidth = bandwidth;
        this.q = q;
        this.s = s;

        calculateCoefficients();
        b0a0 = (cB[0] / cA[0]);
        b1a0 = (cB[1] / cA[0]);
        b2a0 = (cB[2] / cA[0]);
        a1a0 = (cA[1] / cA[0]);
        a2a0 = (cA[2] / cA[0]);
    }

    protected abstract void calculateCoefficients();

    public double process(double sample) {
        in[0] = sample;

        y = b0a0 * in[0] + b1a0 * in[1] + b2a0 * in[2] - a1a0 * out[0] - a2a0 * out[1];

        in[2] = in[1];
        in[1] = in[0];
        out[1] = out[0];
        out[0] = y;

        return y;
    }

}
