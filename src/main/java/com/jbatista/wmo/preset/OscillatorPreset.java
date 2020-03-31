package com.jbatista.wmo.preset;

import com.jbatista.wmo.KeyboardNote;
import com.jbatista.wmo.TransitionCurve;
import com.jbatista.wmo.WaveForm;

/**
 * Holds every setting related to the oscillator: frequency, wave form, envelope generator, and breakpoint.
 * <p>To be implemented:</p>
 * <ul>
 *     <li>Velocity sensitivity.</li>
 *     <li>AM sensitivity.</li>
 *     <li>Speed scaling.</li>
 * </ul>
 *
 * @see WaveForm
 * @see com.jbatista.wmo.synthesis.EnvelopeGenerator
 * @see com.jbatista.wmo.synthesis.Breakpoint
 */
public class OscillatorPreset {

    private int id = 0;

    private WaveForm waveForm = WaveForm.SINE;

    private boolean mute = false;

    private double frequencyRatio = 1;
    private boolean fixedFrequency = false;
    private int frequencyFine = 0;
    private int frequencyDetune = 0;
    private int outputLevel = 99;
    private int velocitySensitivity = 0;
    private int amSensitivity = 0;

    private int attackLevel = 99;
    private int decayLevel = 99;
    private int sustainLevel = 99;
    private int releaseLevel = 0;

    private int attackSpeed = 99;
    private int decaySpeed = 99;
    private int sustainSpeed = 99;
    private int releaseSpeed = 99;

    private KeyboardNote breakpointNote = KeyboardNote.A_4;
    private TransitionCurve breakpointLeftCurve = TransitionCurve.LINEAR_DECREASE;
    private TransitionCurve breakpointRightCurve = TransitionCurve.LINEAR_DECREASE;
    private int breakpointLeftDepth = 0;
    private int breakpointRightDepth = 0;
    private int speedScaling = 0;

    /**
     * @param id An unique ID, from 0 to 5, used to identify de corresponding oscillator on the algorithm.
     * @see com.jbatista.wmo.synthesis.Algorithm
     */
    public OscillatorPreset(int id) {
        this.id = Math.max(0, Math.min(id, 5));
    }

    /**
     * Copy constructor.
     *
     * @param oscillatorPreset The preset to be copied.
     */
    public OscillatorPreset(OscillatorPreset oscillatorPreset) {
        this.id = oscillatorPreset.id;

        this.waveForm = oscillatorPreset.waveForm;

        this.mute = oscillatorPreset.mute;

        this.frequencyRatio = oscillatorPreset.frequencyRatio;
        this.fixedFrequency = oscillatorPreset.fixedFrequency;
        this.frequencyFine = oscillatorPreset.frequencyFine;
        this.frequencyDetune = oscillatorPreset.frequencyDetune;
        this.outputLevel = oscillatorPreset.outputLevel;
        this.velocitySensitivity = oscillatorPreset.velocitySensitivity;
        this.amSensitivity = oscillatorPreset.amSensitivity;

        this.attackLevel = oscillatorPreset.attackLevel;
        this.decayLevel = oscillatorPreset.decayLevel;
        this.sustainLevel = oscillatorPreset.sustainLevel;
        this.releaseLevel = oscillatorPreset.releaseLevel;

        this.attackSpeed = oscillatorPreset.attackSpeed;
        this.decaySpeed = oscillatorPreset.decaySpeed;
        this.sustainSpeed = oscillatorPreset.sustainSpeed;
        this.releaseSpeed = oscillatorPreset.releaseSpeed;

        this.breakpointNote = oscillatorPreset.breakpointNote;
        this.breakpointLeftCurve = oscillatorPreset.breakpointLeftCurve;
        this.breakpointRightCurve = oscillatorPreset.breakpointRightCurve;
        this.breakpointLeftDepth = oscillatorPreset.breakpointLeftDepth;
        this.breakpointRightDepth = oscillatorPreset.breakpointRightDepth;
        this.speedScaling = oscillatorPreset.speedScaling;
    }

    public int getId() {
        return id;
    }

    public WaveForm getWaveForm() {
        return waveForm;
    }

    /**
     * Defines the type of sound the oscillator is going to produce.
     * <p>{@link WaveForm#WHITE_NOISE} ignores the frequency parameters.</p>
     * <p>Defaults to <b>{@link WaveForm#SINE}</b>.</p>
     *
     * @param waveForm A wave form.
     */
    public void setWaveForm(WaveForm waveForm) {
        this.waveForm = waveForm;
    }

    public boolean isMute() {
        return mute;
    }

    public void setMute(boolean mute) {
        this.mute = mute;
    }

    public double getFrequencyRatio() {
        return frequencyRatio;
    }

    /**
     * Defines how the oscillator is going to alter the frequency of the pressed key.
     * <p>Affected by {@link #setFixedFrequency(boolean)}, {@link #setFrequencyFine(int)}, and {@link #setFrequencyDetune(int)}</p>
     * <p>Defaults to <b>1</b>.</p>
     *
     * @param frequencyRatio A value from 0 to 31.
     */
    public void setFrequencyRatio(double frequencyRatio) {
        this.frequencyRatio = Math.max(0, Math.min(frequencyRatio, 31));
    }

