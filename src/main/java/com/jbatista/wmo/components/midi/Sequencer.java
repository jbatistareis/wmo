package com.jbatista.wmo.components.midi;

import com.jbatista.wmo.AudioFormat;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Sequencer {

    private File midiFile;
    private List<Track> tracks = new ArrayList<>();

    private long timePostion = 0;
    private long bpm = 120;

    private long ticksPerBeat = 30;
    private long tickDuration = 16;

    private long currentTick = 0;
    private long deltaTimePlayed = 0;

    public void load(File file) throws InvalidMidiDataException, IOException {
        midiFile = file;
        final Sequence midiSequence = MidiSystem.getSequence(midiFile);
        ticksPerBeat = midiSequence.getResolution();
        setBpm(midiSequence.getTracks()[0]);

        for (javax.sound.midi.Track midiTrack : midiSequence.getTracks()) {
            tracks.add(new Track(this, midiTrack, AudioFormat._44100Hz_16bit));
        }
    }

    public void play(long deltaTimeMillis) {
        if ((deltaTimePlayed += deltaTimeMillis) >= tickDuration) {
            for (Track track : tracks) {
                track.readMidiMessage(currentTick);
            }

            currentTick++;
            deltaTimePlayed = 0;
        }
    }

    public void setBpm(javax.sound.midi.Track midiTrack) {
        byte[] data;
        for (int i = 0; i < midiTrack.size(); i++) {
            data = midiTrack.get(i).getMessage().getMessage();
            if (((data[0] & 0xFF) == 0xFF) && ((data[1] & 0xFF) == 0x51) && ((data[2] & 0xFF) == 0x03)) {
                bpm = 60000000 / (((data[3] & 0xFF) << 16) + ((data[4] & 0xFF) << 8) + (data[5] & 0xFF));
                tickDuration = (60000 / bpm) / ticksPerBeat;

                System.out.println(ticksPerBeat + " " + bpm);
                break;
            }
        }

        bpm = 0;
    }

}
