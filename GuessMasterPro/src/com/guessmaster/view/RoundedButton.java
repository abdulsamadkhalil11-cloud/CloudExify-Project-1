package com.guessmaster.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/** A flat, rounded-corner button with hover/press shading, used throughout the UI. */
public class RoundedButton extends JButton {

    private Color base;
    private final Color hover;
    private final Color pressedColor;
    private boolean hovering = false;
    private boolean pressing = false;

    public RoundedButton(String text, Color base) {
        super(text);
        this.base = base;
        this.hover = base.brighter();
        this.pressedColor = base.darker();
        setForeground(Color.WHITE);
        setFont(new Font("SansSerif", Font.BOLD, 14));
        setFocusPainted(false);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setBorder(BorderFactory.createEmptyBorder(10, 22, 10, 22));

        addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { hovering = true; repaint(); }
            @Override public void mouseExited(MouseEvent e) { hovering = false; repaint(); }
            @Override public void mousePressed(MouseEvent e) { pressing = true; repaint(); }
            @Override public void mouseReleased(MouseEvent e) { pressing = false; repaint(); }
        });
    }

    public void setBaseColor(Color color) {
        this.base = color;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Color fill = pressing ? pressedColor : (hovering ? hover : base);
        if (!isEnabled()) fill = Color.GRAY;
        g2.setColor(fill);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
        g2.dispose();
        super.paintComponent(g);
    }
}
