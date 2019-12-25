package com.jbatista.wmo.filters;

public class Distortion implements Filter {

    private double level = 0;

    public double getLevel() {
        return level;
    }

    public void setLevel(double level) {
        this.level = Math.max(0, Math.min(level, 5));
    }

    @Override
    public double apply(double sample) {
        return (sample / Math.abs(sample)) * (1 - Math.pow(Math.E, level * (Math.pow(sample, 2) / Math.abs(sample))));
    }

}
