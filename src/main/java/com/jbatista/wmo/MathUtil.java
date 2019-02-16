package com.jbatista.wmo;

public class MathUtil {

    public static double lerp(double start, double end, double factor) {
        return start + factor * (end - start);
    }

    public static double oscillator(WaveForm waveForm, double amplitude, double sampleRate, double frequency, double phase, double modulation, long time) {
        switch (waveForm) {
            case SAWTOOTH:
                return oscillator(waveForm, amplitude, sampleRate / frequency, phase, modulation, time);
            default:
                return oscillator(waveForm, amplitude, frequency / sampleRate, phase, modulation, time);
        }
    }

    public static double oscillator(WaveForm waveForm, double amplitude, double frequency, double phase, double modulation, long time) {
        switch (waveForm) {
            case SINE:
                return amplitude * sineWave(frequency, phase, modulation, time);
            case SQUARE:
                return amplitude * squareWave(frequency, phase, modulation, time);
            case TRIANGLE:
                return amplitude * triangleWave(frequency, phase, modulation, time);
            case SAWTOOTH:
                return -(2 * amplitude / Math.PI) * sawtoothWave(frequency, phase, modulation, time);
            default:
                throw new AssertionError(waveForm.name());
        }
    }

    private static double sineWave(double frequency, double phase, double modulation, long time) {
        return Math.sin((2 * Math.PI * frequency * time + phase) + modulation);
    }

    private static double squareWave(double frequency, double phase, double modulation, long time) {
        return Math.signum(sineWave(frequency, phase, modulation, time));
    }

    private static double triangleWave(double frequency, double phase, double modulation, long time) {
        return 2 / Math.PI * Math.asin(sineWave(frequency, phase, modulation, time));
    }

    // TODO modulation
    private static double sawtoothWave(double frequency, double phase, double modulation, long time) {
        return Math.atan(1.0 / Math.tan((time * Math.PI / frequency) + phase));
    }

}
