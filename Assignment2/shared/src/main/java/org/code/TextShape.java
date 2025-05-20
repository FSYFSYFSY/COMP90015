package org.code;

import java.awt.*;

public class TextShape implements Shape {
    private int x, y;
    private String text;
    private int color;
    private final String type = "TextShape";
    private static final long serialVersionUID = 1L;

    public TextShape(int x, int y, String text, int color) {
        this.x = x;
        this.y = y;
        this.text = text;
        this.color = color;
    }

    @Override
    public void draw(Graphics g) {
        g.setColor(new Color(color));
        g.setFont(new Font("Arial", Font.PLAIN, 16)); // 你也可以改字体或大小
        g.drawString(text, x, y);
    }

    @Override
    public boolean containsPoint(int px, int py) {
        return false;
    }
}
