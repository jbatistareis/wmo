package com.jbatista.wmo.preset;

import com.jbatista.wmo.WaveForm;

/**
 * Holds parameters for the entire instrument: transposition, oscillators, feedback, algorithm, pitch envelope, LFO.
 * <p>To be implemented:</p>
 * <ul>
 *     <li>Pitch envelope.</li>
 *     <li>LFO.</li>
 *     <li>Key sync.</li>
 * </ul>
 *
 * @see OscillatorPreset
 * @see AlgorithmPreset
 */
public class InstrumentPreset {

    private String name = "  ------  ";
    private double gain = 1;
    private int transpose = 0;
    private int feedback = 0;

    private int pitchAttackLevel = 50;
    private int pitchDecayLevel = 50;
    private int pitchSustainLevel = 50;
    private int pitchReleaseLevel = 50;

    private int pitchAttackSpeed = 99;
    private int pitchDecaySpeed = 99;
    private int pitchSustainSpeed = 99;
    private int pitchReleaseSpeed = 99;

    private boolean oscillatorKeySync = false;
    private boolean lfoKeySync = false;

    private int lfoSpeed = 35;
    private int lfoDelay = 0;
    private int lfoPmDepth = 0;
    private int lfoAmDepth = 0;

    private int lfoPModeSensitivity = 3;
    private WaveForm lfoWave = WaveForm.TRIANGLE;

    private AlgorithmPreset algorithm = AlgorithmPreset.ALGO_1_OSC_1;

    private final OscillatorPreset[] oscillatorPresets = new OscillatorPreset[]{
            new OscillatorPreset(0), new OscillatorPreset(1), new OscillatorPreset(2),
            new OscillatorPreset(3), new OscillatorPreset(4), new OscillatorPreset(5)};

    public InstrumentPreset() {
    }

    /**
     * Copy constructor.
     *
     * @param instrumentPreset The preset to be copied.
     */
    public InstrumentPreset(InstrumentPreset instrumentPreset) {
        this.name = instrumentPreset.name;
        this.gain = instrumentPreset.gain;
        this.transpose = instrumentPreset.transpose;
        this.feedback = instrumentPreset.feedback;

        this.pitchAttackLevel = instrumentPreset.pitchAttackLevel;
        this.pitchDecayLevel = instrumentPreset.pitchDecayLevel;
        this.pitchSustainLevel = instrumentPreset.pitchSustainLevel;
        this.pitchReleaseLevel = instrumentPreset.pitchReleaseLevel;

        this.pitchAttackSpeed = instrumentPreset.pitchAttackSpeed;
        this.pitchDecaySpeed = instrumentPreset.pitchDecaySpeed;
        this.pitchSustainSpeed = instrumentPreset.pitchSustainSpeed;
        this.pitchReleaseSpeed = instrumentPreset.pitchReleaseSpeed;

        this.oscillatorKeySync = instrumentPreset.oscillatorKeySync;
        this.lfoKeySync = instrumentPreset.lfoKeySync;

        this.lfoSpeed = instrumentPreset.lfoSpeed;
        this.lfoDelay = instrumentPreset.lfoDelay;
        this.lfoPmDepth = instrumentPreset.lfoPmDepth;
        this.lfoAmDepth = instrumentPreset.lfoAmDepth;

        this.lfoPModeSensitivity = instrumentPreset.lfoPModeSensitivity;
        this.lfoWave = instrumentPreset.lfoWave;

        this.algorithm = instrumentPreset.algorithm;

        for (int i = 0; i < 6; i++) {
            this.oscillatorPresets[i] = new OscillatorPreset(instrumentPreset.getOscillatorPresets()[i]);
        }
    }

    @Override
    public String toString() {
        return name;
    }

    public String getName() {
        return name;
    }

    /**
     * How the instrument is called.
     *
     * @param name A string limited to 10 characters.
     */
    public void setName(String name) {
        this.name = (name.length() > 10) ? name.substring(0, 10) : name;
    }

    public double getGain() {
        return gain;
    }

    /**
     * Defines a volume attenuation of the output.
     * <p>Defaults to <b>1</b>.</p>
     *
     * @param gain A value from 0 to 2.
     */
    public void setGain(double gain) {
        this.gain = Math.max(0, Math.min(gain, 2));
    }

    public int getTranspose() {
        return transpose;
    }

    /**
     * Defines an offset of keys, up to 2 octaves up or down.
     * <p>Defaults to <b>0</b>.</p>
     *
     * @param transpose A value from -24 to 24.
     */
    public void setTranspose(int transpose) {
        this.transpose = Math.max(-24, Math.min(transpose, 24));
    }

