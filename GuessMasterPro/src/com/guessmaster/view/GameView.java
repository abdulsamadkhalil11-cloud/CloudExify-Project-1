package com.guessmaster.view;

import com.guessmaster.controller.GameController;
import com.guessmaster.model.Difficulty;
import com.guessmaster.model.GameModel;
import com.guessmaster.model.GameObserver;
import com.guessmaster.util.HighScoreManager;
import com.guessmaster.util.ScoreEntry;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.List;

/**
 * Sole owner of Swing components. Implements GameObserver so the model
 * can push state changes to it directly (Observer pattern).
 *
 * Visual structure: a gradient GradientPanel fills the whole window.
 * A persistent header sits at the top. Below it, a CardLayout swaps
 * between three RoundedPanel "cards" (setup / game / leaderboard).
 * Difficulty selection uses custom DifficultyChip toggles (not a native
 * JComboBox, which ignores custom colors on most look-and-feels) and
 * attempts remaining are shown on a custom-painted AttemptsRing.
 */
public class GameView extends JFrame implements GameObserver {

    // ---- Palette: slate background, indigo accent ----
    private static final Color BG_TOP = new Color(13, 16, 26);
    private static final Color BG_BOTTOM = new Color(35, 24, 58);
    private static final Color CARD_BG = new Color(23, 27, 40);
    private static final Color FIELD_BG = new Color(30, 35, 51);
    private static final Color BORDER_COLOR = new Color(54, 62, 84);
    private static final Color ACCENT = new Color(99, 102, 241);
    private static final Color ACCENT_LIGHT = new Color(129, 140, 248);
    private static final Color ACCENT_GREEN = new Color(34, 197, 94);
    private static final Color ACCENT_RED = new Color(239, 68, 68);
    private static final Color TEXT_LIGHT = new Color(241, 245, 249);
    private static final Color TEXT_MUTED = new Color(148, 163, 184);

    private static final Font FONT_TITLE = new Font("SansSerif", Font.BOLD, 28);
    private static final Font FONT_HEADING = new Font("SansSerif", Font.BOLD, 20);
    private static final Font FONT_BODY = new Font("SansSerif", Font.PLAIN, 14);
    private static final Font FONT_SMALL = new Font("SansSerif", Font.PLAIN, 12);
    private static final Font FONT_GUESS = new Font("SansSerif", Font.BOLD, 26);

    private final GameModel model;
    private final GameController controller;

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel cards = new JPanel(cardLayout);

    // Setup screen widgets
    private JTextField nameField;
    private Difficulty selectedSetupDifficulty = Difficulty.EASY;
    private JLabel rulesLabel;

    // Game screen widgets
    private JLabel titleLabel;
    private JLabel rangeLabel;
    private JLabel messageLabel;
    private JTextField guessField;
    private RoundedButton guessButton;
    private RoundedButton newRoundButton;
    private AttemptsRing attemptsRing;
    private JPanel historyPanel;

    // Leaderboard screen widgets
    private Difficulty selectedLeaderboardDifficulty = Difficulty.EASY;
    private JPanel leaderboardListPanel;

    public GameView(GameModel model, GameController controller) {
        this.model = model;
        this.controller = controller;
        model.addObserver(this);

        setTitle("GuessMaster Pro");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(640, 760);
        setMinimumSize(new Dimension(560, 660));
        setLocationRelativeTo(null);
        setIconImage(buildAppIcon());

        GradientPanel root = new GradientPanel(new BorderLayout(), BG_TOP, BG_BOTTOM);
        root.add(buildHeader(), BorderLayout.NORTH);

        cards.setOpaque(false);
        cards.add(wrapInCard(buildSetupPanel()), "SETUP");
        cards.add(wrapInCard(buildGamePanel()), "GAME");
        cards.add(wrapInCard(buildLeaderboardPanel()), "LEADERBOARD");
        root.add(cards, BorderLayout.CENTER);

        setContentPane(root);
        cardLayout.show(cards, "SETUP");
    }

