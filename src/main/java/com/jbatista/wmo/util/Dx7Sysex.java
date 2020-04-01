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
 * <p>Provides means of reading sysex files.</p>
 *
 * @see <a href="https://github.com/asb2m10/dexed/blob/master/Documentation/sysex-format.txt">DX7 sysex format.</a>
 */
public class Dx7Sysex {

    private static final AlgorithmPreset[] ALGORITHMS = AlgorithmPreset.values();
    private static final KeyboardNote[] NOTES = KeyboardNote.values();
    private static final WaveForm[] WAVE_FORMS = WaveForm.values();
    private static final TransitionCurve[] CURVES = TransitionCurve.values();

    /**
     * <p>Reads a sysex file, and returns a list of {@link InstrumentPreset presets} from it's defined voices.</p>
     *
     * @param sysex The sysex file.
     * @return A list of presets ready to be used.
     * @throws IOException
     * @throws SysexException
     * @see com.jbatista.wmo.synthesis.Instrument
     */
    public static List<InstrumentPreset> extractInstruments(File sysex) throws IOException, SysexException {
        final List<InstrumentPreset> instruments = new ArrayList<>();

        final byte[] header = new byte[6];
        final byte[] bulkVoice = new byte[128];

        try (final RandomAccessFile file = new RandomAccessFile(sysex, "r")) {
            file.read(header);

            switch (header[3]) {
                // single voice
                case 0x00:
                    // TODO
                    break;

                // bulk 32 voices
                case 0x09:
                    for (int i = 0; i < 32; i++) {
                        file.read(bulkVoice);
                        instruments.add(parseBulkVoices(bulkVoice));
                    }

                    break;

                default:
                    throw new SysexException(String.format("Cannot identify voice format at file position 4, expected 0x00 (single) or 0x09 (bulk), found 0x%x.", header[3]));
            }
        }

        return instruments;
    }

    private static InstrumentPreset parseSingleVoice(byte[] patch) {
        // TODO
        return null;
    }

    private static InstrumentPreset parseBulkVoices(byte[] bulkVoice) {
        final InstrumentPreset instrumentPreset = new InstrumentPreset();

        final byte[] operatorParams = new byte[17];
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 17; j++) {
                operatorParams[j] = bulkVoice[j + 17 * i];
            }

            final OscillatorPreset oscillatorPreset = new OscillatorPreset(5 - i);

            oscillatorPreset.setAttackSpeed(operatorParams[0]);
            oscillatorPreset.setDecaySpeed(operatorParams[1]);
            oscillatorPreset.setSustainSpeed(operatorParams[2]);
            oscillatorPreset.setReleaseSpeed(operatorParams[3]);

            oscillatorPreset.setAttackLevel(operatorParams[4]);
            oscillatorPreset.setDecayLevel(operatorParams[5]);
            oscillatorPreset.setSustainLevel(operatorParams[6]);
            oscillatorPreset.setReleaseLevel(operatorParams[7]);

            oscillatorPreset.setBreakpointNote(NOTES[Math.max(21, Math.min(operatorParams[8] + 21, 120))]);
            oscillatorPreset.setBreakpointLeftDepth(operatorParams[9]);
            oscillatorPreset.setBreakpointRightDepth(operatorParams[10]);
            oscillatorPreset.setBreakpointLeftCurve(CURVES[operatorParams[11] & 3]);
            oscillatorPreset.setBreakpointRightCurve(CURVES[operatorParams[11] >> 2]);

            oscillatorPreset.setFrequencyDetune((operatorParams[12] >> 3) - 7);
            oscillatorPreset.setSpeedScaling(operatorParams[12] & 7);

            oscillatorPreset.setVelocitySensitivity(operatorParams[13] >> 2);
            oscillatorPreset.setAmSensitivity(operatorParams[13] & 3);

            oscillatorPreset.setOutputLevel(operatorParams[14]);

            oscillatorPreset.setFrequencyRatio(operatorParams[15] >> 1);
            oscillatorPreset.setFixedFrequency((operatorParams[15] & 1) == 1);

            oscillatorPreset.setFrequencyFine(operatorParams[16]);

            instrumentPreset.addOscillatorPreset(oscillatorPreset);
        }

        instrumentPreset.setPitchAttackSpeed(bulkVoice[102]);
        instrumentPreset.setPitchDecaySpeed(bulkVoice[103]);
        instrumentPreset.setPitchSustainSpeed(bulkVoice[104]);
        instrumentPreset.setPitchReleaseSpeed(bulkVoice[105]);

        instrumentPreset.setPitchAttackLevel(bulkVoice[106]);
        instrumentPreset.setPitchDecayLevel(bulkVoice[107]);
        instrumentPreset.setPitchSustainLevel(bulkVoice[108]);
        instrumentPreset.setPitchReleaseLevel(bulkVoice[109]);

        instrumentPreset.setAlgorithm(ALGORITHMS[bulkVoice[110] + 11]);

        instrumentPreset.setOscillatorKeySync((bulkVoice[111] >> 3) == 1);
        instrumentPreset.setFeedback(bulkVoice[111] & 7);

        instrumentPreset.setLfoSpeed(bulkVoice[112]);
        instrumentPreset.setLfoDelay(bulkVoice[113]);
        instrumentPreset.setLfoPmDepth(bulkVoice[114]);
        instrumentPreset.setLfoAmDepth(bulkVoice[115]);

        instrumentPreset.setLfoPModeSensitivity(bulkVoice[116] >> 4);
        instrumentPreset.setLfoWave(WAVE_FORMS[(bulkVoice[116] >> 1) & 7]);
        instrumentPreset.setLfoKeySync((bulkVoice[116] & 1) == 1);

        instrumentPreset.setTranspose(bulkVoice[117] - 24);

        instrumentPreset.setName(String.valueOf(new char[]{
                (char) bulkVoice[118],
                (char) bulkVoice[119],
                (char) bulkVoice[120],
                (char) bulkVoice[121],
                (char) bulkVoice[122],
                (char) bulkVoice[123],
                (char) bulkVoice[124],
                (char) bulkVoice[125],
                (char) bulkVoice[126],
                (char) bulkVoice[127]}));

        return instrumentPreset;
    }

}
