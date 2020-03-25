package com.jbatista.wmo.preset;

import com.jbatista.wmo.WaveForm;

public class InstrumentPreset {

    private String name = "  ------  ";
    private double gain = 0.01;
    private int transpose = 0;
    private int feedback = 0;

    private int pitchAttackLevel = 50;
    private int pitchDecayLevel = 50;
    private int pitchSustainLevel = 50;
    private int pitchReleaseLevel = 50;

    private int pitchAttackSpeed = 99;
    private int pitchDecaySpeed = 99;
    private int pitchSustainSpeed = 99;
    private int pitchReleaseSpeed = 99;

    private boolean oscillatorKeySync = true;
    private boolean lfoKeySync = true;

    private int lfoSpeed = 35;
    private int lfoDelay = 0;
    private int lfoPmDepth = 0;
    private int lfoAmDepth = 0;

    private int lfoPModeSensitivity = 3;
    private WaveForm lfoWave = WaveForm.TRIANGLE;

    private AlgorithmPreset algorithm = AlgorithmPreset.ALGO_4_OSC_1;
    private OscillatorPreset[] oscillatorPresets = new OscillatorPreset[6];

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getGain() {
        return gain;
    }

    public void setGain(double gain) {
        this.gain = gain;
    }

    public int getTranspose() {
        return transpose;
    }

    public void setTranspose(int transpose) {
        this.transpose = transpose;
    }

    public int getFeedback() {
        return feedback;
    }

    public void setFeedback(int feedback) {
        this.feedback = feedback;
    }

    public int getPitchAttackLevel() {
        return pitchAttackLevel;
    }

    public void setPitchAttackLevel(int pitchAttackLevel) {
        this.pitchAttackLevel = pitchAttackLevel;
    }

    public int getPitchDecayLevel() {
        return pitchDecayLevel;
    }

    public void setPitchDecayLevel(int pitchDecayLevel) {
        this.pitchDecayLevel = pitchDecayLevel;
    }

    public int getPitchSustainLevel() {
        return pitchSustainLevel;
    }

    public void setPitchSustainLevel(int pitchSustainLevel) {
        this.pitchSustainLevel = pitchSustainLevel;
    }

    public int getPitchReleaseLevel() {
        return pitchReleaseLevel;
    }

    public void setPitchReleaseLevel(int pitchReleaseLevel) {
        this.pitchReleaseLevel = pitchReleaseLevel;
    }

    public int getPitchAttackSpeed() {
        return pitchAttackSpeed;
    }

    public void setPitchAttackSpeed(int pitchAttackSpeed) {
        this.pitchAttackSpeed = pitchAttackSpeed;
    }

    public int getPitchDecaySpeed() {
        return pitchDecaySpeed;
    }

    public void setPitchDecaySpeed(int pitchDecaySpeed) {
        this.pitchDecaySpeed = pitchDecaySpeed;
    }

    public int getPitchSustainSpeed() {
        return pitchSustainSpeed;
    }

    public void setPitchSustainSpeed(int pitchSustainSpeed) {
        this.pitchSustainSpeed = pitchSustainSpeed;
    }

    public int getPitchReleaseSpeed() {
        return pitchReleaseSpeed;
    }

    public void setPitchReleaseSpeed(int pitchReleaseSpeed) {
        this.pitchReleaseSpeed = pitchReleaseSpeed;
    }

    public boolean isOscillatorKeySync() {
        return oscillatorKeySync;
    }

    public void setOscillatorKeySync(boolean oscillatorKeySync) {
        this.oscillatorKeySync = oscillatorKeySync;
    }

    public boolean isLfoKeySync() {
        return lfoKeySync;
    }

    public void setLfoKeySync(boolean lfoKeySync) {
        this.lfoKeySync = lfoKeySync;
    }

    public int getLfoSpeed() {
        return lfoSpeed;
    }

    public void setLfoSpeed(int lfoSpeed) {
        this.lfoSpeed = lfoSpeed;
    }

    public int getLfoDelay() {
        return lfoDelay;
    }

    public void setLfoDelay(int lfoDelay) {
        this.lfoDelay = lfoDelay;
    }

    public int getLfoPmDepth() {
        return lfoPmDepth;
    }

    public void setLfoPmDepth(int lfoPmDepth) {
        this.lfoPmDepth = lfoPmDepth;
    }

    public int getLfoAmDepth() {
        return lfoAmDepth;
    }

    public void setLfoAmDepth(int lfoAmDepth) {
        this.lfoAmDepth = lfoAmDepth;
    }

    public int getLfoPModeSensitivity() {
        return lfoPModeSensitivity;
    }

    public void setLfoPModeSensitivity(int lfoPModeSensitivity) {
        this.lfoPModeSensitivity = lfoPModeSensitivity;
    }

    public WaveForm getLfoWave() {
        return lfoWave;
    }

    public void setLfoWave(WaveForm lfoWave) {
        this.lfoWave = lfoWave;
    }

    public AlgorithmPreset getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(AlgorithmPreset algorithm) {
        this.algorithm = algorithm;
    }

    public OscillatorPreset[] getOscillatorPresets() {
        return oscillatorPresets;
    }

    public void addOscillatorPreset(int id, OscillatorPreset oscillatorPreset) {
        this.oscillatorPresets[id] = oscillatorPreset;
    }

}
