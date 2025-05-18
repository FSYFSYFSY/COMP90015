package org.code;

import java.awt.*;
import java.util.List;

public class FreeDraw implements Shape {
    public List<Point> points;
    public int color;

    public FreeDraw(List<Point> points, int color) {
        this.points = points;
        this.color = color;
    }

    @Override
    public void draw(Graphics g) {
        g.setColor(new Color(color));
        for (int i = 1; i < points.size(); i++) {
            Point p1 = points.get(i - 1);
            Point p2 = points.get(i);
            g.drawLine(p1.x, p1.y, p2.x, p2.y);
        }
    }

    @Override
    public boolean containsPoint(int x, int y) {
        final int TOLERANCE = 5;

        for (Point p : points) {
            int dx = p.x - x;
            int dy = p.y - y;
            if (dx * dx + dy * dy <= TOLERANCE * TOLERANCE) {
                return true;
            }
        }
        return false;
    }
}