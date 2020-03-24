package com.jbatista.wmo.synthesis;

import com.jbatista.wmo.KeyboardNote;
import com.jbatista.wmo.TransitionCurve;

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
    private static final int A_MINUS_1_INDEX = 21;
    private static final int C_8_INDEX = 120;

    // <editor-fold defaultstate="collapsed" desc="getters/setters">
    public KeyboardNote getNote() {
        return note;
    }

    public void setNote(KeyboardNote note) {
        int index = NOTES.indexOf(note);

        if (index < 21) {
            note = KeyboardNote.A_MINUS_1;
            index = A_MINUS_1_INDEX;
        } else if (index > 120) {
            note = KeyboardNote.C_8;
            index = C_8_INDEX;
        }

        this.note = note;
        this.breakpoint = index;
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
        this.upperNote = (int) Math.max(0, Math.min((breakpoint + Math.ceil((rightRange * (100 - this.rightDepth)) / 100)), 131));
    }
    // </editor-fold>

    // shamelessly copied from hexter [https://github.com/smbolton/hexter/blob/737dbb04c407184fae0e203c1d73be8ad3fd55ba/src/dx7_voice.c#L500]
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
