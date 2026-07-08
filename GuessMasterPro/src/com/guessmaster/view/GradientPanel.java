package com.guessmaster.view;

import javax.swing.*;
import java.awt.*;

/** Diagonal gradient background so the window isn't a flat solid color. */
public class GradientPanel extends JPanel {

    private final Color top;
    private final Color bottom;

    public GradientPanel(LayoutManager layout, Color top, Color bottom) {
        super(layout);
        this.top = top;
        this.bottom = bottom;
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        GradientPaint gp = new GradientPaint(0, 0, top, getWidth(), getHeight(), bottom);
        g2.setPaint(gp);
        g2.fillRect(0, 0, getWidth(), getHeight());
        g2.dispose();
        // No super.paintComponent(g): JPanel is opaque by default, so calling
        // super here would fill the whole area with the default background
        // color (white) immediately after the gradient — erasing it. This
        // was the actual bug: the gradient was being drawn, then instantly
        // painted over white on every repaint.
    }
}
