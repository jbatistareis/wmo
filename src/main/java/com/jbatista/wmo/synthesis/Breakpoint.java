package com.jbatista.wmo.synthesis;

import com.jbatista.wmo.KeyboardNote;
import com.jbatista.wmo.TransitionCurve;
import com.jbatista.wmo.util.MathFunctions;

import java.util.Arrays;
import java.util.List;

public class Breakpoint {

    private KeyboardNote note = KeyboardNote.A_4;
    private int breakpoint = 81;
    private int lowerNote = 0;
    private int upperNote = 131;

    private TransitionCurve leftCurve = TransitionCurve.LINEAR_DECREASE;
    private TransitionCurve rightCurve = TransitionCurve.LINEAR_DECREASE;

    private int leftDepth = 0;
    private int rightDepth = 0;

    private int leftRange = 36;
    private int rightRange = 39;

    private static final List<KeyboardNote> NOTES = Arrays.asList(KeyboardNote.values());

    // <editor-fold defaultstate="collapsed" desc="getters/setters">
    public KeyboardNote getNote() {
        return note;
    }

    public void setNote(KeyboardNote note) {
        this.note = note;
        this.breakpoint = NOTES.indexOf(note);
        this.leftRange = breakpoint;
        this.rightRange = 131 - breakpoint;

        setLeftDepth(leftDepth);
        setRightDepth(rightDepth);
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
        this.lowerNote = (int) Math.max(0, Math.min((breakpoint - Math.ceil((leftRange * (100 - this.leftDepth)) / 100)), 131));
    }

    public int getRightDepth() {
        return rightDepth;
    }

    public void setRightDepth(int rightDepth) {
        this.rightDepth = Math.max(0, Math.min(rightDepth, 99));
        this.upperNote = (int) Math.max(0, Math.min((breakpoint + Math.ceil((leftRange * (100 - this.leftDepth)) / 100)), 131));
    }
    // </editor-fold>

    double getLevelOffset(int keyId) {
        final double ratio;
        final double offset;
        final TransitionCurve curve;

        if (keyId < breakpoint) {
            if (leftDepth == 0) {
                return 1;
            }

            curve = leftCurve;
            ratio = MathFunctions.percentageInRange(lowerNote, breakpoint, keyId) / 100;
        } else if (keyId > breakpoint) {
            if (rightDepth == 0) {
                return 1;
            }

            curve = rightCurve;
            ratio = (100 - MathFunctions.percentageInRange(breakpoint, upperNote, keyId)) / 100;
        } else {
            return 1;
        }

        switch (curve) {
            case LINEAR_INCREASE:
                offset = MathFunctions.linearInterpolation(1, 2, ratio);
                break;

            case LINEAR_DECREASE:
                offset = MathFunctions.linearInterpolation(0.005, 1, ratio);
                break;

            case SMOOTH_INCREASE:
                offset = MathFunctions.smoothInterpolation(1, 2, ratio);
                break;

            case SMOOTH_DECREASE:
                offset = MathFunctions.smoothInterpolation(0.005, 1, ratio);
                break;

            case EXP_INCREASE:
                offset = MathFunctions.expIncreaseInterpolation(1, 2, ratio);
                break;

            case EXP_DECREASE:
                offset = MathFunctions.expIncreaseInterpolation(0.005, 1, ratio);
                break;

            default:
                offset = 1;
                break;
        }

        return Math.max(0, Math.min(offset, 2));
    }

}
