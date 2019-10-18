package com.jbatista.wmo;

import java.util.Random;

public class MathUtil {

    public static final double TAU = Math.PI * 2;
    public static final double PI_D2 = Math.PI / 2;
    public static final Random RANDOM = new Random();

    public static int valueFrom16bit(boolean bigEndian, byte b1, byte b2) {
        return bigEndian
                ? ((b1 & 0xFF) << 8) + (b2 & 0xFF)
                : (b2 & 0xFF) + ((b1 & 0xFF) << 8);
    }

    public static int valueFrom24bit(boolean bigEndian, byte b1, byte b2, byte b3) {
        return bigEndian
                ? ((b1 & 0xFF) << 16) + ((b2 & 0xFF) << 8) + (b3 & 0xFF)
                : (b3 & 0xFF) + ((b2 & 0xFF) << 8) + ((b1 & 0xFF) << 16);
    }

    public static int valueFrom32bit(boolean bigEndian, byte b1, byte b2, byte b3, byte b4) {
        return bigEndian
                ? ((b1 & 0xFF) << 24) + ((b2 & 0xFF) << 16) + ((b3 & 0xFF) << 8) + (b4 & 0xFF)
                : (b4 & 0xFF) + ((b3 & 0xFF) << 8) + ((b2 & 0xFF) << 16) + ((b1 & 0xFF) << 24);
    }

    public static double lerp(double start, double end, double factor) {
        return start + factor * (end - start);
    }

    public static double frequenceByPosition(int position) {
        return 69.297 * Math.pow(2, (position - 49) / 12d);
    }

}