    public boolean isFixedFrequency() {
        return fixedFrequency;
    }

    /**
     * Defines the mode that the oscillator is going to change the frequency of pressed key.
     * <p>True means that the frequency is going to be one of 1Hz, 10Hz, 100Hz, or 1000Hz.</p>
     * <p>False means that the pressed key frequency is going to to be multiplied by a ratio from 0.5 to 31.</p>
     * <p>Defaults to <b>false</b>.</p>
     *
     * @param fixedFrequency boolean
     * @see #setFrequencyRatio(double)
     */
    public void setFixedFrequency(boolean fixedFrequency) {
        this.fixedFrequency = fixedFrequency;
    }

    public int getFrequencyFine() {
        return frequencyFine;
    }

    /**
     * Adds a fine tune, from 0.00 to 0.99, over the frequency ratio.
     * <p>Defaults to <b>0</b>.</p>
     *
     * @param frequencyFine A value from 0 to 99.
     * @see #setFrequencyRatio(double)
     */
    public void setFrequencyFine(int frequencyFine) {
        this.frequencyFine = Math.max(0, Math.min(frequencyFine, 99));
    }

    public int getFrequencyDetune() {
        return frequencyDetune;
    }

    /**
     * Defines a very little change in the pressed key frequency.
     * <p>Defaults to <b>0</b>.</p>
     *
     * @param frequencyDetune A value from -7 to 7.
     */
    public void setFrequencyDetune(int frequencyDetune) {
        this.frequencyDetune = Math.max(-7, Math.min(frequencyDetune, 7));
    }

    public int getOutputLevel() {
        return outputLevel;
    }

    /**
     * Defines how loud an how much this oscillator is going to affect the oscillators that are modulated by it.
     * <p>Defaults to <b>99</b>.</p>
     *
     * @param outputLevel A value from 0 to 99.
     */
    public void setOutputLevel(int outputLevel) {
        this.outputLevel = Math.max(0, Math.min(outputLevel, 99));
    }

    public int getVelocitySensitivity() {
        return velocitySensitivity;
    }

    /**
     * Defines how much the output level will be affected based on the velocity of the key press.
     * <p>Defaults to <b>0</b>.</p>
     *
     * @param velocitySensitivity A value from 0 to 7.
     * @see #setOutputLevel(int)
     */
    public void setVelocitySensitivity(int velocitySensitivity) {
        this.velocitySensitivity = Math.max(0, Math.min(velocitySensitivity, 7));
    }

    public int getAmSensitivity() {
        return amSensitivity;
    }

    /**
     * Defines how much the LFO will affect the oscillator amplitude.
     * <p>Defaults to <b>0</b>.</p>
     *
     * @param amSensitivity A value from 0 to 3.
     */
    public void setAmSensitivity(int amSensitivity) {
        this.amSensitivity = Math.max(0, Math.min(amSensitivity, 3));
    }

    public int getAttackLevel() {
        return attackLevel;
    }

    /**
     * Defines the <b>attack</b> stage target output level of the envelope generator.
     * <p>Defaults to <b>99</b>.</p>
     *
     * @param attackLevel A value from 0 to 99.
     * @see com.jbatista.wmo.synthesis.EnvelopeGenerator
     */
    public void setAttackLevel(int attackLevel) {
        this.attackLevel = Math.max(0, Math.min(attackLevel, 99));
    }

    public int getDecayLevel() {
        return decayLevel;
    }

    /**
     * Defines the <b>decay</b> stage target output level of the envelope generator.
     * <p>Defaults to <b>99</b>.</p>
     *
     * @param decayLevel A value from 0 to 99.
     * @see com.jbatista.wmo.synthesis.EnvelopeGenerator
     */
    public void setDecayLevel(int decayLevel) {
        this.decayLevel = Math.max(0, Math.min(decayLevel, 99));
    }

    public int getSustainLevel() {
        return sustainLevel;
    }

    /**
     * Defines the <b>sustain</b> stage target output level of the envelope generator.
     * <p>Defaults to <b>99</b>.</p>
     *
     * @param sustainLevel A value from 0 to 99.
     * @see com.jbatista.wmo.synthesis.EnvelopeGenerator
     */
    public void setSustainLevel(int sustainLevel) {
        this.sustainLevel = Math.max(0, Math.min(sustainLevel, 99));
    }

    public int getReleaseLevel() {
        return releaseLevel;
    }


    /**
     * Defines the <b>release</b> stage target output level of the envelope generator.
     * <p>Defaults to <b>0</b>.</p>
     *
     * @param releaseLevel A value from 0 to 99.
     * @see com.jbatista.wmo.synthesis.EnvelopeGenerator
     */
    public void setReleaseLevel(int releaseLevel) {
        this.releaseLevel = Math.max(0, Math.min(releaseLevel, 99));
    }

    public int getAttackSpeed() {
        return attackSpeed;
    }

