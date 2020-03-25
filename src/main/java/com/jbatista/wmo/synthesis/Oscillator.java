package com.jbatista.wmo.synthesis;

import com.jbatista.wmo.WaveForm;
import com.jbatista.wmo.preset.OscillatorPreset;
import com.jbatista.wmo.util.Dsp;
import com.jbatista.wmo.util.MathFunctions;

public class Oscillator {

    private final int id;
    private final int sampleRate;
    private boolean mute = false;

    private final double[] sineFrequency = new double[132];

    // I/O
    private Algorithm algorithm;

    // parameters
    private WaveForm waveForm = WaveForm.SINE;
    private int outputLevel = 75;
    private int correctedOutputLevel;
    private int feedback = 0;
    private double frequencyRatio = 1;
    private boolean fixedFrequency = false;
    private int frequencyFine = 0;
    private int frequencyDetune = 0;
    private final EnvelopeGenerator envelopeGenerator;
    private final Breakpoint breakpoint = new Breakpoint();

    private double modulatorSample;

    Oscillator(int id, int sampleRate, Algorithm algorithm) {
        this.id = id;
        this.sampleRate = sampleRate;
        this.envelopeGenerator = new EnvelopeGenerator(sampleRate);
        this.algorithm = algorithm;
    }

    // <editor-fold defaultstate="collapsed" desc="getters/setters">
    public int getId() {
        return id;
    }

    public boolean isMute() {
        return mute;
    }

    public void setMute(boolean mute) {
        this.mute = mute;
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
        this.feedback = Math.max(0, Math.min(feedback, 7));
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

    double getSample(int keyId, double pitchOffset, long time) {
        if (mute) {
            return 0;
        }

        modulatorSample = 0;
        envelopeGenerator.defineEnvelopeAmplitude(keyId, time);

        for (int i = 2; i < algorithm.pattern.length; i++) {
            if (algorithm.pattern[i][0] == id) {
                modulatorSample += algorithm.oscillators[algorithm.pattern[i][1]].getSample(keyId, pitchOffset, time);
            }
        }

        if (feedback > 0) {
            modulatorSample += Math.pow(2, (feedback - 7)) * produceSample(keyId, pitchOffset * sineFrequency[keyId], modulatorSample, time);
        }

        envelopeGenerator.setPreviousTime(keyId, time);

        return Tables.OSCILLATOR_OUTPUT_LEVELS[correctedOutputLevel]
                * produceSample(keyId, pitchOffset * sineFrequency[keyId], modulatorSample, time);
    }

    private double produceSample(int keyId, double frequency, double modulation, long time) {
        return envelopeGenerator.getEnvelopeAmplitude(keyId)
                * Dsp.oscillator(
                waveForm,
                frequency,
                modulation,
                0,
                time);
    }

    // fixed frequency calculation from [https://github.com/smbolton/hexter/blob/737dbb04c407184fae0e203c1d73be8ad3fd55ba/src/dx7_voice.c#L782]
    void start(int keyId, double frequency) {
        if (!mute) {
            envelopeGenerator.initialize(keyId);

            for (int i = 2; i < algorithm.pattern.length; i++) {
                if (algorithm.pattern[i][0] == id) {
                    algorithm.oscillators[algorithm.pattern[i][1]].start(keyId, frequency);
                }
            }

            sineFrequency[keyId] = ((fixedFrequency
                    ? Math.exp(MathFunctions.NATURAL_LOG10 * (((int) frequencyRatio & 3) + frequencyFine / 100.0))
                    : frequency * ((frequencyRatio == 0) ? 0.5 : frequencyRatio) * Tables.FREQUENCY_FINE[frequencyFine])
                    + Tables.FREQUENCY_DETUNE[frequencyDetune + 7])
                    / sampleRate;

            correctedOutputLevel = Math.max(0, Math.min(outputLevel + breakpoint.getLevelOffset(keyId), 99));
        }
    }

    void stop(int keyId) {
        for (int i = 2; i < algorithm.pattern.length; i++) {
            if (algorithm.pattern[i][0] == id) {
                algorithm.oscillators[algorithm.pattern[i][1]].stop(keyId);
            }
        }

        if (mute) {
            envelopeGenerator.reset(keyId);
        } else {
            envelopeGenerator.setEnvelopeState(keyId, EnvelopeState.PRE_RELEASE);
        }
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
