package com.jbatista.wmo.synthesis;

import com.jbatista.wmo.KeyboardNote;
import com.jbatista.wmo.TransitionCurve;
import com.jbatista.wmo.preset.OscillatorPreset;

/**
 * Performs volume level scaling on keys, making a key sound quieter or louder when going up or down the keyboard.
 * <p>Instances of this class are created by the {@link Oscillator} class.</p>
 * <p>Internally, breakpoints are calculated based on the key ID. So it is important to pass the correct value to {@link Instrument#pressKey(int)}.</p>
 * <ul>
 *     <li>Setting a {@link com.jbatista.wmo.preset.OscillatorPreset#setBreakpointNote(KeyboardNote) note} marks the central point where the volume will change from left and right of it.</li>
 *     <li>Setting the {@link com.jbatista.wmo.preset.OscillatorPreset#setBreakpointLeftCurve(TransitionCurve) left} and {@link com.jbatista.wmo.preset.OscillatorPreset#setBreakpointRightCurve(TransitionCurve) right} curves tells if the volume will increase or decrease, in linear or exponential progression.</li>
 *     <li>Setting the {@link com.jbatista.wmo.preset.OscillatorPreset#setBreakpointLeftDepth(int) left} and {@link com.jbatista.wmo.preset.OscillatorPreset#setBreakpointRightDepth(int) right} depths tells how fast the volume change is going to happen.</li>
 * </ul>
 *
 * @see Oscillator
 * @see KeyboardNote
 * @see TransitionCurve
 */
public class Breakpoint {

    private final int oscillatorId;
    private final Instrument instrument;

    Breakpoint(int oscillatorId, Instrument instrument) {
        this.oscillatorId = oscillatorId;
        this.instrument = instrument;
    }

    private int expCalc(int distance, int depth) {
        return (int) (Math.exp((distance - 72) / 13.5) * depth);
    }

    private int linCalc(int distance, int depth) {
        return (int) (distance / 45d * depth);
    }

    private OscillatorPreset oscillatorPreset() {
        return instrument.preset.getOscillatorPresets()[oscillatorId];
    }

    /**
     * Gives the output level parameter offset, to be added to the oscillator output level parameter. Based on breakpoint position, curve shape, and curve depth.
     * <p>This logic is based on hexter's <a href="https://github.com/smbolton/hexter/blob/737dbb04c407184fae0e203c1d73be8ad3fd55ba/src/dx7_voice.c#L500">dx7_voice.c</a></p>
     *
     * @param keyId ID representing an unique key, in the range of 0 to 131.
     * @return
     * @see <a href="https://github.com/smbolton/hexter">hexter</a>.
     */
    int getLevelOffset(int keyId) {
        final int offset;
        final int distance;
        final int depth;
        final TransitionCurve curve;

        if (keyId < oscillatorPreset().getBreakpointNote().getId()) {
            if (oscillatorPreset().getBreakpointLeftDepth() == 0) {
                return 0;
            }

            curve = oscillatorPreset().getBreakpointLeftCurve();
            depth = oscillatorPreset().getBreakpointLeftDepth();
            distance = oscillatorPreset().getBreakpointNote().getId() - keyId;

        } else if (keyId > oscillatorPreset().getBreakpointNote().getId()) {
            if (oscillatorPreset().getBreakpointRightDepth() == 0) {
                return 0;
            }

            curve = oscillatorPreset().getBreakpointRightCurve();
            depth = oscillatorPreset().getBreakpointRightDepth();
            distance = keyId - oscillatorPreset().getBreakpointNote().getId();
        } else {
            return 0;
        }

        switch (curve) {
            case LINEAR_INCREASE:
                offset = linCalc(distance, depth);
                break;

            case LINEAR_DECREASE:
                offset = -linCalc(distance, depth);
                break;

            case EXP_INCREASE:
                offset = expCalc(distance, depth);
                break;

            case EXP_DECREASE:
                offset = -expCalc(distance, depth);
                break;

            default:
                offset = 0;
                break;
        }

        return offset;
    }

}
