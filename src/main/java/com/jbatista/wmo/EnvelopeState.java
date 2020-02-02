package com.jbatista.wmo;

public enum EnvelopeState {
    ATTACK(0), DECAY(1), SUSTAIN(2), RELEASE(3), IDLE(6), HOLD(4), PRE_RELEASE(5), RELEASE_END(6);

    private int id;

    EnvelopeState(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

}