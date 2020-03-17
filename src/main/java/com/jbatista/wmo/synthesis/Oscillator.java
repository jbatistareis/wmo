package com.jbatista.wmo.synthesis;

import com.jbatista.wmo.WaveForm;
import com.jbatista.wmo.preset.OscillatorPreset;
import com.jbatista.wmo.util.Dsp;
import com.jbatista.wmo.util.MathFunctions;

public class Oscillator {
    private final int id;
    private final double sampleRate;

    private final double[] sineFrequency = new double[132];
    private static final double[] fixedFrequencies = new double[]{1d, 10d, 100d, 1000d};

    // I/O
    private Algorithm algorithm;

    // parameters
    private WaveForm waveForm = WaveForm.SINE;
    private int outputLevel = 75;
    private int feedback = 0;
    private double frequencyRatio = 1;
    private boolean fixedFrequency = false;
    private int frequencyFine = 0;
    private int frequencyDetune = 0;
    private final EnvelopeGenerator envelopeGenerator;
    private final Breakpoint breakpoint = new Breakpoint();
    private double breakpointOffset = 1;

    private double modulatorSample;
    private double feedbackSample;

    Oscillator(int id, double sampleRate, Algorithm algorithm) {
        this.id = id;
        this.sampleRate = sampleRate;
        this.envelopeGenerator = new EnvelopeGenerator(sampleRate);
        this.algorithm = algorithm;
    }

