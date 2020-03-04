package com.jbatista.wmo.preset;

import com.jbatista.wmo.filter.Filter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class InstrumentPreset {

    private String name = "Default preset";
    private double gain = 0.5;
    private int[][] algorithm = new int[][]{{0}, {}};
    private List<OscillatorPreset> oscillatorPresets = new ArrayList<>();
    private List<Filter> filterChain = new ArrayList<>();

    public InstrumentPreset() {
        oscillatorPresets.add(new OscillatorPreset());
    }

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

    public int[][] getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(int[][] algorithm) {
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
