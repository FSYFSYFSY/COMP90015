package org.code;

import java.awt.*;

public class Oval implements Shape {
    private int x, y, width, height;
    private int color;
    private final String type = "Oval";
    private static final long serialVersionUID = 1L;

    public Oval(int startX, int startY, int endX, int endY, int color) {
        this.x = Math.min(startX, endX);
        this.y = Math.min(startY, endY);
        this.width = Math.abs(endX - startX);
        this.height = Math.abs(endY - startY);
        this.color = color;
    }

    @Override
    public void draw(Graphics g) {
        g.setColor(new Color(color));
        g.fillOval(x, y, width, height);
    }

    @Override
    public boolean containsPoint(int px, int py) {
        double cx = x + width / 2.0;
        double cy = y + height / 2.0;
        double a = width / 2.0;
        double b = height / 2.0;

        double dx = px - cx;
        double dy = py - cy;

        return (dx * dx) / (a * a) + (dy * dy) / (b * b) <= 1.0;
    }
}
