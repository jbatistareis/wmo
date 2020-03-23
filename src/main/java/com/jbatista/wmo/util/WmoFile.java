package com.jbatista.wmo.util;

import com.jbatista.wmo.KeyboardNote;
import com.jbatista.wmo.TransitionCurve;
import com.jbatista.wmo.preset.AlgorithmPreset;
import com.jbatista.wmo.preset.InstrumentPreset;
import com.jbatista.wmo.preset.OscillatorPreset;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WmoFile {

    public static List<InstrumentPreset> loadWmoInstruments(File wmo) throws IOException {
        final List<InstrumentPreset> presets = new ArrayList<>();
        final byte[] data = new byte[161];

        try (final RandomAccessFile file = new RandomAccessFile(wmo, "r")) {
            file.read(data);

            for (int i = 0; i < (wmo.length() / data.length); i++) {
                file.read(data);
                presets.add(parseBytes(data));
            }

            return presets;
        } catch (IOException ex) {
            throw ex;
        }
    }

    public static InstrumentPreset parseBytes(byte[] data) {
        // general parameters
        final int pitchEgRate1 = data[132];
        final int pitchEgRate2 = data[133];
        final int pitchEgRate3 = data[134];
        final int pitchEgRate4 = data[135];

        final int pitchEgLevel1 = data[136];
        final int pitchEgLevel2 = data[137];
        final int pitchEgLevel3 = data[138];
        final int pitchEgLevel4 = data[139];

        final int algorithm = data[140];

        final int keySync = data[141];
        final int feedback = data[142];

        final int lfoSpeed = data[143];
        final int lfoDelay = data[144];
        final int lfoPmDepth = data[145];
        final int lfoAmDepth = data[146];

        final int lfoPmModeSensitivity = data[147];
        final int lfoWave = data[148];
        final int lfoKeySync = data[149];

        final int transpose = data[150];

        final String name = String.valueOf(new char[]{
                (char) data[151],
                (char) data[152],
                (char) data[153],
                (char) data[154],
                (char) data[155],
                (char) data[156],
                (char) data[157],
                (char) data[158],
                (char) data[159],
                (char) data[160]});

        // preset
        final InstrumentPreset instrumentPreset = new InstrumentPreset();
        instrumentPreset.setName(name);
        instrumentPreset.setTranspose(transpose);
        instrumentPreset.setAlgorithm(AlgorithmPreset.values()[algorithm]);

        int offset = 0;
        for (int i = 0; i < ((algorithm < 8) ? 4 : 6); i++) {
            // oscillator parameters
            final int operator = data[0 + offset];

            final int egRate1 = data[1 + offset];
            final int egRate2 = data[2 + offset];
            final int egRate3 = data[3 + offset];
            final int egRate4 = data[4 + offset];

            final int egLevel1 = data[5 + offset];
            final int egLevel2 = data[6 + offset];
            final int egLevel3 = data[7 + offset];
            final int egLevel4 = data[8 + offset];

            final int breakpoint = data[9 + offset];
            final int breakpointLeftDepth = data[10 + offset];
            final int breakpointRightDepth = data[11 + offset];
            final int breakpointLeftCurve = data[12 + offset];
            final int breakpointRightCurve = data[13 + offset];

            final int detune = data[14 + offset];
            final int rateScale = data[15 + offset];

            final int velocitySensitivity = data[16 + offset];
            final int modeSensitivity = data[17 + offset];

            final int outputLevel = data[18 + offset];

            final int frequencyCoarse = data[19 + offset];
            final int frequencyMode = data[20 + offset];

            final int frequencyFine = data[21 + offset];

            offset += 22;

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

            oscillatorPreset.setBreakpointNote(KeyboardNote.values()[breakpoint]);
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

    public static void saveSingleWmoInstrument(InstrumentPreset instrument, File wmo) throws IOException {
        final List<InstrumentPreset> instrumentPresets = new ArrayList<>();
        instrumentPresets.add(instrument);

        saveBulkWmoInstruments(instrumentPresets, wmo);
    }

    public static void saveBulkWmoInstruments(List<InstrumentPreset> instruments, File wmo) throws IOException {
        final List<KeyboardNote> notes = Arrays.asList(KeyboardNote.values());
        final List<TransitionCurve> curves = Arrays.asList(TransitionCurve.values());
        final List<AlgorithmPreset> algorithms = Arrays.asList(AlgorithmPreset.values());

        try (final RandomAccessFile file = new RandomAccessFile(wmo, "rw")) {
            for (InstrumentPreset instrument : instruments) {
                for (int i = 0; i < instrument.getOscillatorPresets().size(); i++) {
                    file.write(instrument.getOscillatorPresets().get(i).getId());
                    file.write(instrument.getOscillatorPresets().get(i).getAttackSpeed());
                    file.write(instrument.getOscillatorPresets().get(i).getDecaySpeed());
                    file.write(instrument.getOscillatorPresets().get(i).getSustainSpeed());
                    file.write(instrument.getOscillatorPresets().get(i).getReleaseSpeed());

                    file.write(instrument.getOscillatorPresets().get(i).getAttackLevel());
                    file.write(instrument.getOscillatorPresets().get(i).getDecayLevel());
                    file.write(instrument.getOscillatorPresets().get(i).getSustainLevel());
                    file.write(instrument.getOscillatorPresets().get(i).getReleaseLevel());

                    file.write(notes.indexOf(instrument.getOscillatorPresets().get(i).getBreakpointNote()));
                    file.write(instrument.getOscillatorPresets().get(i).getBreakpointLeftDepth());
                    file.write(instrument.getOscillatorPresets().get(i).getBreakpointRightDepth());
                    file.write(curves.indexOf(instrument.getOscillatorPresets().get(i).getBreakpointRightCurve()));
                    file.write(curves.indexOf(instrument.getOscillatorPresets().get(i).getBreakpointLeftCurve()));

                    file.write(instrument.getOscillatorPresets().get(i).getFrequencyDetune());
                    file.write(0); // TODO rate scale

                    file.write(0); // TODO velocity sensitivity
                    file.write(0);// TODO mod sensitivity

                    file.write(instrument.getOscillatorPresets().get(i).getOutputLevel());

                    file.write((int) instrument.getOscillatorPresets().get(i).getFrequencyRatio());
                    file.write(instrument.getOscillatorPresets().get(i).isFixedFrequency() ? 1 : 0);

                    file.write(instrument.getOscillatorPresets().get(i).getFrequencyFine());
                }

                // TODO pitch EG
                file.write(0); // attack speed
                file.write(0); // decay speed
                file.write(0); // sustain speed
                file.write(0); // release speed

                file.write(0); // attack level
                file.write(0); // decay level
                file.write(0); // sustain level
                file.write(0); // release level

                file.write(algorithms.indexOf(instrument.getAlgorithm()));

                file.write(0); // TODO key sync
                file.write(0); // TODO feedback

                // TODO lfo
                file.write(0); // speed
                file.write(0); // delay
                file.write(0); // pm depth
                file.write(0); // am depth

                file.write(0); // mod sensitivity
                file.write(0); // wave
                file.write(0); // key sync

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
            }
        } catch (IOException ex) {
            throw ex;
        }
    }

}
