package com.jbatista.wmo.synthesis;

import com.jbatista.wmo.KeyboardNote;
import com.jbatista.wmo.TransitionCurve;

/**
 * Performs volume level scaling on keys, making a key sound quieter or louder when going up or down the keyboard.
 * <p>Instances of this class are created by the {@link Oscillator} class.</p>
 * <ul>
 *     <li>Setting a {@link #setNote note} marks the central point where the volume will change from left and right of it.</li>
 *     <li>Setting the {@link #setLeftCurve left} and {@link #setRightCurve right} curves tells if the volume will increase or decrease, in linear or exponential progression.</li>
 *     <li>Setting the {@link #setLeftDepth left} and {@link #setRightDepth right} depths tells how fast the volume change is going to happen.</li>
 * </ul>
 *
 * @see Oscillator
 * @see KeyboardNote
 * @see TransitionCurve
 */
public class Breakpoint {

    private KeyboardNote note = KeyboardNote.A_4;
    private int breakpoint = 81;

    private TransitionCurve leftCurve = TransitionCurve.LINEAR_DECREASE;
    private TransitionCurve rightCurve = TransitionCurve.LINEAR_DECREASE;

    private int leftDepth = 0;
    private int rightDepth = 0;

    Breakpoint() {
    }

    // <editor-fold defaultstate="collapsed" desc="getters/setters">
    public KeyboardNote getNote() {
        return note;
    }

    /**
     * @param note The starting point of the breakpoint.
     * @see com.jbatista.wmo.preset.OscillatorPreset#setBreakpointNote
     */
    public void setNote(KeyboardNote note) {
        if (note.getId() < 21) {
            note = KeyboardNote.A_MINUS_1;
        } else if (note.getId() > 120) {
            note = KeyboardNote.C_8;
        }

        this.note = note;
        this.breakpoint = note.getId();
    }

    /**
     * @return The shape of the progression curve.
     * @see com.jbatista.wmo.preset.OscillatorPreset#getBreakpointLeftCurve
     */
    public TransitionCurve getLeftCurve() {
        return leftCurve;
    }

    /**
     * @param leftCurve The shape of the progression curve.
     * @see com.jbatista.wmo.preset.OscillatorPreset#setBreakpointLeftCurve
     */
    public void setLeftCurve(TransitionCurve leftCurve) {
        this.leftCurve = leftCurve;
    }

    /**
     * @return The shape of the progression curve.
     * @see com.jbatista.wmo.preset.OscillatorPreset#getBreakpointRightCurve
     */
    public TransitionCurve getRightCurve() {
        return rightCurve;
    }

    /**
     * @param rightCurve The shape of the progression curve.
     * @see com.jbatista.wmo.preset.OscillatorPreset#setBreakpointRightCurve
     */
    public void setRightCurve(TransitionCurve rightCurve) {
        this.rightCurve = rightCurve;
    }

    /**
     * @return The speed at which the volume change will occur, to the left of the breakpoint.
     * @see com.jbatista.wmo.preset.OscillatorPreset#getBreakpointLeftDepth
     */
    public int getLeftDepth() {
        return leftDepth;
    }

    /**
     * @param leftDepth The speed at which the volume change will occur, to the left of the breakpoint.
     * @see com.jbatista.wmo.preset.OscillatorPreset#setBreakpointLeftDepth
     */
    public void setLeftDepth(int leftDepth) {
        this.leftDepth = Math.max(0, Math.min(leftDepth, 99));
    }

    /**
     * @return The speed at which the volume change will occur, to the right of the breakpoint.
     * @see com.jbatista.wmo.preset.OscillatorPreset#getBreakpointRightDepth
     */
    public int getRightDepth() {
        return rightDepth;
    }

    /**
     * @param rightDepth The speed at which the volume change will occur, to the right of the breakpoint.
     * @see com.jbatista.wmo.preset.OscillatorPreset#setBreakpointRightDepth
     */
    public void setRightDepth(int rightDepth) {
        this.rightDepth = Math.max(0, Math.min(rightDepth, 99));
    }
    // </editor-fold>

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

        if (keyId < breakpoint) {
            if (leftDepth == 0) {
                return 0;
            }

            curve = leftCurve;
            depth = leftDepth;
            distance = breakpoint - keyId;
        } else if (keyId > breakpoint) {
            if (rightDepth == 0) {
                return 0;
            }

            curve = rightCurve;
            depth = rightDepth;
            distance = keyId - breakpoint;
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

    private int expCalc(int distance, int depth) {
        return (int) (Math.exp((distance - 72) / 13.5) * depth);
    }

    private int linCalc(int distance, int depth) {
        return (int) (distance / 45d * depth);
    }

}
