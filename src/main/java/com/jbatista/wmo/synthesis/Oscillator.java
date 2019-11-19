package com.jbatista.wmo.synthesis;

import com.jbatista.wmo.DspUtil;

import java.util.LinkedHashSet;

public class Oscillator {

    enum EnvelopeState {ATTACK, DECAY, SUSTAIN, HOLD, RELEASE, IDLE}

    private final int id;

    private final double[] effectiveFrequency = new double[144];

    // I/O
    private final LinkedHashSet<Oscillator> modulators = new LinkedHashSet<>();
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
        switch (envelopeState[key.getId()]) {
            case ATTACK:
                if (attackStep[key.getId()] == -1) {
                    attackStep[key.getId()] = (attackAmplitude - envelopeAmplitude[key.getId()]) / (((attackDuration == 0) ? 0.001 : attackDuration) * Instrument.getSampleRate());
                }

                envelopeAmplitude[key.getId()] = envelopeAmplitude[key.getId()] + attackStep[key.getId()];

                if (envelopeAmplitude[key.getId()] >= attackAmplitude) {
                    envelopeState[key.getId()] = EnvelopeState.DECAY;
                }

                break;

            case DECAY:
                if (decayStep[key.getId()] == -1) {
                    decayStep[key.getId()] = (decayAmplitude - attackAmplitude) / (((decayDuration == 0) ? 0.001 : decayDuration) * Instrument.getSampleRate());
                }

                envelopeAmplitude[key.getId()] = envelopeAmplitude[key.getId()] + decayStep[key.getId()];

                if (attackAmplitude >= decayAmplitude) {
                    if (envelopeAmplitude[key.getId()] <= decayAmplitude) {
                        envelopeState[key.getId()] = EnvelopeState.SUSTAIN;
                    }
                } else {
                    if (envelopeAmplitude[key.getId()] >= decayAmplitude) {
                        envelopeState[key.getId()] = EnvelopeState.SUSTAIN;
                    }
                }

                break;

            case SUSTAIN:
                if (sustainStep[key.getId()] == -1) {
                    sustainStep[key.getId()] = (sustainAmplitude - decayAmplitude) / (((sustainDuration == 0) ? 0.001 : sustainDuration) * Instrument.getSampleRate());
                }

                envelopeAmplitude[key.getId()] = envelopeAmplitude[key.getId()] + sustainStep[key.getId()];

                if (decayAmplitude >= sustainAmplitude) {
                    if (envelopeAmplitude[key.getId()] <= sustainAmplitude) {
                        envelopeState[key.getId()] = EnvelopeState.HOLD;
                    }
                } else {
                    if (envelopeAmplitude[key.getId()] >= sustainAmplitude) {
                        envelopeState[key.getId()] = EnvelopeState.HOLD;
                    }
                }

                break;

            case HOLD:
                // do noting
                break;

            case RELEASE:
                if (releaseStep[key.getId()] == -1) {
                    releaseStep[key.getId()] = (releaseAmplitude - envelopeAmplitude[key.getId()]) / (((decayDuration == 0) ? 0.001 : releaseDuration) * Instrument.getSampleRate());
                }

                envelopeAmplitude[key.getId()] = envelopeAmplitude[key.getId()] + releaseStep[key.getId()];

                if (sustainAmplitude >= releaseAmplitude) {
                    if (envelopeAmplitude[key.getId()] <= releaseAmplitude) {
                        envelopeState[key.getId()] = EnvelopeState.IDLE;
                    }
                } else {
                    if (envelopeAmplitude[key.getId()] >= releaseAmplitude) {
                        envelopeState[key.getId()] = EnvelopeState.IDLE;
                    }
                }

                break;

            case IDLE:
                if (envelopeAmplitude[key.getId()] > 0) {
                    envelopeAmplitude[key.getId()] = envelopeAmplitude[key.getId()] - 0.05;
                } else {
                    envelopeAmplitude[key.getId()] = 0.0;
                    key.setActiveOscillator(id, false);
                }

                break;
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

        sampleFrame[key.getId()][0] = gain * envelopeAmplitude[key.getId()] * produceSample(key, phaseL, modulatorFrame[key.getId()][0], time);
        sampleFrame[key.getId()][1] = gain * envelopeAmplitude[key.getId()] * produceSample(key, phaseR, modulatorFrame[key.getId()][1], time);

        feedbackMemory[key.getId()][0] = sampleFrame[key.getId()][0];
        feedbackMemory[key.getId()][1] = sampleFrame[key.getId()][1];

        sample[0] += sampleFrame[key.getId()][0];
        sample[1] += sampleFrame[key.getId()][1];
    }

    private double produceSample(Key key, double phase, double modulation, long time) {
        return DspUtil.oscillator(
                waveForm,
                Instrument.getSampleRate(),
                effectiveFrequency[key.getId()],
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

        //if (!key.isOscillatorActive(id)) {
        // envelopeAmplitude[key.getId()] = envelopeAmplitude.getOrDefault(key.hashCode(), 0.0));
        //}

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

}
