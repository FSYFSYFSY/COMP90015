package org.code;

import java.awt.*;

public class Triangle implements Shape {
    private int x1, y1, x2, y2, x3, y3;
    private int color;
    private final String type = "Triangle";
    private static final long serialVersionUID = 1L;

    public Triangle(int x1, int y1, int x2, int y2, int color) {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
        this.color = color;

        double dx = x2 - x1;
        double dy = y2 - y1;
        double d = Math.sqrt(dx * dx + dy * dy);

        double h = (Math.sqrt(3) / 2) * d;

        double mx = (x1 + x2) / 2.0;
        double my = (y1 + y2) / 2.0;

        double ux = -dy / d;
        double uy = dx / d;

        this.x3 = (int) Math.round(mx + ux * h);
        this.y3 = (int) Math.round(my + uy * h);
    }

    @Override
    public void draw(Graphics g) {
        g.setColor(new Color(color));
        int[] xs = {x1, x2, x3};
        int[] ys = {y1, y2, y3};
        g.fillPolygon(xs, ys, 3);
    }

    @Override
    public boolean containsPoint(int px, int py) {
        double denominator = ((y2 - y3)*(x1 - x3) + (x3 - x2)*(y1 - y3));
        double a = ((y2 - y3)*(px - x3) + (x3 - x2)*(py - y3)) / denominator;
        double b = ((y3 - y1)*(px - x3) + (x1 - x3)*(py - y3)) / denominator;
        double c = 1 - a - b;

        return 0 <= a && a <= 1 &&
                0 <= b && b <= 1 &&
                0 <= c && c <= 1;
    }
}