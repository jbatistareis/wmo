package com.jbatista.wmo;

public class DspUtil {

    public enum WaveForm {SINE, SQUARE, TRIANGLE, SAWTOOTH, WHITE_NOISE}

    public static double oscillator(WaveForm waveForm, double sampleRate, double frequency, double modulation, double phase, long time) {
        switch (waveForm) {
            case SINE:
                return sineWave(sampleRate, frequency, modulation, phase, time);
            case SQUARE:
                return squareWave(sampleRate, frequency, modulation, phase, time);
            case TRIANGLE:
                return triangleWave(sampleRate, frequency, modulation, phase, time);
            case SAWTOOTH:
                return sawtoothWave(sampleRate, frequency, modulation, phase, time);
            case WHITE_NOISE:
                return whiteNoise();
            default:
                throw new AssertionError(waveForm.name());
        }
    }

    public static double modulator(WaveForm waveForm, double sampleRate, double frequency, double frequencyRatio, double strength, long time) {
        switch (waveForm) {
            case SINE:
                return strength * sineWave(sampleRate, frequency / frequencyRatio, 0, 0, time);
            case SQUARE:
                return strength * squareWave(sampleRate, frequency / frequencyRatio, 0, 0, time);
            case TRIANGLE:
                return strength * triangleWave(sampleRate, frequency / frequencyRatio, 0, 0, time);
            case SAWTOOTH:
                return strength * sawtoothWave(sampleRate, frequency / frequencyRatio, 0, 0, time);
            case WHITE_NOISE:
                return strength * whiteNoise();
            default:
                throw new AssertionError(waveForm.name());
        }
    }

    private static double sineWave(double sampleRate, double frequency, double modulation, double phase, long time) {
        return Math.sin((MathUtil.TAU * frequency * time) / sampleRate + modulation + phase);
    }

    private static double squareWave(double sampleRate, double frequency, double modulation, double phase, long time) {
        return Math.signum(sineWave(sampleRate, frequency, modulation, phase, time));
    }

    private static double triangleWave(double sampleRate, double frequency, double modulation, double phase, long time) {
        return MathUtil.PI_D2 * Math.asin(sineWave(sampleRate, frequency, modulation, phase, time));
    }

    private static double sawtoothWave(double sampleRate, double frequency, double modulation, double phase, long time) {
        return -MathUtil.PI_D2 * Math.atan(1 / Math.tan(((Math.PI * time * (frequency + modulation)) / sampleRate)));
    }

    private static double whiteNoise() {
        return 2 * MathUtil.RANDOM.nextDouble() - 1;
    }

}
