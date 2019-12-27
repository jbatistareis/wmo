package com.jbatista.wmo.preset;

import com.jbatista.wmo.EnvelopeCurve;
import com.jbatista.wmo.WaveForm;

public class OscillatorPreset {

    private int id = 0;

    private double frequencyRatio = 1;
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

    private EnvelopeCurve attackCurve = EnvelopeCurve.LINEAR;
    private EnvelopeCurve decayCurve = EnvelopeCurve.LINEAR;
    private EnvelopeCurve sustainCurve = EnvelopeCurve.LINEAR;
    private EnvelopeCurve releaseCurve = EnvelopeCurve.LINEAR;

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

    public EnvelopeCurve getAttackCurve() {
        return attackCurve;
    }

    public void setAttackCurve(EnvelopeCurve attackCurve) {
        this.attackCurve = attackCurve;
    }

    public EnvelopeCurve getDecayCurve() {
        return decayCurve;
    }

    public void setDecayCurve(EnvelopeCurve decayCurve) {
        this.decayCurve = decayCurve;
    }

    public EnvelopeCurve getSustainCurve() {
        return sustainCurve;
    }

    public void setSustainCurve(EnvelopeCurve sustainCurve) {
        this.sustainCurve = sustainCurve;
    }

    public EnvelopeCurve getReleaseCurve() {
        return releaseCurve;
    }

    public void setReleaseCurve(EnvelopeCurve releaseCurve) {
        this.releaseCurve = releaseCurve;
    }

}