    /** Draws a simple rounded "G" mark, avoiding any external image asset. */
    private Image buildAppIcon() {
        BufferedImage img = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setPaint(new GradientPaint(0, 0, ACCENT, 64, 64, ACCENT_LIGHT));
        g2.fillRoundRect(0, 0, 64, 64, 18, 18);
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("SansSerif", Font.BOLD, 34));
        FontMetrics fm = g2.getFontMetrics();
        String s = "G";
        int x = (64 - fm.stringWidth(s)) / 2;
        int y = (64 - fm.getHeight()) / 2 + fm.getAscent();
        g2.drawString(s, x, y);
        g2.dispose();
        return img;
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(22, 32, 8, 32));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        left.setOpaque(false);

        JLabel badge = new JLabel("G") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setPaint(new GradientPaint(0, 0, ACCENT, getWidth(), getHeight(), ACCENT_LIGHT));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        badge.setPreferredSize(new Dimension(34, 34));
        badge.setHorizontalAlignment(SwingConstants.CENTER);
        badge.setForeground(Color.WHITE);
        badge.setFont(new Font("SansSerif", Font.BOLD, 16));

        JLabel wordmark = new JLabel("GuessMaster Pro");
        wordmark.setFont(new Font("SansSerif", Font.BOLD, 17));
        wordmark.setForeground(TEXT_LIGHT);

        left.add(badge);
        left.add(wordmark);

        JLabel tagline = new JLabel("Number guessing, done properly");
        tagline.setFont(FONT_SMALL);
        tagline.setForeground(TEXT_MUTED);
        tagline.setHorizontalAlignment(SwingConstants.RIGHT);

        header.add(left, BorderLayout.WEST);
        header.add(tagline, BorderLayout.EAST);
        return header;
    }

    private JPanel wrapInCard(JPanel content) {
        JPanel outer = new JPanel(new GridBagLayout());
        outer.setOpaque(false);
        outer.setBorder(new EmptyBorder(10, 24, 24, 24));

        RoundedPanel card = new RoundedPanel(CARD_BG, 22, 10);
        card.setLayout(new BorderLayout());
        card.add(content, BorderLayout.CENTER);
        card.setBorder(BorderFactory.createCompoundBorder(
                card.getBorder(),
                BorderFactory.createLineBorder(BORDER_COLOR, 1, true)));

        GridBagConstraints gc = new GridBagConstraints();
        gc.weightx = 1; gc.weighty = 1;
        gc.fill = GridBagConstraints.BOTH;
        outer.add(card, gc);
        return outer;
    }

    /** Builds a row of exclusive-select DifficultyChip toggles wired to onPick. */
    private JPanel buildDifficultyChipRow(Difficulty initiallySelected, java.util.function.Consumer<Difficulty> onPick) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 8));
        row.setOpaque(false);
        ButtonGroup group = new ButtonGroup();
        for (Difficulty d : Difficulty.values()) {
            DifficultyChip chip = new DifficultyChip(d.getLabel(), ACCENT, FIELD_BG, BORDER_COLOR);
            group.add(chip);
            chip.setSelected(d == initiallySelected);
            chip.addActionListener(e -> onPick.accept(d));
            row.add(chip);
        }
        return row;
    }

    // ---------------------------------------------------------------- SETUP

    private JPanel buildSetupPanel() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new GridBagLayout());
        panel.setBorder(new EmptyBorder(28, 46, 28, 46));

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(8, 0, 8, 0);

        JLabel title = new JLabel("New Game");
        title.setFont(FONT_TITLE);
        title.setForeground(TEXT_LIGHT);
        title.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel subtitle = new JLabel("Pick a difficulty and try to beat your best score.");
        subtitle.setFont(FONT_SMALL);
        subtitle.setForeground(TEXT_MUTED);
        subtitle.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel nameLbl = sectionLabel("PLAYER NAME");
        nameField = new JTextField("Player");
        styleTextField(nameField);

        JLabel diffLbl = sectionLabel("DIFFICULTY");

        RoundedPanel rulesCard = new RoundedPanel(FIELD_BG, 12, 0);
        rulesCard.setLayout(new BorderLayout());
        rulesCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(12, 14, 12, 14)));
        rulesLabel = new JLabel();
        rulesLabel.setForeground(TEXT_MUTED);
        rulesLabel.setFont(FONT_SMALL);
        rulesLabel.setHorizontalAlignment(SwingConstants.CENTER);
        rulesLabel.setText(buildDifficultySummary(selectedSetupDifficulty));
        rulesCard.add(rulesLabel, BorderLayout.CENTER);

        JPanel chipRow = buildDifficultyChipRow(selectedSetupDifficulty, d -> {
            selectedSetupDifficulty = d;
            rulesLabel.setText(buildDifficultySummary(d));
        });

        RoundedButton startButton = new RoundedButton("Start Game", ACCENT);
        startButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        startButton.addActionListener(e -> {
            controller.setPlayerName(nameField.getText());
            controller.startNewGame(selectedSetupDifficulty);
            cardLayout.show(cards, "GAME");
            guessField.requestFocusInWindow();
        });

        RoundedButton leaderboardButton = new RoundedButton("View Leaderboard", FIELD_BG);
        leaderboardButton.addActionListener(e -> {
            refreshLeaderboard();
            cardLayout.show(cards, "LEADERBOARD");
        });

        c.gridy = 0; panel.add(title, c);
        c.gridy = 1; panel.add(subtitle, c);
        c.gridy = 2; panel.add(Box.createVerticalStrut(6), c);
        c.gridy = 3; panel.add(nameLbl, c);
        c.gridy = 4; panel.add(nameField, c);
        c.gridy = 5; panel.add(diffLbl, c);
        c.gridy = 6; panel.add(chipRow, c);
        c.gridy = 7; c.insets = new Insets(6, 0, 8, 0); panel.add(rulesCard, c);
        c.insets = new Insets(8, 0, 8, 0);
        c.gridy = 8; panel.add(Box.createVerticalStrut(6), c);
        c.gridy = 9; panel.add(startButton, c);
        c.gridy = 10; panel.add(leaderboardButton, c);

        return panel;
    }

    private String buildDifficultySummary(Difficulty d) {
        return "<html><div style='text-align:center'>Range " + d.getMin() + "&ndash;" + d.getMax()
                + " &nbsp;|&nbsp; Attempts allowed: " + d.getMaxAttempts() + "</div></html>";
    }

    // ----------------------------------------------------------------- GAME

    private JPanel buildGamePanel() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BorderLayout());
        panel.setBorder(new EmptyBorder(20, 40, 20, 40));

        JPanel top = new JPanel();
        top.setOpaque(false);
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));

        titleLabel = new JLabel("Difficulty");
        titleLabel.setFont(FONT_HEADING);
        titleLabel.setForeground(TEXT_LIGHT);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        rangeLabel = new JLabel("Range");
        rangeLabel.setFont(FONT_SMALL);
        rangeLabel.setForeground(TEXT_MUTED);
        rangeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        attemptsRing = new AttemptsRing(FIELD_BG, ACCENT, TEXT_LIGHT);
        attemptsRing.setAlignmentX(Component.CENTER_ALIGNMENT);

        top.add(titleLabel);
        top.add(Box.createVerticalStrut(4));
        top.add(rangeLabel);
        top.add(Box.createVerticalStrut(12));
        top.add(attemptsRing);

        JPanel center = new JPanel();
        center.setOpaque(false);
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));

        messageLabel = new JLabel("Guess a number.");
        messageLabel.setFont(FONT_BODY);
        messageLabel.setForeground(TEXT_LIGHT);
        messageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);

        guessField = new JTextField();
        guessField.setMaximumSize(new Dimension(220, 52));
        guessField.setPreferredSize(new Dimension(220, 52));
        guessField.setHorizontalAlignment(JTextField.CENTER);
        guessField.setFont(FONT_GUESS);
        guessField.setAlignmentX(Component.CENTER_ALIGNMENT);
        styleTextField(guessField);
        guessField.addKeyListener(new KeyAdapter() {
            @Override public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) submitGuess();
            }
        });

        guessButton = new RoundedButton("Guess", ACCENT);
        guessButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        guessButton.addActionListener(e -> submitGuess());

        historyPanel = new JPanel();
        historyPanel.setOpaque(false);
        historyPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 6, 6));

        newRoundButton = new RoundedButton("Play Again", ACCENT_GREEN);
        newRoundButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        newRoundButton.setVisible(false);
        newRoundButton.addActionListener(e -> {
            controller.startNewGame(model.getDifficulty());
            guessField.requestFocusInWindow();
        });

        RoundedButton menuButton = new RoundedButton("Back to Menu", FIELD_BG);
        menuButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        menuButton.addActionListener(e -> cardLayout.show(cards, "SETUP"));

        center.add(Box.createVerticalStrut(12));
        center.add(messageLabel);
        center.add(Box.createVerticalStrut(16));
        center.add(guessField);
        center.add(Box.createVerticalStrut(12));
        center.add(guessButton);
        center.add(Box.createVerticalStrut(14));
        center.add(historyPanel);
        center.add(Box.createVerticalStrut(8));
        center.add(newRoundButton);
        center.add(Box.createVerticalStrut(14));
        center.add(menuButton);

        panel.add(top, BorderLayout.NORTH);
        panel.add(center, BorderLayout.CENTER);
        return panel;
    }

    private void submitGuess() {
        String text = guessField.getText();
        if (text.isBlank()) return;
        controller.submitGuess(text);
        guessField.selectAll();
    }

    // ------------------------------------------------------------ LEADERBOARD

    private JPanel buildLeaderboardPanel() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BorderLayout());
        panel.setBorder(new EmptyBorder(24, 40, 24, 40));

        JLabel title = new JLabel("Leaderboard");
        title.setFont(FONT_TITLE);
        title.setForeground(TEXT_LIGHT);
        title.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel chipRow = buildDifficultyChipRow(selectedLeaderboardDifficulty, d -> {
            selectedLeaderboardDifficulty = d;
            refreshLeaderboard();
        });

        JPanel north = new JPanel();
        north.setOpaque(false);
        north.setLayout(new BoxLayout(north, BoxLayout.Y_AXIS));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        chipRow.setAlignmentX(Component.CENTER_ALIGNMENT);
        north.add(title);
        north.add(Box.createVerticalStrut(14));
        north.add(chipRow);
        north.add(Box.createVerticalStrut(10));

        leaderboardListPanel = new JPanel();
        leaderboardListPanel.setOpaque(false);
        leaderboardListPanel.setLayout(new BoxLayout(leaderboardListPanel, BoxLayout.Y_AXIS));
        JScrollPane scroll = new JScrollPane(leaderboardListPanel);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        RoundedButton backButton = new RoundedButton("Back to Menu", FIELD_BG);
        backButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        backButton.addActionListener(e -> cardLayout.show(cards, "SETUP"));

        JPanel south = new JPanel();
        south.setOpaque(false);
        south.add(backButton);

        panel.add(north, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);
        panel.add(south, BorderLayout.SOUTH);
        return panel;
    }

    private void refreshLeaderboard() {
        Difficulty d = selectedLeaderboardDifficulty;
        leaderboardListPanel.removeAll();
        List<ScoreEntry> scores = HighScoreManager.getInstance().getScores(d.getLabel());

        if (scores.isEmpty()) {
            JLabel empty = new JLabel("No scores yet for " + d.getLabel() + ". Be the first.");
            empty.setForeground(TEXT_MUTED);
            empty.setFont(FONT_BODY);
            empty.setAlignmentX(Component.CENTER_ALIGNMENT);
            leaderboardListPanel.add(Box.createVerticalStrut(20));
            leaderboardListPanel.add(empty);
        } else {
            int rank = 1;
            for (ScoreEntry entry : scores) {
                leaderboardListPanel.add(buildScoreRow(rank++, entry));
                leaderboardListPanel.add(Box.createVerticalStrut(8));
            }
        }
        leaderboardListPanel.revalidate();
        leaderboardListPanel.repaint();
    }

    private JPanel buildScoreRow(int rank, ScoreEntry entry) {
        RoundedPanel row = new RoundedPanel(FIELD_BG, 12, 0);
        row.setLayout(new BorderLayout());
        row.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(4, 16, 4, 16)));
        row.setMaximumSize(new Dimension(2000, 52));
        row.setAlignmentX(Component.CENTER_ALIGNMENT);

        Color rankColor = rank == 1 ? ACCENT_LIGHT : (rank <= 3 ? ACCENT : TEXT_LIGHT);
        JLabel left = new JLabel("#" + rank + "   " + entry.getPlayerName());
        left.setForeground(rankColor);
        left.setFont(new Font("SansSerif", Font.BOLD, 14));

        JLabel right = new JLabel(entry.getAttemptsUsed() + " attempts  \u00b7  " + entry.getFormattedTimestamp());
        right.setForeground(TEXT_MUTED);
        right.setFont(FONT_SMALL);

        row.add(left, BorderLayout.WEST);
        row.add(right, BorderLayout.EAST);
        return row;
    }

    // ------------------------------------------------------------- STYLE HELPERS

    private JLabel sectionLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.BOLD, 11));
        label.setForeground(TEXT_MUTED);
        return label;
    }

    private void styleTextField(JTextField field) {
        field.setBackground(FIELD_BG);
        field.setForeground(TEXT_LIGHT);
        field.setCaretColor(ACCENT_LIGHT);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(10, 14, 10, 14)));
    }

    private void addHistoryChip(int value, boolean correct) {
        JLabel chip = new JLabel(String.valueOf(value));
        chip.setOpaque(true);
        chip.setBackground(correct ? ACCENT_GREEN : FIELD_BG);
        chip.setForeground(Color.WHITE);
        chip.setFont(new Font("SansSerif", Font.BOLD, 12));
        chip.setBorder(new EmptyBorder(6, 12, 6, 12));
        historyPanel.add(chip);
        historyPanel.revalidate();
        historyPanel.repaint();
    }

    // -------------------------------------------------------------- OBSERVER

    @Override
    public void onGameStateChanged(GameModel m) {
        Difficulty d = m.getDifficulty();
        titleLabel.setText(d.getLabel() + " Mode");
        rangeLabel.setText("Range: " + d.getMin() + " \u2013 " + d.getMax());

        List<Integer> history = m.getGuessHistory();
        historyPanel.removeAll();
        for (int i = 0; i < history.size(); i++) {
            boolean isLastAndCorrect = m.isWon() && i == history.size() - 1;
            addHistoryChip(history.get(i), isLastAndCorrect);
        }

        String rawMessage = m.getLastMessage();
        String arrow = "";
        if (rawMessage.startsWith("Too low")) arrow = "\u25B2 ";
        else if (rawMessage.startsWith("Too high")) arrow = "\u25BC ";
        messageLabel.setText(arrow + rawMessage);

        Color ringColor;
        if (m.isOver()) {
            ringColor = m.isWon() ? ACCENT_GREEN : ACCENT_RED;
            messageLabel.setForeground(ringColor);
            guessButton.setEnabled(false);
            guessField.setEnabled(false);
            newRoundButton.setVisible(true);
        } else {
            ringColor = ACCENT;
            messageLabel.setForeground(TEXT_LIGHT);
            guessButton.setEnabled(true);
            guessField.setEnabled(true);
            newRoundButton.setVisible(false);
        }
        attemptsRing.setProgress(m.getAttemptsUsed(), d.getMaxAttempts(), ringColor);
    }
}