    // <editor-fold defaultstate="collapsed" desc="getters/setters">
    public int getId() {
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

    public EnvelopeGenerator getEnvelopeGenerator() {
        return envelopeGenerator;
    }

    public Breakpoint getBreakpoint() {
        return breakpoint;
    }
    // </editor-fold>

    double getFrame(int keyId, double pitchOffset, long time) {
        modulatorSample = 0;
        feedbackSample = 0;

        for (int i = 1; i < algorithm.pattern.length; i++) {
            if (algorithm.pattern[i][0] == id) {
                modulatorSample += algorithm.oscillators[algorithm.pattern[i][1]].getFrame(keyId, pitchOffset, time);
            }
        }

        /*
            instead of self modulation, feedback is done with additive synthesis
            positive values produces a sawtooth, negative ones produces a square
        */
        if (feedback > 0) {
            feedbackSample = Tables.FEEDBACK_OUTPUT_LEVELS[feedback]
                    * ((produceSample(pitchOffset * sineFrequency[keyId] * 2, 0, time) / 2)
                    + (produceSample(pitchOffset * sineFrequency[keyId] * 3, 0, time) / 3)
                    + (produceSample(pitchOffset * sineFrequency[keyId] * 4, 0, time) / 4)
                    + (produceSample(pitchOffset * sineFrequency[keyId] * 5, 0, time) / 5)
                    + (produceSample(pitchOffset * sineFrequency[keyId] * 6, 0, time) / 6));
        } else if (feedback < 0) {
            feedbackSample = Tables.FEEDBACK_OUTPUT_LEVELS[-feedback]
                    * ((produceSample(pitchOffset * sineFrequency[keyId] * 3, 0, time) / 3)
                    + (produceSample(pitchOffset * sineFrequency[keyId] * 5, 0, time) / 5)
                    + (produceSample(pitchOffset * sineFrequency[keyId] * 7, 0, time) / 7)
                    + (produceSample(pitchOffset * sineFrequency[keyId] * 9, 0, time) / 9)
                    + (produceSample(pitchOffset * sineFrequency[keyId] * 11, 0, time) / 11));
        }

        envelopeGenerator.defineEnvelopeAmplitude(keyId, time);
        envelopeGenerator.setPreviousTime(keyId, time);

        return Tables.OSCILLATOR_OUTPUT_LEVELS[outputLevel]
                * breakpointOffset
                * envelopeGenerator.getEnvelopeAmplitude(keyId)
                * (produceSample(pitchOffset * sineFrequency[keyId], modulatorSample, time) + feedbackSample);
    }

    private double produceSample(double frequency, double modulation, long time) {
        return Dsp.oscillator(
                waveForm,
                frequency,
                modulation,
                0,
                time);
    }

    // fixed frequency calculation from [https://github.com/smbolton/hexter/blob/737dbb04c407184fae0e203c1d73be8ad3fd55ba/src/dx7_voice.c#L782]
    void start(int keyId, double frequency) {
        envelopeGenerator.reset(keyId);

        for (int i = 1; i < algorithm.pattern.length; i++) {
            if (algorithm.pattern[i][0] == id) {
                algorithm.oscillators[algorithm.pattern[i][1]].start(keyId, frequency);
            }
        }

        sineFrequency[keyId] = ((fixedFrequency
                ? Math.exp(MathFunctions.NATURAL_LOG10 * (((int) frequencyRatio & 3) + frequencyFine / 100.0))
                : frequency * ((frequencyRatio == 0) ? 0.5 : frequencyRatio) * Tables.FREQUENCY_FINE[frequencyFine])
                + Tables.FREQUENCY_DETUNE[frequencyDetune + 7])
                / sampleRate;

        breakpointOffset = breakpoint.getLevelOffset(keyId);
    }

    void stop(int keyId) {
        for (int i = 1; i < algorithm.pattern.length; i++) {
            if (algorithm.pattern[i][0] == id) {
                algorithm.oscillators[algorithm.pattern[i][1]].stop(keyId);
            }
        }

        envelopeGenerator.setEnvelopeState(keyId, EnvelopeState.PRE_RELEASE);
    }

    boolean isActive(int keyId) {
        return envelopeGenerator.getEnvelopeState(keyId) != EnvelopeState.IDLE;
    }

    public double getEffectiveFrequency() {
        return (fixedFrequency
                ? Math.exp(MathFunctions.NATURAL_LOG10 * (((int) frequencyRatio & 3) + frequencyFine / 100.0))
                : ((frequencyRatio == 0) ? 0.5 : frequencyRatio) * Tables.FREQUENCY_FINE[frequencyFine]);
    }

    public void loadOscillatorPreset(OscillatorPreset oscillatorPreset) {
        setFrequencyRatio(oscillatorPreset.getFrequencyRatio());
        setFixedFrequency(oscillatorPreset.isFixedFrequency());
        setFrequencyFine(oscillatorPreset.getFrequencyFine());
        setFrequencyDetune(oscillatorPreset.getFrequencyDetune());
        setOutputLevel(oscillatorPreset.getOutputLevel());
        setFeedback(oscillatorPreset.getFeedback());
        setWaveForm(oscillatorPreset.getWaveForm());

        envelopeGenerator.setAttackLevel(oscillatorPreset.getAttackLevel());
        envelopeGenerator.setDecayLevel(oscillatorPreset.getDecayLevel());
        envelopeGenerator.setSustainLevel(oscillatorPreset.getSustainLevel());
        envelopeGenerator.setReleaseLevel(oscillatorPreset.getReleaseLevel());

        envelopeGenerator.setAttackSpeed(oscillatorPreset.getAttackSpeed());
        envelopeGenerator.setDecaySpeed(oscillatorPreset.getDecaySpeed());
        envelopeGenerator.setSustainSpeed(oscillatorPreset.getSustainSpeed());
        envelopeGenerator.setReleaseSpeed(oscillatorPreset.getReleaseSpeed());

        breakpoint.setNote(oscillatorPreset.getBreakpointNote());
        breakpoint.setLeftCurve(oscillatorPreset.getBreakpointLeftCurve());
        breakpoint.setRightCurve(oscillatorPreset.getBreakpointRightCurve());
        breakpoint.setLeftDepth(oscillatorPreset.getBreakpointLeftDepth());
        breakpoint.setRightDepth(oscillatorPreset.getBreakpointRightDepth());
    }

}
