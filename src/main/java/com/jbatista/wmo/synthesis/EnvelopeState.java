package com.jbatista.wmo.synthesis;

/**
 * Represents the various stages of the envelope generator.
 * <p>The first four stages are the common ADSR stages, {@link #PRE_IDLE} represents the silencing of the oscillators after {@link #RELEASE} ends,
 * {@link #IDLE} represents 0 output, and {@link #HOLD} represents no envelope change after the {@link #SUSTAIN} ends. </p>
 */
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