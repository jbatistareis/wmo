package com.jbatista.wmo.preset;

import com.jbatista.wmo.EnvelopeCurve;
import com.jbatista.wmo.WaveForm;

public class OscillatorPreset {

    private int id = 0;

    private double frequencyRatio = 1;
    private int outputLevel = 75;
    private int feedback = 0;
    private WaveForm waveForm = WaveForm.SINE;

    private double attackAmplitude = 0;
    private double decayAmplitude = 0;
    private double sustainAmplitude = 1;
    private double releaseAmplitude = 0;

    private double attackDuration = 0;
    private double decayDuration = 0;
    private double sustainDuration = 0;
    private double releaseDuration = 0;

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

    public double getAttackAmplitude() {
        return attackAmplitude;
    }

    public void setAttackAmplitude(double attackAmplitude) {
        this.attackAmplitude = attackAmplitude;
    }

    public double getDecayAmplitude() {
        return decayAmplitude;
    }

    public void setDecayAmplitude(double decayAmplitude) {
        this.decayAmplitude = decayAmplitude;
    }

    public double getSustainAmplitude() {
        return sustainAmplitude;
    }

    public void setSustainAmplitude(double sustainAmplitude) {
        this.sustainAmplitude = sustainAmplitude;
    }

    public double getReleaseAmplitude() {
        return releaseAmplitude;
    }

    public void setReleaseAmplitude(double releaseAmplitude) {
        this.releaseAmplitude = releaseAmplitude;
    }

    public double getAttackDuration() {
        return attackDuration;
    }

    public void setAttackDuration(double attackDuration) {
        this.attackDuration = attackDuration;
    }

    public double getDecayDuration() {
        return decayDuration;
    }

    public void setDecayDuration(double decayDuration) {
        this.decayDuration = decayDuration;
    }

    public double getSustainDuration() {
        return sustainDuration;
    }

    public void setSustainDuration(double sustainDuration) {
        this.sustainDuration = sustainDuration;
    }

    public double getReleaseDuration() {
        return releaseDuration;
    }

    public void setReleaseDuration(double releaseDuration) {
        this.releaseDuration = releaseDuration;
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
