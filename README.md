# Physics Simulator

A 2D physics simulator written in Java. Groups of bodies move according to different force laws (Newton's universal gravitation, motion towards a fixed point, or no force). It runs in **batch mode** (command line, JSON output) and in **GUI mode** (a Swing interface with live tables and a graphical viewer of the simulation).

Developed as a project for the course *Tecnología de la Programación II* (Programming Technology II) at Universidad Complutense de Madrid.

## Features

- **Model**: moving bodies (`mv_body`) and stationary bodies (`st_body`), organised in groups. Each group has its own force law.
- **Force laws**:
  - `nlug`: Newton's universal gravitation (parameter `G`).
  - `mtfp`: motion towards a fixed point (parameters `c` and `g`).
  - `nf`: no force.
- **Builder-based factories**: bodies and force laws are created from JSON (*Factory* + *Builder* patterns).
- **MVC architecture** using the *Observer* pattern: views register as observers of the simulator.
- **GUI** (Swing):
  - load input files
  - change the force law of each group
  - run and stop the simulation
  - groups and bodies tables
  - status bar
  - graphical viewer with zoom and panning
- **Unit tests** (JUnit 5) for the model and the factories.

## Project structure

```
src/simulator/
├── control/     Controller (loads JSON input, runs the simulation)
├── factories/   Builder, BuilderBasedFactory and the concrete builders
├── launcher/    Main (argument parsing, batch/gui modes)
├── misc/        Vector2D
├── model/       Body, MovingBody, StationaryBody, BodiesGroup, PhysicsSimulator, force laws
└── view/        MainWindow, ControlPanel, ForceLawsDialog, tables, StatusBar, Viewer
tests/           JUnit tests for the model and the factories
lib/             commons-cli-1.4.jar, json.jar
resources/
├── examples/    Sample inputs and expected outputs
├── icons/       GUI icons
├── uml/         UML diagrams
└── viewer/      viewer.html: web visualiser for the JSON output of batch mode
```

## Requirements

- Java 11 or later.

## Build and run

> Always run from the project root folder. The GUI loads its icons using relative paths (`resources/icons/...`).

### From the command line

Linux / macOS:

```bash
javac -d bin -cp "lib/*" $(find src -name "*.java")

# GUI mode (default)
java -cp "bin:lib/*" simulator.launcher.Main -i resources/examples/input/ex4.json

# Batch mode
java -cp "bin:lib/*" simulator.launcher.Main -m batch -i resources/examples/input/ex1.json \
     -o out.json -s 1000 -dt 1000 -fl nlug
```

Windows (PowerShell):

```powershell
javac -d bin -cp "lib/*" (Get-ChildItem -Recurse src -Filter *.java).FullName
java -cp "bin;lib/*" simulator.launcher.Main -i resources/examples/input/ex4.json
```

### From Eclipse

Go to `File → Import → Existing Projects into Workspace` and select the project folder. The `.classpath` already includes the libraries in `lib/` and JUnit 5. Run `simulator.launcher.Main` with the arguments you need.

## Command-line options

| Option | Description | Default |
|---|---|---|
| `-m, --mode <gui\|batch>` | Execution mode | `gui` |
| `-i, --input <file>` | JSON input file (required in batch mode) | — |
| `-o, --output <file>` | Output file (batch mode only) | standard output |
| `-s, --steps <n>` | Number of simulation steps (batch mode only) | `150` |
| `-dt, --delta-time <d>` | Real time, in seconds, per simulation step | `2500.0` |
| `-fl, --force-laws <type[:json]>` | Default force law, e.g. `nlug`, `mtfp:{c:[0,0],g:9.8}` | `nlug` |
| `-h, --help` | Print help | — |

## Input format

```json
{
  "groups": ["g1", "g2"],
  "laws": [ { "id": "g1", "laws": { "type": "mtfp", "data": {} } } ],
  "bodies": [
    { "type": "mv_body", "data": { "id": "a1", "gid": "g1", "p": [0.0, 0.0], "v": [500.0, 0.0], "m": 5.97e24 } },
    { "type": "st_body", "data": { "id": "c1", "gid": "g2", "p": [1.0e11, 0.0], "m": 1.989e30 } }
  ]
}
```

`laws` is optional. Groups without an entry use the force law given with `-fl`.

## Graphical viewer (GUI)

Hover the mouse over the viewer and use these keys:

| Key | Action |
|---|---|
| `h` | Show / hide help |
| `v` | Show / hide velocity and force vectors |
| `+` / `-` | Zoom in / out |
| `=` | Rescale so that all bodies are visible |
| `j` `l` `i` `m` | Move left / right / up / down |
| `k` | Reset to the origin |
| `g` | Show the next group (after the last one, all groups are shown) |

## Tests

The tests in `tests/` use JUnit 5. In Eclipse, right-click the `tests` folder and choose *Run As → JUnit Test*.

The files in `resources/examples/expected_output` were generated with the commands listed in that folder's `README.md`. Batch mode reproduces them exactly.
