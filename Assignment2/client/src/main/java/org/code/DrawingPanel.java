package org.code;

import org.code.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.List;
import java.util.ArrayList;

public class DrawingPanel extends JPanel {
    private List<Shape> shapes = new ArrayList<>();
    private int startX, startY;
    private List<Point> freedrawPoints = new ArrayList<>();
    private String selectedShape;
    private int currentColor;

    public static final Color[] COLOR_PALETTE = {
            Color.BLACK, Color.WHITE, Color.RED, Color.GREEN,
            Color.BLUE, Color.YELLOW, Color.ORANGE, Color.PINK,
            Color.CYAN, Color.MAGENTA, Color.GRAY, Color.LIGHT_GRAY,
            new Color(128, 0, 0),
            new Color(0, 128, 0),
            new Color(0, 0, 128),
            new Color(128, 128, 0)
    };

    public void setShapes(List<Shape> shapes) {
        this.shapes = shapes;
    }

    public void setColor(int color) {
        this.currentColor = color;
    }

    public DrawingPanel() {
        setBackground(Color.WHITE);

        addMouseListener((new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                startX = e.getX();
                startY = e.getY();
                freedrawPoints.clear();
                freedrawPoints.add(new Point(startX,startY));
            }

            public void mouseReleased(MouseEvent e) {
                int endX = e.getX();
                int endY = e.getY();
                String shapeType = selectedShape;
                int color = COLOR_PALETTE[currentColor].getRGB();

                switch(shapeType){
                    case "Line" -> new Line(startX, startY, endX, endY, color);
                    case "Rectangle" -> new Rectangle(startX, startY, endX, endY, color);
                    case "Triangle" -> new Triangle(startX, startY, endX, endY, color);
                    case "Oval" -> new Oval(startX, startY, endX, endY, color);
                    case "FreeDraw" -> new FreeDraw(freedrawPoints, color);
                }
            }
        }));

        addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                freedrawPoints.add(new Point(e.getX(), e.getY()));
                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // 遍历并绘制所有图形
        for (Shape shape : shapes) {
            shape.draw(g);  // 每个图形类实现了自己的 draw()
        }
    }
}
