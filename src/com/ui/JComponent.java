package com.ui;

import com.dataStructure.Vector2;
import com.file.Serialize;
import com.jade.Component;
import com.jade.Window;

import java.awt.Graphics2D;

public abstract class JComponent extends Serialize {
    protected static int MAX_ID = 0;
    public int id;
    protected JWindow parent;
    public Vector2 position, size;

    public JComponent() {
        this.id = MAX_ID;
        MAX_ID++;
    }

    public void start() {

    }

    public void update(double dt) {

    }

    public void draw(Graphics2D g2) {

    }

    protected boolean mouseInButton() {
        return Window.mouseListener().x > this.position.x && Window.mouseListener().x < this.position.x + this.size.x &&
                Window.mouseListener().y > this.position.y && Window.mouseListener().y < this.position.y + this.size.y;
    }
}
