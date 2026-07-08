package com.guessmaster.model;

/**
 * Defines the difficulty levels available in the game.
 * Each level fixes the numeric range and the number of attempts allowed.
 * This is a simple application of the Strategy pattern: the controller
 * asks the active Difficulty for its rules instead of branching on if/else.
 */
public enum Difficulty {
    EASY("Easy", 1, 50, 10),
    MEDIUM("Medium", 1, 100, 8),
    HARD("Hard", 1, 250, 8),
    EXPERT("Expert", 1, 1000, 7),
    INSANE("Insane", 1, 5000, 8);

    private final String label;
    private final int min;
    private final int max;
    private final int maxAttempts;

    Difficulty(String label, int min, int max, int maxAttempts) {
        this.label = label;
        this.min = min;
        this.max = max;
        this.maxAttempts = maxAttempts;
    }

    public String getLabel() { return label; }
    public int getMin() { return min; }
    public int getMax() { return max; }
    public int getMaxAttempts() { return maxAttempts; }

    @Override
    public String toString() { return label; }
}
