package org.code;

import java.awt.*;
import java.util.List;

public class FreeDraw implements Shape {
    public List<Point> points;
    public int color;
    public int size;
    private final String type = "FreeDraw";
    private static final long serialVersionUID = 1L;

    public FreeDraw(List<Point> points, int color, int size) {
        this.points = points;
        this.color = color;
        this.size = size;
    }

    @Override
    public void draw(Graphics g) {
        if (points.size() < 2) return;

        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(new Color(color));
        g2d.setStroke(new BasicStroke(size, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        for (int i = 1; i < points.size(); i++) {
            Point p1 = points.get(i - 1);
            Point p2 = points.get(i);
            g2d.drawLine(p1.x, p1.y, p2.x, p2.y);
        }

        // 可选：重置 stroke（避免影响其他图形）
        g2d.setStroke(new BasicStroke());
    }

    @Override
    public boolean containsPoint(int x, int y) {
        for (Point p : points) {
            int dx = p.x - x;
            int dy = p.y - y;
            if (dx * dx + dy * dy <= size * size) {
                return true;
            }
        }
        return false;
    }
}