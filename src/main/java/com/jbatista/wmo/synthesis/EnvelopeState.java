package com.jbatista.wmo.synthesis;

enum EnvelopeState {
    ATTACK(0), DECAY(1), SUSTAIN(2), RELEASE(3), PRE_IDLE(4), HOLD(5), IDLE(6);

    private int id;

    EnvelopeState(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

}