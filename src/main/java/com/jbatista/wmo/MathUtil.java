package com.jbatista.wmo;

public class MathUtil {

    public static final double PIx2 = 2 * Math.PI;
    public static final double PId2 = 2 / Math.PI;

    public static double lerp(double start, double end, double factor) {
        return start + factor * (end - start);
    }

    public static double oscillator(WaveForm waveForm, double sampleRate, double frequency, double phase, double modulation, long time) {
        switch (waveForm) {
            case SAWTOOTH:
                return sawtoothWave(sampleRate / frequency, phase, modulation, time);
            default:
                return oscillator(waveForm, frequency / sampleRate, phase, modulation, time);
        }
    }

    public static double oscillator(WaveForm waveForm, double frequency, double phase, double modulation, long time) {
        switch (waveForm) {
            case SINE:
                return sineWave(frequency, phase, modulation, time);
            case SQUARE:
                return squareWave(frequency, phase, modulation, time);
            case TRIANGLE:
                return triangleWave(frequency, phase, modulation, time);
            case SAWTOOTH:
                return sawtoothWave(frequency, phase, modulation, time);
            default:
                throw new AssertionError(waveForm.name());
        }
    }

    private static double sineWave(double frequency, double phase, double modulation, long time) {
        return Math.sin((PIx2 * frequency * time + phase) + modulation);
    }

    private static double squareWave(double frequency, double phase, double modulation, long time) {
        return Math.signum(sineWave(frequency, phase, modulation, time));
    }

    private static double triangleWave(double frequency, double phase, double modulation, long time) {
        return PId2 * Math.asin(sineWave(frequency, phase, modulation, time));
    }

    // TODO set phase
    private static double sawtoothWave(double frequency, double phase, double modulation, long time) {
        return ((time + frequency * 2) % frequency) / (frequency + modulation) - 0.5;
    }

}