    public int getFeedback() {
        return feedback;
    }

    /**
     * Defines the degree of self modulation of the oscillator defined by the selected algorithm as the one receiving feedback.
     *
     * @param feedback A value from 0 to 7.
     * @see com.jbatista.wmo.synthesis.Oscillator
     * @see AlgorithmPreset
     */
    public void setFeedback(int feedback) {
        this.feedback = Math.max(0, Math.min(feedback, 7));
    }

    public int getPitchAttackLevel() {
        return pitchAttackLevel;
    }

    /**
     * Defines the <b>attack</b> stage frequency ratio level of the pitch envelope generator.
     * <p>Defaults to <b>50</b>.</p>
     *
     * @param pitchAttackLevel A value from 0 to 99.
     * @see com.jbatista.wmo.synthesis.EnvelopeGenerator
     */
    public void setPitchAttackLevel(int pitchAttackLevel) {
        this.pitchAttackLevel = Math.max(0, Math.min(pitchAttackLevel, 99));
    }

    public int getPitchDecayLevel() {
        return pitchDecayLevel;
    }

    /**
     * Defines the <b>decay</b> stage frequency ratio level of the pitch envelope generator.
     * <p>Defaults to <b>50</b>.</p>
     *
     * @param pitchDecayLevel A value from 0 to 99.
     * @see com.jbatista.wmo.synthesis.EnvelopeGenerator
     */
    public void setPitchDecayLevel(int pitchDecayLevel) {
        this.pitchDecayLevel = Math.max(0, Math.min(pitchDecayLevel, 99));
    }

    public int getPitchSustainLevel() {
        return pitchSustainLevel;
    }

    /**
     * Defines the <b>sustain</b> stage frequency ratio level of the pitch envelope generator.
     * <p>Defaults to <b>50</b>.</p>
     *
     * @param pitchSustainLevel A value from 0 to 99.
     * @see com.jbatista.wmo.synthesis.EnvelopeGenerator
     */
    public void setPitchSustainLevel(int pitchSustainLevel) {
        this.pitchSustainLevel = Math.max(0, Math.min(pitchSustainLevel, 99));
    }

    public int getPitchReleaseLevel() {
        return pitchReleaseLevel;
    }

    /**
     * Defines the <b>release</b> stage frequency ratio level of the pitch envelope generator.
     * <p>Defaults to <b>50</b>.</p>
     *
     * @param pitchReleaseLevel A value from 0 to 99.
     * @see com.jbatista.wmo.synthesis.EnvelopeGenerator
     */
    public void setPitchReleaseLevel(int pitchReleaseLevel) {
        this.pitchReleaseLevel = Math.max(0, Math.min(pitchReleaseLevel, 99));
    }

    public int getPitchAttackSpeed() {
        return pitchAttackSpeed;
    }

    /**
     * Defines the speed of progression of the <b>attack</b> stage of the pitch envelope generator.
     * <p>Defaults to <b>99</b>.</p>
     *
     * @param pitchAttackSpeed A value from 0 to 99.
     */
    public void setPitchAttackSpeed(int pitchAttackSpeed) {
        this.pitchAttackSpeed = Math.max(0, Math.min(pitchAttackSpeed, 99));
    }

    public int getPitchDecaySpeed() {
        return pitchDecaySpeed;
    }

    /**
     * Defines the speed of progression of the <b>decay</b> stage of the pitch envelope generator.
     * <p>Defaults to <b>99</b>.</p>
     *
     * @param pitchDecaySpeed A value from 0 to 99.
     */
    public void setPitchDecaySpeed(int pitchDecaySpeed) {
        this.pitchDecaySpeed = Math.max(0, Math.min(pitchDecaySpeed, 99));
    }

    public int getPitchSustainSpeed() {
        return pitchSustainSpeed;
    }

    /**
     * Defines the speed of progression of the <b>sustain</b> stage of the pitch envelope generator.
     * <p>Defaults to <b>99</b>.</p>
     *
     * @param pitchSustainSpeed A value from 0 to 99.
     */
    public void setPitchSustainSpeed(int pitchSustainSpeed) {
        this.pitchSustainSpeed = Math.max(0, Math.min(pitchSustainSpeed, 99));
    }

    public int getPitchReleaseSpeed() {
        return pitchReleaseSpeed;
    }

