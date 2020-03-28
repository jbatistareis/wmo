package com.jbatista.wmo.synthesis;

import com.jbatista.wmo.preset.OscillatorPreset;
import com.jbatista.wmo.util.Dsp;
import com.jbatista.wmo.util.MathFunctions;

/**
 * Represents a digital oscillator.
 * <p>Instances of this class are created by the {@link Algorithm} class. When initialized, it instantiates {@link EnvelopeGenerator} and {@link Breakpoint}.</p>
 *
 * @see Algorithm
 * @see EnvelopeGenerator
 * @see Breakpoint
 */
public class Oscillator {

    final int id;

    private final double[] sineFrequency = new double[132];
    private final Instrument instrument;
    private final int sampleRate;

    private int correctedOutputLevel;
    private final EnvelopeGenerator envelopeGenerator;
    private final Breakpoint breakpoint;

    private double modulatorSample;

    Oscillator(int id, int sampleRate, Instrument instrument) {
        this.id = id;
        this.sampleRate = sampleRate;
        this.instrument = instrument;
        this.envelopeGenerator = new EnvelopeGenerator(id, sampleRate, instrument);
        this.breakpoint = new Breakpoint(id, instrument);
    }

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
        if (oscillatorPreset().isMute()) {
            return 0;
        }

        modulatorSample = 0;

        for (int i = 2; i < algorithm().length; i++) {
            if (algorithm()[i][0] == id) {
                modulatorSample += instrument.algorithm.oscillators[algorithm()[i][1]].getSample(keyId, pitchOffset, time);
            }
        }

        if (algorithm()[1][0] == id) {
            modulatorSample += Math.pow(2, (instrument.preset.getFeedback() - 7))
                    * produceSample(keyId, pitchOffset * sineFrequency[keyId], modulatorSample, time);
        }

        envelopeGenerator.advanceEnvelope(keyId);

        return produceSample(keyId, pitchOffset * sineFrequency[keyId], modulatorSample, time);
    }

    private double produceSample(int keyId, double frequency, double modulation, long time) {
        return Tables.OSCILLATOR_OUTPUT_LEVELS[correctedOutputLevel]
                * envelopeGenerator.getEnvelopeAmplitude(keyId)
                * Dsp.oscillator(oscillatorPreset().getWaveForm(), frequency, modulation, 0, time);
    }

    /**
     * Puts the oscillator in the <code>attack</code> stage, the envelope keeps progressing to <code>sustain</code> until the {@link Oscillator#stop(int) stop} method is called.
     * <p>The fixed frequency calculation was taken from hexter's <a href="https://github.com/smbolton/hexter/blob/737dbb04c407184fae0e203c1d73be8ad3fd55ba/src/dx7_voice.c#L782">dx7_voice.c</a>.</p>
     *
     * @param keyId     ID representing an unique key, in the range of 0 to 131.
     * @param frequency Indicates the frequency at which this oscillator is going to operate.
     * @see <a href="https://github.com/smbolton/hexter">hexter</a>
     */
    void start(int keyId, double frequency) {
        if (!oscillatorPreset().isMute()) {
            for (int i = 2; i < algorithm().length; i++) {
                if (algorithm()[i][0] == id) {
                    instrument.algorithm.oscillators[algorithm()[i][1]].start(keyId, frequency);
                }
            }

            sineFrequency[keyId] = ((oscillatorPreset().isFixedFrequency()
                    ? Math.exp(MathFunctions.NATURAL_LOG10 * (((int) oscillatorPreset().getFrequencyRatio() & 3) + oscillatorPreset().getFrequencyFine() / 100.0))
                    : frequency * ((oscillatorPreset().getFrequencyRatio() == 0) ? 0.5 : oscillatorPreset().getFrequencyRatio()) * Tables.FREQUENCY_FINE[oscillatorPreset().getFrequencyFine()])
                    + Tables.FREQUENCY_DETUNE[oscillatorPreset().getFrequencyDetune() + 7])
                    / sampleRate;

            correctedOutputLevel = Math.max(0, Math.min(oscillatorPreset().getOutputLevel() + breakpoint.getLevelOffset(keyId), 99));

            envelopeGenerator.initialize(keyId);
        }
    }

    /**
     * Puts the oscillator in the <code>release</code> stage.
     *
     * @param keyId ID representing an unique key, in the range of 0 to 131.
     */
    void stop(int keyId) {
        for (int i = 2; i < algorithm().length; i++) {
            if (algorithm()[i][0] == id) {
                instrument.algorithm.oscillators[algorithm()[i][1]].stop(keyId);
            }
        }

        if (oscillatorPreset().isMute()) {
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
    boolean isActive(int keyId) {
        return envelopeGenerator.getEnvelopeState(keyId) != EnvelopeState.IDLE;
    }

    /**
     * Helper method for display purposes.
     *
     * @return If the oscillator is in fixed frequency mode, returns it's frequency in Hz. Otherwise, returns the frequency ratio.
     */
    public double getEffectiveFrequency() {
        return (oscillatorPreset().isFixedFrequency()
                ? Math.exp(MathFunctions.NATURAL_LOG10 * (((int) oscillatorPreset().getFrequencyRatio() & 3) + oscillatorPreset().getFrequencyFine() / 100.0))
                : ((oscillatorPreset().getFrequencyRatio() == 0) ? 0.5 : oscillatorPreset().getFrequencyRatio()) * Tables.FREQUENCY_FINE[oscillatorPreset().getFrequencyFine()]);
    }

    private OscillatorPreset oscillatorPreset() {
        return instrument.preset.getOscillatorPresets()[id];
    }

    private int[][] algorithm() {
        return instrument.preset.getAlgorithm().getPattern();
    }

}
