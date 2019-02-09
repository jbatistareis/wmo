package com.jbatista.wmo;

import com.jbatista.wmo.WaveForm;

public class MathUtil {

    private static final double _2xPI = 2 * Math.PI;
    private static final double _2dPI = 2 / Math.PI;

    public static double lerp(double start, double end, double factor) {
        return start + factor * (end - start);
    }

    public static double oscillator(WaveForm waveForm, double sampleRate, double frequency, double modulation, long time) {
        switch (waveForm) {
            case SINE:
                return sineWave(frequency / sampleRate, modulation, time);
            case SQUARE:
                return squareWave(frequency / sampleRate, modulation, time);
            case TRIANGLE:
                return triangleWave(frequency / sampleRate, modulation, time);
            case SAWTOOTH:
                return sawtoothWave(sampleRate / frequency, modulation, time);
            default:
                throw new AssertionError(waveForm.name());
        }
    }

    public static double sineWave(double frequency, double modulation, long time) {
        return Math.sin(_2xPI * frequency * time + modulation);
    }

    public static double squareWave(double frequency, double modulation, long time) {
        return Math.signum(sineWave(frequency, modulation, time));
    }

    public static double triangleWave(double frequency, double modulation, long time) {
        return _2dPI * Math.asin(sineWave(frequency, modulation, time));
    }

    public static double sawtoothWave(double frequency, double modulation, long time) {
        return ((time + frequency * 2) % frequency) / (frequency + modulation) - 0.5;
    }

}
