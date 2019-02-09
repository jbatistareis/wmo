package com.jbatista.wmo.components;

import com.jbatista.wmo.WaveForm;

public class Util {

    private static final double _2xPI = 2 * Math.PI;
    private static final double _2dPI = 2 / Math.PI;

    public static double lerp(double start, double end, double factor) {
        return start + factor * (end - start);
    }

    public static double oscillator(WaveForm waveForm, double amplitude, double sampleRate, double frequency, long frame) {
        switch (waveForm) {
            case SINE:
                return amplitude * Math.sin(_2xPI * (frequency / sampleRate) * frame);
            case SQUARE:
                return amplitude * Math.signum(Math.sin(_2xPI * (frequency / sampleRate) * frame));
            case TRIANGLE:
                return amplitude * _2dPI * Math.asin(Math.sin(_2xPI * (frequency / sampleRate) * frame));
            case SAWTOOTH:
                return amplitude * ((frame + sampleRate / frequency * 2) % (sampleRate / frequency)) / (sampleRate / frequency) - amplitude / 2;
            default:
                throw new AssertionError(waveForm.name());
        }
    }

}
