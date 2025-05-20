package org.code;

import java.awt.*;

public class Line implements Shape {
    public int x1, y1, x2, y2;
    public int color;
    public int size;
    private final String type = "Line";
    private static final long serialVersionUID = 1L;

    public Line(int x1, int y1, int x2, int y2, int color, int size) {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
        this.color = color;
        this.size = size;

    }

    @Override
    public void draw(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(new Color(color));
        g2d.setStroke(new BasicStroke(size));
        g2d.drawLine(x1, y1, x2, y2);
        g2d.setStroke(new BasicStroke());
    }

    @Override
    public boolean containsPoint(int x, int y) {
        double dist1 = Math.hypot(x - x1, y - y1);
        double dist2 = Math.hypot(x - x2, y - y2);
        double lineLen = Math.hypot(x2 - x1, y2 - y1);
        return Math.abs((dist1 + dist2) - lineLen) < size;
    }

}