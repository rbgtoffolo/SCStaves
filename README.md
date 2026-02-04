# 🎼 SC-Staves

This project aims to create a score viewer for Supercollider using the vexflow library and Supercollider's helpBrowser. The class converts data from a Pbind into musical notation.

As I am not an experienced JS programmer, I utilized AI assistance to develop features and comprehend vexflow functionality.

The project is currently in its early stages of  development and is open to suggestions, feedback, and potential collaborations.

## Dependencies

To make the Staves class work, two dependencies are required: JSONlib and vexflow.js.

### JSONlib

Just run **Quarks.install("JSONlib");** in Supercollider.

### Vexflow


1. Download Vexflow at: https://github.com/0xfe/vexflow/releases
2. Locate **vexflow.js** file in the build/cjs folder.
3. Copy vexflow.js to the Extensions/Staves folder.


## 🚀 Implemented Features

* 💉 **Dynamic Code Injection**: The class reads the local `vexflow.js` file and injects it directly into the HTML to bypass local file security restrictions (CORS).
* 🎨 **SVG Rendering**: Uses VexFlow's SVG backend to ensure maximum visual quality and resizing without loss of resolution.
* 🎼 **Clef Support**: Implementation of Treble (`treble`), Bass (`bass`) clefs, and native support for C Clefs (Alto, Tenor, etc.).
* ⏱️ **Time Signatures**: Support for dynamic time signature definitions (e.g., "4/4", "7/8", "3/2").
* 🔄 **Asynchronous Synchronization**: Management of VexFlow 5 promises to ensure fonts (`Bravura`/`Academico`) are loaded before drawing.
* 🎹 **Automatic Grand Staff**: Intelligent distribution of notes between Treble and Bass staves based on pitch (split point at Middle C).
* 📏 **Dynamic Scaling**: Notation size control (`small`, `medium`, `large`) configurable via SuperCollider.
* ✨ **Microtonality**: Native support for quarter tones (MIDI `x.5`) with dedicated accidentals (Stein-Zimmermann).
* 3️⃣ **Tuplets**: Automatic detection of tuplets (triplets, quintuplets, etc.) with ratio calculation and intelligent bracket formatting.
* 📐 **Quantization**: Built-in rhythmic quantization to align durations to a grid (e.g., 0.25), useful for cleaning up algorithmic output.

## 📁 File Structure

For correct operation, files must be in the same folder:
- 📄 `Staves.sc`: Class definition and bridge logic.
- 🌐 `Staves.html`: Template with the `window.drawMusic` function.
- ⚙️ `vexflow.js`: The VexFlow library.

## 🛠️ How to use

```supercollider
// Initialize the class
a = Staves.new;

// 1. Configure the view
// Arguments: timeSig, keySig, size ("small", "medium", "large")
a.setup("3/4", "C", "small");

// 2. Generate score from a Pbind
// The system automatically converts durations and pitches (including microtones)
p = Pbind(
    \degree, Pseq((0..5), 1),
    \dur, Pseq([
        0.5/5, 0.5/5, 0.5/5, 0.5/5, 0.5/5, // Quintuplet
        0.25, 0.25,                        // Sixteenth notes
        0.5                                // Eighth note
    ], 1)
);

// Renders 8 events from the Pbind, optionally quantizing to 16th notes (0.25)
a.createScore(p, 8, 0.25);
```
