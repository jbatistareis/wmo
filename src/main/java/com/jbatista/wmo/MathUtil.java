package com.jbatista.wmo;

import java.util.Random;

public class MathUtil {

    public static final double TAU = Math.PI * 2;
    public static final double PI_D2 = Math.PI / 2;
    public static final Random RANDOM = new Random();

    private static final int SIGNED_24BIT_MAX = 8388607;

    public static int valueFrom16bit(boolean bigEndian, boolean signed, byte b1, byte b2) {
        return (bigEndian
                ? ((b1 & 0xFF) << 8) + (b2 & 0xFF)
                : (b2 & 0xFF) + ((b1 & 0xFF) << 8))
                - (signed ? Short.MAX_VALUE : 0);
    }

    public static long valueFrom24bit(boolean bigEndian, boolean signed, byte b1, byte b2, byte b3) {
        return (bigEndian
                ? ((b1 & 0xFF) << 16) + ((b2 & 0xFF) << 8) + (b3 & 0xFF)
                : (b3 & 0xFF) + ((b2 & 0xFF) << 8) + ((b1 & 0xFF) << 16))
                - (signed ? SIGNED_24BIT_MAX : 0);
    }

    public static long valueFrom32bit(boolean bigEndian, boolean signed, byte b1, byte b2, byte b3, byte b4) {
        return bigEndian
                ? ((b1 & 0xFF) << 24) + ((b2 & 0xFF) << 16) + ((b3 & 0xFF) << 8) + (b4 & 0xFF)
                : (b4 & 0xFF) + ((b3 & 0xFF) << 8) + ((b2 & 0xFF) << 16) + ((b1 & 0xFF) << 24)
                - (signed ? Integer.MAX_VALUE : 0);
    }

    public static void valueTo16bit(boolean bigEndian, byte[] buffer, int bufferIndex, int value) {
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

    public static void valueTo24bit(boolean bigEndian, byte[] buffer, int bufferIndex, long value) {
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

    public static void valueTo32bit(boolean bigEndian, byte[] buffer, int bufferIndex, long value) {
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


    public static double lerp(double start, double end, double factor) {
        return (1 - factor) * start + factor * end;
    }

    public static double frequencyByKeyPosition(boolean midi, int position) {
        return 440 * Math.pow(2, (position - (midi ? 69 : 49)) / 12d);
    }

}
