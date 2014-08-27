OnsetDete{



	var <group, volSynth, <>volBus, target, outBus;
	var <>listener;
	var <>waitArray;
	var reactorArgs, listenerArgs;
	var pauseTime, pauseTask;
	var <curThresh;
	var <>currentFunction;


	*new{|inBus, target, thresh, waitArray, item|

		^super.new.makeFull(inBus, target, thresh, waitArray, item);
	}


	*newPaused{|inBus, target, thresh, waitArray, item|

		^super.new.makePaused(inBus, target, thresh, waitArray, item);
	}


	//Not in Use
	makeFull{|inBus, mtarget, thresh, mwaitArray, item|


		this.general(mtarget, mwaitArray, item);


		listener = SynthOSC(\onsetDetTrigger,

			this.lisArgsMake(thresh, listenerArgs, inBus);,
			group,
			\addToHead,
			this.listFunctionMake();
		);

	}





	//Make a Pause Listener

	makePaused{|inBus, mtarget, thresh, mwaitArray, item|
		("makePaused" ++  inBus + thresh +mtarget++ item).postln;

		this.general(mtarget, mwaitArray, item);


		listener = SynthOSC.newPaused(\onsetDetTrigger,

			this.lisArgsMake(thresh, listenerArgs, inBus);,
			group,
			\addToHead,
			this.listFunctionMake(item);
		);

	}

	general{|mtarget, mwaitArray, item|

		if(mtarget.isKindOf(SynthOSC),{
			target = mtarget.synth;
		},{
				target = mtarget
		});

		this.changeFunction(item);

		pauseTime = 1;
		pauseTask = Task({
			this.pauseListening(1);
			if(pauseTime != 0, {

			pauseTime.wait;
			this.pauseListening(0);
			});


		});
		("mwaitArray"++mwaitArray).postln;
		waitArray = mwaitArray?[2, 0.1];

		group = Group.new(target, \addAfter);
		outBus = Bus.control(Server.default, 1);
	}

	lisArgsMake{|thresh, listenerArgs, inBus|
		curThresh = thresh?(0.4);

		listenerArgs = listenerArgs ++ ["inBus", inBus] ++ ["outBus", outBus] ++["thresh", curThresh];
		listenerArgs.postln;
		^listenerArgs;
	}

	listFunctionMake{
		var listenerFunction;
		^listenerFunction = {|msg| Task({|self|
			1.do{
				var lisValue, waitTime;
				lisValue = msg[3];


				waitTime = ((waitArray[1].rand)+waitArray[0]);
				this.changed(\waitTime, waitTime);
				this.pauseListen(waitTime);
				currentFunction.value(msg);

			0.01.wait;
			//lisValue = 0;
			}
		}).start;
		};
	}

	changeFunction{|item = ({|msg| msg[3].postln})|
		currentFunction = item;

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


	run{

		Task({
			1.do{
				listener.run;

				listener.synth.set(\gate, (1));

			}
		}).start;
	}

	free{
		listener.free;
		^super.free;
	}


}