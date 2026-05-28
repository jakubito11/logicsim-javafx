# LogicSim

LogicSim is a desktop JavaFX application. It was an attempt at rewriting an older project https://github.com/jakubito11/logicsim that was written as a web appliaction written in JavaScript. Written during a Java course at FEL CTU.

## What can you do?

- add logical gates, outputs, inputs and digital chips to editor
- connect nodes with wires
- run, stop and reset a simulation of the drawn circuit
- save and load a circuit in `.json` format

Components can be simulated

- **Logic Gates:** AND, OR, NOT, NAND, NOR, XOR, XNOR
- **Inputs:** Logical 1, Logical 0, Clock Generator
- **Outputs:** Logical Output, Light Bulb
- **Digital Chips:** JK/D/T Flip-Flop, Counter, Ring Counter, Multiplexor, Demultiplexor, 7 Segment Decoder, 7 Segment Display, Full Adder, Half Adder, Latch

## Requirements

- JDK 11 or newer
- Maven
- JavaFX

## Installation

### 1) Launching from the source code of the project

```bash
git clone https://github.com/jakubito11/logicsim_javafx.git
cd sausajak
mvn clean javafx:run
```

## Shortcuts

Najčastejšie skratky:
- `Ctrl/Cmd + N` new circuit
- `Ctrl/Cmd + O` open a new circuit from folder
- `Ctrl/Cmd + S` save to JSON
- `Delete` delete selected component
- `Ctrl/Cmd + =` zoom in
- `Ctrl/Cmd + Space` zoom out
- `Ctrl/Cmd + M` go back to select/drag mode
- `Esc` stop the current action
- `F11` fullscreen mode
- `Ctrl/Cmd + Q` exit the app

Mouse:
- click on a component in the top menu and add it to the editor
- click on a node A and the node B to create a connection (output -> input)
- zoom in and out with mousewheel
- in the **Move** drag the editor to change the position

## Author

Jakub Šauša
