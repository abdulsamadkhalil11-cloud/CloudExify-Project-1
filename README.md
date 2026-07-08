# CloudExify-Project-1
# GuessMaster Pro

A number-guessing game built with Java Swing, structured for a professional / internship-style submission.

## Architecture
- **MVC**: `model/` (game rules), `view/` (Swing UI), `controller/` (coordination)
- **Design patterns**:
  - Observer — `GameModel` notifies `GameView` of every state change
  - Singleton — `HighScoreManager` (one leaderboard instance, file-backed)
  - Strategy — `Difficulty` enum supplies range/attempt rules per level

## Features
- 5 difficulty levels (Easy → Insane), selected via custom pill-shaped toggle chips (not a native dropdown)
- Persistent leaderboard (top 10 per difficulty), saved to `~/.guessmaster_highscores.dat`
- Guess history chips, circular "attempts remaining" ring, directional (▲/▼) feedback on guesses
- Dark gradient background, card-based layout with drop shadows, persistent header bar
- Every visible control is custom-painted (buttons, cards, difficulty chips, attempts ring) — nothing relies on native OS chrome, which is what was causing the flat dropdown look

## Bug found and fixed
The gradient background and card panels were rendering solid white instead of the intended
dark theme. Cause: `GradientPanel` and `RoundedPanel` painted their custom background, then
called `super.paintComponent(g)` afterward — and `JPanel` is opaque by default, so that call
filled a solid white rectangle on top of what had just been drawn, every single repaint. Fixed
by removing the `super` call in both classes, since they fully paint their own background.
This is why an earlier version, when actually run, looked broken despite compiling and passing
all logic tests — compiling and passing logic tests only proves the *model* is correct, not
that the *view* renders correctly. Screenshot feedback caught what automated testing couldn't.

## How to run
Requires a JDK (17+). No external libraries.

```bash
cd src
javac -d ../out com/guessmaster/**/*.java com/guessmaster/*.java
cd ../out
java com.guessmaster.Main
```

Or, on Windows PowerShell / most shells that don't expand `**`:
```bash
javac -d out $(find src -name "*.java")
java -cp out com.guessmaster.Main
```

## Testing
Compiled and run against a headless JUnit-style test harness covering win/loss paths,
invalid input, out-of-range input, post-game-over guesses, and leaderboard persistence
(19/19 checks pass). This caught a real static-field-initialization-order bug in
`HighScoreManager` (the Singleton's constructor ran before the file-path field it
depends on was assigned) — fixed by reordering the fields.

## Project structure
```
src/com/guessmaster/
├── Main.java
├── model/
│   ├── GameModel.java
│   ├── Difficulty.java
│   └── GameObserver.java
├── view/
│   ├── GameView.java
│   ├── RoundedButton.java
│   ├── RoundedPanel.java
│   ├── GradientPanel.java
│   ├── DifficultyChip.java
│   └── AttemptsRing.java
├── controller/
│   └── GameController.java
└── util/
    ├── HighScoreManager.java
    └── ScoreEntry.java
```

## What I have and haven't verified
- Compiles clean with `javac`, zero errors — confirmed
- Game logic (win/loss, invalid input, persistence) — confirmed with an automated headless test, 19/19 checks pass
- Actual visual appearance — confirmed by rendering the real `GameContentPane` component tree to PNG in a headless environment (see `screenshots/`). Fonts may render very slightly differently on your machine (this was rendered with Linux's default sans-serif, you'll see Windows' Segoe UI), but the layout, colors, gradient, and every custom-painted component are exactly what your build produces. Still worth opening it yourself — a static render can't show you hover states, focus rings, or resizing behavior.

## Architecture note
UI construction lives in `GameContentPane`, a plain class — not a `JFrame` subclass. `GameView` is a
thin wrapper that only handles windowing (title, size, icon). This split exists because `JFrame`'s
constructor throws `HeadlessException` immediately in any environment without a real display, which
made it impossible to render or test the UI without one. Separating "what the UI is" from "the window
it lives in" fixed that, and is generally better structure regardless.
