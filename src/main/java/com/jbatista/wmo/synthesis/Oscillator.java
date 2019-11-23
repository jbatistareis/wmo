package com.jbatista.wmo.synthesis;

import com.jbatista.wmo.DspUtil;
import com.jbatista.wmo.MathUtil;

import java.util.LinkedList;

public class Oscillator {

    enum EnvelopeState {ATTACK, DECAY, SUSTAIN, HOLD, RELEASE, RELEASE_END, IDLE}

    public enum EnvelopeCurve {LINEAR, SMOOTH, ACCELERATION, DECELERATION}

    private final int id;
    private final int hash;

    private final double[] effectiveFrequency = new double[144];
    private final double sampleRate;

    // I/O
    private final LinkedList<Oscillator> modulators = new LinkedList<>();

    // parameters
    private DspUtil.WaveForm waveForm = DspUtil.WaveForm.SINE;

    private int outputLevel = 75;
    private int feedbackLevel = 0;
    private double frequencyRatio = 1;

    private double attackAmplitude = 0;
    private double decayAmplitude = 0;
    private double sustainAmplitude = 1;
    private double releaseAmplitude = 0;

    private double attackDuration;
    private double decayDuration;
    private double sustainDuration;
    private double releaseDuration;

    private final EnvelopeCurve[] envelopeCurves = new EnvelopeCurve[]{EnvelopeCurve.LINEAR, EnvelopeCurve.LINEAR, EnvelopeCurve.LINEAR, EnvelopeCurve.LINEAR};

    // envelope data
    private final EnvelopeState[] envelopeState = new EnvelopeState[144];
    private final double[] envelopeAmplitude = new double[144];
    // attack -> decay -> sustain -> release
    private double[][] envelopes = new double[4][0];
    private final int[] envelopePosition = new int[144];

    private final boolean[] keyReleased = new boolean[144];

    private final double[][] modulatorSample = new double[144][1];
    private final double[] feedbackSample = new double[144];

    Oscillator(int id, double sampleRate) {
        this.id = id;
        this.sampleRate = sampleRate;
        this.hash = ((Integer) this.id).hashCode();

        setAttackDuration(0);
        setDecayDuration(0);
        setSustainDuration(0);
        setReleaseDuration(0);
    }

    // <editor-fold defaultstate="collapsed" desc="getters/setters">
    int getId() {
        return id;
    }

    public DspUtil.WaveForm getWaveForm() {
        return waveForm;
    }

    public void setWaveForm(DspUtil.WaveForm waveForm) {
        this.waveForm = waveForm;
    }

    public int getOutputLevel() {
        return outputLevel;
    }

    public void setOutputLevel(int outputLevel) {
        this.outputLevel = Math.max(0, Math.min(outputLevel, 99));
    }

    public int getFeedbackLevel() {
        return feedbackLevel;
    }

    public void setFeedbackLevel(int feedbackLevel) {
        this.feedbackLevel = Math.max(0, Math.min(feedbackLevel, 7));
    }

    public double getFrequencyRatio() {
        return frequencyRatio;
    }

    public void setFrequencyRatio(double frequencyRatio) {
        this.frequencyRatio = Math.max(0, Math.min(frequencyRatio, 32));
    }

    public double getAttackAmplitude() {
        return attackAmplitude;
    }

    public void setAttackAmplitude(double attackAmplitude) {
        this.attackAmplitude = Math.max(0, Math.min(attackAmplitude, 1));
    }

    public double getDecayAmplitude() {
        return decayAmplitude;
    }

    public void setDecayAmplitude(double decayAmplitude) {
        this.decayAmplitude = Math.max(0, Math.min(decayAmplitude, 1));
        calculateEnvelope(1, this.decayDuration, this.attackAmplitude, this.decayAmplitude);
    }

    public double getSustainAmplitude() {
        return sustainAmplitude;
    }

