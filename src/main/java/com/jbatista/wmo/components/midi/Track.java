package com.jbatista.wmo.components.midi;

import com.jbatista.wmo.AudioFormat;
import com.jbatista.wmo.WaveForm;
import com.jbatista.wmo.components.play.Instrument;

public class Track {

    private Instrument instrument;
    private Sequencer sequencer;
    private javax.sound.midi.Track midiTrack;

    private int currentMessage = 0;

    protected Track(Sequencer sequencer, javax.sound.midi.Track midiTrack, AudioFormat audioFormat) {
        this.sequencer = sequencer;
        this.midiTrack = midiTrack;

        // TODO presets
        this.instrument = new Instrument(WaveForm.SINE, audioFormat);
    }

    public byte[] getFrame() {
        return instrument.getByteFrame(true);
    }

    protected void readMidiMessage(long tick) {
        if (midiTrack.get(currentMessage).getTick() == tick) {
            System.out.print(currentMessage + ": ");
            // TODO decode
            for (byte data : midiTrack.get(currentMessage).getMessage().getMessage()) {
                System.out.print(Integer.toHexString(data & 0xFF) + ' ');
            }
            System.out.println();

            currentMessage++;
        }
    }

}
