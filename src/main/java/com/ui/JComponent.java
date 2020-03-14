package com.ui;

import com.file.Serialize;
import com.jade.MouseListener;
import com.renderer.UIRenderComponent;
import org.joml.Vector2f;

import java.awt.Graphics2D;

public abstract class JComponent extends Serialize {
    protected static int MAX_ID = 0;
    protected Tab parent;
    protected UIRenderComponent renderComponent;
    public boolean isLineBreak = false;
    public boolean isCentered = false;
    public int id;
    public boolean visible = false;

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
        return MouseListener.positionScreenCoords().x > renderComponent.getPosX() && MouseListener.positionScreenCoords().x < renderComponent.getPosX() + renderComponent.getWidth() &&
                MouseListener.positionScreenCoords().y > renderComponent.getPosY() && MouseListener.positionScreenCoords().y < renderComponent.getPosY() + renderComponent.getHeight();
    }
}
