# Wave Magic Orchestra
###### A FM sound synthesis library


[~~Demo here~~](https://github.com/jbatistareis/wmo-demo) (outdated, beeps and boops version)


## Description
This library replicates the functionality of an old school Yamaha synthesizer, i'm not aiming to perfection, or creating an emulator, i just want to create something that sounds good enough.

It uses FM synthesis ~~(don't tell anyone, but it actually is PM)~~ to simulate various instruments, ***80's style***.


## Features (for now) 
* Up to 168 key frequencies
* Up to 168 voices (in theory, as many as your CPU can handle)
* Create your own algorithm...
* ...or choose one of the classic 4 and 6 operators algorithms
* Filters (high pass, low pass, band pass, distortion, etc...)


## Note
WIP!


## TODO
* Proper README/documentation
* API fine tuning
* Parameters fine tuning
* Improve feedback
* Pitch envelope
* Break points
* Create some instrument presets
* MIDI support
* Filter serialization
* More filters
* Filters running in parallel (aggregate the results of various filters, instead of just applying one filter after another)
* ~~Investigate crackings~~ **OK!**
* ~~Squash bugs on the envelope generator~~ **OK!**
* Channel separation/manipulation