# Wave Magic Orchestra
###### A FM sound synthesis library


### [Demo here!](https://github.com/jbatistareis/wmo-demo) (outdated, just beeps and boops version)


## Description
This library replicates the functionality of an old school Yamaha synthesizer, i'm not aiming to perfection, or creating an emulator, i just want to create something that sounds good enough.

It uses FM synthesis ~~(don't tell anyone, but it actually is PM)~~ to simulate various instruments, ***80's style***.


## Features (for now) 
* Up to 144 key frequencies
* Up to 144 voices (in theory, as many as your processor can handle)
* Create your own algorithm...
* ...or choose one of the classic 4 and 6 operators algorithms
* Filters (high pass, low pass, band pass, distortion, etc...)
* MIDI support (in the near future)


## Note
Currently undergoing major changes to be more similar to a DX family synthesizer


## TODO
* Proper README/documentation
* Parameters fine tuning
* Improve feedback
* Create some instrument presets
* MIDI interpretation
* Filter serialization
* More filters
* Filters running in parallel (aggregate the results of various filters, instead of just applying one filter after another)
* Investigate crackings
* Squash bugs on the envelope generator
* Channel separation/manipulation