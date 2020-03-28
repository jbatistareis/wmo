package com.jbatista.wmo.util;

import com.jbatista.wmo.KeyboardNote;
import com.jbatista.wmo.TransitionCurve;
import com.jbatista.wmo.WaveForm;
import com.jbatista.wmo.preset.AlgorithmPreset;
import com.jbatista.wmo.preset.InstrumentPreset;
import com.jbatista.wmo.preset.OscillatorPreset;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

/**
 * Provides means of reading sysex files.
 *
 * @see <a href="https://github.com/asb2m10/dexed/blob/master/Documentation/sysex-format.txt">DX7 sysex format.</a>
 */
public class Dx7Sysex {

    private static final String ERROR_HEADER = "\nThe following errors where found during processing:";
    private static final String START_ERROR = "Incorrect sysex start, expected 0xf0, found 0x%x";
    private static final String ID_ERROR = "Incorrect ID, expected 0x43, found 0x%x";
    private static final String FORMAT_ERROR = "Incorrect format, expected 0x00 or 0x09, found 0x%x";
    private static final String BYTE_COUNT_MS_ERROR = "Incorrect byte count MS, expected 0x01 or 0x20, found 0x%x";
    private static final String BYTE_COUNT_LS_ERROR = "Incorrect byte count LS, expected 0x1B or 0x00, found 0x%x";

    private static final AlgorithmPreset[] ALGORITHMS = AlgorithmPreset.values();
    private static final KeyboardNote[] NOTES = KeyboardNote.values();
    private static final WaveForm[] WAVE_FORMS = WaveForm.values();
    private static final TransitionCurve[] CURVES = TransitionCurve.values();

    /**
     * Reads a sysex file, and returns a list of {@link InstrumentPreset presets} from it's defined voices.
     *
     * @param sysex The sysex file.
     * @return A list of presets ready to be used.
     * @throws IOException
     * @throws SysexException
     * @see com.jbatista.wmo.synthesis.Instrument
     */
    public static List<InstrumentPreset> extractPresets(File sysex) throws IOException, SysexException {
        final List<InstrumentPreset> instruments = new ArrayList<>();

        final byte[] header = new byte[6];
        final byte[] bulkVoice = new byte[128];

        try (final RandomAccessFile file = new RandomAccessFile(sysex, "r")) {
            file.read(header);
            checkHeader(header);

            switch (header[3]) {
                // single voice
                case 0x00:
                    // TODO
                    break;

                // bulk 32 voices
                case 0x09:
                    for (int i = 0; i < 32; i++) {
                        file.read(bulkVoice);
                        instruments.add(parseBulkVoice(bulkVoice));
                    }

                    break;

                default:
                    throw new SysexException(String.format(FORMAT_ERROR, header[3]));
            }
        } catch (IOException | SysexException ex) {
            throw ex;
        }

        return instruments;
    }


    private static void checkHeader(byte[] header) throws SysexException {
        final StringBuilder sbError = new StringBuilder(ERROR_HEADER);

        if (header[0] != 0xFFFFFFF0) {
            sbError.append('\n').append(String.format(START_ERROR, header[0]));
        }
        if (header[1] != 0x43) {
            sbError.append('\n').append(String.format(ID_ERROR, header[1]));
        }
        if ((header[3] != 0x00) && (header[3] != 0x09)) {
            sbError.append('\n').append(String.format(FORMAT_ERROR, header[3]));
        }
        if ((header[4] != 0x10) && (header[4] != 0x20)) {
            sbError.append('\n').append(String.format(BYTE_COUNT_MS_ERROR, header[4]));
        }
        if ((header[5] != 0x1B) && (header[5] != 0x00)) {
            sbError.append('\n').append(String.format(BYTE_COUNT_LS_ERROR, header[5]));
        }

        if (sbError.length() > 52) {
            throw new SysexException(sbError.toString());
        }
    }

    private static InstrumentPreset parseSingleVoice(byte[] patch) {
        // TODO
        return null;
    }