    /**
     * Defines the speed of progression of the <b>attack</b> stage of the envelope generator.
     * <p>Defaults to <b>99</b>.</p>
     *
     * @param attackSpeed A value from 0 to 99.
     * @see com.jbatista.wmo.synthesis.EnvelopeGenerator
     */
    public void setAttackSpeed(int attackSpeed) {
        this.attackSpeed = Math.max(0, Math.min(attackSpeed, 99));
    }

    public int getDecaySpeed() {
        return decaySpeed;
    }

    /**
     * Defines the speed of progression of the <b>decay</b> stage of the envelope generator.
     * <p>Defaults to <b>99</b>.</p>
     *
     * @param decaySpeed A value from 0 to 99.
     * @see com.jbatista.wmo.synthesis.EnvelopeGenerator
     */
    public void setDecaySpeed(int decaySpeed) {
        this.decaySpeed = Math.max(0, Math.min(decaySpeed, 99));
    }

    public int getSustainSpeed() {
        return sustainSpeed;
    }

    /**
     * Defines the speed of progression of the <b>sustain</b> stage of the envelope generator.
     * <p>Defaults to <b>99</b>.</p>
     *
     * @param sustainSpeed A value from 0 to 99.
     * @see com.jbatista.wmo.synthesis.EnvelopeGenerator
     */
    public void setSustainSpeed(int sustainSpeed) {
        this.sustainSpeed = Math.max(0, Math.min(sustainSpeed, 99));
    }

    public int getReleaseSpeed() {
        return releaseSpeed;
    }

    /**
     * Defines the speed of progression of the <b>release</b> stage of the envelope generator.
     * <p>Defaults to <b>99</b>.</p>
     *
     * @param releaseSpeed A value from 0 to 99.
     * @see com.jbatista.wmo.synthesis.EnvelopeGenerator
     */
    public void setReleaseSpeed(int releaseSpeed) {
        this.releaseSpeed = Math.max(0, Math.min(releaseSpeed, 99));
    }

    public KeyboardNote getBreakpointNote() {
        return breakpointNote;
    }

    /**
     * Defines the center note used for volume level scaling.
     *
     * @param breakpointNote The center note for breakpoint calculations.
     * @see com.jbatista.wmo.synthesis.Breakpoint
     */
    public void setBreakpointNote(KeyboardNote breakpointNote) {
        if (breakpointNote.getId() < 21) {
            this.breakpointNote = KeyboardNote.A_MINUS_1;
        } else if (breakpointNote.getId() > 120) {
            this.breakpointNote = KeyboardNote.C_8;
        } else {
            this.breakpointNote = breakpointNote;
        }
    }

    public TransitionCurve getBreakpointLeftCurve() {
        return breakpointLeftCurve;
    }

    /**
     * Defines if the volume level will increase or decrease, in linear or exponential progression, to the left of the breakpoint.
     *
     * @param breakpointLeftCurve The shape of the progression curve.
     * @see com.jbatista.wmo.synthesis.Breakpoint
     */
    public void setBreakpointLeftCurve(TransitionCurve breakpointLeftCurve) {
        this.breakpointLeftCurve = breakpointLeftCurve;
    }

    public TransitionCurve getBreakpointRightCurve() {
        return breakpointRightCurve;
    }

    /**
     * Defines if the volume level will increase or decrease, in linear or exponential progression, to the right of the breakpoint.
     *
     * @param breakpointRightCurve The shape of the progression curve.
     * @see com.jbatista.wmo.synthesis.Breakpoint
     */
    public void setBreakpointRightCurve(TransitionCurve breakpointRightCurve) {
        this.breakpointRightCurve = breakpointRightCurve;
    }

    public int getBreakpointLeftDepth() {
        return breakpointLeftDepth;
    }

    /**
     * Defines how fast the volume level change is going to happen, to the left of the breakpoint.
     *
     * @param breakpointLeftDepth A value from 0 to 99.
     * @see com.jbatista.wmo.synthesis.Breakpoint
     */
    public void setBreakpointLeftDepth(int breakpointLeftDepth) {
        this.breakpointLeftDepth = Math.max(0, Math.min(breakpointLeftDepth, 99));
    }

    public int getBreakpointRightDepth() {
        return breakpointRightDepth;
    }

    /**
     * Defines how fast the volume level change is going to happen, to the right of the breakpoint.
     *
     * @param breakpointRightDepth A value from 0 to 99.
     * @see com.jbatista.wmo.synthesis.Breakpoint
     */
    public void setBreakpointRightDepth(int breakpointRightDepth) {
        this.breakpointRightDepth = Math.max(0, Math.min(breakpointRightDepth, 99));
    }

    public int getSpeedScaling() {
        return speedScaling;
    }

    /**
     * Defines the factor of how faster envelopes will be as they move from lower to higher notes.
     *
     * @param speedScaling A value from 0 to 7.
     */
    public void setSpeedScaling(int speedScaling) {
        this.speedScaling = Math.max(0, Math.min(speedScaling, 7));
    }

}
