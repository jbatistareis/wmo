package com.jbatista.wmo.components;

import com.jbatista.wmo.WaveForm;

public class Util {

    private static final double _2xPI = 2 * Math.PI;
    private static final double _2dPI = 2 / Math.PI;

    public static double lerp(double start, double end, double factor) {
        return start + factor * (end - start);
    }

    public static double oscillator(WaveForm waveForm, double sampleRate, double frequency, long time) {
        switch (waveForm) {
            case SINE:
                return sineWave(sampleRate, frequency, time);
            case SQUARE:
                return squareWave(sampleRate, frequency, time);
            case TRIANGLE:
                return triangleWave(sampleRate, frequency, time);
            case SAWTOOTH:
                return sawtoothWave(sampleRate, frequency, time);
            default:
                throw new AssertionError(waveForm.name());
        }
    }

    public static double sineWave(double sampleRate, double frequency, long time) {
        return Math.sin(_2xPI * (frequency / sampleRate) * time);
    }

    public static double squareWave(double sampleRate, double frequency, long time) {
        return Math.signum(Math.sin(_2xPI * (frequency / sampleRate) * time));
    }

    public static double triangleWave(double sampleRate, double frequency, long time) {
        return _2dPI * Math.asin(Math.sin(_2xPI * (frequency / sampleRate) * time));
    }

    public static double sawtoothWave(double sampleRate, double frequency, long time) {
        return ((time + sampleRate / frequency * 2) % (sampleRate / frequency)) / (sampleRate / frequency) - 0.5;
    }

}
