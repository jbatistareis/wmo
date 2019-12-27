package com.jbatista.wmo;

import java.util.Random;

/*
    Interpolation algorithms based on Lewis Van Winkle's 2009 blog post "Simple Interpolation" [https://codeplea.com/simple-interpolation]
*/

public class MathUtil {

    public static final double TAU = Math.PI * 2;
    public static final double PI_D2 = Math.PI / 2;
    public static final Random RANDOM = new Random();

    private static final int SIGNED_24BIT_MAX = 8388607;

    public static int primitiveFrom16bit(boolean bigEndian, boolean signed, byte b1, byte b2) {
        return (bigEndian
                ? ((b1 & 0xFF) << 8) + (b2 & 0xFF)
                : (b2 & 0xFF) + ((b1 & 0xFF) << 8))
                - (signed ? Short.MAX_VALUE : 0);
    }

    public static long primitiveFrom24bit(boolean bigEndian, boolean signed, byte b1, byte b2, byte b3) {
        return (bigEndian
                ? ((b1 & 0xFF) << 16) + ((b2 & 0xFF) << 8) + (b3 & 0xFF)
                : (b3 & 0xFF) + ((b2 & 0xFF) << 8) + ((b1 & 0xFF) << 16))
                - (signed ? SIGNED_24BIT_MAX : 0);
    }

    public static long primitiveFrom32bit(boolean bigEndian, boolean signed, byte b1, byte b2, byte b3, byte b4) {
        return bigEndian
                ? ((b1 & 0xFF) << 24) + ((b2 & 0xFF) << 16) + ((b3 & 0xFF) << 8) + (b4 & 0xFF)
                : (b4 & 0xFF) + ((b3 & 0xFF) << 8) + ((b2 & 0xFF) << 16) + ((b1 & 0xFF) << 24)
                - (signed ? Integer.MAX_VALUE : 0);
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

}
