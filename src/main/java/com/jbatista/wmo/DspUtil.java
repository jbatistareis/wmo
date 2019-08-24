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
            // TODO
            case SAWTOOTH:
                return 0;
            // return -(2 * amplitude / Math.PI) * sawtoothWave(frequency, phase, modulation, time);
            default:
                throw new AssertionError(waveForm.name());
        }
    }

    private static double sineWave(double sampleRate, double frequency, double phase, double modulation, long time) {
        return Math.sin((MathUtil.PI_T2 * frequency * time) / sampleRate + phase) + modulation;
    }

    private static double squareWave(double sampleRate, double frequency, double phase, double modulation, long time) {
        return Math.signum(sineWave(frequency, sampleRate, phase, modulation, time));
    }

    private static double triangleWave(double sampleRate, double frequency, double phase, double modulation, long time) {
        return MathUtil.PI_D2 * Math.asin(sineWave(frequency, sampleRate, phase, modulation, time));
    }

    // TODO
    private static double sawtoothWave(double frequency, double sampleRate, double phase, double modulation, long time) {
        return Math.atan(1.0 / Math.tan((time * Math.PI / frequency) + phase));
    }

}
