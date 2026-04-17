Staves {
	var <window, <webView, <notesData, <type, <clef, <timeSignature;
	var <localPath;

	*new {
		^super.new.init;
	}

	init {
		notesData = [];
		localPath = PathName(this.class.filenameSymbol.asString).pathOnly;
	}

	setup { |argType = \single, argClef = "treble", argTime = "4/4"|
		var htmlString, htmlPath, vexPath;
		type = argType;
		clef = argClef;
		timeSignature = argTime; // Armazena a fórmula (ex: "3/4", "7/8")

		htmlPath = localPath ++ "Staves.html";
		vexPath = localPath ++ "vexflow.js";

		if(File.exists(htmlPath).not) { ^"Erro: staves.html não encontrado".error };

		htmlString = File.readAllString(htmlPath);

		if(File.exists(vexPath)) {
			var vexJs = File.readAllString(vexPath);
			htmlString = htmlString.replace("<script src=\"vexflow.js\"></script>", "<script>" ++ vexJs ++ "</script>");
		};

		{
			window = Window("Staves Renderer", Rect(100, 100, 850, 500)).front;
			webView = WebView(window, window.view.bounds);
			webView.setHtml(htmlString);

			webView.onLoadFinished = { this.updateView };
		}.defer;
	}
	// Método para atualizar a vista usando runJavaScript (API moderna)

	updateView {
		var json;

		if(webView.notNil) {
			json = "[" ++ notesData.collect { |item|
				var midiVal = if(item[\midi].isSequenceableCollection) {
					"[" ++ item[\midi].collect(_.asFloat).join(",") ++ "]"
				} {
					item[\midi].asFloat.asString
				};
				"{ \"midi\": %, \"dur\": %, \"hasTie\": % }"
				.format(midiVal, item[\dur].asFloat, item[\hasTie] ?? false)
			}.join(",") ++ "]";

			{
				var jsCommand =
				"if(window.drawMusic) { window.drawMusic(% , '%', '%', '%'); }"
				.format(json, type, clef, timeSignature);

				webView.runJavaScript(jsCommand);
			}.defer(0.1);
		};
	}

	addNotes { |array|
		notesData = array.collect { |item|
			if(item.isArray and: { item.size == 2 }) {
				(midi: item[0], dur: item[1].asFloat)
			} {
				if(item.isArray) {
					(midi: item, dur: 1)
				} {
					(midi: item.asFloat, dur: 1)
				}
			}
		};

		{ this.updateView }.defer(0.05);
	}
	normalizeNotesForTimeSig { |notes|
		var beatsPerMeasure, beatValue, pulseUnit;
		var acc = 0.0;
		var normalized = List.new;

		beatsPerMeasure = timeSignature.split($/)[0].asInteger;
		beatValue = timeSignature.split($/)[1].asInteger;
		pulseUnit = 4.0 / beatValue;

		notes.do { |n|
			var remaining = n[\dur].asFloat;

			while(
				{ remaining > 0.0001 },
				{
					var space = (beatsPerMeasure * pulseUnit) - acc;
					var dur = remaining.min(space);

					normalized.add((
						midi: n[\midi],
						dur: dur,
						hasTie: (remaining > dur)
					));

					remaining = remaining - dur;
					acc = acc + dur;

					if(acc >= ((beatsPerMeasure * pulseUnit) - 0.0001)) {
						acc = 0.0;
					};
				}
			);
		};

		^normalized
	}

renderPbind { |pbind, numEvents = 8|
	var stream = pbind.asStream;
	var collected = [];

	numEvents.do {
		var ev = stream.next(Event.default);
		if(ev.notNil) {
			var midi, dur;
			ev.use {
				midi = ~midinote.value;
				dur = ~dur.value;
			};
			collected = collected.add((midi: midi.round, dur: dur));
		};
	};

	notesData = collected;
	{ this.updateView }.defer(0.1);
}
}