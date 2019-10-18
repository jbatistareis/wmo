package com.jbatista.wmo.midi;

import com.jbatista.wmo.MathUtil;

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

            // parse events
            switch (data.length) {
                case 3:
                    if ((data[0] & 0xF0) == 0x90) {
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

                    break;
                case 6:
                    if (MathUtil.valueFrom24bit(true, data[0], data[1], data[2]) == 0xFF5103) {
                        events.put(
                                midiTrack.get(i).getTick(),
                                new Event(
                                        Event.Type.SET_BPM,
                                        60000000 / MathUtil.valueFrom24bit(true, data[3], data[4], data[5]),
                                        0,
                                        0));
                    }

                    break;
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
