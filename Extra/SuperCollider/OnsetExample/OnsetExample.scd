(
Routine.run {
    var c;

    // create a condition variable to control execution of the Routine
    c = Condition.new;

    s.bootSync(c);
    \BOOTED.postln;
	a = AudioInCompo(0, false, {arg msg;

	});


	f = Platform.resourceDir +/+ "sounds/a11wlk01.wav";
	h = ();
	h.buffer = CutBuffer1(f, s, loopNum: 1);
	h.buffer.vol(0.1);
	1.wait;
	h.buffer.playBuffer(0);
	h.onset = OnsetDete(a.outBus, a.liveAudio, 0.2, [0.01, 0.01], {|msg| h.buffer.pause(-1);});

    \b_alloc_DONE.postln;
};
)


