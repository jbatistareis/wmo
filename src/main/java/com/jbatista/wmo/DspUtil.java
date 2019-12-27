package com.jbatista.wmo;

public class DspUtil {

    public static double oscillator(WaveForm waveForm, double frequency, double modulation, double phase, long time) {
        switch (waveForm) {
            case SINE:
                return sineWave(frequency, modulation, phase, time);
            case SQUARE:
                return squareWave(frequency, modulation, phase, time);
            case TRIANGLE:
                return triangleWave(frequency, modulation, phase, time);
            case SAWTOOTH:
                return sawtoothWave(frequency, modulation, phase, time);
            case WHITE_NOISE:
                return whiteNoise();
            default:
                throw new AssertionError(waveForm.name());
        }
    }

    // from [https://www.musicdsp.org/en/latest/Effects/46-waveshaper.html]
    public static double waveshaper(double sample, double factor) {
        factor = Math.max(-1, Math.min(factor, 1));
        final double k = 2 * factor / (1 - factor);

        return (1 + k) * sample / (1 + k * Math.abs(sample));
    }

    private static double sineWave(double frequency, double modulation, double phase, long time) {
        return Math.sin(MathUtil.TAU * frequency * time + modulation + phase);
    }

    private static double squareWave(double frequency, double modulation, double phase, long time) {
        return Math.signum(sineWave(frequency, modulation, phase, time));
    }

    private static double triangleWave(double frequency, double modulation, double phase, long time) {
        return MathUtil.PI_D2 * Math.asin(sineWave(frequency, modulation, phase, time));
    }

    private static double sawtoothWave(double frequency, double modulation, double phase, long time) {
        return -MathUtil.PI_D2 * Math.atan(1 / Math.tan(Math.PI * time * frequency + modulation + phase));
    }

    private static double whiteNoise() {
        return 2 * MathUtil.RANDOM.nextDouble() - 1;
    }

}
