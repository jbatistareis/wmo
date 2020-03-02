# Wave Magic Orchestra
###### A FM sound synthesis library


## Description
This library replicates the functionality of an old school Yamaha synthesizer, i'm not aiming to perfection, or creating an emulator, i just want to create something that sounds good enough.

It uses FM synthesis ~~(don't tell anyone, but it actually is PM)~~ to simulate various instruments, ***80's style***.


## Features (for now) 
* Up to 168 key frequencies
* Up to 168 voices (in theory, as many as your CPU can handle)
* Breakpoint / Level Scaling / Keyboard Tracking / Keyboard Following
* Create your own algorithm...
* ...or choose one of the classic 4 and 6 operators algorithms
* Filters (high pass, low pass, band pass, distortion, etc...)


## Note
WIP!


## TODO
* Proper README/documentation
* [A demo interface](https://github.com/jbatistareis/wmo-operator)
* API fine tuning
* Parameters fine tuning
* Improve feedback
* Pitch envelope
* ~~Break points~~ **OK!**
* Create some instrument presets
* MIDI support
* ~~Filter serialization~~ **OK!**
* More filters
* ~~Filters running in parallel (aggregate the results of various filters, instead of just applying one filter after another)~~ **OK!**
* ~~Investigate crackings~~ **OK!**
* ~~Squash bugs on the envelope generator~~ **OK!**
* Channel separation/manipulation