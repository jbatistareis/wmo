package com.jbatista.wmo;

public class MathUtil {

    private static final double _2xPI = 2 * Math.PI;
    private static final double _2dPI = 2 / Math.PI;

    public static double lerp(double start, double end, double factor) {
        return start + factor * (end - start);
    }

    public static double oscillator(WaveForm waveForm, double sampleRate, double frequency, double modulation, long time) {
        switch (waveForm) {
            case SAWTOOTH:
                return sawtoothWave(sampleRate / frequency, modulation, time);
            default:
                return oscillator(waveForm, frequency / sampleRate, modulation, time);
        }
    }

    public static double oscillator(WaveForm waveForm, double frequency, double modulation, long time) {
        switch (waveForm) {
            case SINE:
                return sineWave(frequency, modulation, time);
            case SQUARE:
                return squareWave(frequency, modulation, time);
            case TRIANGLE:
                return triangleWave(frequency, modulation, time);
            case SAWTOOTH:
                return sawtoothWave(frequency, modulation, time);
            default:
                throw new AssertionError(waveForm.name());
        }
    }

    private static double sineWave(double frequency, double modulation, long time) {
        return Math.sin(_2xPI * frequency * time + modulation);
    }

    private static double squareWave(double frequency, double modulation, long time) {
        return Math.signum(sineWave(frequency, modulation, time));
    }

    private static double triangleWave(double frequency, double modulation, long time) {
        return _2dPI * Math.asin(sineWave(frequency, modulation, time));
    }

    private static double sawtoothWave(double frequency, double modulation, long time) {
        return ((time + frequency * 2) % frequency) / (frequency + modulation) - 0.5;
    }

}
