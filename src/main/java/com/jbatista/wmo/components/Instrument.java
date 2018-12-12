package com.jbatista.wmo.components;

import com.jbatista.wmo.WaveForm;

public class Instrument {

    private String name = "Instrument";
    private WaveForm waveForm = WaveForm.SINE;
    private double sampleRate = 44100;
    private double carrierFrequency = 440;
    private double amplitude = 20;

    private double attack = 0.5;
    private double decay = 0;
    private double sustain = 1;
    private double release = 0.5;

    private byte frameByte;

    private double attackFrames;
    private double attackStep;
    private double attackAmplitude;

    private double decayFrames;
    private double decayStep;
    private double decayAmplitude;

    private double sustainAmplitude;

    private double releaseFrames;
    private double releaseStep;
    private double releaseAmplitude;

    private long elapsed = 0;

    private static enum KeyState {
        HIT, ATTACK, DECAY, SUSTAIN, RELEASE, IDLE
    }
    private KeyState keyState = KeyState.IDLE;

    public Instrument(String name) {
        this.name = name;
    }

    // reacts to key press, apply envelope
    public void getBytes(byte[] buffer) {
        switch (keyState) {
            // setup
            case HIT:
                elapsed = 0;

                sustainAmplitude = lerp(0, amplitude, sustain);

                attackFrames = sampleRate * attack;
                attackStep = amplitude / attackFrames;
                attackAmplitude = 0;

                decayFrames = sampleRate * decay;
                decayStep = (amplitude - sustainAmplitude) / decayFrames;
                decayAmplitude = amplitude;

                releaseFrames = sampleRate * release;
                releaseStep = sustainAmplitude / releaseFrames;
                releaseAmplitude = sustainAmplitude;

                keyState = (attackFrames > 0) ? KeyState.ATTACK : KeyState.SUSTAIN;

                break;

            case ATTACK:
                frameByte = (byte) oscilator((attackAmplitude = attackAmplitude + attackStep), (carrierFrequency / sampleRate), elapsed++);

                // L
                buffer[0] = frameByte;
                buffer[1] = frameByte;

                // R
                buffer[2] = frameByte;
                buffer[3] = frameByte;

                keyState = (attackFrames-- > 0) ? KeyState.ATTACK : (decayFrames > 0) ? KeyState.DECAY : KeyState.SUSTAIN;

                break;

            case DECAY:
                keyState = (decayFrames > 0) ? KeyState.DECAY : KeyState.SUSTAIN;

                break;

            case SUSTAIN:
                frameByte = (byte) oscilator(sustainAmplitude, (carrierFrequency / sampleRate), elapsed++);

                // L
                buffer[0] = frameByte;
                buffer[1] = frameByte;

                // R
                buffer[2] = frameByte;
                buffer[3] = frameByte;

                break;

            case RELEASE:
                frameByte = (byte) oscilator((releaseAmplitude = releaseAmplitude - releaseStep), (carrierFrequency / sampleRate), elapsed++);

                // L
                buffer[0] = frameByte;
                buffer[1] = frameByte;

                // R
                buffer[2] = frameByte;
                buffer[3] = frameByte;

                keyState = (releaseFrames-- > 0) ? KeyState.RELEASE : KeyState.IDLE;

                break;

            // reset
            case IDLE:
                // L
                buffer[0] = 0;
                buffer[1] = 0;

                // R
                buffer[2] = 0;
                buffer[3] = 0;

                break;
        }
    }

    public void pressKey() {
        keyState = KeyState.HIT;
    }

    public void releaseKey() {
        keyState = KeyState.RELEASE;
    }

    private double lerp(double start, double end, double factor) {
        return start + factor * (end - start);
    }

    private double oscilator(double amplitude, double phi, long frame) {
        return amplitude * Math.sin(2 * Math.PI * phi * frame);
    }

}
