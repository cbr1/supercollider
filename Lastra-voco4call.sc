//audio buffer
//b = Buffer.read(s, Platform.resourceDir +/+ "sounds/a11wlk01.wav");

b = Buffer.read(s, Platform.userHomeDir +/+ "SC/grave.wav");


//sine 4 test
{SinOsc.ar(440, mul: 0.4) ! 2 }.play(s);

(
s=Server.local;
Server.local.options.memSize = 65536; //32768
p = ProxySpace.push(s);
s.boot;
)

//Cello call I mvt
(
SynthDef(\lastra,
	{ |dl1, dl2, mixi=0.5, mixo=0.5, frqN=50, ampN=0.1, frq=1, bat=10, dlt1=1, dlt2=2, fb=0.2, frqt=900, lgtm=0.1, delco=1, deca=1, fadeTime=0.1, gain=1, gate=0|

				 var snd = PlayBuf.ar(1, b, BufRateScale.kr(b), loop: 1);

		LocalOut.ar(
				[
				dl1=(
					LPF.ar(
						(DelayN.ar(
							LocalIn.ar(2 ) * Lag.kr(fb,1)
							+ (snd*mixi)
							+ (LFClipNoise.ar(frqN,ampN) * (1-mixi)),
							1,
							Lag.kr((1/(Lag.kr(TRand.kr(0, bat, Impulse.kr(frq)), frq)
								+ dlt1)),
								lgtm)
								)
						),
						frqt)
					),
				dl2=(
					LPF.ar(
						(DelayN.ar(
							LocalIn.ar(2)*Lag.kr(fb,1)
							+(snd*mixi)+(LFClipNoise.ar(frqN,ampN)*(1-mixi)),
							1,
							Lag.kr(
								(1/(Lag.kr(TRand.kr(0, bat, Impulse.kr(frq)), frq)
								+dlt2)),
								lgtm)
								)
						),
						frqt)
					)
				]
			);

		Out.ar(
			[0, 1],
			[
			(dl1 * mixo) + (CombN.ar(dl1, 2, delco, deca)*(1-mixo)),
			(dl2 * mixo) + (CombN.ar(dl2, 2, delco*0.4, deca*1.2)*(1-mixo))] * EnvGen.ar(Env.asr(fadeTime, gain, fadeTime), gate, doneAction:2)
			)

	}).send(s);
);

(
SynthDef(\voco,
	{ |gin, fltr, dly, frmAr, out=0, frqt=50,
       lagT=0.003, hrm=12, bw=0.25, noise=10,
       fb=0.1, tide=1, fadeTime=0.1, gain=1, crv=1, gate=1|

		 var snd = PlayBuf.ar(1, b, BufRateScale.kr(b), loop: 1);


		LocalOut.ar(
				[
				dly=(
					DelayN.ar(
						LocalIn.ar(1)*(Lag.kr(fb, 1))
						+fltr=(
							BPF.ar(
								PitchShift.ar(snd*gin, 0.02, 0.99, 0, 0.002),
								frmAr = Lag.kr(frqt, lagT)*(2**(Array.series(10, 0, 2)/hrm))
								* LFNoise0.ar(25).range(0.2.lag(0.1), 0.6.lag(0.01)),
								((frmAr*(2**(bw/hrm)))-(frmAr*(2**(bw.neg/hrm))))/frmAr
							)
						),
						maxdelaytime:4,
						delaytime:Lag.kr(tide * Array.rand(10, 0.1, 3.9), lagT)
					)
				)
			]
		);

		Out.ar(
			[0, 1],
			(dly + fltr) * EnvGen.kr(Env.asr(fadeTime, gain, fadeTime, crv), gate, doneAction:2)
		);
}).send(s);
);

~voc1 = \voco;
~voc1.fadeTime = 1;
~voc1.xset(\gin, 0.9, \frqt, 4092, \hrm, 12, \bw, 0.3, \fb, 0.12, \tide, 1.5, \gain, 0.9, \gate, 1);
~voc1.release(3);

~voc2 = \voco;
~voc2.fadeTime(3);
~voc2.xset(\gin, 0.6, \frqt, 1542, \hrm, 37, \bw, 0.41, \fb, 0.1, \tide, 1.89, \gain, 0.6, \gate, 1);
~voc2.release(3);

~lastra = \lastra; //mixi=1mic
~lastra.fadeTime = 10;
~lastra.xset(\mixi, 0.9, \mixo, 0.9, \frqN, 440, \ampN, 1, \frq, 178, \bat, 15, \dlt1, 5, \dlt2, 1, \lgtm, 0.02, \fb, 0.9, \frqt, 4000, \delco, 0.57, \deca, 5, \gain, 0.01, \gate, 1);
~lastra.release;

~lastra.end(10);
//clean Proxy Space
p.clear(5);
p.pop;