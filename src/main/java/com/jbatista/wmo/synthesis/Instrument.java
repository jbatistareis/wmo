package com.jbatista.wmo.synthesis;

import com.jbatista.wmo.KeyboardNote;
import com.jbatista.wmo.preset.InstrumentPreset;
import com.jbatista.wmo.util.MathFunctions;

/**
 * <p>Represents an entire keyboard and its functions.</p>
 * <p>This class provides means of controlling keys and obtaining audio, to control the parameters use a {@link InstrumentPreset preset}.</p>
 * <p>Audio is obtained in PCM frames that can be written directly to audio outputs, obtaining a frame is the same as to read a PCM file.</p>
 *
 * @see InstrumentPreset
 * @see #setPreset(InstrumentPreset)
 */
public class Instrument {
    private static final KeyboardNote[] NOTES = KeyboardNote.values();

    InstrumentPreset preset = new InstrumentPreset();

    private int keyId = 0;
    private final boolean[] keysQueue = new boolean[132];

    private final int sampleRate;
    private final Algorithm algorithm;
    private final FilterChain filterChain = new FilterChain();

    private int intFrameSample;
    private short shortFrameSample;
    private float floatFrameSample;
    private double doubleFrameSample;
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
        silenceAllKeys();
        this.preset = preset;
    }

    /**
     * <p>Creates a mono PCM frame.</p>
     * <p>Not very useful to play audio, use {@link #getByteFrame}, {@link #getShortFrame}, or {@link #getFloatFrame} to obtain a frame that can be used as audio data.</p>
     *
     * @return A single audio frame.
     * @see #getByteFrame
     * @see #getShortFrame
     * @see #getFloatFrame
     */
    public double getSample() {
        doubleFrameSample = 0;

        for (keyId = 0; keyId < 132; keyId++) {
            if (keysQueue[keyId]) {
                doubleFrameSample += algorithm.getSample(keyId);

                if (!algorithm.hasActiveCarriers(keyId)) {
                    keysQueue[keyId] = false;
                }
            }
        }

        doubleFrameSample = preset.getGain() * filterChain.getResult(doubleFrameSample);

        return doubleFrameSample;
    }

    /**
     * <p>Creates a stereo 16bit PCM frame, composed of two identical channels, in a interleaved array: <code>[L, L][R, R]</code>.</p>
     * <p>Ideal to be used with {@link javax.sound.sampled.SourceDataLine#write(byte[], int, int)}.</p>
     *
     * @param bigEndian Defines the endianness of the values.
     * @return A single audio frame.
     * @see #getSample
     */
    public byte[] getByteFrame(boolean bigEndian) {
        getSample();
        intFrameSample = (int) (doubleFrameSample * MathFunctions.SIGNED_16_BIT_MAX);

        // TODO channel stuff, [L][R]
        MathFunctions.primitiveTo16bit(bigEndian, buffer16bit, 0, intFrameSample);
        MathFunctions.primitiveTo16bit(bigEndian, buffer16bit, 2, intFrameSample);

        return buffer16bit;
    }

    /**
     * <p>Creates a stereo PCM frame, composed of two identical channels, in a interleaved array: <code>[L][R]</code>.</p>
     * <p>Ideal to be used with libraries that support this kind of data, like <a href="https://libgdx.badlogicgames.com/">LibGdx</a>.</p>
     *
     * @return A single audio frame.
     * @see #getSample
     */
    public short[] getShortFrame() {
        getSample();
        shortFrameSample = (short) doubleFrameSample;

        // TODO channel stuff, [L][R]
        shortBuffer[0] = shortFrameSample;
        shortBuffer[1] = shortFrameSample;

        return shortBuffer;
    }

    /**
     * <p>Creates a stereo PCM frame, composed of two identical channels, in a interleaved array: <code>[L][R]</code>.</p>
     * <p>Ideal to be used with libraries that support this kind of data, like <a href="https://libgdx.badlogicgames.com/">LibGdx</a>.</p>
     *
     * @return A single audio frame.
     * @see #getSample
     */
    public float[] getFloatFrame() {
        getSample();
        floatFrameSample = (float) doubleFrameSample;

        // TODO channel stuff, [L][R]
        floatBuffer[0] = floatFrameSample;
        floatBuffer[1] = floatFrameSample;

        return floatBuffer;
    }

    /**
     * <p>Helper method that extracts the {@link KeyboardNote#getId id} from the {@link KeyboardNote} and passes to {@link #pressKey(int)}.</p>
     *
     * @param key The note that is being pressed.
     * @see #pressKey(int)
     */
    public void pressKey(KeyboardNote key) {
        pressKey(key.getId());
    }

    /**
     * <p>Presses a key and starts the carrier chain defined by the {@link Algorithm}, applying the defined transposition setting.</p>
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
     * <p>Helper method that extracts the {@link KeyboardNote#getId id} from the {@link KeyboardNote} and passes to {@link #releaseKey(int)}.</p>
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
     * <p>Releases all keys.</p>
     *
     * @see Algorithm#stopAll
     */
    public void releaseAllKeys() {
        algorithm.stopAll();
    }

    /**
     * <p>Silences all keys.</p>
     *
     * @see Algorithm#stopAll
     */
    public void silenceAllKeys() {
        algorithm.silenceAll();
    }


}
