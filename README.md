# Wave Magic Orchestra
###### A FM sound synthesis library


### [Demo here!](https://github.com/jbatistareis/wmo-demo)


## Usage  
_tl;dr instantiate an instrument, build its keys based on a frequency, add modulators as seen fit, press some keys, get the frame_  
This library create audio frames at 16bit 44100Hz. To produce a constant sound, you need to perform multiple calls to it using a loop, or feeding an audio dataline:   
```
final Instrument instrument = new Instrument();
// creates a key that plays A4
final Key key = instrument.buildKey(440);

...
// this code blocks the current thread, it has to run from another one
// get a SourceDataLine using the Sound API
sourceDataLine.open(new AudioFormat(44100, 16, 2, true, true));
sourceDataLine.start();

// the write method blocks the thread, so it can safely be used in this kind loop
while (true) {
  sourceDataLine.write(instrument.getFrame(), 0, 4);
}
...

// plays it
key.pressKey();
```  
To achieve different sounds, you can change the phase, set an envelope or add modulation.


## TODO
* More audio formats
* Better modulation
* Lots of stuff