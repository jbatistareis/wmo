package com.jbatista.wmo.synthesis;

import com.jbatista.wmo.DspUtil;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;

public class Oscillator {

    enum EnvelopeState {ATTACK, DECAY, SUSTAIN, RELEASE, IDLE}

    private final int id;

    private final Map<Integer, Double> effectiveFrequency = new HashMap<>();

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
    private double attackFrames;
    private double decayFrames;
    private double sustainFrames;
    private double releaseFrames;

    private final Map<Integer, Double> attackStep = new HashMap<>();
    private final Map<Integer, Double> decayStep = new HashMap<>();
    private final Map<Integer, Double> sustainStep = new HashMap<>();
    private final Map<Integer, Double> releaseStep = new HashMap<>();

    private final Map<Integer, Integer> envelopePosition = new HashMap<>();
    private final Map<Integer, EnvelopeState> envelopeState = new HashMap<>();
    private final Map<Integer, Double> envelopeAmplitude = new HashMap<>();

    private final Map<Integer, Boolean> keyReleased = new HashMap<>();

    private final Map<Integer, double[]> sampleFrame = new HashMap<>();
    private final Map<Integer, double[]> modulatorFrame = new HashMap<>();

    private final Map<Integer, double[]> feedbackMemory = new HashMap<>();

    public Oscillator() {
        this.id = this.hashCode();

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
        this.attackFrames = Instrument.getSampleRate() * ((this.attackDuration > 0) ? this.attackDuration : 0.005);
    }

    public double getDecayDuration() {
        return decayDuration;
    }

    public void setDecayDuration(double decayDuration) {
        this.decayDuration = Math.max(0, Math.min(decayDuration, 1));
        this.decayFrames = Instrument.getSampleRate() * ((this.decayDuration > 0) ? this.decayDuration : 0.005);
    }

    public double getSustainDuration() {
        return sustainDuration;
    }

    public void setSustainDuration(double sustainDuration) {
        this.sustainDuration = Math.max(0, Math.min(sustainDuration, 1));
        this.sustainFrames = Instrument.getSampleRate() * ((this.sustainDuration > 0) ? this.sustainDuration : 0.005);
    }

    public double getReleaseDuration() {
        return releaseDuration;
    }

    public void setReleaseDuration(double releaseDuration) {
        this.releaseDuration = Math.max(0, Math.min(releaseDuration, 1));
        this.releaseFrames = Instrument.getSampleRate() * ((this.releaseDuration > 0) ? this.releaseDuration : 0.005);
    }

    private double[] getSampleFrame(int keyHash) {
        if (!sampleFrame.containsKey(keyHash)) {
            sampleFrame.put(keyHash, new double[]{0, 0});
        }

        return sampleFrame.get(keyHash);
    }

    private double[] getModulatorFrame(int keyHash) {
        if (!modulatorFrame.containsKey(keyHash)) {
            modulatorFrame.put(keyHash, new double[]{0, 0});
        }

        return modulatorFrame.get(keyHash);
    }