    public void setSustainAmplitude(double sustainAmplitude) {
        this.sustainAmplitude = Math.max(0, Math.min(sustainAmplitude, 1));
        calculateEnvelope(2, this.sustainDuration, this.decayAmplitude, this.sustainAmplitude);
    }

    public double getReleaseAmplitude() {
        return releaseAmplitude;
    }

    public void setReleaseAmplitude(double releaseAmplitude) {
        this.releaseAmplitude = Math.max(0, Math.min(releaseAmplitude, 1));
    }

    public double getAttackDuration() {
        return attackDuration;
    }

    public void setAttackDuration(double attackDuration) {
        this.attackDuration = Math.max(0, Math.min(attackDuration, 1));
    }

    public double getDecayDuration() {
        return decayDuration;
    }

    public void setDecayDuration(double decayDuration) {
        this.decayDuration = Math.max(0, Math.min(decayDuration, 1));
        calculateEnvelope(1, this.decayDuration, this.attackAmplitude, this.decayAmplitude);
    }

    public double getSustainDuration() {
        return sustainDuration;
    }

    public void setSustainDuration(double sustainDuration) {
        this.sustainDuration = Math.max(0, Math.min(sustainDuration, 1));
        calculateEnvelope(2, this.sustainDuration, this.decayAmplitude, this.sustainAmplitude);
    }

    public double getReleaseDuration() {
        return releaseDuration;
    }

    public void setReleaseDuration(double releaseDuration) {
        this.releaseDuration = Math.max(0, Math.min(releaseDuration, 1));
    }

    public EnvelopeCurve getAttackCurve() {
        return envelopeCurves[0];
    }

    public void setAttackCurve(EnvelopeCurve envelopeCurve) {
        envelopeCurves[0] = envelopeCurve;
    }

    public EnvelopeCurve getDecayCurve() {
        return envelopeCurves[1];
    }

    public void setDecayCurve(EnvelopeCurve envelopeCurve) {
        envelopeCurves[1] = envelopeCurve;
    }

    public EnvelopeCurve getSustainCurve() {
        return envelopeCurves[2];
    }

    public void setSustainCurve(EnvelopeCurve envelopeCurve) {
        envelopeCurves[2] = envelopeCurve;
    }

    public EnvelopeCurve getReleaseCurve() {
        return envelopeCurves[3];
    }

    public void setReleaseCurve(EnvelopeCurve envelopeCurve) {
        envelopeCurves[3] = envelopeCurve;
    }

    public boolean addModulator(Oscillator modulator) {
        if (modulators.contains(modulator)) {
            return false;
        }

        return modulators.add(modulator);
    }

    public boolean removeModulator(Oscillator modulator) {
        return modulators.remove(modulator);
    }

    public void clearModulators() {
        modulators.clear();
    }

    public Oscillator[] getModulators() {
        return modulators.toArray(new Oscillator[0]);
    }
    // </editor-fold>

    boolean fillFrame(int keyId, double[] sample, long time) {
        defineEnvelopeAmplitude(keyId);

        modulatorSample[keyId][0] = 0;
        feedbackSample[keyId] = 0;

        if (!modulators.isEmpty() && (feedbackLevel == 0)) {
            for (Oscillator oscillator : modulators) {
                oscillator.fillFrame(keyId, modulatorSample[keyId], time);
            }

            modulatorSample[keyId][0] /= modulators.size();
        } else if (feedbackLevel > 0) {
            feedbackSample[keyId] = Tables.outputLevels[feedbackLevel * 14]
                    * ((produceSample(effectiveFrequency[keyId] * 2, 0, time) / 2)
                    + (produceSample(effectiveFrequency[keyId] * 3, 0, time) / 3)
                    + (produceSample(effectiveFrequency[keyId] * 4, 0, time) / 4)
                    + (produceSample(effectiveFrequency[keyId] * 5, 0, time) / 5));
        }

        sample[0] += Tables.outputLevels[outputLevel] * envelopeAmplitude[keyId] * (produceSample(effectiveFrequency[keyId], modulatorSample[keyId][0], time) + feedbackSample[keyId]);

        return envelopeState[keyId] != EnvelopeState.IDLE;
    }

