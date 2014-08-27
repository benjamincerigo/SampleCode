/*Listener -

- LastEdited by Benjamin Cerigo
- Version 1
- Date Tue 15 Oct 2013 15:55:30 CEST
- Last Work done: Unknown

*/



ListenerSarah1{

	//The listener class makes 2 SynthOSC a listener and an ender. creates In realtion to the name that has been given.

	//also creates the nessarcary busses and groups so that it contains the synths.


	//The arguments

	//target is the target synth that the group of Synth OSCs will be stored after. This should be a Synth(\ampToLevels);

	//inBus it the outBus of the Synth(\ampToLevels).

	//The listener Def is the def which is going ot listener to sound coing in this is a very spseific synth and must be contructed as explained at the end of this class.

	//the ender Def is the same as the listener Def but as gate armuent is not needed but a reset arument is needed that is set to 0

	//listener args ar ehte aruments for the listener SYnth. shoudl realted to the Def given.

	//Ender args same as listener args. but for the sender Def

	//holder. This is an extra and is currently not being used.



	var <>listener, <>ender;
	var <typeOfListener;
	var <shardedBus, <listeningGroup;
	var <>theEndValue, <>curThresh;
	var target, tag;
	var <>devValue;
	var <>endSend;
	var <pauseTime, pauseTask;
	var <threshBus, threshTask, <>theNextThresh, <synthThresh, <canThresh;
	var <>plusNum;






	*new{|target, inBus, listenerDef=\onsetDetTrigger, enderDef=\onsetCounter, theEndValue, thresh, listernerArgs, enderArgs|

		^super.new.makeFull(target, inBus, listenerDef , enderDef, theEndValue, thresh, listernerArgs, enderArgs);
	}


	*newPaused{|target, inBus, listenerDef=\onsetDet, enderDef=\onsetCounter, theEndValue, thresh, listernerArgs, enderArgs|

		^super.new.makePaused(target, inBus, listenerDef , enderDef , theEndValue, thresh, listernerArgs, enderArgs);
	}




	makeFull{|mtarget, inBus, listenerDef = \onsetDet, enderDef = \onsetCounter, endValue, thresh, listenerArgs, enderArgs|


		target = mtarget;




		this.general(target, endValue, enderDef, thresh);

		listener = SynthOSC(listenerDef.asSymbol,
			this.lisArgsMake(thresh, listenerArgs, inBus);,
			listeningGroup,
			\addToHead,
			this.listFunctionMake();
		);

		ender = SynthOSC(enderDef.asSymbol,
			this.endArgsMake(enderArgs),
			listeningGroup,
			\addToTail,
			this.enderFunctionMake(endValue);
		);
		this.changed(\AEnder, [0, theEndValue]);
		this.changed(\Ready);
		canThresh = true;
	}





	//Make a Pause Listener

		makePaused{|mtarget, inBus, listenerDef = \onsetDet, enderDef = \onsetCounter, endValue, thresh, listenerArgs, enderArgs|





		this.general(mtarget, listenerDef);
		(tag + "endValue" + endValue).postln;
		(tag+ "theEndValue" + theEndValue).postln;

		listener = SynthOSC.newPaused(listenerDef.asSymbol,

			this.lisArgsMake(thresh, listenerArgs, inBus);,
			listeningGroup,
			\addToHead,
			this.listFunctionMake();
		);

		ender = SynthOSC.newPaused(enderDef.asSymbol,
			this.endArgsMake(enderArgs),
			listeningGroup,
			\addToTail,
			this.enderFunctionMake(endValue);
		);
		this.changed(\AEnder, [0, theEndValue]);
		this.changed(\Ready);
		(tag+ "theEndValue" + theEndValue).postln;
	}




	general{|mtarget, listenerDef, endValue|


		tag = "Listener";
		(tag + "size" + endValue.size).postln;
		pauseTime = 1;
		pauseTask = Task({
			this.pauseListening(1);
			if(pauseTime != 0, {
			pauseTime.wait;
			this.pauseListening(0);
			});


		});
		plusNum = 0;




		if(endValue.isArray == true, {
			(tag + "isAnArray").postln;
			theEndValue = endValue[0];

			},{
				(tag + "isnotArray").postln;
				theEndValue = endValue;

		});

		if(mtarget.isKindOf(SynthOSC),{
			target = mtarget.synth;
		},{
				target = mtarget
		});




		shardedBus = Bus.control(Server.default, 1);
		listeningGroup = Group.new(target, \addAfter);

	}





	//Listener makers

	lisArgsMake{|thresh, listenerArgs, inBus|
		curThresh = thresh?(0.4);

		listenerArgs = listenerArgs ++ ["inBus", inBus] ++ ["outBus", shardedBus] ++["thresh", curThresh];
		^listenerArgs;
	}


	listFunctionMake{
		var listenerFunction;
		^listenerFunction = {|msg| Task({|self|
			1.do{
				var lisValue;
				lisValue = msg[3];

				//"hasBeenAHit".postln;
				this.changed(\AListener, lisValue);
				0.01.wait;
				//lisValue = 0;
			}
		}).start;
		};
	}




	//Ender Make
	endArgsMake{|enderArgs|

		enderArgs = enderArgs ++ ["inBus", shardedBus];
		^enderArgs;
	}

	enderFunctionMake{|endValue|


		var enderFunction;
		theEndValue = endValue?(10);



		enderFunction = {|msg|
			var toSend;



			msg.postln;


			devValue= msg[3];
			("devValue"+devValue).postln;
			devValue = devValue +plusNum;
			("devValue"+devValue).postln;


			Routine.run{
				var c;
				c = Condition.new;
				Server.default.sync(c);
				"didThisTask".postln;




				listener.synth.set(\gate, (-1));
				Server.default.sync(c);
				if(synthThresh.isNil != true,{
				synthThresh.isRunning.postln;

				if(synthThresh.isRunning, {
					"SynthTryFree".postln;
					("SynthThresh ID:"+synthThresh.nodeID).postln;
				synthThresh.postln;
				synthThresh.free;
				});
				});
				threshBus.set(1);


				Server.default.sync(c);

				canThresh = true;

			};
			this.changed(\AEnder, [devValue, canThresh]);












			if((devValue >= theEndValue),{
				this.changed(\reachedTheEnd);
				(tag+"reachedTheEnd").postln;
			});


		};
		^enderFunction;
	}

	//Extra







	reset{
		Task({
			1.do{

				listener.synth.set(\gate, (-1));
				ender.synth.set(\reset, (-1));
				0.01.wait;
				ender.synth.set(\reset, (1));
				this.changed(\reset);

			}
		}).start;
		this.changed(\Ready);

	}

	getGoing{
		var isOk;
		if(this == \Ready, {
			listener.synth.set(\gate, (1));
			isOk = true;
		},{
				isOk = false;
		});
		this.changed(\Running);
		^isOk;
	}





	run{|plus|
		if(plus.isNil != true, {
		plusNum = plus;
		});
		listeningGroup.moveAfter(target);
		Task({
			1.do{
				listener.run;
				ender.run;
				listener.synth.set(\gate, (1));

			}
		}).start;
		canThresh = true;
		this.changed(\Running);
	}



	endSection{

		try{(tag + "ended").postln;}{(tag+ "it was this that did not work").postln};
		//this.reset;
		Task({
			this.reset;
			0.1.wait;
			this.pause;
			this.changed(\Ended);
		}).start;


	}

	pauseListen{|waitTime|
		pauseTime = waitTime;
		if(pauseTask.isPlaying,{
			pauseTask.stop;});

		pauseTask.reset;
		pauseTask.play;

	}
	pauseSynths{

				listener.pause;
				ender.pause;
	}

	free{
		listener.free;
		ender.free;
		^super.free;
	}

	pauseListening{|onOff|

		if(onOff == 1, {

			listener.synth.set(\gate, -1);
			this.changed(\paused, true);

			},{
				listener.synth.set(\gate, 1);
				this.changed(\paused, false);
		});


	}

	threshPause{|pauseTime, env, nextThresh|
		//Env up to 8.


		if(canThresh == true, {
			canThresh = false;
			Routine.run{
				var c;
				c = Condition.new;
				Server.default.sync(c);
				if(threshBus.isNil,{
					threshBus = Bus.control(Server.default, 1);
					listener.synth.map(\thresh, threshBus);
				});

				threshBus.set(1);
				theNextThresh = nextThresh?curThresh;

				Server.default.sync(c);

				if(pauseTime != 0, {

					this.pauseListening(1);
					pauseTime.wait;
					this.pauseListening(-1);
				});
				Server.default.sync(c);

				this.changed(\ThreshReduction);

				synthThresh = Synth(\env, [i_outbus: threshBus, env: env], listeningGroup, \addToHead);
				synthThresh.postln;
				NodeWatcher.register(synthThresh, true);


			};
	});
	}







}

///The synth must have the same as the SendReply cmdName but as a path.

// it must have a replyID arument which is set at defalut (-1);
//It must have a gate set defalut to 1;

//Must have an inBus
//must and an outBus

/* here is an Example

SynthDef(\onsetDetTrigger,{arg inBus = 100, outBus = 100, thresh = 0.15, bufFrames = 512, replyID = (-1), gate = 1;
    var in, freq, hasFreq, triggerOut, gates, sendTrig, sendValue, chain, onset, trigger, triggerBool, array;



    in= In.ar(inBus);
	gates = Gate.ar(in, DC.kr(1)*gate);

	chain = FFT(LocalBuf(bufFrames), gates);
	onset = Onsets.kr(chain, thresh, \rcomplex);

	triggerBool = (onset >= 0.1);
	trigger = Trig.kr(triggerBool, 0.05);

	sendTrig = SendReply.kr(trigger, '/onsetDetTrigger', trigger, replyID);
	triggerOut = Out.kr(outBus, trigger);
}).writeDefFile;
);

*/