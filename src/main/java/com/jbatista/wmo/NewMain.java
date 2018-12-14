package com.jbatista.wmo;

import com.jbatista.wmo.components.Instrument;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.SourceDataLine;

public class NewMain {

    public static void main(String[] args) throws InterruptedException {
        // setup
        final Mixer.Info[] mixerInfo = AudioSystem.getMixerInfo();
        for (int i = 0; i < mixerInfo.length; i++) {
            System.out.println(i + ": " + mixerInfo[i].getName());
        }
        System.out.println();

        final Mixer mixer = AudioSystem.getMixer(mixerInfo[0]);
        final Line.Info[] lineInfo = mixer.getSourceLineInfo();
        for (int i = 0; i < lineInfo.length; i++) {
            System.out.println(i);
        }
        System.out.println();

        // start
        final Instrument instrument = new Instrument("TEST");

        new Thread(() -> {
            try (SourceDataLine sourceDataLine = (SourceDataLine) mixer.getLine(lineInfo[0])) {
                sourceDataLine.open(new AudioFormat(44100, 16, 2, true, true));
                sourceDataLine.start();

                while (true) {
                    sourceDataLine.write(instrument.getFrame(), 0, 4);
                }
            } catch (LineUnavailableException ex) {
                Logger.getLogger(NewMain.class.getName()).log(Level.SEVERE, null, ex);
            }
        }).start();

        instrument.pressKey();
        Thread.sleep(2000);
        instrument.releaseKey();
        Thread.sleep(1000);
        instrument.pressKey();
        Thread.sleep(1000);
        instrument.releaseKey();
        Thread.sleep(1000);
        instrument.pressKey();

        // sourceDataLine.write(data, 0, data.length);
    }

}
