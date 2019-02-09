package com.jbatista.wmo;

import com.jbatista.wmo.WaveForm;

public class MathUtil {

    private static final double _2xPI = 2 * Math.PI;
    private static final double _2dPI = 2 / Math.PI;

    public static double lerp(double start, double end, double factor) {
        return start + factor * (end - start);
    }

    public static double oscillator(WaveForm waveForm, double frequency, long time) {
        switch (waveForm) {
            case SINE:
                return sineWave(frequency, time);
            case SQUARE:
                return squareWave(frequency, time);
            case TRIANGLE:
                return triangleWave(frequency, time);
            case SAWTOOTH:
                return sawtoothWave(frequency, time);
            default:
                throw new AssertionError(waveForm.name());
        }
    }

    public static double sineWave(double frequency, long time) {
        return Math.sin(_2xPI * frequency * time);
    }

    public static double squareWave(double frequency, long time) {
        return Math.signum(Math.sin(_2xPI * frequency * time));
    }

    public static double triangleWave(double frequency, long time) {
        return _2dPI * Math.asin(Math.sin(_2xPI * frequency * time));
    }

    public static double sawtoothWave(double frequency, long time) {
        return ((time + frequency * 2) % frequency) / frequency - 0.5;
    }

}
