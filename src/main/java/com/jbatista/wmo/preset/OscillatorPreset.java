package com.jbatista.wmo.preset;

import com.jbatista.wmo.KeyboardNote;
import com.jbatista.wmo.TransitionCurve;
import com.jbatista.wmo.WaveForm;

public class OscillatorPreset {

    private int id = 0;

    private double frequencyRatio = 1;
    private boolean fixedFrequency = false;
    private int frequencyFine = 0;
    private int frequencyDetune = 0;
    private int outputLevel = 75;
    private int feedback = 0;
    private WaveForm waveForm = WaveForm.SINE;

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

    public OscillatorPreset(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getFrequencyRatio() {
        return frequencyRatio;
    }

    public void setFrequencyRatio(double frequencyRatio) {
        this.frequencyRatio = frequencyRatio;
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
        this.frequencyFine = frequencyFine;
    }

    public int getFrequencyDetune() {
        return frequencyDetune;
    }

    public void setFrequencyDetune(int frequencyDetune) {
        this.frequencyDetune = frequencyDetune;
    }

    public int getOutputLevel() {
        return outputLevel;
    }

    public void setOutputLevel(int outputLevel) {
        this.outputLevel = outputLevel;
    }

    public int getFeedback() {
        return feedback;
    }

    public void setFeedback(int feedback) {
        this.feedback = feedback;
    }

    public WaveForm getWaveForm() {
        return waveForm;
    }

    public void setWaveForm(WaveForm waveForm) {
        this.waveForm = waveForm;
    }

    public int getAttackLevel() {
        return attackLevel;
    }

    public void setAttackLevel(int attackLevel) {
        this.attackLevel = attackLevel;
    }

    public int getDecayLevel() {
        return decayLevel;
    }

    public void setDecayLevel(int decayLevel) {
        this.decayLevel = decayLevel;
    }

    public int getSustainLevel() {
        return sustainLevel;
    }

    public void setSustainLevel(int sustainLevel) {
        this.sustainLevel = sustainLevel;
    }

    public int getReleaseLevel() {
        return releaseLevel;
    }

    public void setReleaseLevel(int releaseLevel) {
        this.releaseLevel = releaseLevel;
    }

    public int getAttackSpeed() {
        return attackSpeed;
    }

    public void setAttackSpeed(int attackSpeed) {
        this.attackSpeed = attackSpeed;
    }

    public int getDecaySpeed() {
        return decaySpeed;
    }

    public void setDecaySpeed(int decaySpeed) {
        this.decaySpeed = decaySpeed;
    }

    public int getSustainSpeed() {
        return sustainSpeed;
    }

    public void setSustainSpeed(int sustainSpeed) {
        this.sustainSpeed = sustainSpeed;
    }

    public int getReleaseSpeed() {
        return releaseSpeed;
    }

    public void setReleaseSpeed(int releaseSpeed) {
        this.releaseSpeed = releaseSpeed;
    }

    public KeyboardNote getBreakpointNote() {
        return breakpointNote;
    }

    public void setBreakpointNote(KeyboardNote breakpointNote) {
        this.breakpointNote = breakpointNote;
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
        this.breakpointLeftDepth = breakpointLeftDepth;
    }

    public int getBreakpointRightDepth() {
        return breakpointRightDepth;
    }

    public void setBreakpointRightDepth(int breakpointRightDepth) {
        this.breakpointRightDepth = breakpointRightDepth;
    }

}
