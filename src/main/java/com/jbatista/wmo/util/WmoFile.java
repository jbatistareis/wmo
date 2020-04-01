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
import java.util.Arrays;
import java.util.List;

/**
 * <p>Provides means of serializing WMO presets.</p>
 */
public class WmoFile {

    private static final List<AlgorithmPreset> ALGORITHMS = Arrays.asList(AlgorithmPreset.values());
    private static final KeyboardNote[] NOTES = KeyboardNote.values();
    private static final WaveForm[] WAVE_FORMS = WaveForm.values();
    private static final TransitionCurve[] CURVES = TransitionCurve.values();

    public static List<InstrumentPreset> loadInstruments(File wmoFile) throws IOException {
        final List<InstrumentPreset> presets = new ArrayList<>();
        final byte[] data = new byte[162];

        try (final RandomAccessFile file = new RandomAccessFile(wmoFile, "r")) {
            file.read(data);

            for (int i = 0; i < (wmoFile.length() / data.length); i++) {
                file.read(data);
                presets.add(parseBytes(data));
            }

            return presets;
        } catch (IOException ex) {
            throw ex;
        }
    }

    public static InstrumentPreset parseBytes(byte[] data) {
        final InstrumentPreset instrumentPreset = new InstrumentPreset();

        int offset = 0;
        for (int i = 0; i < 6; i++) {
            final OscillatorPreset oscillatorPreset = new OscillatorPreset(data[0 + offset]);

            oscillatorPreset.setAttackSpeed(data[1 + offset]);
            oscillatorPreset.setDecaySpeed(data[2 + offset]);
            oscillatorPreset.setSustainSpeed(data[3 + offset]);
            oscillatorPreset.setReleaseSpeed(data[4 + offset]);

            oscillatorPreset.setAttackLevel(data[5 + offset]);
            oscillatorPreset.setDecayLevel(data[6 + offset]);
            oscillatorPreset.setSustainLevel(data[7 + offset]);
            oscillatorPreset.setReleaseLevel(data[8 + offset]);

            oscillatorPreset.setBreakpointNote(NOTES[data[9 + offset]]);
            oscillatorPreset.setBreakpointLeftDepth(data[10 + offset]);
            oscillatorPreset.setBreakpointRightDepth(data[11 + offset]);
            oscillatorPreset.setBreakpointLeftCurve(CURVES[data[12 + offset]]);
            oscillatorPreset.setBreakpointRightCurve(CURVES[data[13 + offset]]);

            oscillatorPreset.setFrequencyDetune(data[14 + offset]);
            oscillatorPreset.setSpeedScaling(data[15 + offset]);

            oscillatorPreset.setVelocitySensitivity(data[16 + offset]);
            oscillatorPreset.setAmSensitivity(data[17 + offset]);

            oscillatorPreset.setOutputLevel(data[18 + offset]);

            oscillatorPreset.setFrequencyRatio(data[19 + offset]);
            oscillatorPreset.setFixedFrequency(data[20 + offset] == 1);

            oscillatorPreset.setFrequencyFine(data[21 + offset]);

            instrumentPreset.addOscillatorPreset(oscillatorPreset);

            offset += 22;
        }

        instrumentPreset.setPitchAttackSpeed(data[132]);
        instrumentPreset.setPitchDecaySpeed(data[133]);
        instrumentPreset.setPitchSustainSpeed(data[134]);
        instrumentPreset.setPitchReleaseSpeed(data[135]);

        instrumentPreset.setPitchAttackLevel(data[136]);
        instrumentPreset.setPitchDecayLevel(data[137]);
        instrumentPreset.setPitchSustainLevel(data[138]);
        instrumentPreset.setPitchReleaseLevel(data[139]);

        instrumentPreset.setAlgorithm(ALGORITHMS.get(data[140]));

        instrumentPreset.setOscillatorKeySync(data[141] == 1);
        instrumentPreset.setFeedback(data[142]);

        instrumentPreset.setLfoSpeed(data[143]);
        instrumentPreset.setLfoDelay(data[144]);
        instrumentPreset.setLfoPmDepth(data[145]);
        instrumentPreset.setLfoAmDepth(data[146]);

        instrumentPreset.setLfoPModeSensitivity(data[147]);
        instrumentPreset.setLfoWave(WAVE_FORMS[data[148]]);
        instrumentPreset.setLfoKeySync(data[149] == 1);

        instrumentPreset.setTranspose(data[150]);

        instrumentPreset.setName(String.valueOf(new char[]{
                (char) data[151],
                (char) data[152],
                (char) data[153],
                (char) data[154],
                (char) data[155],
                (char) data[156],
                (char) data[157],
                (char) data[158],
                (char) data[159],
                (char) data[160]}));

        // TODO instrumentPreset.setGain(data[161]);

        return instrumentPreset;
    }

