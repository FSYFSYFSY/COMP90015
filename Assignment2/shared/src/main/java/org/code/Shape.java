package org.code;

import java.awt.Graphics;
import java.io.Serializable;

public interface Shape extends Serializable {
    void draw(Graphics g);
    boolean containsPoint(int x, int y);
}
