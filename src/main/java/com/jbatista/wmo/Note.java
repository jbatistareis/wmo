package com.jbatista.wmo;

public enum Note {

    A4(440);

    private double frequency;

    private Note(double frequency) {
        this.frequency = frequency;
    }

    public double getFrequency() {
        return frequency;
    }

}
