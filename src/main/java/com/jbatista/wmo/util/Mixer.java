package com.jbatista.wmo.util;

import com.jbatista.wmo.synthesis.Instrument;

public class Mixer {

    private final Instrument[] instruments;

    private int index;
    private double masterGain = 1;
    private double frameSample;
    private final byte[] buffer16bit = new byte[]{0, 0, 0, 0};
    private final short[] shortBuffer = new short[]{0, 0};
    private final float[] floatBuffer = new float[]{0, 0};

    public Mixer(Instrument... instruments) {
        this.instruments = instruments;
    }

    public double getMasterGain() {
        return masterGain;
    }

    public void setMasterGain(double masterGain) {
        this.masterGain = Math.max(0, Math.min(masterGain, 2));
    }

    public void setGain(int id, double value) {
        instruments[id].setGain(value);
    }

    public double getSample() {
        frameSample = 0;

        for (index = 0; index < instruments.length; index++) {
            frameSample += instruments[index].getSample();
        }

        frameSample *= masterGain;

        return frameSample;
    }

    public byte[] getByteFrame(boolean bigEndian) {
        getSample();
        frameSample *= 32768;

        // TODO channel stuff, [L][R]
        MathFunctions.primitiveTo16bit(bigEndian, buffer16bit, 0, (int) frameSample);
        MathFunctions.primitiveTo16bit(bigEndian, buffer16bit, 2, (int) frameSample);

        return buffer16bit;
    }

    public short[] getShortFrame() {
        getSample();

        // TODO channel stuff, [L][R]
        shortBuffer[0] = (short) frameSample;
        shortBuffer[1] = (short) frameSample;

        return shortBuffer;
    }

    public float[] getFloatFrame() {
        getSample();

        // TODO channel stuff, [L][R]
        floatBuffer[0] = (float) frameSample;
        floatBuffer[1] = (float) frameSample;

        return floatBuffer;
    }

}
