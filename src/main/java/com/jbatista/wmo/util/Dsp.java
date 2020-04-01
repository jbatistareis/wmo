package com.jbatista.wmo.util;

import com.jbatista.wmo.WaveForm;

/**
 * <p>Provides common methods for audio wave creation and modification.</p>
 */
public class Dsp {

    public static double oscillator(WaveForm waveForm, double frequency, double modulation, double phase, long time) {
        switch (waveForm) {
            case SINE:
                return sineWave(frequency, modulation, phase, time);
            case SQUARE:
                return squareWave(frequency, modulation, phase, time);
            case TRIANGLE:
                return triangleWave(frequency, modulation, phase, time);
            case SAWTOOTH_UP:
                return sawtoothWave(true, frequency, modulation, phase, time);
            case SAWTOOTH_DOWN:
                return sawtoothWave(false, frequency, modulation, phase, time);
            case WHITE_NOISE:
                return whiteNoise();
            default:
                // wave form not implemented
                return 0;
        }
    }

    /**
     * <p>Distorts the wave angle according to a factor.</p>
     * <p>As it moves to negative, the wave turns into a triangle, then distorts completely.</p>
     * <p>As it moves to positive, the wave turns into a square.</p>
     *
     * @param sample A PCM audio sample.
     * @param factor A value from -1 to 1.
     * @return The modified sample.
     * @see <a href="https://www.musicdsp.org/en/latest/Effects/46-waveshaper.html">Music DSP article</a>
     */
    public static double waveshaper(double sample, double factor) {
        factor = Math.max(-1, Math.min(factor, 1));
        final double k = 2 * factor / (1 - factor);

        return (1 + k) * sample / (1 + k * Math.abs(sample));
    }

    private static double sineWave(double frequency, double modulation, double phase, long time) {
        return Math.sin(MathFunctions.TAU * frequency * time + modulation + phase);
    }

    private static double squareWave(double frequency, double modulation, double phase, long time) {
        return Math.signum(sineWave(frequency, modulation, phase, time));
    }

    private static double triangleWave(double frequency, double modulation, double phase, long time) {
        return MathFunctions.FRAC_2_DIV_PI * Math.asin(sineWave(frequency, modulation, phase, time));
    }

    private static double sawtoothWave(boolean up, double frequency, double modulation, double phase, long time) {
        return (up ? 1 : -1) * MathFunctions.FRAC_2_DIV_PI * Math.atan(Math.tan(Math.PI * time * frequency + modulation + phase));
    }

    private static double whiteNoise() {
        return 2 * MathFunctions.RANDOM.nextDouble() - 1;
    }

}
