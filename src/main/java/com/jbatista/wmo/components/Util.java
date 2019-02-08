package com.jbatista.wmo.components;

public class Util {

    protected static double lerp(double start, double end, double factor) {
        return start + factor * (end - start);
    }

}
