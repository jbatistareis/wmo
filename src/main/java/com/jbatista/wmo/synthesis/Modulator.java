package com.jbatista.wmo.synthesis;

import com.jbatista.wmo.DspUtil;
import com.jbatista.wmo.DspUtil.WaveForm;

public class Modulator {

    private final Instrument instrument;

    private WaveForm waveForm = WaveForm.SINE;
    private double frequencyRatio = 1;
    private double strength = 1;

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
        return DspUtil.modulator(
                waveForm,
                instrument.getSampleRate(),
                frequency,
                frequencyRatio,
                strength,
                time);
    }

}
