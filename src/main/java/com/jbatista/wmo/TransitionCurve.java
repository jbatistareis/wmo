package com.jbatista.wmo;

public enum TransitionCurve {
    LINEAR_DECREASE(0), EXP_DECREASE(1), EXP_INCREASE(2), LINEAR_INCREASE(3), SMOOTH_INCREASE(4), SMOOTH_DECREASE(5), LINEAR(6), SMOOTH(7);

    private int id;

    TransitionCurve(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

}