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
* Load voices from SYSEX files (some settings can produce A LOT of noise or sound very different)
* Filters (high pass, low pass, band pass, distortion, etc...)


## Note
WIP!


## TODO
* Proper README/documentation
* API fine tuning
* Parameters fine tuning
* Improve feedback
* Pitch envelope
* LFO
* Transpose
* MIDI support
* Filter serialization
* More filters
* Channel separation/manipulation
* [~~A demo interface~~](https://github.com/jbatistareis/wmo-operator) **OK!**
* ~~Create some instrument presets~~ **OK!**
* ~~Break points~~ **OK!**
* ~~Filters running in parallel (aggregate the results of various filters, instead of just applying one filter after another)~~ **OK!**
* ~~Investigate crackings~~ **OK!**
* ~~Squash bugs on the envelope generator~~ **OK!**