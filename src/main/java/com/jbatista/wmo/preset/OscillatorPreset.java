package com.jbatista.wmo.preset;

import com.jbatista.wmo.KeyboardNote;
import com.jbatista.wmo.TransitionCurve;
import com.jbatista.wmo.WaveForm;

public class OscillatorPreset {

    private int id = 0;

    private WaveForm waveForm = WaveForm.SINE;

    private double frequencyRatio = 1;
    private boolean fixedFrequency = false;
    private int frequencyFine = 0;
    private int frequencyDetune = 0;
    private int outputLevel = 75;
    private int velocitySensitivity = 0;
    private int amSensitivity = 0;

    private int attackLevel = 0;
    private int decayLevel = 0;
    private int sustainLevel = 99;
    private int releaseLevel = 0;

    private int attackSpeed = 99;
    private int decaySpeed = 99;
    private int sustainSpeed = 99;
    private int releaseSpeed = 99;

    private KeyboardNote breakpointNote = KeyboardNote.A_4;
    private TransitionCurve breakpointLeftCurve = TransitionCurve.LINEAR_DECREASE;
    private TransitionCurve breakpointRightCurve = TransitionCurve.LINEAR_DECREASE;
    private int breakpointLeftDepth = 0;
    private int breakpointRightDepth = 0;
    private int rateScaling = 0;

    public OscillatorPreset(int id) {
        setId(id);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = Math.max(0, Math.min(id, 5));
    }

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
        this.frequencyRatio = Math.max(0, Math.min(frequencyRatio, 31));
    }

    public boolean isFixedFrequency() {
        return fixedFrequency;
    }

    public void setFixedFrequency(boolean fixedFrequency) {
        this.fixedFrequency = fixedFrequency;
    }

    public int getFrequencyFine() {
        return frequencyFine;
    }

    public void setFrequencyFine(int frequencyFine) {
        this.frequencyFine = Math.max(0, Math.min(frequencyFine, 99));
    }

    public int getFrequencyDetune() {
        return frequencyDetune;
    }

    public void setFrequencyDetune(int frequencyDetune) {
        this.frequencyDetune = Math.max(-7, Math.min(frequencyDetune, 7));
    }

    public int getOutputLevel() {
        return outputLevel;
    }

    public void setOutputLevel(int outputLevel) {
        this.outputLevel = Math.max(0, Math.min(outputLevel, 99));
    }

    public int getVelocitySensitivity() {
        return velocitySensitivity;
    }

    public void setVelocitySensitivity(int velocitySensitivity) {
        this.velocitySensitivity = Math.max(0, Math.min(velocitySensitivity, 7));
    }

    public int getAmSensitivity() {
        return amSensitivity;
    }

    public void setAmSensitivity(int amSensitivity) {
        this.amSensitivity = Math.max(0, Math.min(amSensitivity, 3));
    }

    public int getAttackLevel() {
        return attackLevel;
    }

    public void setAttackLevel(int attackLevel) {
        this.attackLevel = Math.max(0, Math.min(attackLevel, 99));
    }

    public int getDecayLevel() {
        return decayLevel;
    }

    public void setDecayLevel(int decayLevel) {
        this.decayLevel = Math.max(0, Math.min(decayLevel, 99));
    }

    public int getSustainLevel() {
        return sustainLevel;
    }

    public void setSustainLevel(int sustainLevel) {
        this.sustainLevel = Math.max(0, Math.min(sustainLevel, 99));
    }

    public int getReleaseLevel() {
        return releaseLevel;
    }

    public void setReleaseLevel(int releaseLevel) {
        this.releaseLevel = Math.max(0, Math.min(releaseLevel, 99));
    }

    public int getAttackSpeed() {
        return attackSpeed;
    }

    public void setAttackSpeed(int attackSpeed) {
        this.attackSpeed = Math.max(0, Math.min(attackSpeed, 99));
    }

    public int getDecaySpeed() {
        return decaySpeed;
    }

    public void setDecaySpeed(int decaySpeed) {
        this.decaySpeed = Math.max(0, Math.min(decaySpeed, 99));
    }

    public int getSustainSpeed() {
        return sustainSpeed;
    }

    public void setSustainSpeed(int sustainSpeed) {
        this.sustainSpeed = Math.max(0, Math.min(sustainSpeed, 99));
    }

    public int getReleaseSpeed() {
        return releaseSpeed;
    }

    public void setReleaseSpeed(int releaseSpeed) {
        this.releaseSpeed = Math.max(0, Math.min(releaseSpeed, 99));
    }

    public KeyboardNote getBreakpointNote() {
        return breakpointNote;
    }

    public void setBreakpointNote(KeyboardNote breakpointNote) {
        if (breakpointNote.getId() < 21) {
            this.breakpointNote = KeyboardNote.A_MINUS_1;
        } else if (breakpointNote.getId() > 120) {
            this.breakpointNote = KeyboardNote.C_8;
        } else {
            this.breakpointNote = breakpointNote;
        }
    }

    public TransitionCurve getBreakpointLeftCurve() {
        return breakpointLeftCurve;
    }

    public void setBreakpointLeftCurve(TransitionCurve breakpointLeftCurve) {
        this.breakpointLeftCurve = breakpointLeftCurve;
    }

    public TransitionCurve getBreakpointRightCurve() {
        return breakpointRightCurve;
    }

    public void setBreakpointRightCurve(TransitionCurve breakpointRightCurve) {
        this.breakpointRightCurve = breakpointRightCurve;
    }

    public int getBreakpointLeftDepth() {
        return breakpointLeftDepth;
    }

    public void setBreakpointLeftDepth(int breakpointLeftDepth) {
        this.breakpointLeftDepth = Math.max(0, Math.min(breakpointLeftDepth, 99));
    }

    public int getBreakpointRightDepth() {
        return breakpointRightDepth;
    }

    public void setBreakpointRightDepth(int breakpointRightDepth) {
        this.breakpointRightDepth = Math.max(0, Math.min(breakpointRightDepth, 99));
    }

    public int getRateScaling() {
        return rateScaling;
    }

    public void setRateScaling(int rateScaling) {
        this.rateScaling = Math.max(0, Math.min(rateScaling, 7));
    }

}
