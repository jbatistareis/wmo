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
    private final long instrumentId;

    private final double[] sineFrequency = new double[132];
    private final int sampleRate;

    private int correctedOutputLevel;
    private long previousTime;

    private final EnvelopeGenerator envelopeGenerator;
    private final Breakpoint breakpoint;

    Oscillator(int id, int sampleRate, long instrumentId) {
        this.id = id;
        this.sampleRate = sampleRate;
        this.instrumentId = instrumentId;
        this.envelopeGenerator = new EnvelopeGenerator(id, sampleRate, instrumentId);
        this.breakpoint = new Breakpoint(id, instrumentId);
    }

    private OscillatorPreset oscillatorPreset() {
        return Instrument.presets.get(instrumentId).getOscillatorPresets()[id];
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
    double getSample(int keyId, double pitchOffset, double modulation, long time) {
        if (oscillatorPreset().isMute()) {
            return 0;
        }

        if (time != previousTime) {
            envelopeGenerator.advanceEnvelope(keyId);
        }

        previousTime = time;

        return produceSample(keyId, pitchOffset * sineFrequency[keyId], modulation, time);
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
        if (oscillatorPreset().isMute()) {
            envelopeGenerator.reset(keyId);
        } else {
            envelopeGenerator.stop(keyId);
        }
    }

    /**
     * Puts the oscillator gradually in the <code>idle</code> stage.
     *
     * @param keyId ID representing an unique key, in the range of 0 to 131.
     */
    void silence(int keyId) {
        if (oscillatorPreset().isMute()) {
            envelopeGenerator.reset(keyId);
        } else {
            envelopeGenerator.silence(keyId);
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

}
