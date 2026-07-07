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
- 5 difficulty levels (Easy → Insane)
- Persistent leaderboard (top 10 per difficulty), saved to `~/.guessmaster_highscores.dat`
- Guess history chips, progress bar, live feedback
- Dark, flat, professional UI (custom rounded buttons, no default Swing chrome)

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
│   └── RoundedButton.java
├── controller/
│   └── GameController.java
└── util/
    ├── HighScoreManager.java
    └── ScoreEntry.java
```
