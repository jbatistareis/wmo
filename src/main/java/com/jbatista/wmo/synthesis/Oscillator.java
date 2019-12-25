package com.jbatista.wmo.synthesis;

import com.jbatista.wmo.DspUtil;
import com.jbatista.wmo.EnvelopeState;
import com.jbatista.wmo.WaveForm;
import com.jbatista.wmo.preset.OscillatorPreset;

import java.util.LinkedList;

public class Oscillator {
    private final int id;
    private final int hash;

    private final double[] sineFrequency = new double[144];

    // I/O
    private final EnvelopeGenerator envelopeGenerator = new EnvelopeGenerator();
    private final LinkedList<Oscillator> modulators = new LinkedList<>();

    // parameters
    private WaveForm waveForm = WaveForm.SINE;
    private int outputLevel = 75;
    private int feedback = 0;
    private double frequencyRatio = 1;

    Oscillator(int id) {
        this.id = id;
        this.hash = ((Integer) this.id).hashCode();
    }

    // <editor-fold defaultstate="collapsed" desc="getters/setters">
    int getId() {
        return id;
    }

    public WaveForm getWaveForm() {
        return waveForm;
    }

    public void setWaveForm(WaveForm waveForm) {
        this.waveForm = waveForm;
    }

    public int getOutputLevel() {
        return outputLevel;
    }

    public void setOutputLevel(int outputLevel) {
        this.outputLevel = Math.max(0, Math.min(outputLevel, 99));
    }

    public int getFeedback() {
        return feedback;
    }

    public void setFeedback(int feedback) {
        this.feedback = Math.max(-7, Math.min(feedback, 7));
    }

    public double getFrequencyRatio() {
        return frequencyRatio;
    }

    public void setFrequencyRatio(double frequencyRatio) {
        this.frequencyRatio = Math.max(0, Math.min(frequencyRatio, 32));
    }
    // </editor-fold>

    double getFrame(int keyId, long time) {
        double modulatorSample = 0;
        double feedbackSample = 0;
        envelopeGenerator.defineEnvelopeAmplitude(keyId, time);

        if (!modulators.isEmpty()) {
            for (Oscillator oscillator : modulators) {
                modulatorSample += oscillator.getFrame(keyId, time);
            }

            modulatorSample /= modulators.size();
        }

        /*
            instead of self modulation, feedback is done with additive synthesis
            positive values produces a sawtooth, negative ones produces a square
        */
        if (feedback > 0) {
            feedbackSample = Tables.feedbackOutputLevels[feedback]
                    * ((produceSample(sineFrequency[keyId] * 2, 0, time) / 2)
                    + (produceSample(sineFrequency[keyId] * 3, 0, time) / 3)
                    + (produceSample(sineFrequency[keyId] * 4, 0, time) / 4)
                    + (produceSample(sineFrequency[keyId] * 5, 0, time) / 5)
                    + (produceSample(sineFrequency[keyId] * 6, 0, time) / 6));
        } else if (feedback < 0) {
            feedbackSample = Tables.feedbackOutputLevels[-feedback]
                    * ((produceSample(sineFrequency[keyId] * 3, 0, time) / 3)
                    + (produceSample(sineFrequency[keyId] * 5, 0, time) / 5)
                    + (produceSample(sineFrequency[keyId] * 7, 0, time) / 7)
                    + (produceSample(sineFrequency[keyId] * 9, 0, time) / 9)
                    + (produceSample(sineFrequency[keyId] * 11, 0, time) / 11));
        }

        envelopeGenerator.setPreviousTime(keyId, time);
        return Tables.oscillatorOutputLevels[outputLevel]
                * envelopeGenerator.getEnvelopeAmplitude(keyId)
                * (produceSample(sineFrequency[keyId], modulatorSample, time) + feedbackSample);
    }

    boolean isActive(int keyId) {
        return envelopeGenerator.getEnvelopeState(keyId) != EnvelopeState.IDLE;
    }

    private double produceSample(double frequency, double modulation, long time) {
        return DspUtil.oscillator(
                waveForm,
                frequency,
                modulation,
                0,
                time);
    }

    void start(int keyId, double frequency) {
        envelopeGenerator.setPreviousTime(keyId, -1);

        for (Oscillator oscillator : modulators) {
            oscillator.start(keyId, frequency);
        }

        envelopeGenerator.calculateEnvelope(
                0,
                envelopeGenerator.getAttackDuration(),
                envelopeGenerator.getEnvelopeAmplitude(keyId),
                envelopeGenerator.getAttackAmplitude());

        envelopeGenerator.setEnvelopePosition(keyId, 0);

        sineFrequency[keyId] = (frequency * frequencyRatio) / Instrument.getSampleRate();
        envelopeGenerator.setEnvelopeState(keyId, EnvelopeState.ATTACK);
    }

    void stop(int keyId) {
        for (Oscillator oscillator : modulators) {
            oscillator.stop(keyId);
        }

        envelopeGenerator.calculateEnvelope(
                3,
                envelopeGenerator.getReleaseDuration(),
                envelopeGenerator.getEnvelopeAmplitude(keyId),
                envelopeGenerator.getReleaseAmplitude());

        envelopeGenerator.setEnvelopeState(keyId, EnvelopeState.RELEASE);
    }

    public boolean addModulator(Oscillator modulator) {
        if (modulators.contains(modulator)) {
            return false;
        }

        return modulators.add(modulator);
    }

    public void loadOscillatorPreset(OscillatorPreset oscillatorPreset) {
        setFrequencyRatio(oscillatorPreset.getFrequencyRatio());
        setOutputLevel(oscillatorPreset.getOutputLevel());
        setFeedback(oscillatorPreset.getFeedback());
        setWaveForm(oscillatorPreset.getWaveForm());

        envelopeGenerator.setAttackAmplitude(oscillatorPreset.getAttackAmplitude());
        envelopeGenerator.setDecayAmplitude(oscillatorPreset.getDecayAmplitude());
        envelopeGenerator.setSustainAmplitude(oscillatorPreset.getSustainAmplitude());
        envelopeGenerator.setReleaseAmplitude(oscillatorPreset.getReleaseAmplitude());

        envelopeGenerator.setAttackDuration(oscillatorPreset.getAttackDuration());
        envelopeGenerator.setDecayDuration(oscillatorPreset.getDecayDuration());
        envelopeGenerator.setSustainDuration(oscillatorPreset.getSustainDuration());
        envelopeGenerator.setReleaseDuration(oscillatorPreset.getReleaseDuration());

        envelopeGenerator.setAttackCurve(oscillatorPreset.getAttackCurve());
        envelopeGenerator.setDecayCurve(oscillatorPreset.getDecayCurve());
        envelopeGenerator.setSustainCurve(oscillatorPreset.getSustainCurve());
        envelopeGenerator.setReleaseCurve(oscillatorPreset.getReleaseCurve());
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof Oscillator) && (obj.hashCode() == this.hash);
    }

    @Override
    public int hashCode() {
        return hash;
    }
}
