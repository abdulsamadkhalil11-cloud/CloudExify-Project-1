package com.guessmaster.controller;

import com.guessmaster.model.Difficulty;
import com.guessmaster.model.GameModel;
import com.guessmaster.util.HighScoreManager;

/**
 * Thin coordination layer: the View calls into this, this calls the Model,
 * and (for the high-score side effect) the Singleton HighScoreManager.
 * Keeping this logic out of the View keeps GameView focused on layout only.
 */
public class GameController {

    private final GameModel model;
    private String playerName = "Player";

    public GameController(GameModel model) {
        this.model = model;
    }

    public void setPlayerName(String name) {
        this.playerName = (name == null || name.isBlank()) ? "Player" : name.trim();
    }

    public void startNewGame(Difficulty difficulty) {
        model.startNewGame(difficulty);
    }

    /** Returns true if this guess ended the round in a win. */
    public GameModel.Result submitGuess(String rawInput) {
        int value;
        try {
            value = Integer.parseInt(rawInput.trim());
        } catch (NumberFormatException e) {
            return GameModel.Result.INVALID;
        }
        GameModel.Result result = model.guess(value);
        if (result == GameModel.Result.CORRECT) {
            HighScoreManager.getInstance().recordScore(
                    playerName, model.getDifficulty().getLabel(), model.getAttemptsUsed());
        }
        return result;
    }
}