    public static void saveSingleInstrument(InstrumentPreset instrument, File wmoFile) throws IOException {
        try (final RandomAccessFile file = new RandomAccessFile(wmoFile, "rw")) {
            write(0, file, instrument);
        }
    }

    public static void overwriteInstrumentAtPosition(int position, InstrumentPreset instrument, File wmoFile) throws IOException {
        try (final RandomAccessFile file = new RandomAccessFile(wmoFile, "rw")) {
            write(position * 162, file, instrument);
        }
    }

    public static void saveBulkInstruments(List<InstrumentPreset> instruments, File wmoFile) throws IOException {
        try (final RandomAccessFile file = new RandomAccessFile(wmoFile, "rw")) {
            for (InstrumentPreset instrument : instruments) {
                write(file.getFilePointer(), file, instrument);
            }
        }
    }

    private static void write(long index, RandomAccessFile file, InstrumentPreset instrument) throws IOException {
        file.seek(index);

        for (int i = 0; i < 6; i++) {
            file.write(instrument.getOscillatorPresets()[i].getId());

            file.write(instrument.getOscillatorPresets()[i].getAttackSpeed());
            file.write(instrument.getOscillatorPresets()[i].getDecaySpeed());
            file.write(instrument.getOscillatorPresets()[i].getSustainSpeed());
            file.write(instrument.getOscillatorPresets()[i].getReleaseSpeed());

            file.write(instrument.getOscillatorPresets()[i].getAttackLevel());
            file.write(instrument.getOscillatorPresets()[i].getDecayLevel());
            file.write(instrument.getOscillatorPresets()[i].getSustainLevel());
            file.write(instrument.getOscillatorPresets()[i].getReleaseLevel());

            file.write(instrument.getOscillatorPresets()[i].getBreakpointNote().getId());
            file.write(instrument.getOscillatorPresets()[i].getBreakpointLeftDepth());
            file.write(instrument.getOscillatorPresets()[i].getBreakpointRightDepth());
            file.write(instrument.getOscillatorPresets()[i].getBreakpointLeftCurve().getId());
            file.write(instrument.getOscillatorPresets()[i].getBreakpointRightCurve().getId());

            file.write(instrument.getOscillatorPresets()[i].getFrequencyDetune());
            file.write(instrument.getOscillatorPresets()[i].getSpeedScaling());

            file.write(instrument.getOscillatorPresets()[i].getVelocitySensitivity());
            file.write(instrument.getOscillatorPresets()[i].getAmSensitivity());

            file.write(instrument.getOscillatorPresets()[i].getOutputLevel());

            file.write((int) instrument.getOscillatorPresets()[i].getFrequencyRatio());
            file.write(instrument.getOscillatorPresets()[i].isFixedFrequency() ? 1 : 0);

            file.write(instrument.getOscillatorPresets()[i].getFrequencyFine());
        }

        file.write(instrument.getPitchAttackSpeed());
        file.write(instrument.getPitchDecaySpeed());
        file.write(instrument.getPitchSustainSpeed());
        file.write(instrument.getPitchReleaseSpeed());

        file.write(instrument.getPitchAttackLevel());
        file.write(instrument.getPitchDecayLevel());
        file.write(instrument.getPitchSustainLevel());
        file.write(instrument.getPitchReleaseLevel());

        file.write(ALGORITHMS.indexOf(instrument.getAlgorithm()));

        file.write(instrument.isOscillatorKeySync() ? 1 : 0);
        file.write(instrument.getFeedback());

        file.write(instrument.getLfoSpeed());
        file.write(instrument.getLfoDelay());
        file.write(instrument.getLfoPmDepth());
        file.write(instrument.getLfoAmDepth());

        file.write(instrument.getLfoPModeSensitivity());
        file.write(instrument.getLfoWave().getId());
        file.write(instrument.isLfoKeySync() ? 1 : 0);

        file.write(instrument.getTranspose());

        file.write(instrument.getName().charAt(0));
        file.write(instrument.getName().charAt(1));
        file.write(instrument.getName().charAt(2));
        file.write(instrument.getName().charAt(3));
        file.write(instrument.getName().charAt(4));
        file.write(instrument.getName().charAt(5));
        file.write(instrument.getName().charAt(6));
        file.write(instrument.getName().charAt(7));
        file.write(instrument.getName().charAt(8));
        file.write(instrument.getName().charAt(9));

        file.write(0);
    }

}
