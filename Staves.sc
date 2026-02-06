// Depends on JSONlib

Staves {
    var <window, <webView, <localPath, <notesData, <>posX, <>posY, <>winWidth, <>winHeight;
    var <clef, <>timeSignature, <>keySignature, <>staffSize, <>scale, <canvasHeight;
    var <>quantization;

    *new { | timeSignature = "4/4", keySignature = "C", staffSize = "medium", quantization = nil, posX = 100, posY = 500, winWidth = 950, winHeight= 450 |
		^super.newCopyArgs(timeSignature, keySignature, staffSize, quantization, posX, posY, winWidth, winHeight).init;
    }

    init {
		notesData = [];
        localPath = PathName(this.class.filenameSymbol.asString).pathOnly;
    }

	setup { | timeSignature = "4/4", keySignature = "C", staffSize = "medium", quantization = nil, posX = 100, posY = 500, winWidth = 950, winHeight= 450 |
        var htmlString, htmlPath, vexPath;
        this.timeSignature = timeSignature;
		this.keySignature = keySignature;
		this.staffSize = staffSize;
		this.posX = posX;
		this.posY = posY;
		this.winWidth = winWidth;
		this.winHeight = winHeight;
		this.quantization = quantization;

		if(staffSize == "large") {
			scale = 1.2;
			canvasHeight = 600;
			this.winHeight = 600;
			this.winWidth = 1024
		} {
			if(staffSize == "small")
			{ scale = 0.8;
			canvasHeight = 250;
			this.winHeight = 250; }
			{ scale = 1.0;
			canvasHeight = 350;
			this.winHeight = 350; };

		};

        htmlPath = localPath ++ "Staves.html";
        vexPath = localPath ++ "vexflow/vexflow.js";

        if(File.exists(htmlPath).not) { ^"Erro: HTML não encontrado".error };

        htmlString = File.readAllString(htmlPath);

        if(File.exists(vexPath)) {
            htmlString = htmlString.replace(
                "<script src=\"vexflow/vexflow.js\"></script>",
                "<script>" ++ File.readAllString(vexPath) ++ "</script>"
            );
        };

        {
            window = Window("Score View", Rect(this.posX, this.posY, this.winWidth, this.winHeight)).front;
			webView = WebView();
			window.layout = VLayout(webView);
			window.layout.margins_(0);
			window.layout.spacing_(0);
			webView.setHtml(htmlString);

			webView.onLoadFinished = { this.updateView };
        }.defer;
    }

	// Data processing and manipulation methods

	createScore { |pbind, numEvents = 8, quant|
		var stream = pbind.asStream;
		var collected = [];
		var q = quant ? quantization;

		numEvents.do {
			var ev = stream.next(Event.default);
			if(ev.notNil) {
				var midi, dur;
				ev.use {
					midi = ~midinote.value;
					dur = ~dur.value;
				};
				if(q.notNil) {
					dur = dur.round(q);
				};
				collected = collected.add((midi: midi, dur: dur));
			};
		};

		notesData = collected;

		{ this.updateView }.defer(0.1);
	}

    updateView {
		var json;
        if(webView.notNil) {
            {

				json = notesData.asJSON;
				// Post << json; // for Debug
                webView.runJavaScript(
					"window.drawMusic('%', '%', '%', '%', %, %, %);".format("treble", "bass",  timeSignature, keySignature, json, scale, canvasHeight)
                );
            }.defer(0.1);
        };
    }
}