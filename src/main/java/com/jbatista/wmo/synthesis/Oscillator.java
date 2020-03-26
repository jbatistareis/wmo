package com.jbatista.wmo.synthesis;

import com.jbatista.wmo.WaveForm;
import com.jbatista.wmo.preset.OscillatorPreset;
import com.jbatista.wmo.util.Dsp;
import com.jbatista.wmo.util.MathFunctions;

/**
 * Represents an actual digital oscillator.
 * <p>Instances of this class are created by the {@link Algorithm} class. When initialized, it instantiates {@link EnvelopeGenerator} and {@link Breakpoint}.</p>
 *
 * @see Algorithm
 */
public class Oscillator {

    private final int id;
    private final double[] sineFrequency = new double[132];
    private final Algorithm algorithm;
    private final int sampleRate;

    private boolean mute = false;
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

    /**
     * @param id         ID of the oscillator, in the range of 0 to 5.
     * @param algorithm  The Algorithm instance that it is bound to.
     * @param sampleRate The sample rate that this oscillator is going to operate.
     */
    Oscillator(int id, Algorithm algorithm, int sampleRate) {
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

    /**
     * Creates a mono audio sample frame based on the pitch and time offset, instantiated and controlled by the {@link Algorithm} it is bound to.
     * <p>The state of the oscillator is defined by the use of the methods {@link #start start} and {@link #stop stop}.</p>
     * <p>If this oscillator has modulators, the other oscillators are started in chain.</p>
     *
     * @param keyId       ID representing an unique key, in the range of 0 to 131.
     * @param pitchOffset Defines the value which will multiply the frequency, creating a pitch bend. (WIP)
     * @param time        Time offset.
     * @return A single audio frame.
     * @see Algorithm#getSample
     */
    double getSample(int keyId, double pitchOffset, long time) {
        if (mute) {
            return 0;
        }

        modulatorSample = 0;

        for (int i = 2; i < algorithm.pattern.length; i++) {
            if (algorithm.pattern[i][0] == id) {
                modulatorSample += algorithm.oscillators[algorithm.pattern[i][1]].getSample(keyId, pitchOffset, time);
            }
        }

        if (feedback > 0) {
            modulatorSample += Math.pow(2, (feedback - 7)) * produceSample(keyId, pitchOffset * sineFrequency[keyId], modulatorSample, time);
        }

        envelopeGenerator.advanceEnvelope(keyId);

        return Tables.OSCILLATOR_OUTPUT_LEVELS[correctedOutputLevel]
                * produceSample(keyId, pitchOffset * sineFrequency[keyId], modulatorSample, time);
    }

    private double produceSample(int keyId, double frequency, double modulation, long time) {
        return envelopeGenerator.getEnvelopeAmplitude(keyId) * Dsp.oscillator(waveForm, frequency, modulation, 0, time);
    }

    // fixed frequency calculation from [https://github.com/smbolton/hexter/blob/737dbb04c407184fae0e203c1d73be8ad3fd55ba/src/dx7_voice.c#L782]

    /**
     * Puts the oscillator in the <code>attack</code> stage, the envelope keeps progressing to <code>sustain</code> until the {@link Oscillator#stop(int) stop} method is called.
     * <p>The fixed frequency calculation was taken from hexter's <a href="https://github.com/smbolton/hexter/blob/737dbb04c407184fae0e203c1d73be8ad3fd55ba/src/dx7_voice.c#L782">dx7_voice.c</a>.</p>
     *
     * @param keyId     ID representing an unique key, in the range of 0 to 131.
     * @param frequency Indicates the frequency at which this oscillator is going to operate.
     * @see <a href="https://github.com/smbolton/hexter">hexter</a>
     */
    void start(int keyId, double frequency) {
        if (!mute) {
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

            envelopeGenerator.initialize(keyId);
        }
    }

    /**
     * Puts the oscillator in the <code>release</code> stage.
     *
     * @param keyId ID representing an unique key, in the range of 0 to 131.
     */
    void stop(int keyId) {
        for (int i = 2; i < algorithm.pattern.length; i++) {
            if (algorithm.pattern[i][0] == id) {
                algorithm.oscillators[algorithm.pattern[i][1]].stop(keyId);
            }
        }

        if (mute) {
            envelopeGenerator.reset(keyId);
        } else {
            envelopeGenerator.setEnvelopeState(keyId, EnvelopeState.RELEASE);
        }
    }

    /**
     * Tells if the oscillator is in the <code>idle</code> stage.
     *
     * @param keyId ID representing an unique key, in the range of 0 to 131.
     * @return True if the oscillator is currently idle.
     * @see Algorithm#getSample
     * @see Algorithm#hasActiveCarriers
     */
    public boolean isActive(int keyId) {
        return envelopeGenerator.getEnvelopeState(keyId) != EnvelopeState.IDLE;
    }

    /**
     * Helper method for display purposes.
     *
     * @return If the oscillator is in fixed frequency mode, returns it's frequency in Hz. Otherwise returns the frequency ratio.
     */
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