    /**
     * Defines the speed of progression of the <b>release</b> stage of the pitch envelope generator.
     * <p>Defaults to <b>99</b>.</p>
     *
     * @param pitchReleaseSpeed A value from 0 to 99.
     */
    public void setPitchReleaseSpeed(int pitchReleaseSpeed) {
        this.pitchReleaseSpeed = Math.max(0, Math.min(pitchReleaseSpeed, 99));
    }

    public boolean isOscillatorKeySync() {
        return oscillatorKeySync;
    }

    /**
     * Defines if the phase of the oscillators is going to be the same for every key.
     * <p>Defaults to <b>false</b>.</p>
     *
     * @param oscillatorKeySync boolean
     */
    public void setOscillatorKeySync(boolean oscillatorKeySync) {
        this.oscillatorKeySync = oscillatorKeySync;
    }

    public boolean isLfoKeySync() {
        return lfoKeySync;
    }

    /**
     * Defines if the LFO phase is going to be the same for every key.
     * <p>Defaults to <b>false</b>.</p>
     *
     * @param lfoKeySync boolean
     */
    public void setLfoKeySync(boolean lfoKeySync) {
        this.lfoKeySync = lfoKeySync;
    }

    public int getLfoSpeed() {
        return lfoSpeed;
    }

    /**
     * Defines the frequency of the LFO.
     * <p>The frequency is adjusted from 0.0625 Hz to 47.1744 Hz.</p>
     *
     * @param lfoSpeed A value from 0 to 99.
     */
    public void setLfoSpeed(int lfoSpeed) {
        this.lfoSpeed = Math.max(0, Math.min(lfoSpeed, 99));
    }

    public int getLfoDelay() {
        return lfoDelay;
    }

    /**
     * Defines how long the LFO is going take to activate after a key press.
     * <p>Defaults to <b>0</b>.</p>
     *
     * @param lfoDelay A value from 0 to 99.
     */
    public void setLfoDelay(int lfoDelay) {
        this.lfoDelay = Math.max(0, Math.min(lfoDelay, 99));
    }

    public int getLfoPmDepth() {
        return lfoPmDepth;
    }

    /**
     * Alters the LFO wave shape.
     * <p>Defaults to <b>0</b>.</p>
     *
     * @param lfoPmDepth A value from 0 to 99.
     */
    public void setLfoPmDepth(int lfoPmDepth) {
        this.lfoPmDepth = Math.max(0, Math.min(lfoPmDepth, 99));
    }

    public int getLfoAmDepth() {
        return lfoAmDepth;
    }

    /**
     * Alters the LFO wave shape.
     * <p>Defaults to <b>0</b>.</p>
     *
     * @param lfoAmDepth A value from 0 to 99.
     */
    public void setLfoAmDepth(int lfoAmDepth) {
        this.lfoAmDepth = Math.max(0, Math.min(lfoAmDepth, 99));
    }

    public int getLfoPModeSensitivity() {
        return lfoPModeSensitivity;
    }

    /**
     * Defines how much the LFO wave is going to vary its amplitude.
     * <p>Defaults to <b>0</b>.</p>
     *
     * @param lfoPModeSensitivity A value from 0 to 7.
     */
    public void setLfoPModeSensitivity(int lfoPModeSensitivity) {
        this.lfoPModeSensitivity = Math.max(0, Math.min(lfoPModeSensitivity, 7));
    }

    public WaveForm getLfoWave() {
        return lfoWave;
    }

    /**
     * Defines the how the LFO is going to affect the oscillators.
     * <p>Defaults to <b>{@link WaveForm#TRIANGLE}</b>.</p>
     *
     * @param lfoWave A wave form.
     */
    public void setLfoWave(WaveForm lfoWave) {
        this.lfoWave = lfoWave;
    }

    public AlgorithmPreset getAlgorithm() {
        return algorithm;
    }

    /**
     * Defines how the instrument will sound.
     * <p>Defaults to <b>{@link AlgorithmPreset#ALGO_1_OSC_1}</b>.</p>
     *
     * @param algorithm An algorithm.
     */
    public void setAlgorithm(AlgorithmPreset algorithm) {
        this.algorithm = algorithm;
    }

    public OscillatorPreset[] getOscillatorPresets() {
        return oscillatorPresets;
    }

    /**
     * Puts an oscillator preset on the list of presets according to its ID.
     *
     * @param oscillatorPreset A preset.
     */
    public void addOscillatorPreset(OscillatorPreset oscillatorPreset) {
        this.oscillatorPresets[oscillatorPreset.getId()] = oscillatorPreset;
    }

}
