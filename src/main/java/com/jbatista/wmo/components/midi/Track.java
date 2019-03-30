package com.jbatista.wmo.components.midi;

import java.util.HashMap;
import java.util.Map;

public class Track {

    private Sequencer sequencer;
    private Map<Long, Event> events = new HashMap<>();
    private Event event;

    protected Track(Sequencer sequencer, javax.sound.midi.Track midiTrack) {
        this.sequencer = sequencer;

        byte[] data;
        for (int i = 0; i < midiTrack.size(); i++) {
            data = midiTrack.get(i).getMessage().getMessage();

            if (((data[0] & 0xFF) == 0xFF) && ((data[1] & 0xFF) == 0x51) && ((data[2] & 0xFF) == 0x03)) {
                events.put(
                        midiTrack.get(i).getTick(),
                        new Event(
                                Event.Type.SET_BPM,
                                60000000 / (((data[3] & 0xFF) << 16) + ((data[4] & 0xFF) << 8) + (data[5] & 0xFF)),
                                0,
                                0));
            } else if ((data[0] & 0xF0) == 0x90) {
                events.put(
                        midiTrack.get(i).getTick(),
                        new Event(
                                Event.Type.KEY_PRESS,
                                data[1] & 0xFF,
                                data[2] & 0xFF,
                                data[0] & 0x0F));
            } else if ((data[0] & 0xF0) == 0x80) {
                events.put(
                        midiTrack.get(i).getTick(),
                        new Event(
                                Event.Type.KEY_RELEASE,
                                data[1] & 0xFF,
                                data[2] & 0xFF,
                                data[0] & 0x0F));
            }
        }
    }

    protected void readMidiMessage(long tick) {
        event = events.get(tick);
        if (event != null) {
            switch (event.getType()) {
                case SET_BPM:
                    sequencer.setBpm(event.getValue1());
                    break;
                case SET_INSTRUMENT:
                    break;
                case KEY_PRESS:
                    sequencer.pressKey(event.getChannel(), event.getValue1(), event.getValue2());
                    break;
                case KEY_RELEASE:
                    sequencer.releaseKey(event.getChannel(), event.getValue1(), event.getValue2());
                    break;
                default:
                    break;
            }
        }
    }

}
