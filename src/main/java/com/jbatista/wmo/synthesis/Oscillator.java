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

    private double phaseL = 0;
    private double phaseR = 0;

    // envelope data
    private final double[] attackStep = new double[144];
    private final double[] decayStep = new double[144];
    private final double[] sustainStep = new double[144];
    private final double[] releaseStep = new double[144];

    private final EnvelopeState[] envelopeState = new EnvelopeState[144];
    private final double[] envelopeAmplitude = new double[144];

    private final boolean[] keyReleased = new boolean[144];

    private final double[][] sampleFrame = new double[144][2];
    private final double[][] modulatorFrame = new double[144][2];

    private final double[][] feedbackMemory = new double[144][4];

    Oscillator(int id) {
        this.id = id;
        this.hash = ((Integer) this.id).hashCode();

        setAttackDuration(0);
        setDecayDuration(0);
        setSustainDuration(0);
        setReleaseDuration(0);
    }

    // <editor-fold defaultstate="collapsed" desc="getters/setters">
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

    private double[] sampleFrame(int keyId) {
        return sampleFrame[keyId];
    }

    public double getPhaseL() {
        return phaseL;
    }

    public void setPhaseL(double phaseL) {
        this.phaseL = Math.max(0, Math.min(phaseL, 1));
    }

    public double getPhaseR() {
        return phaseR;
    }

    public void setPhaseR(double phaseR) {
        this.phaseR = Math.max(0, Math.min(phaseR, 1));
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

    void fillFrame(Key key, double[] sample, long time) {
        createEnvelope(key.getId());

        if (envelopeState[key.getId()] == EnvelopeState.IDLE) {
            key.setActiveOscillator(id, false);
        }

        modulatorFrame[key.getId()][0] = 0.0;
        modulatorFrame[key.getId()][1] = 0.0;

        feedbackMemory[key.getId()][2] = feedbackMemory[key.getId()][0];
        feedbackMemory[key.getId()][3] = feedbackMemory[key.getId()][1];

        if (!modulators.isEmpty()) {
            for (Oscillator oscillator : modulators) {
                oscillator.fillFrame(key, modulatorFrame[key.getId()], time);
            }

            modulatorFrame[key.getId()][0] /= modulators.size();
            modulatorFrame[key.getId()][1] /= modulators.size();
        }

        if (feedback != null) {
            modulatorFrame[key.getId()][0] = (feedback.feedbackMemory[key.getId()][0] - feedback.feedbackMemory[key.getId()][2]) / 2;
            modulatorFrame[key.getId()][1] = (feedback.feedbackMemory[key.getId()][1] - feedback.feedbackMemory[key.getId()][3]) / 2;
        }

        sampleFrame[key.getId()][0] = gain * envelopeAmplitude[key.getId()] * produceSample(key.getId(), phaseL, modulatorFrame[key.getId()][0], time);
        sampleFrame[key.getId()][1] = gain * envelopeAmplitude[key.getId()] * produceSample(key.getId(), phaseR, modulatorFrame[key.getId()][1], time);

        feedbackMemory[key.getId()][0] = sampleFrame[key.getId()][0];
        feedbackMemory[key.getId()][1] = sampleFrame[key.getId()][1];

        sample[0] += sampleFrame[key.getId()][0];
        sample[1] += sampleFrame[key.getId()][1];
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
                    envelopeAmplitude[keyId] = envelopeAmplitude[keyId] - 0.05;
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

    private double produceSample(int keyId, double phase, double modulation, long time) {
        return DspUtil.oscillator(
                waveForm,
                Instrument.getSampleRate(),
                effectiveFrequency[keyId],
                modulation,
                phase,
                time);
    }

    void start(Key key) {
        for (Oscillator oscillator : modulators) {
            oscillator.start(key);
        }

        effectiveFrequency[key.getId()] = key.getFrequency() * frequencyRatio;

        if (envelopeState[key.getId()] == null) {
            key.setActiveOscillator(id, false);
        } else {
            key.setActiveOscillator(id, !envelopeState[key.getId()].equals(EnvelopeState.IDLE));
        }


        attackStep[key.getId()] = -1;
        decayStep[key.getId()] = -1;
        sustainStep[key.getId()] = -1;
        releaseStep[key.getId()] = -1;

        key.setActiveOscillator(id, true);
        keyReleased[key.getId()] = false;
        envelopeState[key.getId()] = EnvelopeState.ATTACK;
    }

    void stop(Key key) {
        for (Oscillator oscillator : modulators) {
            oscillator.stop(key);
        }

        envelopeState[key.getId()] = EnvelopeState.RELEASE;
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
