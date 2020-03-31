package com.jbatista.wmo.midi;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.Transmitter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class WmoMidiDevice implements MidiDevice {

    private static final Info INFO = new WmoInfo();
    private final List<Receiver> receivers = new ArrayList<>();
    private final int sampleRate;
    byte[] soundBank = new byte[0];

    public WmoMidiDevice(int sampleRate) {
        this.sampleRate = sampleRate;
    }

    public void setSoundBank(File soundBank) throws IOException {
        this.soundBank = Files.readAllBytes(soundBank.toPath());
    }

    @Override
    public Info getDeviceInfo() {
        return INFO;
    }

    @Override
    public void open() throws MidiUnavailableException {
        // ignore
    }

    @Override
    public void close() {
        // ignore
    }

    @Override
    public boolean isOpen() {
        return true;
    }

    @Override
    public long getMicrosecondPosition() {
        return -1;
    }

    @Override
    public int getMaxReceivers() {
        return -1;
    }

    @Override
    public int getMaxTransmitters() {
        return 0;
    }

    @Override
    public Receiver getReceiver() throws MidiUnavailableException {
        final WmoReceiver receiver = new WmoReceiver(this, sampleRate);
        receivers.add(receiver);

        return receiver;
    }

    @Override
    public List<Receiver> getReceivers() {
        return receivers;
    }

    @Override
    public Transmitter getTransmitter() throws MidiUnavailableException {
        throw new MidiUnavailableException("WMO doesn't support MIDI transmitters");
    }

    @Override
    public List<Transmitter> getTransmitters() {
        return null;
    }

}
