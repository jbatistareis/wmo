package com.jbatista.wmo.synthesis;

import com.jbatista.wmo.KeyboardNote;
import com.jbatista.wmo.preset.InstrumentPreset;
import com.jbatista.wmo.util.MathFunctions;

/**
 * Represents an entire keyboard and its functions.
 * <p>This class provides means of controlling keys and obtaining audio, to control the parameters, use a {@link InstrumentPreset preset}.</p>
 * <p>Audio is obtained in PCM frames that can be written directly to audio outputs, obtaining a frame is the same as to read a PCM file.</p>
 *
 * @see InstrumentPreset
 * @see #setPreset(InstrumentPreset)
 */
public class Instrument {

    private static final KeyboardNote[] NOTES = KeyboardNote.values();

    private int keyId = 0;
    private final boolean[] keysQueue = new boolean[132];

    private final int sampleRate;
    final Algorithm algorithm;
    private final FilterChain filterChain = new FilterChain();
    InstrumentPreset preset = new InstrumentPreset();

    private double frameSample;
    private final byte[] buffer16bit = new byte[]{0, 0, 0, 0};
    private final short[] shortBuffer = new short[]{0, 0};
    private final float[] floatBuffer = new float[]{0, 0};

    public Instrument(int sampleRate) {
        this.sampleRate = sampleRate;
        this.algorithm = new Algorithm(sampleRate, this);
    }

    public int getSampleRate() {
        return sampleRate;
    }

    public FilterChain getFilterChain() {
        return filterChain;
    }

    public InstrumentPreset getPreset() {
        return preset;
    }

    public void setPreset(InstrumentPreset preset) {
        this.preset = preset;
    }

    /**
     * Creates a mono PCM frame.
     * <p>Not very useful to play audio, use {@link #getByteFrame}, {@link #getShortFrame}, or {@link #getFloatFrame} to obtain a frame that can be used as audio data.</p>
     *
     * @return A single audio frame.
     * @see #getByteFrame
     * @see #getShortFrame
     * @see #getFloatFrame
     */
    public double getSample() {
        frameSample = 0;

        for (keyId = 0; keyId < 132; keyId++) {
            if (keysQueue[keyId]) {
                frameSample += algorithm.getSample(keyId);

                if (!algorithm.hasActiveCarriers(keyId)) {
                    keysQueue[keyId] = false;
                }
            }
        }

        frameSample = preset.getGain() * filterChain.getResult(frameSample);

        return frameSample;
    }

    /**
     * Creates a stereo 16bit PCM frame, composed of two identical channels, in a interleaved array: <code>[L, L][R, R]</code>.
     * <p>Ideal to be used with {@link javax.sound.sampled.SourceDataLine#write(byte[], int, int)}.</p>
     *
     * @param bigEndian Defines the endianness of the values.
     * @return A single audio frame.
     * @see #getSample
     */
    public byte[] getByteFrame(boolean bigEndian) {
        getSample();
        frameSample *= MathFunctions.SIGNED_16_BIT_MAX;

        // TODO channel stuff, [L][R]
        MathFunctions.primitiveTo16bit(bigEndian, buffer16bit, 0, (int) frameSample);
        MathFunctions.primitiveTo16bit(bigEndian, buffer16bit, 2, (int) frameSample);

        return buffer16bit;
    }

    /**
     * Creates a stereo PCM frame, composed of two identical channels, in a interleaved array: <code>[L][R]</code>.
     * <p>Ideal to be used with libraries that support this kind of data, like <a href="https://libgdx.badlogicgames.com/">LibGdx</a>.</p>
     *
     * @return A single audio frame.
     * @see #getSample
     */
    public short[] getShortFrame() {
        getSample();

        // TODO channel stuff, [L][R]
        shortBuffer[0] = (short) frameSample;
        shortBuffer[1] = (short) frameSample;

        return shortBuffer;
    }

    /**
     * Creates a stereo PCM frame, composed of two identical channels, in a interleaved array: <code>[L][R]</code>.
     * <p>Ideal to be used with libraries that support this kind of data, like <a href="https://libgdx.badlogicgames.com/">LibGdx</a>.</p>
     *
     * @return A single audio frame.
     * @see #getSample
     */
    public float[] getFloatFrame() {
        getSample();

        // TODO channel stuff, [L][R]
        floatBuffer[0] = (float) frameSample;
        floatBuffer[1] = (float) frameSample;

        return floatBuffer;
    }

    /**
     * Helper method that extracts the {@link KeyboardNote#getId id} from the {@link KeyboardNote} and passes to {@link #pressKey(int)}.
     *
     * @param key The note that is being pressed.
     * @see #pressKey(int)
     */
    public void pressKey(KeyboardNote key) {
        pressKey(key.getId());
    }

    /**
     * Presses a key and starts the carrier chain defined by the {@link Algorithm}, applying the defined transposition setting.
     * <p>Use this method to interact with the instrument.</p>
     *
     * @param keyId ID representing an unique key, in the range of 0 to 131.
     * @see Algorithm
     * @see Oscillator
     */
    public void pressKey(int keyId) {
        keyId += preset.getTranspose();

        if ((keyId >= 0) || (keyId <= 131)) {
            algorithm.start(keyId, NOTES[keyId].getFrequency());
            keysQueue[keyId] = true;
        }
    }

    /**
     * Helper method that extracts the {@link KeyboardNote#getId id} from the {@link KeyboardNote} and passes to {@link #releaseKey(int)}.
     *
     * @param key The note that is being released.
     * @see #releaseKey(int)
     */
    public void releaseKey(KeyboardNote key) {
        releaseKey(key.getId());
    }

    /**
     * Releases a key and stops the carrier chain defined by the {@link Algorithm}, applying the defined transposition setting.
     * <p>Use this method to interact with the instrument.</p>
     *
     * @param keyId ID representing an unique key, in the range of 0 to 131.
     * @see Algorithm
     * @see Oscillator
     */
    public void releaseKey(int keyId) {
        keyId += preset.getTranspose();

        if ((keyId >= 0) || (keyId <= 131)) {
            algorithm.stop(keyId);
        }
    }

    /**
     * Releases all keys, silencing the instrument.
     *
     * @see Algorithm#stopAll
     */
    public void releaseAllKeys() {
        algorithm.stopAll();
    }

}
