package com.guessmaster.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Holds all game state. Knows nothing about Swing.
 * Notifies registered GameObservers whenever something worth
 * redrawing happens (Observer pattern).
 */
public class GameModel {

    public enum Result { TOO_LOW, TOO_HIGH, CORRECT, INVALID, GAME_OVER, ALREADY_OVER }

    private final List<GameObserver> observers = new ArrayList<>();
    private final Random random = new Random();

    private Difficulty difficulty;
    private int target;
    private int attemptsUsed;
    private boolean over;
    private boolean won;
    private String lastMessage = "";
    private final List<Integer> guessHistory = new ArrayList<>();

    public void addObserver(GameObserver o) { observers.add(o); }

    private void notifyObservers() {
        for (GameObserver o : observers) o.onGameStateChanged(this);
    }

    public void startNewGame(Difficulty difficulty) {
        this.difficulty = difficulty;
        this.target = random.nextInt(difficulty.getMax() - difficulty.getMin() + 1) + difficulty.getMin();
        this.attemptsUsed = 0;
        this.over = false;
        this.won = false;
        this.lastMessage = "Guess a number between " + difficulty.getMin() + " and " + difficulty.getMax() + ".";
        this.guessHistory.clear();
        notifyObservers();
    }

    public Result guess(int value) {
        if (over) {
            lastMessage = "Round already finished. Start a new game.";
            notifyObservers();
            return Result.ALREADY_OVER;
        }
        if (value < difficulty.getMin() || value > difficulty.getMax()) {
            lastMessage = "Enter a number between " + difficulty.getMin() + " and " + difficulty.getMax() + ".";
            notifyObservers();
            return Result.INVALID;
        }

        attemptsUsed++;
        guessHistory.add(value);

        if (value == target) {
            over = true;
            won = true;
            lastMessage = "Correct! The number was " + target + ".";
            notifyObservers();
            return Result.CORRECT;
        }

        Result result = value < target ? Result.TOO_LOW : Result.TOO_HIGH;

        if (attemptsUsed >= difficulty.getMaxAttempts()) {
            over = true;
            won = false;
            lastMessage = "Out of attempts. The number was " + target + ".";
            notifyObservers();
            return Result.GAME_OVER;
        }

        lastMessage = (result == Result.TOO_LOW ? "Too low." : "Too high.")
                + " Try again (" + (difficulty.getMaxAttempts() - attemptsUsed) + " left).";
        notifyObservers();
        return result;
    }

    public Difficulty getDifficulty() { return difficulty; }
    public int getAttemptsUsed() { return attemptsUsed; }
    public int getAttemptsRemaining() { return difficulty == null ? 0 : difficulty.getMaxAttempts() - attemptsUsed; }
    public boolean isOver() { return over; }
    public boolean isWon() { return won; }
    public int getTarget() { return target; }
    public String getLastMessage() { return lastMessage; }
    public List<Integer> getGuessHistory() { return guessHistory; }
}
