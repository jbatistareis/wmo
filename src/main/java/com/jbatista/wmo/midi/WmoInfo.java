package com.jbatista.wmo.midi;

import javax.sound.midi.MidiDevice;

public class WmoInfo extends MidiDevice.Info {

    WmoInfo() {
        super("WMO MIDI Receiver", "https://github.com/jbatistareis", "Receives and interprets MIDI signals", "0.3");
    }

}
