package com.jbatista.wmo.components.play;

import com.jbatista.wmo.DspUtil;
import com.jbatista.wmo.MathUtil;
import com.jbatista.wmo.WaveForm;

public class Modulator {

    private final Instrument instrument;

    private WaveForm waveForm = WaveForm.SINE;
    private double strength = 1.0;
    private double frequency = 440;
    private double phaseL = 0;
    private double phaseR = 0;

    private final double[] sample = new double[2];
    private double[] wave;

    protected Modulator(Instrument instrument) {
        this.instrument = instrument;
        setSample();
    }

    // <editor-fold defaultstate="collapsed" desc="getters/setters">
    public WaveForm getWaveForm() {
        return waveForm;
    }

    public void setWaveForm(WaveForm waveForm) {
        setSample();
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
        setSample();
        this.frequency = frequency;
    }

    public double getPhaseL() {
        return phaseL;
    }

    public void setPhaseL(double phaseL) {
        this.phaseL = Math.max(0, Math.min(phaseL, 1));
    }

    public double getPhaseR() {
        return phaseR;
    }

    public void setPhaseR(double phaseR) {
        this.phaseR = Math.max(0, Math.min(phaseR, 1));
    }
    // </editor-fold>

    protected double[] calculate(long time) {
        sample[0] = strength * (wave[(int) ((time + MathUtil.PI_T2 * instrument.getPhaseL() * instrument.getSampleRate()) % wave.length)]);
        sample[1] = strength * (wave[(int) ((time + MathUtil.PI_T2 * instrument.getPhaseR() * instrument.getSampleRate()) % wave.length)]);

        return sample;
    }

    private void setSample() {
        this.wave = new double[(int) (instrument.getSampleRate() / frequency)];
        for (int i = 0; i < wave.length; i++) {
            wave[i] = DspUtil.oscillator(
                    waveForm,
                    instrument.getSampleRate(),
                    frequency,
                    0,
                    0,
                    i);
        }
    }

}
