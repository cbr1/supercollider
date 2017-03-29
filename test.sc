//ASIO DRIVER
s = Server.local.options.device_("US-16x08 ASIO");
//MME DRIVER
//s = Server.local.options.device_("Speakers (US-16x08)");
s = Server.local;

s = Server.default;
//s.boot;

s.makeWindow;

Server.killAll;
//Server.default.boot;

/////////////////////////////////////////
FreqScope.new(400, 200, 0);
/////////////TEST IT!/////////////////////
{SinOsc.ar(440, mul: 0.2).dup(2)}.play(s);

s.queryAllNodes;

///////REcord seq ////////////////////////
s.recHeaderFormat = "wav";
s.prepareForRecord;
(
~rec = Task({
						s.record;
						//DURATA REGISTRAZIONE
						300.do({ arg i; (i+1).postln; 1.wait;});

		     	s.stopRecording;
						});
)
~rec.start;
~rec.stop;

Quarks.gui;
//Quarks.install("dewdrop_lib");

MethodOverride.printAll;

Platform.userExtensionDir;

(
SynthDef.new("test_bundle", {|freq = #[440,660]|

	var env = Env([0.0, 0.5, 0.0, 1.0, 0.9, 0.0], [0.05, 0.1, 0.01, 1.0, 1.5], -4);
	var envgen = EnvGen.ar(env, Impulse.kr(10), doneAction: 1);
	var out =  SinOsc.ar(freq, mul: 0.2) * envgen;
	Out.ar(0,out!2)}).send;
)

Spec.add(\freq, [20, 120000, \exp]);


(
s.makeBundle(nil, {Synth("test_bundle",[\freq, #[440,60]])});
s.makeBundle(2.0, {Synth("test_bundle",[\freq, #[2400,1060]])});
)