    private void defineEnvelopeAmplitude(int keyId) {
        switch (envelopeState[keyId]) {
            case ATTACK:
                if (applyEnvelope(keyId, 0)) {
                    envelopePosition[keyId] = 0;
                    envelopeState[keyId] = EnvelopeState.DECAY;
                }
                break;

            case DECAY:
                if (applyEnvelope(keyId, 1)) {
                    envelopePosition[keyId] = 0;
                    envelopeState[keyId] = EnvelopeState.SUSTAIN;
                }
                break;

            case SUSTAIN:
                if (applyEnvelope(keyId, 2)) {
                    envelopePosition[keyId] = 0;
                    envelopeState[keyId] = EnvelopeState.HOLD;
                }
                break;

            case HOLD:
                // do noting
                break;

            case RELEASE:
                if (applyEnvelope(keyId, 3)) {
                    envelopeState[keyId] = EnvelopeState.RELEASE_END;
                }
                break;

            case RELEASE_END:
                if (envelopeAmplitude[keyId] > 0.005) {
                    envelopeAmplitude[keyId] -= 0.005;
                } else {
                    envelopeAmplitude[keyId] = 0.0;
                    envelopeState[keyId] = EnvelopeState.IDLE;
                }
                break;

            case IDLE:
                // do nothing
                break;
        }
    }

    private boolean applyEnvelope(int keyId, int envelopeState) {
        if (envelopePosition[keyId] < envelopes[envelopeState].length) {
            envelopeAmplitude[keyId] = envelopes[envelopeState][envelopePosition[keyId]];
            envelopePosition[keyId] += 1;

            return false;
        } else {
            return true;
        }
    }

    void calculateEnvelope(int envelopeCurve, double duration, double startAmplitude, double endAmplitude) {
        final int samples = (int) (((duration == 0) ? 0.001 : duration) * sampleRate);
        final double factor = 1d / samples;
        double accumulator = 0;

        if (envelopes[envelopeCurve].length != samples) {
            envelopes[envelopeCurve] = new double[samples];
        }

        for (int i = 0; i < samples; i++) {
            switch (envelopeCurves[envelopeCurve]) {
                case LINEAR:
                    envelopes[envelopeCurve][i] = MathUtil.linearInterpolation(startAmplitude, endAmplitude, accumulator);
                    break;

                case SMOOTH:
                    envelopes[envelopeCurve][i] = MathUtil.smoothInterpolation(startAmplitude, endAmplitude, accumulator);
                    break;

                case ACCELERATION:
                    envelopes[envelopeCurve][i] = MathUtil.accelerationInterpolation(startAmplitude, endAmplitude, accumulator);
                    break;

                case DECELERATION:
                    envelopes[envelopeCurve][i] = MathUtil.decelerationInterpolation(startAmplitude, endAmplitude, accumulator);
                    break;

                default:
                    break;
            }

            accumulator += factor;
        }
    }


    private double produceSample(double frequency, double modulation, long time) {
        return DspUtil.oscillator(
                waveForm,
                sampleRate,
                frequency,
                modulation,
                0,
                time);
    }

    void start(int keyId, double frequency) {
        for (Oscillator oscillator : modulators) {
            oscillator.start(keyId, frequency);
        }

        calculateEnvelope(0, attackDuration, envelopeAmplitude[keyId], attackAmplitude);
        envelopePosition[keyId] = 0;

        effectiveFrequency[keyId] = frequency * frequencyRatio;
        keyReleased[keyId] = false;
        envelopeState[keyId] = EnvelopeState.ATTACK;
    }

    void stop(int keyId) {
        for (Oscillator oscillator : modulators) {
            oscillator.stop(keyId);
        }

        calculateEnvelope(0, releaseDuration, envelopeAmplitude[keyId], releaseAmplitude);

        envelopeState[keyId] = EnvelopeState.RELEASE;
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
