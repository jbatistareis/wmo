package com.jbatista.wmo.synthesis;

import com.jbatista.wmo.KeyboardNote;
import com.jbatista.wmo.MathUtil;
import com.jbatista.wmo.TransitionCurve;

import java.util.Arrays;
import java.util.List;

public class Breakpoint {

    private KeyboardNote note = KeyboardNote.A_4;
    private int noteIndex = 81;

    private TransitionCurve leftCurve = TransitionCurve.LINEAR_DECREASE;
    private TransitionCurve rightCurve = TransitionCurve.LINEAR_DECREASE;

    private int leftDepth = 0;
    private int rightDepth = 0;

    private int leftRange = 36;
    private int rightRange = 39;

    private double centerFrequency = KeyboardNote.A_4.getFrequency();
    private double lowerFrequency = KeyboardNote.C_MINUS_2.getFrequency();
    private double upperFrequency = KeyboardNote.B_8.getFrequency();

    private static final List<KeyboardNote> NOTES = Arrays.asList(KeyboardNote.values());
    private static final int C_MINUS_2_INDEX = 0;
    private static final int B_8_INDEX = 131;

    // <editor-fold defaultstate="collapsed" desc="getters/setters">
    public KeyboardNote getNote() {
        return note;
    }

    public void setNote(KeyboardNote note) {
        this.note = note;
        this.noteIndex = NOTES.indexOf(note);
        this.leftRange = noteIndex - C_MINUS_2_INDEX;
        this.rightRange = B_8_INDEX - noteIndex;
        this.centerFrequency = this.note.getFrequency();
    }

    public TransitionCurve getLeftCurve() {
        return leftCurve;
    }

    public void setLeftCurve(TransitionCurve leftCurve) {
        this.leftCurve = leftCurve;
    }

    public TransitionCurve getRightCurve() {
        return rightCurve;
    }

    public void setRightCurve(TransitionCurve rightCurve) {
        this.rightCurve = rightCurve;
    }

    public int getLeftDepth() {
        return leftDepth;
    }

    public void setLeftDepth(int leftDepth) {
        this.leftDepth = Math.max(0, Math.min(leftDepth, 99));
        this.lowerFrequency = NOTES.get((int) (noteIndex - Math.ceil((leftRange * (100 - this.leftDepth)) / 100))).getFrequency();
    }

    public int getRightDepth() {
        return rightDepth;
    }

    public void setRightDepth(int rightDepth) {
        this.rightDepth = Math.max(0, Math.min(rightDepth, 99));
        this.upperFrequency = NOTES.get((int) (noteIndex + Math.ceil((rightRange * (100 - this.rightDepth)) / 100))).getFrequency();
    }
    // </editor-fold>

    double getLevelOffset(double frequency) {
        final double ratio;
        final double offset;
        final TransitionCurve curve;

        if (frequency < centerFrequency) {
            if (leftDepth == 0) {
                return 1;
            }

            curve = leftCurve;
            ratio = MathUtil.percentageInRange(lowerFrequency, centerFrequency, frequency) / 100;
        } else if (frequency > centerFrequency) {
            if (rightDepth == 0) {
                return 1;
            }

            curve = rightCurve;
            ratio = Math.abs(MathUtil.percentageInRange(centerFrequency, upperFrequency, frequency) - 100) / 100;
        } else {
            return 1;
        }

        switch (curve) {
            case LINEAR_INCREASE:
                offset = MathUtil.linearInterpolation(1, 0, ratio) + 1;
                break;

            case LINEAR_DECREASE:
                offset = MathUtil.linearInterpolation(0, 1, ratio);
                break;

            case SMOOTH_INCREASE:
                offset = MathUtil.smoothInterpolation(1, 0, ratio) + 1;
                break;

            case SMOOTH_DECREASE:
                offset = MathUtil.smoothInterpolation(0, 1, ratio);
                break;

            case EXP_INCREASE:
                offset = MathUtil.expIncreaseInterpolation(1, 0, ratio) + 1;
                break;

            case EXP_DECREASE:
                offset = MathUtil.expIncreaseInterpolation(0, 1, ratio);
                break;

            default:
                offset = 1;
                break;
        }

        return (offset < 0) ? 0 : offset;
    }

}