    private static InstrumentPreset parseBulkVoice(byte[] bulkVoice) {
        // general parameters
        final int pitchEgRate1 = bulkVoice[102];
        final int pitchEgRate2 = bulkVoice[103];
        final int pitchEgRate3 = bulkVoice[104];
        final int pitchEgRate4 = bulkVoice[105];

        final int pitchEgLevel1 = bulkVoice[106];
        final int pitchEgLevel2 = bulkVoice[107];
        final int pitchEgLevel3 = bulkVoice[108];
        final int pitchEgLevel4 = bulkVoice[109];

        final int algorithm = bulkVoice[110];

        final int oscillatorKeySync = bulkVoice[111] >> 3;
        final int feedback = bulkVoice[111] & 7;

        final int lfoSpeed = bulkVoice[112];
        final int lfoDelay = bulkVoice[113];
        final int lfoPmDepth = bulkVoice[114];
        final int lfoAmDepth = bulkVoice[115];

        final int lfoPmModeSensitivity = bulkVoice[116] >> 4;
        final int lfoWave = (bulkVoice[116] >> 1) & 7;
        final int lfoKeySync = bulkVoice[116] & 1;

        final int transpose = bulkVoice[117];

        final String name = String.valueOf(new char[]{
                (char) bulkVoice[118],
                (char) bulkVoice[119],
                (char) bulkVoice[120],
                (char) bulkVoice[121],
                (char) bulkVoice[122],
                (char) bulkVoice[123],
                (char) bulkVoice[124],
                (char) bulkVoice[125],
                (char) bulkVoice[126],
                (char) bulkVoice[127]});

        // preset
        final InstrumentPreset instrumentPreset = new InstrumentPreset();

        instrumentPreset.setPitchAttackSpeed(pitchEgRate1);
        instrumentPreset.setPitchDecaySpeed(pitchEgRate2);
        instrumentPreset.setPitchSustainSpeed(pitchEgRate3);
        instrumentPreset.setPitchReleaseSpeed(pitchEgRate4);

        instrumentPreset.setPitchAttackLevel(pitchEgLevel1);
        instrumentPreset.setPitchDecayLevel(pitchEgLevel2);
        instrumentPreset.setPitchSustainLevel(pitchEgLevel3);
        instrumentPreset.setPitchReleaseLevel(pitchEgLevel4);

        instrumentPreset.setAlgorithm(ALGORITHMS[algorithm + 11]);

        instrumentPreset.setOscillatorKeySync(oscillatorKeySync == 1);
        instrumentPreset.setFeedback(feedback);

        instrumentPreset.setLfoSpeed(lfoSpeed);
        instrumentPreset.setLfoDelay(lfoDelay);
        instrumentPreset.setLfoPmDepth(lfoPmDepth);
        instrumentPreset.setLfoAmDepth(lfoAmDepth);

        instrumentPreset.setLfoPModeSensitivity(lfoPmModeSensitivity);
        instrumentPreset.setLfoWave(WAVE_FORMS[lfoWave]);
        instrumentPreset.setLfoKeySync(lfoKeySync == 1);

        instrumentPreset.setTranspose(transpose - 24);

        instrumentPreset.setName(name);

        final byte[] operatorParams = new byte[17];
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 17; j++) {
                operatorParams[j] = bulkVoice[j + 17 * i];
            }

            // oscillator parameters
            final int operator = 5 - i;

            final int egRate1 = operatorParams[0];
            final int egRate2 = operatorParams[1];
            final int egRate3 = operatorParams[2];
            final int egRate4 = operatorParams[3];

            final int egLevel1 = operatorParams[4];
            final int egLevel2 = operatorParams[5];
            final int egLevel3 = operatorParams[6];
            final int egLevel4 = operatorParams[7];

            final int breakpoint = operatorParams[8];
            final int breakpointLeftDepth = operatorParams[9];
            final int breakpointRightDepth = operatorParams[10];
            final int breakpointLeftCurve = operatorParams[11] & 3;
            final int breakpointRightCurve = operatorParams[11] >> 2;

            final int detune = (operatorParams[12] >> 3) - 7;
            final int rateScale = operatorParams[12] & 7;

            final int velocitySensitivity = operatorParams[13] >> 2;
            final int amSensitivity = operatorParams[13] & 3;

            final int outputLevel = operatorParams[14];

            final int frequencyCoarse = operatorParams[15] >> 1;
            final int frequencyMode = operatorParams[15] & 1;

            final int frequencyFine = operatorParams[16];

            // set oscillator preset
            final OscillatorPreset oscillatorPreset = new OscillatorPreset(operator);

            oscillatorPreset.setAttackSpeed(egRate1);
            oscillatorPreset.setDecaySpeed(egRate2);
            oscillatorPreset.setSustainSpeed(egRate3);
            oscillatorPreset.setReleaseSpeed(egRate4);

            oscillatorPreset.setAttackLevel(egLevel1);
            oscillatorPreset.setDecayLevel(egLevel2);
            oscillatorPreset.setSustainLevel(egLevel3);
            oscillatorPreset.setReleaseLevel(egLevel4);

            oscillatorPreset.setBreakpointNote(NOTES[Math.max(21, Math.min(breakpoint + 21, 120))]);
            oscillatorPreset.setBreakpointLeftDepth(breakpointLeftDepth);
            oscillatorPreset.setBreakpointRightDepth(breakpointRightDepth);
            oscillatorPreset.setBreakpointLeftCurve(CURVES[breakpointLeftCurve]);
            oscillatorPreset.setBreakpointRightCurve(CURVES[breakpointRightCurve]);

            oscillatorPreset.setFrequencyDetune(detune);
            oscillatorPreset.setSpeedScaling(rateScale);

            oscillatorPreset.setVelocitySensitivity(velocitySensitivity);
            oscillatorPreset.setAmSensitivity(amSensitivity);

            oscillatorPreset.setOutputLevel(outputLevel);

            oscillatorPreset.setFrequencyRatio(frequencyCoarse);
            oscillatorPreset.setFixedFrequency(frequencyMode == 1);

            oscillatorPreset.setFrequencyFine(frequencyFine);

            instrumentPreset.addOscillatorPreset(oscillatorPreset);
        }

        return instrumentPreset;
    }

}
