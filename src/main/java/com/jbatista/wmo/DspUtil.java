package com.jbatista.wmo;

public class DspUtil {

    public static double oscillator(WaveForm waveForm, double sampleRate, double frequency, double phase, double modulation, long time) {
        switch (waveForm) {
            case SINE:
                return sineWave(sampleRate, frequency, phase, modulation, time);
            case SQUARE:
                return squareWave(sampleRate, frequency, phase, modulation, time);
            case TRIANGLE:
                return triangleWave(sampleRate, frequency, phase, modulation, time);
            case SAWTOOTH:
                return sawtoothWave(sampleRate, frequency, phase, modulation, time);
            default:
                throw new AssertionError(waveForm.name());
        }
    }

    private static double sineWave(double sampleRate, double frequency, double phase, double modulation, long time) {
        return Math.sin((MathUtil.TAU * frequency * time) / sampleRate + phase) + modulation;
    }

    private static double squareWave(double sampleRate, double frequency, double phase, double modulation, long time) {
        return Math.signum(sineWave(sampleRate, frequency, phase, modulation, time));
    }

    private static double triangleWave(double sampleRate, double frequency, double phase, double modulation, long time) {
        return MathUtil.PI_D2 * Math.asin(sineWave(sampleRate, frequency, phase, modulation, time));
    }

    private static double sawtoothWave(double sampleRate, double frequency, double phase, double modulation, long time) {
        return -MathUtil.PI_D2 * Math.atan(1 / Math.tan(((Math.PI * time * frequency) / sampleRate)) + phase);
    }

}
