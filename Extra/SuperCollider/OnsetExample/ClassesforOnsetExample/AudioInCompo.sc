AudioInCompo{
	var <mainGroup, <outBus, <liveAudio, <inBus, <liveSound, <currentLevel;


	*new{|inBus, hear/*trueFalse*/, guiFunction|
		^super.new.makeNew(inBus, hear, guiFunction);
	}



	makeNew{|mInBus, hear, guiFunction|
		var hearValue, osc;

		inBus = mInBus?(0);
		mainGroup = Group.head;
		outBus = Bus.audio(Server.default, 1);

		if(hear?(true) == false, {
			hearValue = 0;
			},{
				hearValue = 1;
		});


		liveAudio = SynthOSC(\inAudio, [\inBus, inBus, \outBus, outBus, \hearValue, hearValue], mainGroup, \addToHead, guiFunction);

	}
	hear{|onOff|
		if(onOff == true, {

			liveAudio.set(\hearValue, 1);
			},{
				liveAudio.set(\hearValue, 0);
		});
	}
}