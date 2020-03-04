package com.jbatista.wmo.util;

// Detailed description of the format can be found on https://github.com/asb2m10/dexed/blob/master/Documentation/sysex-format.txt

import com.jbatista.wmo.KeyboardNote;
import com.jbatista.wmo.TransitionCurve;
import com.jbatista.wmo.preset.AlgorithmPreset;
import com.jbatista.wmo.preset.InstrumentPreset;
import com.jbatista.wmo.preset.OscillatorPreset;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

public class Dx7Sysex {

    private static final String ERROR_HEADER = "\nThe following errors where found during processing:";
    private static final String START_ERROR = "Incorrect sysex start, expected 0xf0, found 0x%x";
    private static final String ID_ERROR = "Incorrect ID, expected 0x43, found 0x%x";
    private static final String FORMAT_ERROR = "Incorrect format, expected 0x00 or 0x09, found 0x%x";
    private static final String BYTE_COUNT_MS_ERROR = "Incorrect byte count MS, expected 0x01 or 0x20, found 0x%x";
    private static final String BYTE_COUNT_LS_ERROR = "Incorrect byte count LS, expected 0x1B or 0x00, found 0x%x";

    public static List<InstrumentPreset> readInstruments(File sysex) throws IOException, SysexException {
        final List<InstrumentPreset> instruments = new ArrayList<>();

        final byte[] header = new byte[6];
        final byte[] voice = new byte[128];

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
                        file.read(voice);
                        instruments.add(parseBulkVoice(voice));
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

    private static void parseSingleVoice(byte[] patch) {
        // TODO
    }

    private static InstrumentPreset parseBulkVoice(byte[] voice) {
        // general parameters
        final int pitchEgRate1 = voice[102];
        final int pitchEgRate2 = voice[103];
        final int pitchEgRate3 = voice[104];
        final int pitchEgRate4 = voice[105];

        final int pitchEgLevel1 = voice[106];
        final int pitchEgLevel2 = voice[107];
        final int pitchEgLevel3 = voice[108];
        final int pitchEgLevel4 = voice[109];

        final int algorithm = voice[110];

        final int keySync = voice[111] >> 3;
        final int feedback = voice[111] & 7;

        final int lfoSpeed = voice[112];
        final int lfoDelay = voice[113];
        final int lfoPmDepth = voice[114];
        final int lfoAmDepth = voice[115];

        final int lfoPmModeSensitivity = voice[116] >> 4;
        final int lfoWave = (voice[116] >> 1) & 7;
        final int lfoKeySync = voice[116] & 1;

        final int transpose = voice[117];

        final String name = String.valueOf(new char[]{
                (char) voice[118],
                (char) voice[119],
                (char) voice[120],
                (char) voice[121],
                (char) voice[122],
                (char) voice[123],
                (char) voice[124],
                (char) voice[125],
                (char) voice[126],
                (char) voice[127]});

        // preset
        final InstrumentPreset instrumentPreset = new InstrumentPreset();
        instrumentPreset.setName(name);
        instrumentPreset.setAlgorithm(AlgorithmPreset.values()[algorithm + 8].getAlgorithm());


        final byte[] operatorParams = new byte[17];
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 17; j++) {
                operatorParams[j] = voice[j + 17 * i];
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
            final int modeSensitivity = operatorParams[13] & 3;

            final int outputLevel = operatorParams[14];

            final int frequencyCoarse = operatorParams[15] >> 1;
            final int frequencyMode = operatorParams[15] & 1;

            final int frequencyFine = operatorParams[16];

            // set oscillator preset
            final OscillatorPreset oscillatorPreset = new OscillatorPreset();
            oscillatorPreset.setId(operator);

            oscillatorPreset.setAttackSpeed(egRate1);
            oscillatorPreset.setDecaySpeed(egRate2);
            oscillatorPreset.setSustainSpeed(egRate3);
            oscillatorPreset.setReleaseSpeed(egRate4);

            oscillatorPreset.setAttackLevel(egLevel1);
            oscillatorPreset.setDecayLevel(egLevel2);
            oscillatorPreset.setSustainLevel(egLevel3);
            oscillatorPreset.setReleaseLevel(egLevel4);

            oscillatorPreset.setBreakpointNote(KeyboardNote.values()[Math.min(breakpoint + 21, 131)]);
            oscillatorPreset.setBreakpointLeftDepth(breakpointLeftDepth);
            oscillatorPreset.setBreakpointRightDepth(breakpointRightDepth);
            oscillatorPreset.setBreakpointLeftCurve(TransitionCurve.values()[breakpointLeftCurve]);
            oscillatorPreset.setBreakpointRightCurve(TransitionCurve.values()[breakpointRightCurve]);

            oscillatorPreset.setFrequencyDetune(detune);

            oscillatorPreset.setOutputLevel(outputLevel);

            oscillatorPreset.setFrequencyRatio(frequencyCoarse);
            oscillatorPreset.setFixedFrequency(frequencyMode == 1);

            oscillatorPreset.setFrequencyFine(frequencyFine);

            instrumentPreset.getOscillatorPresets().add(oscillatorPreset);
        }

        return instrumentPreset;
    }

}
