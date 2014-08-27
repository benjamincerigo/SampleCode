CutBuffer1{


	var <bufferArray, <curPath;

	var <playing, <group, volSynth, <>volBus;
	var >topAmp, >fadeTime, <>loopNum, doneAc;
	var <paused;


	*new{|folderPath, groupTarget, add='addAfter', loopNum|



		^super.new.makeFull(folderPath, groupTarget, add, loopNum);
	}

	makeFull{|folderPath, groupTarget, add, mloopNum|
		bufferArray = folderPath.pathMatch collect: Buffer.read(Server.default,_);
		playing = List.new(bufferArray.size);
		group = Group.new(groupTarget, add);
		volBus = Bus.control(Server.default, 1);
		volBus.set(1.0);
		topAmp = 1;
		fadeTime = 0.01;
		paused = false;
		loopNum = mloopNum?0;





		}

	playBuffer{|playNum, stopNum|
		//Play a number with the number in the Array, Stop a buffer in the array oldest is first -1 will stop all.

		if(stopNum == (-1), {
			playing do: _.set(\gate, 0);
			},{
				stopNum do: _.set(\gate, 0);
		});

		if(playNum!= (-1), {
		playing.add(Synth(\cut_Buffer, [bufnum:bufferArray.at(playNum), volume: volBus, loop: loopNum], group, \addToTail));
		});
	}


	//VolSet
	vol{|env|
		var bool1, bool2;

		if((volSynth.isNil == false)&&(try{volSynth.isRunning};), {
				volSynth.free;

			});
		if(env.isKindOf(Env), {

		volSynth =Synth(\env, [i_outbus: volBus, env: env], group, \addToHead);
			NodeWatcher.register(volSynth, true);
		}, {

			volBus.set(env);
		});
	}

	//Vol Switch

	volSwitch{|on = false|

		var env, topAmpD;


		if(on, {
			env = Env([topAmp,0], [fadeTime], 4);
			}, {

				env = Env([0,topAmp], [fadeTime], -4);
		});


		volSynth =Synth(\env, [i_outbus: volBus, env: env], group, \addToHead);

	}



	//Pause
	pause{|num|

		var onOff;
		if(paused, {

			this.volSwitch(false);
			onOff = 1;
			paused = false;

			}, {
			this.volSwitch(true);
			onOff = 0;
				paused = true;

		});


		if(num!= (-1), {

			playing do: _.set(\speed, onOff);
		}, {
				playing[num].set(\speed, onOff);
		});

		^paused;



	}

	freeAll{
		playing do: _.set(\gate, 0);
		bufferArray do: _.free;
		group.free;
	}

	freeSynths{
		playing do: _.set(\gate, 0);
		group.freeAllMsg;


	}



}






		