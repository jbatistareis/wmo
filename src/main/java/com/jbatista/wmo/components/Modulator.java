package com.jbatista.wmo.components;

import com.jbatista.wmo.MathUtil;
import com.jbatista.wmo.WaveForm;

public class Modulator {

    private final Instrument instrument;

    private WaveForm waveForm = WaveForm.SINE;
    private double strength = 1.0;
    private double frequency = 440;

    protected Modulator(Instrument instrument) {
        this.instrument = instrument;
    }

    // <editor-fold defaultstate="collapsed" desc="getters/setters">
    public WaveForm getWaveForm() {
        return waveForm;
    }

    public void setWaveForm(WaveForm waveForm) {
        this.waveForm = waveForm;
    }

    public double getStrength() {
        return strength;
    }

    public void setStrength(double strength) {
        this.strength = strength;
    }

    public double getFrequency() {
        return frequency;
    }

    public void setFrequency(double frequency) {
        this.frequency = frequency;
    }
    // </editor-fold>

    protected double calculate(long time) {
        return strength * MathUtil.oscillator(waveForm, instrument.getSampleRate(), frequency, 0, time);
    }

}
