# Wave Magic Orchestra
###### A FM sound synthesis library


## Description
This library replicates the functionality of an old school Yamaha synthesizer, i'm not aiming to perfection, or creating an emulator, i just want to create something that sounds good enough.  
It uses FM synthesis ~~(don't tell anyone, but it actually is PM)~~ to simulate various instruments, ***80's style***.  
Checkout an interface demonstrating what can be done with it, called [WMO Operator](https://github.com/jbatistareis/wmo-operator).


## Features (for now)
* Up to 132 keys (on any desired frequency)
* Up to 6 oscillators per key (can play as many keys as your CPU can handle)
* Breakpoint / Level Scaling / Keyboard Tracking / Keyboard Following
* Use one of the classic 4 and 6 operator algorithms
* Load voices from SYSEX files (some settings can produce A LOT of noise or sound very different...)
* Filters (high pass, low pass, band pass, distortion, etc...)


## Note
WIP!


## Basics
The idea is that you instantiate an [Instrument](https://github.com/jbatistareis/wmo/blob/master/src/main/java/com/jbatista/wmo/synthesis/Instrument.java), pass a [Preset](https://github.com/jbatistareis/wmo/blob/master/src/main/java/com/jbatista/wmo/preset/InstrumentPreset.java), and finally, [press](https://github.com/jbatistareis/wmo/blob/master/src/main/java/com/jbatista/wmo/synthesis/Instrument.java#L140) and [release](https://github.com/jbatistareis/wmo/blob/master/src/main/java/com/jbatista/wmo/synthesis/Instrument.java#L167) a key. *Midi support is being worked on.*  
Presets can be obtained from DX7 sysex files using the [Dx7Sysex](https://github.com/jbatistareis/wmo/blob/master/src/main/java/com/jbatista/wmo/util/Dx7Sysex.java) utilitarian class, which reads bulk exported voices and returns a list of presets, one for each voice.  
Audio is obtained via 16bit [PCM](https://en.wikipedia.org/wiki/Pulse-code_modulation) samples, created by calling the one of the "getFrame" method variants ([getByteFrame](https://github.com/jbatistareis/wmo/blob/master/src/main/java/com/jbatista/wmo/synthesis/Instrument.java#L89), [getShortFrame](https://github.com/jbatistareis/wmo/blob/master/src/main/java/com/jbatista/wmo/synthesis/Instrument.java#L107), or [getFloatFrame](https://github.com/jbatistareis/wmo/blob/master/src/main/java/com/jbatista/wmo/synthesis/Instrument.java#L124)).  


## Example
This example utilizes [Java AudioSystem](https://docs.oracle.com/javase/8/docs/api/javax/sound/sampled/AudioSystem.html) to play the samples, it is a very basic use case.  
See [WMO Operator](https://github.com/jbatistareis/wmo-operator) for another example, it uses LibGdx key press and release events to trigger the Instrument.
```java
final List<InstrumentPreset> presetsList = Dx7Sysex.extractInstruments(new File("rom1a.syx"));
final Instrument instrument = new Instrument(44100);
instrument.setPreset(presetsList.get(20)); // preset 20 should be VIBES 1 on the cartridge dump used

/*
    we are creating a child thread because writing to the audio output is a blocking operation
    so we want this part to work without being bothered
*/
final Thread thread = new Thread(() -> {
    final AudioFormat audioFormat = new AudioFormat(44100, 16, 2, true, true);

    try (final SourceDataLine dataLine = AudioSystem.getSourceDataLine(audioFormat)) {
    
        // initializes the audio output
        dataLine.open(audioFormat);
        dataLine.start();
    
        // this blocks the thread, and have to be called as long as we want to play
        while (true) {
            // an audio frame have 4 bytes, 2 for each 16bit channel ([8bit L][8bit L]-[8bit R][8bit R])
            dataLine.write(instrument.getByteFrame(true), 0, 4);
        }
    } catch (LineUnavailableException e) {
        e.printStackTrace();
    }
});
thread.start();

// wait just a little for the audio initialization
Thread.sleep(500);

// this is from Stairway to Heaven intro
final KeyboardNote[] notes = new KeyboardNote[]{KeyboardNote.A_4, KeyboardNote.C_5, KeyboardNote.E_5, KeyboardNote.A_5, KeyboardNote.B_5};

// now we play then with 0.4s of duration, and 0.2s spacing
for(KeyboardNote note : notes){
    instrument.pressKey(note);
    Thread.sleep(400);
    instrument.releaseKey(note);
    Thread.sleep(200);
}
```


## TODO
- [ ] Proper README/documentation
- [x] [A demo interface](https://github.com/jbatistareis/wmo-operator)
- [ ] Pitch envelope
- [x] Break points
- [x] Envelope rate/speed scaling
- [x] Transpose
- [x] Improve feedback
- [ ] LFO
- [x] Filters running in parallel (aggregate the results of various filters, instead of just applying one filter after another)
- [ ] Filter serialization
- [ ] More filters
- [ ] Channel separation/manipulation
- [x] Instrument presets
- [x] API fine tuning
- [x] Parameters fine tuning
- [ ] MIDI support