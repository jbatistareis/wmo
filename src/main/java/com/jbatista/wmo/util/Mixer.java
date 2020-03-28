package com.jbatista.wmo.util;

import com.jbatista.wmo.synthesis.FilterChain;
import com.jbatista.wmo.synthesis.Instrument;

/**
 * Aggregates the output of a series of instruments together, producing a single PCM frame.
 * <p>Audio is obtained the same way of an instrument.</p>
 *
 * @see Instrument
 */
public class Mixer {

    private final Instrument[] instruments;
    private final FilterChain filterChain = new FilterChain();

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

    public FilterChain getFilterChain() {
        return filterChain;
    }

    /**
     * Defines how loud the final output will be.
     *
     * @param masterGain A value from 0 to 2.
     */
    public void setMasterGain(double masterGain) {
        this.masterGain = Math.max(0, Math.min(masterGain, 2));
    }

    /**
     * Defines how loud a specific instrument will be.
     *
     * @param position The position of an added instrument.
     * @param gain     A value from 0 to 1.
     */
    public void setGain(int position, double gain) {
        instruments[position].getPreset().setGain(gain);
    }

    /**
     * @return A single audio frame.
     * @see Instrument#getSample()
     */
    public double getSample() {
        frameSample = 0;

        for (index = 0; index < instruments.length; index++) {
            frameSample += instruments[index].getSample();
        }

        frameSample = masterGain * filterChain.getResult(frameSample);

        return frameSample;
    }

    /**
     * @return A single audio frame.
     * @see Instrument#getByteFrame(boolean)
     */
    public byte[] getByteFrame(boolean bigEndian) {
        getSample();
        frameSample *= 32768;

        // TODO channel stuff, [L][R]
        MathFunctions.primitiveTo16bit(bigEndian, buffer16bit, 0, (int) frameSample);
        MathFunctions.primitiveTo16bit(bigEndian, buffer16bit, 2, (int) frameSample);

        return buffer16bit;
    }

    /**
     * @return A single audio frame.
     * @see Instrument#getShortFrame()
     */
    public short[] getShortFrame() {
        getSample();

        // TODO channel stuff, [L][R]
        shortBuffer[0] = (short) frameSample;
        shortBuffer[1] = (short) frameSample;

        return shortBuffer;
    }

    /**
     * @return A single audio frame.
     * @see Instrument#getFloatFrame()
     */
    public float[] getFloatFrame() {
        getSample();

        // TODO channel stuff, [L][R]
        floatBuffer[0] = (float) frameSample;
        floatBuffer[1] = (float) frameSample;

        return floatBuffer;
    }

}
