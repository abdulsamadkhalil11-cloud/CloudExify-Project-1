package com.guessmaster.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/** Immutable record of a single completed, won round. */
public class ScoreEntry implements Comparable<ScoreEntry> {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final String playerName;
    private final String difficulty;
    private final int attemptsUsed;
    private final LocalDateTime timestamp;

    public ScoreEntry(String playerName, String difficulty, int attemptsUsed, LocalDateTime timestamp) {
        this.playerName = playerName;
        this.difficulty = difficulty;
        this.attemptsUsed = attemptsUsed;
        this.timestamp = timestamp;
    }

    public static ScoreEntry fromLine(String line) {
        String[] parts = line.split("\\|", 4);
        return new ScoreEntry(parts[0], parts[1], Integer.parseInt(parts[2]),
                LocalDateTime.parse(parts[3], FMT));
    }

    public String toLine() {
        return playerName + "|" + difficulty + "|" + attemptsUsed + "|" + timestamp.format(FMT);
    }

    public String getPlayerName() { return playerName; }
    public String getDifficulty() { return difficulty; }
    public int getAttemptsUsed() { return attemptsUsed; }
    public String getFormattedTimestamp() { return timestamp.format(FMT); }

    @Override
    public int compareTo(ScoreEntry o) {
        return Integer.compare(this.attemptsUsed, o.attemptsUsed);
    }
}
