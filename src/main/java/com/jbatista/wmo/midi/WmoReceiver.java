package com.jbatista.wmo.midi;

import com.jbatista.wmo.preset.InstrumentPreset;
import com.jbatista.wmo.synthesis.Instrument;
import com.jbatista.wmo.util.Mixer;
import com.jbatista.wmo.util.WmoFile;

import javax.sound.midi.MidiMessage;
import javax.sound.midi.Receiver;
import java.util.Arrays;

public class WmoReceiver implements Receiver {

    private final WmoMidiDevice wmoMidiDevice;
    private final Instrument[] channels = new Instrument[16];
    private final Mixer mixer;

    WmoReceiver(WmoMidiDevice wmoMidiDevice, int sampleRate) {
        this.wmoMidiDevice = wmoMidiDevice;

        for (int i = 0; i < 16; i++) {
            channels[i] = new Instrument(sampleRate);
        }

        mixer = new Mixer(channels);
    }

    public Mixer getMixer() {
        return mixer;
    }

    @Override
    public void send(MidiMessage message, long timeStamp) {
        byte[] messageData = message.getMessage();
        int status = messageData[0] & 0xF0;
        int channel = messageData[0] & 0x0F;

        switch (status) {
            case 0x80:
                channels[channel].releaseKey(messageData[1] + 24); // TODO velocity
                break;

            case 0x90:
                if (messageData[2] == 0) { // velocity 0 is NOTE OFF
                    channels[channel].releaseKey(messageData[1] + 24);
                } else {
                    channels[channel].pressKey(messageData[1] + 24); // TODO velocity
                }
                break;

            case 0xB0:
                switch (messageData[1]) {
                    case 0x7B:
                        channels[channel].releaseAllKeys();
                        break;

                    default:
                        // message not implemented
                        break;
                }
                break;

            case 0xC0:
                channels[channel].setPreset(buildPreset(messageData[1]));
                break;

            case 0xFF:
                close();
                break;

            default:
                // message not implemented
                break;
        }
    }

    @Override
    public void close() {
        for (int i = 0; i < 16; i++) {
            channels[i].releaseAllKeys();
        }
    }

    private InstrumentPreset buildPreset(int id) {
        final int from = 162 * id;
        final int to = from + 162;

        if (to > wmoMidiDevice.soundBank.length) {
            return new InstrumentPreset();
        }

        return WmoFile.parseBytes(Arrays.copyOfRange(wmoMidiDevice.soundBank, from, to));
    }

}
