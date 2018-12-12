package com.jbatista.wmo.components;

import com.jbatista.wmo.WaveForm;

public class Instrument {

    private String name = "Instrument";
    private WaveForm waveForm = WaveForm.SINE;
    private double sampleRate = 44100;
    private double carrierFrequency = 440;
    private double amplitude = 20;

    private double attack = 1;
    private double decay = 1;
    private double sustain = 1;
    private double release = 1;

    private long elapsed = 0;

    private static enum KeyState {
        PRESSED, RELEASE, IDLE
    }
    private KeyState keyState = KeyState.IDLE;

    public Instrument(String name) {
        this.name = name;
    }

    // reacts to key press, apply envelope
    public byte[] getByteArray(int size) {
        final byte[] data = new byte[size];

        switch (keyState) {
            // attack, decay, sustain
            case PRESSED:
                for (int i = 0; i < size; i++) {
                    data[i] = (byte) (amplitude * Math.sin((2 * Math.PI) * (carrierFrequency / sampleRate) * elapsed++));
                }

                break;

            // release
            case RELEASE:
                for (int i = 0; i < size; i++) {
                    data[i] = 0;
                }

                // after envelope
                keyState = KeyState.IDLE;

                break;

            // reset
            case IDLE:
                elapsed = 0;
                for (int i = 0; i < size; i++) {
                    data[i] = 0;
                }

                break;
        }

        return data;
    }

    public void pressKey() {
        keyState = KeyState.PRESSED;
    }

    public void releaseKey() {
        keyState = KeyState.RELEASE;
    }

}
