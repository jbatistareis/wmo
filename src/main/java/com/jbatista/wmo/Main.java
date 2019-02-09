package com.jbatista.wmo;

import com.jbatista.wmo.components.Instrument;
import com.jbatista.wmo.components.Key;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.SourceDataLine;

public class Main {

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
        final Instrument instrument = new Instrument();
        final Key key1 = instrument.buildKey(440);
        final Key key2 = instrument.buildKey(880);
        final Key key3 = instrument.buildKey(660);

        new Thread(() -> {
            try (SourceDataLine sourceDataLine = (SourceDataLine) mixer.getLine(lineInfo[0])) {
                sourceDataLine.open(new AudioFormat(44100, 16, 2, true, true));
                sourceDataLine.start();

                while (true) {
                    sourceDataLine.write(instrument.getFrame(), 0, 4);
                }
            } catch (LineUnavailableException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }
        }).start();

        key1.pressKey();
        Thread.sleep(2000);

        key1.releaseKey();
        Thread.sleep(1000);

        key2.pressKey();
        Thread.sleep(2000);

        key2.releaseKey();
        Thread.sleep(1000);

        key1.pressKey();
        key2.pressKey();
        Thread.sleep(2000);
        
        key3.pressKey();
        Thread.sleep(4000);
        
        key3.releaseKey();
    }

}
