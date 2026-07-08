package com.guessmaster;

import com.guessmaster.controller.GameController;
import com.guessmaster.model.GameModel;
import com.guessmaster.view.GameView;

import javax.swing.*;

/**
 * GuessMaster Pro
 * A professional Java Swing number-guessing game.
 *
 * Architecture: MVC
 *   model      -> GameModel, Difficulty, GameObserver
 *   view       -> GameView, RoundedButton
 *   controller -> GameController
 *   util       -> HighScoreManager (Singleton), ScoreEntry
 *
 * Design patterns used:
 *   - MVC              : separates game rules, UI, and coordination
 *   - Observer          : GameModel notifies GameView of state changes
 *   - Singleton         : HighScoreManager has one instance app-wide
 *   - Strategy (light)  : Difficulty enum supplies range/attempt rules
 */
public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
                // Fall back to default look and feel silently.
            }
            GameModel model = new GameModel();
            GameController controller = new GameController(model);
            GameView view = new GameView(model, controller);
            view.setVisible(true);
        });
    }
}
