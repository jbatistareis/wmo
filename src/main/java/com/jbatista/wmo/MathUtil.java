package com.jbatista.wmo;

public class MathUtil {

    public static int valueIn16bit(byte b1, byte b2) {
        return ((b1 & 0xFF) << 8) + (b2 & 0xFF);
    }

    public static int valueIn24bit(byte b1, byte b2, byte b3) {
        return ((b1 & 0xFF) << 16) + ((b2 & 0xFF) << 8) + (b3 & 0xFF);
    }

    public static int valueIn32bit(byte b1, byte b2, byte b3, byte b4) {
        return ((b1 & 0xFF) << 24) + ((b2 & 0xFF) << 16) + ((b3 & 0xFF) << 8) + (b4 & 0xFF);
    }

    public static double lerp(double start, double end, double factor) {
        return start + factor * (end - start);
    }

    public static double frequenceByPosition(int position) {
        return 69.297 * Math.pow(2, (position - 49) / 12d);
    }

}
