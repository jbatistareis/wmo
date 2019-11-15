package com.jbatista.wmo.synthesis;

import com.jbatista.wmo.DspUtil;
import com.jbatista.wmo.DspUtil.WaveForm;

public class Modulator {

    private final Instrument instrument;

    private WaveForm waveForm;
    private double frequencyRatio;
    private double strength;

    protected Modulator(Instrument instrument, WaveForm waveForm, double frequencyRatio, double strength) {
        this.instrument = instrument;
        this.waveForm = waveForm;
        this.frequencyRatio = frequencyRatio;
        this.strength = strength;
    }

    // <editor-fold defaultstate="collapsed" desc="getters/setters">
    public WaveForm getWaveForm() {
        return waveForm;
    }

    public void setWaveForm(WaveForm waveForm) {
        this.waveForm = waveForm;
    }

    public double getFrequencyRatio() {
        return frequencyRatio;
    }

    public void setFrequencyRatio(double frequencyRatio) {
        this.frequencyRatio = frequencyRatio;
    }

    public double getStrength() {
        return strength;
    }

    public void setStrength(double strength) {
        this.strength = strength;
    }
    // </editor-fold>

    protected double getSample(double frequency, long time) {
        return DspUtil.oscillator(
                waveForm,
                instrument.getSampleRate(),
                frequency,
                frequencyRatio,
                strength,
                time);
    }

}
