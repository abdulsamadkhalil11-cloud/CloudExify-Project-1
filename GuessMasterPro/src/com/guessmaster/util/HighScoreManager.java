package com.guessmaster.util;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Singleton pattern: exactly one leaderboard manager for the whole app,
 * accessed via getInstance(). Persists scores to a plain-text file
 * ("highscores.dat") in the user's home directory, keyed by difficulty.
 */
public final class HighScoreManager {

    private static final int MAX_ENTRIES_PER_DIFFICULTY = 10;
    private static final Path FILE_PATH =
            Paths.get(System.getProperty("user.home"), ".guessmaster_highscores.dat");

    // Must be declared last: the constructor below calls load(), which reads
    // FILE_PATH. Static fields initialize top-to-bottom, so if INSTANCE were
    // declared first, FILE_PATH would still be null when load() runs.
    private static final HighScoreManager INSTANCE = new HighScoreManager();

    private final Map<String, List<ScoreEntry>> scoresByDifficulty = new LinkedHashMap<>();

    private HighScoreManager() {
        load();
    }

    public static HighScoreManager getInstance() {
        return INSTANCE;
    }

    public synchronized void recordScore(String playerName, String difficulty, int attemptsUsed) {
        ScoreEntry entry = new ScoreEntry(
                playerName.isBlank() ? "Player" : playerName.trim(),
                difficulty, attemptsUsed, LocalDateTime.now());

        List<ScoreEntry> list = scoresByDifficulty.computeIfAbsent(difficulty, k -> new ArrayList<>());
        list.add(entry);
        Collections.sort(list);
        while (list.size() > MAX_ENTRIES_PER_DIFFICULTY) {
            list.remove(list.size() - 1);
        }
        save();
    }

    public synchronized List<ScoreEntry> getScores(String difficulty) {
        return new ArrayList<>(scoresByDifficulty.getOrDefault(difficulty, Collections.emptyList()));
    }

    private void load() {
        if (!Files.exists(FILE_PATH)) return;
        try (BufferedReader reader = Files.newBufferedReader(FILE_PATH)) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue;
                try {
                    ScoreEntry entry = ScoreEntry.fromLine(line);
                    scoresByDifficulty.computeIfAbsent(entry.getDifficulty(), k -> new ArrayList<>()).add(entry);
                } catch (Exception ignored) {
                    // Skip malformed lines rather than crashing the app.
                }
            }
            for (List<ScoreEntry> list : scoresByDifficulty.values()) {
                Collections.sort(list);
            }
        } catch (IOException e) {
            System.err.println("Could not load high scores: " + e.getMessage());
        }
    }

    private void save() {
        try (BufferedWriter writer = Files.newBufferedWriter(FILE_PATH,
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
            for (List<ScoreEntry> list : scoresByDifficulty.values()) {
                for (ScoreEntry entry : list) {
                    writer.write(entry.toLine());
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            System.err.println("Could not save high scores: " + e.getMessage());
        }
    }
}
