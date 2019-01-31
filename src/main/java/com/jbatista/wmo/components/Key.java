package com.jbatista.wmo.components;

import com.jbatista.wmo.WaveForm;

public class Key {

    private static final double _2xPI = 2 * Math.PI;
    private static final double _2dPI = 2 / Math.PI;

    private final byte[] buffer = new byte[]{0, 0, 0, 0};
    private final String name;
    private final Instrument instrument;

    private final WaveForm waveForm = WaveForm.SAWTOOTH;
    private double sampleRate = 44100;
    private double carrierFrequency = 440;
    // max 32768 (half of 16bit)
    private double amplitude = 15000;

    private double attack = 0.1;
    private double decay = 0;
    private double sustain = 1;
    private double release = 0.1;

    private short frameData;

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

    public Key(String name, Instrument instrument) {
        this.name = name;
        this.instrument = instrument;
    }

    // reacts to key press, apply envelope
    public byte[] getFrame() {
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
                frameData = (short) oscilator((attackAmplitude = attackAmplitude + attackStep), sampleRate, carrierFrequency, elapsed++);

                // L
                buffer[0] = (byte) (frameData >> 8);
                buffer[1] = (byte) frameData;

                // R
                buffer[2] = (byte) (frameData >> 8);
                buffer[3] = (byte) frameData;

                keyState = (attackFrames-- > 0) ? KeyState.ATTACK : (decayFrames > 0) ? KeyState.DECAY : KeyState.SUSTAIN;

                break;

            case DECAY:
                keyState = (decayFrames > 0) ? KeyState.DECAY : KeyState.SUSTAIN;

                break;

            case SUSTAIN:
                frameData = (short) oscilator(sustainAmplitude, sampleRate, carrierFrequency, elapsed++);

                // L
                buffer[0] = (byte) (frameData >> 8);
                buffer[1] = (byte) frameData;

                // R
                buffer[2] = (byte) (frameData >> 8);
                buffer[3] = (byte) frameData;

                break;

            case RELEASE:
                frameData = (short) oscilator((releaseAmplitude = releaseAmplitude - releaseStep), sampleRate, carrierFrequency, elapsed++);

                // L
                buffer[0] = (byte) (frameData >> 8);
                buffer[1] = (byte) frameData;

                // R
                buffer[2] = (byte) (frameData >> 8);
                buffer[3] = (byte) frameData;

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

        return buffer;
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

    private double oscilator(double amplitude, double sampleRate, double carrierFrequency, long frame) {
        switch (waveForm) {
            case SINE:
                return amplitude * Math.sin(_2xPI * (carrierFrequency / sampleRate) * frame);
            case SQUARE:
                return amplitude * Math.signum(Math.sin(_2xPI * (carrierFrequency / sampleRate) * frame));
            case TRIANGLE:
                return amplitude * _2dPI * Math.asin(Math.sin(_2xPI * (carrierFrequency / sampleRate) * frame));
            case SAWTOOTH:
                return amplitude * ((frame + sampleRate / carrierFrequency * 2) % (sampleRate / carrierFrequency)) / (sampleRate / carrierFrequency) - amplitude / 2;
            default:
                throw new AssertionError(waveForm.name());
        }
    }

    private double modulator() {
        return 0;
    }

}
