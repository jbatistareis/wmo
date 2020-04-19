package com.jbatista.wmo.util;

import java.util.Random;

/**
 * <p>Provides commons mathematical methods and constants.</p>
 *
 * @see <a href="https://codeplea.com/simple-interpolation">CodePlea article: "Simple Interpolation"</a>
 */
public class MathFunctions {

    public static final double TAU = 6.2831;
    public static final double FRAC_PI_DIV_2 = 1.5707;
    public static final double FRAC_2_DIV_PI = 0.6366;
    public static final double NATURAL_LOG10 = 2.3025;
    public static final int SIGNED_16_BIT_MAX = 32767;
    public static final int SIGNED_24_BIT_MAX = 8388607;
    public static final Random RANDOM = new Random();

    public static int primitiveFrom16bit(boolean bigEndian, boolean signed, byte[] data, int offset) {
        return (bigEndian
                ? ((data[offset] & 0xFF) << 8) | (data[offset + 1] & 0xFF)
                : ((data[offset + 1] & 0xFF) << 8) | (data[offset] & 0xFF))
                - (!signed ? Short.MAX_VALUE : 0);
    }

    public static long primitiveFrom24bit(boolean bigEndian, boolean signed, byte[] data, int offset) {
        return (bigEndian
                ? ((data[offset] & 0xFF) << 16) | ((data[offset + 1] & 0xFF) << 8) | (data[offset + 2] & 0xFF)
                : ((data[offset + 2] & 0xFF) << 16) | ((data[offset + 1] & 0xFF) << 8) | (data[offset] & 0xFF))
                - (!signed ? SIGNED_24_BIT_MAX : 0);
    }

    public static long primitiveFrom32bit(boolean bigEndian, boolean signed, byte[] data, int offset) {
        return (bigEndian
                ? ((data[offset] & 0xFF) << 24) | ((data[offset + 1] & 0xFF) << 16) | ((data[offset + 2] & 0xFF) << 8) | (data[offset + 3] & 0xFF)
                : ((data[offset + 3] & 0xFF) << 24) | ((data[offset + 2] & 0xFF) << 16) | ((data[offset + 1] & 0xFF) << 8) | (data[offset] & 0xFF))
                - (!signed ? Integer.MAX_VALUE : 0);
    }

    public static void primitiveTo16bit(boolean bigEndian, byte[] buffer, int bufferIndex, int value) {
        if (buffer.length < 2) {
            throw new IllegalArgumentException("Buffer size must be of at least 2");
        }

        if (bigEndian) {
            buffer[bufferIndex + 0] = (byte) (value >> 8);
            buffer[bufferIndex + 1] = (byte) value;
        } else {
            buffer[bufferIndex + 0] = (byte) value;
            buffer[bufferIndex + 1] = (byte) (value >> 8);
        }
    }

    public static void primitiveTo24bit(boolean bigEndian, byte[] buffer, int bufferIndex, long value) {
        if (buffer.length < 3) {
            throw new IllegalArgumentException("Buffer size must be of at least 3");
        }

        if (bigEndian) {
            buffer[bufferIndex + 0] = (byte) (value >> 16);
            buffer[bufferIndex + 1] = (byte) (value >> 8);
            buffer[bufferIndex + 2] = (byte) value;
        } else {
            buffer[bufferIndex + 0] = (byte) value;
            buffer[bufferIndex + 1] = (byte) (value >> 8);
            buffer[bufferIndex + 2] = (byte) (value >> 16);
        }
    }

    public static void primitiveTo32bit(boolean bigEndian, byte[] buffer, int bufferIndex, long value) {
        if (buffer.length < 4) {
            throw new IllegalArgumentException("Buffer size must be of at least 4");
        }

        if (bigEndian) {
            buffer[bufferIndex + 0] = (byte) (value >> 24);
            buffer[bufferIndex + 1] = (byte) (value >> 16);
            buffer[bufferIndex + 2] = (byte) (value >> 8);
            buffer[bufferIndex + 3] = (byte) value;
        } else {
            buffer[bufferIndex + 0] = (byte) value;
            buffer[bufferIndex + 1] = (byte) (value >> 8);
            buffer[bufferIndex + 2] = (byte) (value >> 16);
            buffer[bufferIndex + 3] = (byte) (value >> 24);
        }
    }


    public static double linearInterpolation(double start, double end, double factor) {
        return start + factor * (end - start);
    }

    public static double smoothInterpolation(double start, double end, double factor) {
        return linearInterpolation(start, end, Math.pow(factor, 2) * (3 - 2 * factor));
    }

    public static double expIncreaseInterpolation(double start, double end, double factor) {
        return linearInterpolation(start, end, Math.pow(factor, 3));
    }

    public static double expDecreaseInterpolation(double start, double end, double factor) {
        return linearInterpolation(start, end, 1 - Math.pow(1 - factor, 3));
    }

    public static double frequencyByKeyPosition(boolean midi, int position) {
        return 440 * Math.pow(2, (position - (midi ? 69 : 49)) / 12d);
    }

    public static double percentageInRange(double lowerLimit, double upperLimit, double value) {
        return Math.max(0, Math.min(((value - lowerLimit) / (upperLimit - lowerLimit)) * 100, 100));
    }

}
