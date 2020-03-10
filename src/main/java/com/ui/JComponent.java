package com.ui;

import com.dataStructure.Vector2;
import com.file.Serialize;
import com.jade.MouseListener;
import com.jade.Window;

import java.awt.Graphics2D;

public abstract class JComponent extends Serialize {
    protected static int MAX_ID = 0;
    public boolean isLineBreak = false;
    public boolean isCentered = false;
    public int id;
    public boolean visible = false;
    protected Tab parent;
    public Vector2 position, size;

    public JComponent() {
        this.id = MAX_ID;
        MAX_ID++;
    }

    // This is called after all windows and components have been initialized
    public void start() {

    }

    // This is called once every frame after the game world is updated
    public void update(double dt) {

    }

    // This is called once every frame after the game world is drawn
    public void draw(Graphics2D g2) {

    }

    protected boolean mouseInButton() {
        return MouseListener.getX() > this.position.x && MouseListener.getX() < this.position.x + this.size.x &&
                MouseListener.getY() > this.position.y && MouseListener.getY() < this.position.y + this.size.y;
    }
}