package com.jbatista.wmo.components.midi;

import com.jbatista.wmo.components.play.Instrument;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Sequencer {

    private File midiFile;
    private List<Track> tracks = new ArrayList<>();

    private long timePostion = 0;
    private long bpm = 120;

    private long ticksPerBeat = 30;
    private long tickDuration = 16;

    private long currentTick = 0;
    private long deltaTimePlayed = 0;

    private Map<Integer, Instrument> channelInstruments = new HashMap<>();

    public void load(File file) throws InvalidMidiDataException, IOException {
        midiFile = file;
        final Sequence midiSequence = MidiSystem.getSequence(midiFile);
        ticksPerBeat = midiSequence.getResolution();

        for (javax.sound.midi.Track midiTrack : midiSequence.getTracks()) {
            tracks.add(new Track(this, midiTrack));
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

    protected void setBpm(int bpm) {
        this.bpm = bpm;
        tickDuration = (60000 / bpm) / ticksPerBeat;

        System.out.println(bpm + " " + tickDuration);
    }

    protected void setChannelInstrument(byte channel, byte instrumentCode) {

    }

    protected void pressKey(int channel, int key, int speed) {
        System.out.println("P");
    }

    protected void releaseKey(int channel, int key, int speed) {
        System.out.println("R");
    }

}