    double[] getFeedbackMemory(int keyHash) {
        if (!feedbackMemory.containsKey(keyHash)) {
            feedbackMemory.put(keyHash, new double[]{0, 0, 0, 0});
        }

        return feedbackMemory.get(keyHash);
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
    // </editor-fold>

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

    void fillFrame(Key key, double[] sample, long time) {
        switch (envelopeState.get(key.hashCode())) {
            case ATTACK:
                if (!attackStep.containsKey(key.hashCode())) {
                    envelopePosition.put(key.hashCode(), 0);
                    attackStep.put(key.hashCode(), (attackAmplitude - envelopeAmplitude.get(key.hashCode())) / attackFrames);
                }

                envelopeAmplitude.put(key.hashCode(), envelopeAmplitude.get(key.hashCode()) + attackStep.get(key.hashCode()));
                envelopePosition.put(key.hashCode(), envelopePosition.get(key.hashCode()) + 1);

                if (envelopePosition.get(key.hashCode()) >= attackFrames) {
                    envelopeState.put(key.hashCode(), EnvelopeState.DECAY);
                }

                break;

            case DECAY:
                if (!decayStep.containsKey(key.hashCode())) {
                    envelopePosition.put(key.hashCode(), 0);
                    decayStep.put(key.hashCode(), (decayAmplitude - envelopeAmplitude.get(key.hashCode())) / decayFrames);
                }

                envelopeAmplitude.put(key.hashCode(), envelopeAmplitude.get(key.hashCode()) + decayStep.get(key.hashCode()));
                envelopePosition.put(key.hashCode(), envelopePosition.get(key.hashCode()) + 1);

                if (envelopePosition.get(key.hashCode()) >= decayFrames) {
                    envelopeState.put(key.hashCode(), EnvelopeState.SUSTAIN);
                }

                break;

            case SUSTAIN:
                if (!sustainStep.containsKey(key.hashCode())) {
                    envelopePosition.put(key.hashCode(), 0);
                    sustainStep.put(key.hashCode(), (sustainAmplitude - envelopeAmplitude.get(key.hashCode())) / sustainFrames);
                }

                if (envelopePosition.get(key.hashCode()) <= sustainFrames) {
                    envelopeAmplitude.put(key.hashCode(), envelopeAmplitude.get(key.hashCode()) + sustainStep.get(key.hashCode()));
                    envelopePosition.put(key.hashCode(), envelopePosition.get(key.hashCode()) + 1);
                }

                break;

            case RELEASE:
                if (!releaseStep.containsKey(key.hashCode())) {
                    envelopePosition.put(key.hashCode(), 0);
                    releaseStep.put(key.hashCode(), (releaseAmplitude - envelopeAmplitude.get(key.hashCode())) / releaseFrames);
                }

                envelopeAmplitude.put(key.hashCode(), envelopeAmplitude.get(key.hashCode()) + releaseStep.get(key.hashCode()));

                envelopePosition.put(key.hashCode(), envelopePosition.get(key.hashCode()) + 1);
                if (envelopePosition.get(key.hashCode()) >= releaseFrames) {
                    envelopeState.put(key.hashCode(), EnvelopeState.IDLE);
                }

                break;

            case IDLE:
                envelopeAmplitude.put(key.hashCode(), 0.0);
                key.setActiveOscillator(id, false);

                break;
        }

        getModulatorFrame(key.hashCode())[0] = 0.0;
        getModulatorFrame(key.hashCode())[1] = 0.0;

        getFeedbackMemory(key.hashCode())[2] = getFeedbackMemory(key.hashCode())[0];
        getFeedbackMemory(key.hashCode())[3] = getFeedbackMemory(key.hashCode())[1];

        if (!modulators.isEmpty()) {
            for (Oscillator oscillator : modulators) {
                oscillator.fillFrame(key, getModulatorFrame(key.hashCode()), time);
            }

            getModulatorFrame(key.hashCode())[0] /= modulators.size();
            getModulatorFrame(key.hashCode())[1] /= modulators.size();
        }

        if (feedback != null) {
            getModulatorFrame(key.hashCode())[0] = (feedback.getFeedbackMemory(key.hashCode())[0] - feedback.getFeedbackMemory(key.hashCode())[2]) / 2;
            getModulatorFrame(key.hashCode())[1] = (feedback.getFeedbackMemory(key.hashCode())[1] - feedback.getFeedbackMemory(key.hashCode())[3]) / 2;
        }

        getSampleFrame(key.hashCode())[0] = gain * envelopeAmplitude.get(key.hashCode()) * produceSample(key, phaseL, getModulatorFrame(key.hashCode())[0], time);
        getSampleFrame(key.hashCode())[1] = gain * envelopeAmplitude.get(key.hashCode()) * produceSample(key, phaseR, getModulatorFrame(key.hashCode())[1], time);

        getFeedbackMemory(key.hashCode())[0] = getSampleFrame(key.hashCode())[0];
        getFeedbackMemory(key.hashCode())[1] = getSampleFrame(key.hashCode())[1];

        sample[0] += getSampleFrame(key.hashCode())[0];
        sample[1] += getSampleFrame(key.hashCode())[1];
    }

    private double produceSample(Key key, double phase, double modulation, long time) {
        return DspUtil.oscillator(
                waveForm,
                Instrument.getSampleRate(),
                effectiveFrequency.get(key.hashCode()),
                modulation,
                phase,
                time);
    }

    void start(Key key) {
        for (Oscillator oscillator : modulators) {
            oscillator.start(key);
        }

        effectiveFrequency.put(key.hashCode(), key.getFrequency() * frequencyRatio);
        key.setActiveOscillator(id, !envelopeState.getOrDefault(key.hashCode(), EnvelopeState.IDLE).equals(EnvelopeState.IDLE));

        attackStep.remove(key.hashCode());
        decayStep.remove(key.hashCode());
        sustainStep.remove(key.hashCode());
        releaseStep.remove(key.hashCode());

        //if (!key.isOscillatorActive(id)) {
        envelopeAmplitude.put(key.hashCode(), envelopeAmplitude.getOrDefault(key.hashCode(), 0.0));
        //}

        key.setActiveOscillator(id, true);
        keyReleased.put(key.hashCode(), false);
        envelopeState.put(key.hashCode(), EnvelopeState.ATTACK);
    }

    void stop(Key key) {
        for (Oscillator oscillator : modulators) {
            oscillator.stop(key);
        }

        envelopeState.put(key.hashCode(), EnvelopeState.RELEASE);
    }

}
