(
( // \Onset Det Send Through  '/onsetDettriger'
SynthDef(\onsetDetTriggerSarah,{arg inBus = 100, outBus = 100, thresh = 0.15, bufFrames = 512, replyID = (-1), gate = 1;
    var in, freq, hasFreq, triggerOut, gates, sendTrig, sendValue, chain, onset, trigger, triggerBool, array;



    in= In.ar(inBus);
	gates = Gate.ar(in, DC.kr(1)*gate);


	chain = FFT(LocalBuf(bufFrames), gates);
	onset = Onsets.kr(chain, thresh, \rcomplex);
	//Poll.kr(Impulse.kr(20), onset, \thresh);
	triggerBool = (onset >= 0.1);
	trigger = Trig.kr(triggerBool, 0.05);

	sendTrig = SendReply.kr(Impulse.kr(10), '/onsetDetTriggerSarah', [thresh], replyID);
	triggerOut = Out.kr(outBus, trigger);
}).writeDefFile;
);
( // \Onset Det Send Through  '/onsetDettriger'
SynthDef(\onsetDetTrigger,{arg inBus = 100, outBus = 100, thresh = 0.15, bufFrames = 512, replyID = (-1), gate = 1;
    var in, freq, hasFreq, triggerOut, gates, sendTrig, sendValue, chain, onset, trigger, triggerBool, array;



    in= In.ar(inBus);
	gates = Gate.ar(in, DC.kr(1)*gate);
	//Poll.kr(Impulse.kr(20), thresh, \thresh);

	chain = FFT(LocalBuf(bufFrames), gates);
	onset = Onsets.kr(chain, thresh, \rcomplex);

	triggerBool = (onset >= 0.1);
	trigger = Trig.kr(triggerBool, 0.05);

	sendTrig = SendReply.kr(trigger, '/onsetDetTrigger', trigger, replyID);
	triggerOut = Out.kr(outBus, trigger);
}).writeDefFile;
);

(
SynthDef(\wave, { |out, freq=440, amp=0.1, sustain=0.1, mod=0.2|
	OffsetOut.ar(out,
		EnvGen.ar(Env.perc(ExpRand(0.001, 0.05), sustain, amp), doneAction: 2)
		*
		SinOsc.ar(freq, SinOsc.ar(sustain.reciprocal * 8, [0, Rand(0, pi)], mod))
	)
}).writeDefFile;
);

(
SynthDef(\inAudio, {|inBus = 0, outBus = 0, outBus2 = 1, hearValue= 1, replyID = (-1)|
	var imp, delimp, in;
	in = SoundIn.ar(inBus);
	imp = Impulse.kr(10);
	delimp = Delay1.kr(imp);
	SendReply.kr(imp, '/inAudio', [Amplitude.kr(in), K2A.ar(Peak.ar(in, delimp).lag(0, 3))], replyID);
	Out.ar(outBus, in);
	Out.ar(outBus2, in*hearValue);
}).writeDefFile;
);
(
SynthDef(\onsetCounter, {|inBus = 0, reset =0, replyID = (-1)|
	var in, step, send, resetTrig;

	in= In.kr(inBus);

	resetTrig = (DC.kr(1)*reset);
	//Poll(Impulse.kr(10), resetTrig, \resetTrig);
	step = PulseCount.kr(in, resetTrig);

	send = SendReply.kr(in,'/onsetCounter' ,step, replyID);



}).writeDefFile;
);

(
SynthDef(\env, { arg i_outbus=0, gateOfEnv = 1;
    var env, envctl, gen, gates;

    // make a dummy 8 segment envelope
    env = Env.newClear(8);

    // create a control argument array
    envctl = \env.kr( env.asArray );
	gen = EnvGen.kr(envctl, doneAction: 2);
	gates = Gate.kr(gen, DC.kr(1)*gateOfEnv);

    ReplaceOut.kr(i_outbus, gates);
}).writeDefFile;
);


(
SynthDef(\cut_Buffer, { arg out = 0, bufnum, gate=1, speed = 1, volume= 1, t_restart, startPos = 0, loop = 0, doneAc = 2;
	var env, gen, vol, playRate, start;

	vol = In.kr(volume);

	env = Env.adsr(0.001, 0.01, 1.0, 0.001, 1.0, 0);
	gen = EnvGen.kr(env, gate, doneAction: 2);
	playRate = speed*BufRateScale.kr(bufnum);
	start = startPos * BufFrames.kr(bufnum);

    Out.ar(out,
		(PlayBuf.ar(1, bufnum, playRate, t_restart, start, loop, doneAc)*gen)*vol);
}).writeDefFile;
);
);