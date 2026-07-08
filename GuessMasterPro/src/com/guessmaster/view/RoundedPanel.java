package com.guessmaster.view;

import javax.swing.*;
import java.awt.*;

/**
 * A panel drawn as a rounded rectangle with a soft shadow beneath it.
 * Used as the "card" that setup / game / leaderboard content sits on,
 * so the app doesn't look like raw Swing components on a flat background.
 */
public class RoundedPanel extends JPanel {

    private final int arc;
    private final Color fill;
    private final int shadowSize;

    public RoundedPanel(Color fill, int arc, int shadowSize) {
        this.fill = fill;
        this.arc = arc;
        this.shadowSize = shadowSize;
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(
                shadowSize + 20, 28, shadowSize + 24, 28));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight() - shadowSize;

        // Soft layered shadow: several translucent rounded rects offset downward.
        for (int i = shadowSize; i > 0; i--) {
            float alpha = 0.03f * (shadowSize - i + 1);
            g2.setColor(new Color(0, 0, 0, Math.min(0.35f, alpha)));
            g2.fillRoundRect(i / 2, i, w - i, h, arc, arc);
        }

        g2.setColor(fill);
        g2.fillRoundRect(0, 0, w, h, arc, arc);

        g2.dispose();
        // Deliberately no super.paintComponent(g) call: this panel fully paints
        // its own background above. Calling super here (JPanel is opaque by
        // default) would fill a solid rectangle on top of the rounded shape.
    }
}
