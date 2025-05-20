package org.code;

import java.awt.*;

public class Rectangle implements Shape {
    private int x, y, width, height;
    private int color;
    private final String type = "Rectangle";
    private static final long serialVersionUID = 1L;

    public Rectangle(int startX, int startY, int endX, int endY, int color) {
        this.x = Math.min(startX, endX);
        this.y = Math.min(startY, endY);
        this.width = Math.abs(endX - startX);
        this.height = Math.abs(endY - startY);
        this.color = color;
    }

    @Override
    public void draw(Graphics g) {
        g.setColor(new Color(color));
        g.fillRect(x, y, width, height);
    }

    @Override
    public boolean containsPoint(int px, int py) {
        return px >= x && px <= x + width && py >= y && py <= y + height;
    }

}
