package com.jbatista.wmo.synthesis;

import com.jbatista.wmo.DspUtil;

import java.util.LinkedList;

public class Oscillator {

    enum EnvelopeState {ATTACK, DECAY, SUSTAIN, HOLD, RELEASE, RELEASE_END, IDLE}

    private final int id;
    private final int hash;

    private final double[] effectiveFrequency = new double[144];

    // I/O
    private final LinkedList<Oscillator> modulators = new LinkedList<>();
    private Oscillator feedback = null;

    // parameters
    private DspUtil.WaveForm waveForm = DspUtil.WaveForm.SINE;

    private double gain = 1;
    private double frequencyRatio = 1;

    private double attackAmplitude = 0;
    private double decayAmplitude = 0;
    private double sustainAmplitude = 1;
    private double releaseAmplitude = 0;

    private double attackDuration;
    private double decayDuration;
    private double sustainDuration;
    private double releaseDuration;

    // envelope data
    private final double[] attackStep = new double[144];
    private final double[] decayStep = new double[144];
    private final double[] sustainStep = new double[144];
    private final double[] releaseStep = new double[144];

    private final EnvelopeState[] envelopeState = new EnvelopeState[144];
    private final double[] envelopeAmplitude = new double[144];

    private final boolean[] keyReleased = new boolean[144];

    private final double[][] modulatorSample = new double[144][1];
    private final double[][] feedbackMemory = new double[144][3];

    Oscillator(int id) {
        this.id = id;
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

    public void setFeedback(Oscillator feedback) {
        this.feedback = feedback;
    }

    public double getGain() {
        return gain;
    }

    public void setGain(double gain) {
        this.gain = Math.max(0, Math.min(gain, 2));
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
    }

    public double getSustainAmplitude() {
        return sustainAmplitude;
    }

    public void setSustainAmplitude(double sustainAmplitude) {
        this.sustainAmplitude = Math.max(0, Math.min(sustainAmplitude, 1));
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
    }

    public double getSustainDuration() {
        return sustainDuration;
    }

    public void setSustainDuration(double sustainDuration) {
        this.sustainDuration = Math.max(0, Math.min(sustainDuration, 1));
    }

    public double getReleaseDuration() {
        return releaseDuration;
    }

    public void setReleaseDuration(double releaseDuration) {
        this.releaseDuration = Math.max(0, Math.min(releaseDuration, 1));
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
        createEnvelope(keyId);

        modulatorSample[keyId][0] = 0.0;

        if (!modulators.isEmpty() && (feedback == null)) {
            for (Oscillator oscillator : modulators) {
                oscillator.fillFrame(keyId, modulatorSample[keyId], time);
            }

            modulatorSample[keyId][0] /= modulators.size();
        } else if (feedback != null) {
            modulatorSample[keyId][0] = (feedback.feedbackMemory[keyId][1] - feedback.feedbackMemory[keyId][2]) / 2;
        }

        sample[0] += gain * envelopeAmplitude[keyId] * produceSample(keyId, modulatorSample[keyId][0], time);

        feedbackMemory[keyId][2] = feedbackMemory[keyId][1];
        feedbackMemory[keyId][1] = feedbackMemory[keyId][0];
        feedbackMemory[keyId][0] = sample[0];

        return envelopeState[keyId] != EnvelopeState.IDLE;
    }

    private void createEnvelope(int keyId) {
        switch (envelopeState[keyId]) {
            case ATTACK:
                if (attackStep[keyId] == -1) {
                    attackStep[keyId] = (attackAmplitude - envelopeAmplitude[keyId]) / (((attackDuration == 0) ? 0.001 : attackDuration) * Instrument.getSampleRate());
                }

                envelopeAmplitude[keyId] = envelopeAmplitude[keyId] + attackStep[keyId];

                if (envelopeAmplitude[keyId] >= attackAmplitude) {
                    envelopeState[keyId] = EnvelopeState.DECAY;
                }

                break;

            case DECAY:
                if (decayStep[keyId] == -1) {
                    decayStep[keyId] = (decayAmplitude - attackAmplitude) / (((decayDuration == 0) ? 0.001 : decayDuration) * Instrument.getSampleRate());
                }

                envelopeAmplitude[keyId] = envelopeAmplitude[keyId] + decayStep[keyId];

                if (attackAmplitude >= decayAmplitude) {
                    if (envelopeAmplitude[keyId] <= decayAmplitude) {
                        envelopeState[keyId] = EnvelopeState.SUSTAIN;
                    }
                } else {
                    if (envelopeAmplitude[keyId] >= decayAmplitude) {
                        envelopeState[keyId] = EnvelopeState.SUSTAIN;
                    }
                }

                break;

            case SUSTAIN:
                if (sustainStep[keyId] == -1) {
                    sustainStep[keyId] = (sustainAmplitude - decayAmplitude) / (((sustainDuration == 0) ? 0.001 : sustainDuration) * Instrument.getSampleRate());
                }

                envelopeAmplitude[keyId] = envelopeAmplitude[keyId] + sustainStep[keyId];

                if (decayAmplitude >= sustainAmplitude) {
                    if (envelopeAmplitude[keyId] <= sustainAmplitude) {
                        envelopeState[keyId] = EnvelopeState.HOLD;
                    }
                } else {
                    if (envelopeAmplitude[keyId] >= sustainAmplitude) {
                        envelopeState[keyId] = EnvelopeState.HOLD;
                    }
                }

                break;

            case HOLD:
                // do noting
                break;

            case RELEASE:
                if (releaseStep[keyId] == -1) {
                    releaseStep[keyId] = (releaseAmplitude - envelopeAmplitude[keyId]) / (((decayDuration == 0) ? 0.001 : releaseDuration) * Instrument.getSampleRate());
                }

                envelopeAmplitude[keyId] = envelopeAmplitude[keyId] + releaseStep[keyId];

                if (sustainAmplitude >= releaseAmplitude) {
                    if (envelopeAmplitude[keyId] <= releaseAmplitude) {
                        envelopeState[keyId] = EnvelopeState.RELEASE_END;
                    }
                } else {
                    if (envelopeAmplitude[keyId] >= releaseAmplitude) {
                        envelopeState[keyId] = EnvelopeState.RELEASE_END;
                    }
                }

                break;

            case RELEASE_END:
                if (envelopeAmplitude[keyId] > 0) {
                    envelopeAmplitude[keyId] = envelopeAmplitude[keyId] - 0.005;
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

    private double produceSample(int keyId, double modulation, long time) {
        return DspUtil.oscillator(
                waveForm,
                Instrument.getSampleRate(),
                effectiveFrequency[keyId],
                modulation,
                0,
                time);
    }

    void start(int keyId, double frequency) {
        for (Oscillator oscillator : modulators) {
            oscillator.start(keyId, frequency);
        }

        effectiveFrequency[keyId] = frequency * frequencyRatio;

        attackStep[keyId] = -1;
        decayStep[keyId] = -1;
        sustainStep[keyId] = -1;
        releaseStep[keyId] = -1;

        keyReleased[keyId] = false;
        envelopeState[keyId] = EnvelopeState.ATTACK;
    }

    void stop(int keyId) {
        for (Oscillator oscillator : modulators) {
            oscillator.stop(keyId);
        }

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
