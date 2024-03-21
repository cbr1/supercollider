//default GUI!!!
Server.default.makeGui;
s.volume.gui;
ServerOptions.devices; //list devices
ServerOptions.outDevices;
postln; // output devices
s = Server.local; //FIRST
s.options.device = "ASIO : ASIO4ALL v2"; //use ASIO4ALL
s.options.device = "ASIO : JackRouter"   //use Linux Jack Server
//s.options.device = "MME : Altoparlanti (2- EDIROL UA-700)"

s.options.numAudioBusChannels; //BUS
s.options.numOutputBusChannels  = 2;//IN
s.options.numInputBusChannels = 2; //OUT
s.options.sampleRate = 48000;
//BOOT
s.boot;
s.plotTree; //plot Albero synth
s.meter;
s = Server.local.boot;

//*** ERROR: failed to open UDP socket: address in use.
Server.freeAll;
Server.killAll;

