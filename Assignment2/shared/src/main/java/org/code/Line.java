package org.code;

import java.awt.*;

public class Line implements Shape {
    public int x1, y1, x2, y2;
    public int color;

    public Line(int x1, int y1, int x2, int y2, int color) {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
        this.color = color;
    }

    @Override
    public void draw(Graphics g) {
        g.setColor(new Color(color));
        g.drawLine(x1, y1, x2, y2);
    }

    @Override
    public boolean containsPoint(int x, int y) {
        double dist1 = Math.hypot(x - x1, y - y1);
        double dist2 = Math.hypot(x - x2, y - y2);
        double lineLen = Math.hypot(x2 - x1, y2 - y1);
        return Math.abs((dist1 + dist2) - lineLen) < 5.0;
    }
}