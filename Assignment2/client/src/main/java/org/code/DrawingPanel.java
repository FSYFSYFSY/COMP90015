package org.code;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.rmi.RemoteException;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;

public class DrawingPanel extends JPanel {
    private List<Shape> shapes = new ArrayList<>();
    private int startX, startY;
    private List<Point> freedrawPoints = null;
    private String selectedShape = "Line2";
    private int currentColor;


    public void setShapes(List<Shape> shapes) {
        this.shapes = shapes;
    }

    public void SetSelectedShape(String selectedShape) {this.selectedShape = selectedShape;}

    public void setCurrentColor(int currentColor) {this.currentColor = currentColor;}

    public DrawingPanel(WhiteBoardService service) {
        setBackground(Color.WHITE);

        addMouseListener((new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                startX = e.getX();
                startY = e.getY();
                freedrawPoints = new ArrayList<>();
                //freedrawPoints.add(new Point(startX,startY));
            }

            public void mouseReleased(MouseEvent e) {
                int endX = e.getX();
                int endY = e.getY();
                String shapeType = selectedShape;
                int color = currentColor;

                Shape shape = switch(shapeType){
                    case "Line2" -> new Line(startX, startY, endX, endY, color, 2);
                    case "Line4" -> new Line(startX, startY, endX, endY, color, 4);
                    case "Line8" -> new Line(startX, startY, endX, endY, color, 8);
                    case "Rectangle" -> new Rectangle(startX, startY, endX, endY, color);
                    case "Triangle" -> new Triangle(startX, startY, endX, endY, color);
                    case "Oval" -> new Oval(startX, startY, endX, endY, color);
                    case "FreeDraw2" -> new FreeDraw(freedrawPoints, color, 2);
                    case "FreeDraw4" -> new FreeDraw(freedrawPoints, color, 4);
                    case "FreeDraw8" -> new FreeDraw(freedrawPoints, color, 8);
                    case "Erase2" -> new FreeDraw(freedrawPoints, getBackground().getRGB(), 2);
                    case "Erase4" -> new FreeDraw(freedrawPoints, getBackground().getRGB(), 4);
                    case "Erase8" -> new FreeDraw(freedrawPoints, getBackground().getRGB(), 8);
                    case "Text" -> {
                        String inputText = JOptionPane.showInputDialog("Please enter the text：");
                        if (inputText != null && !inputText.trim().isEmpty()) {
                            yield new TextShape(endX, endY, inputText.trim(), color);
                        } else {
                            yield new TextShape(endX, endY, "", color);
                        }
                    }
                    //case "ShapeErase" -> new ShapeErase(freedrawPoints);
                    default -> null;
                };

                try {
                    service.drawShape(shape);
                } catch (RemoteException ex) {
                    throw new RuntimeException(ex);
                }
                //repaint();
            }
        }));

        addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                Set<String> freeOrEraseShapes = Set.of("FreeDraw2", "FreeDraw4", "FreeDraw8", "Erase2", "Erase4", "Erase8");
                if (freeOrEraseShapes.contains(selectedShape)){freedrawPoints.add(new Point(e.getX(), e.getY()));}
                //repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        for (Shape shape : shapes) {
            shape.draw(g);
        }
    }
}
