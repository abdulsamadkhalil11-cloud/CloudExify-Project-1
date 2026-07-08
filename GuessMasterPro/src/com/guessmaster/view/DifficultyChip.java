package com.guessmaster.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * A pill-shaped toggle button, fully custom-painted. Used in place of
 * JComboBox for difficulty selection: native combo boxes mostly ignore
 * setBackground/setForeground on Windows' L&F, which is why the old
 * dropdown looked unstyled. JToggleButton lets us disable native content
 * painting via setContentAreaFilled(false) and draw everything ourselves.
 */
public class DifficultyChip extends JToggleButton {

    private final Color accent;
    private final Color idleBg;
    private final Color idleBorder;
    private boolean hovering = false;

    public DifficultyChip(String text, Color accent, Color idleBg, Color idleBorder) {
        super(text);
        this.accent = accent;
        this.idleBg = idleBg;
        this.idleBorder = idleBorder;
        setFocusPainted(false);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFont(new Font("SansSerif", Font.BOLD, 13));
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setBorder(BorderFactory.createEmptyBorder(9, 14, 9, 14));

        addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { hovering = true; repaint(); }
            @Override public void mouseExited(MouseEvent e) { hovering = false; repaint(); }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int w = getWidth();
        int h = getHeight();

        if (isSelected()) {
            g2.setPaint(new GradientPaint(0, 0, accent, w, h, accent.brighter()));
            g2.fillRoundRect(0, 0, w, h, h, h);
            setForeground(Color.WHITE);
        } else {
            g2.setColor(hovering ? idleBg.brighter() : idleBg);
            g2.fillRoundRect(0, 0, w, h, h, h);
            g2.setColor(idleBorder);
            g2.setStroke(new BasicStroke(1.3f));
            g2.drawRoundRect(0, 0, w - 1, h - 1, h, h);
            setForeground(new Color(178, 188, 204));
        }
        g2.dispose();
        // Safe to call super here: setContentAreaFilled(false) tells the
        // button UI delegate not to fill a background, unlike a plain JPanel.
        super.paintComponent(g);
    }
}
