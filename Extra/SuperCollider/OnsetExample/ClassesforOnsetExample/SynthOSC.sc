SynthOSC{


	//This is the Class that makes The synth and OSC in one. The Synth Def must contrain a a replyID argument and the Synthdef name is the same as the path which the OSC is sent. The function that the Osc does can be changed and be recalled.

	var <>synth, <>osc, <>currentFunction;

	*new {arg synthDef, synthArgs, target, position, item;
		^super.new.makeSynthOSC(synthDef, synthArgs, target, position, item);

	}
	*newPaused{arg synthDef, synthArgs, target, position, item;
		^super.new.makePaused(synthDef, synthArgs, target, position, item);

	}




	makeSynthOSC{|synthDef, synthArgs, target, position, item|

		var name, server, inTarget, id;


		inTarget = target.asTarget;
		server = inTarget.server;
		//synthDef.postln;

		name = "/" ++ synthDef.asString;


		synth = Synth(synthDef, synthArgs, target, position);
		id = this.idMake;
		osc = OSCdef(id.asString, {|msg|
			{
				if(msg[2] == id, {
					currentFunction.value(msg);
				});
			}.defer(0);
		},name, server.addr);
		this.changeFunction(item);
	}

	makePaused{|synthDef, synthArgs, target, position, item|

		var name, server, inTarget, id;


		inTarget = target.asTarget;
		server = inTarget.server;
		//synthDef.postln;

		name = "/" ++ synthDef.asString;


		synth = Synth.newPaused(synthDef, synthArgs, target, position);
		id = this.idMake;

		osc = OSCdef(id.asString, {|msg|
			{
				if(msg[2] == id, {
					currentFunction.value(msg);
				});
			}.defer(0);
		},name, server.addr).disable;
		this.changeFunction(item);
	}

	idMake{
		var id;
		id = synth.nodeID;
		synth.set(\replyID, id);
		^id;
	}

	changeFunction{|item = ({|msg| msg[3].postln})|
		currentFunction = item;

	}
	run{
		osc.enable;
		synth.run(true);
	}

	free{
		synth.free;
		osc.free;
		super.free;
	}

	pause{
		synth.run(false);
		osc.disable;
	}





}



