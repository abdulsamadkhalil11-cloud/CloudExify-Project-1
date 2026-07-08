package com.guessmaster.model;

/**
 * Observer pattern: the View implements this and registers itself
 * with the GameModel. The model calls back whenever its state changes,
 * so the View never needs to poll the model.
 */
public interface GameObserver {
    void onGameStateChanged(GameModel model);
}
