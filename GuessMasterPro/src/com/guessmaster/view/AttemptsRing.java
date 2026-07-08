package com.guessmaster.view;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Arc2D;

/** A ring that drains as attempts are used, with the remaining count in the center. */
public class AttemptsRing extends JPanel {

    private int used = 0;
    private int max = 10;
    private Color ringColor;
    private final Color trackColor;
    private final Color textColor;

    public AttemptsRing(Color trackColor, Color ringColor, Color textColor) {
        this.trackColor = trackColor;
        this.ringColor = ringColor;
        this.textColor = textColor;
        setOpaque(false);
        setPreferredSize(new Dimension(116, 116));
    }

    public void setProgress(int used, int max, Color ringColor) {
        this.used = used;
        this.max = Math.max(max, 1);
        this.ringColor = ringColor;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int stroke = 9;
        int size = Math.min(getWidth(), getHeight()) - stroke - 2;
        int x = (getWidth() - size) / 2;
        int y = (getHeight() - size) / 2;

        g2.setStroke(new BasicStroke(stroke, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.setColor(trackColor);
        g2.drawOval(x, y, size, size);

        double remainingFraction = (double) (max - used) / max;
        double sweep = 360.0 * remainingFraction;
        g2.setColor(ringColor);
        g2.draw(new Arc2D.Double(x, y, size, size, 90, sweep, Arc2D.OPEN));

        int remaining = Math.max(max - used, 0);
        String number = String.valueOf(remaining);
        g2.setColor(textColor);
        g2.setFont(new Font("SansSerif", Font.BOLD, 26));
        FontMetrics fm = g2.getFontMetrics();
        int tx = getWidth() / 2 - fm.stringWidth(number) / 2;
        int ty = getHeight() / 2 - 2;
        g2.drawString(number, tx, ty);

        String label = "LEFT";
        g2.setFont(new Font("SansSerif", Font.PLAIN, 10));
        FontMetrics fm2 = g2.getFontMetrics();
        int tx2 = getWidth() / 2 - fm2.stringWidth(label) / 2;
        g2.drawString(label, tx2, ty + 16);

        g2.dispose();
    }
}
