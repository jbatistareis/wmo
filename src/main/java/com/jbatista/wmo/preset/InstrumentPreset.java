package com.jbatista.wmo.preset;

import com.jbatista.wmo.filter.Filter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class InstrumentPreset {

    private String name = "  ------  ";
    private double gain = 0.01;
    private int transpose = 0;
    private int feedback = 0;
    private AlgorithmPreset algorithm = AlgorithmPreset.ALGO_4_OSC_1;
    private List<OscillatorPreset> oscillatorPresets = new ArrayList<>();
    private List<Filter> filterChain = new ArrayList<>();

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

    public AlgorithmPreset getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(AlgorithmPreset algorithm) {
        this.algorithm = algorithm;
    }

    public List<OscillatorPreset> getOscillatorPresets() {
        return oscillatorPresets;
    }

    public void setOscillatorPresets(List<OscillatorPreset> oscillatorPresets) {
        this.oscillatorPresets = oscillatorPresets;
    }

    public void addOscillatorPresets(OscillatorPreset... oscillatorPresets) {
        Collections.addAll(this.oscillatorPresets, oscillatorPresets);
    }

    public List<Filter> getFilterChain() {
        return filterChain;
    }

    public void setFilterChain(List<Filter> filterChain) {
        this.filterChain = filterChain;
    }

}
