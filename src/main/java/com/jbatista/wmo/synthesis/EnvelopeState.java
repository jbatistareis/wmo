package com.jbatista.wmo.synthesis;

enum EnvelopeState {
    ATTACK(0), DECAY(1), SUSTAIN(2), RELEASE(3), IDLE(6), HOLD(4), RELEASE_END(5);

    private int id;

    EnvelopeState(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

}