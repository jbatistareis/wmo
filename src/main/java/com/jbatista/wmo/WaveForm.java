package com.jbatista.wmo;

public enum WaveForm {
    TRIANGLE(0), SAWTOOTH_UP(1), SAWTOOTH_DOWN(2), SQUARE(3), SINE(4), SAMPLE_AND_HOLD(5), WHITE_NOISE(6);

    private int id;

    WaveForm(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

}
