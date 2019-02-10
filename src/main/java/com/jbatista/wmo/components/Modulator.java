package com.jbatista.wmo.components;

import com.jbatista.wmo.MathUtil;
import com.jbatista.wmo.WaveForm;

public class Modulator {

    private final Instrument instrument;

    private WaveForm waveForm = WaveForm.SINE;
    private double strength = 1.0;
    private double frequency = 440;
    private double phaseL = 0;
    private double phaseR = 0;

    private double effectivePhaseL = 0;
    private double effectivePhaseR = 0;

    private final double[] frame = new double[2];

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

    public double getPhaseL() {
        return phaseL;
    }

    public void setPhaseL(double phaseL) {
        this.phaseL = Math.max(0, Math.min(phaseL, 1));
        this.effectivePhaseL = MathUtil.lerp(0, MathUtil.PIx2, this.phaseL);
    }

    public double getPhaseR() {
        return phaseR;
    }

    public void setPhaseR(double phaseR) {
        this.phaseR = Math.max(0, Math.min(phaseR, 1));
        this.effectivePhaseR = MathUtil.lerp(0, MathUtil.PIx2, this.phaseR);
    }
    // </editor-fold>

    protected double[] calculate(long time) {
        frame[0] = strength * MathUtil.oscillator(waveForm, instrument.getSampleRate(), frequency, effectivePhaseL, 0, time);
        frame[1] = strength * MathUtil.oscillator(waveForm, instrument.getSampleRate(), frequency, effectivePhaseR, 0, time);

        return frame;
    }

}
