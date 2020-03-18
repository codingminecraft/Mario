package com.ui;

import com.file.Serialize;
import com.jade.MouseListener;
import com.renderer.RenderComponent;
import com.renderer.UIRenderComponent;
import org.joml.Vector2f;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

public abstract class JComponent extends Serialize {
    protected static int MAX_ID = 0;
    protected Tab parent;
    protected List<UIRenderComponent> renderComponents = new ArrayList<>();
    public UIRenderComponent mainComp;
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

    public List<UIRenderComponent> getRenderComponents() {
        return this.renderComponents;
    }

    // This is called once every frame after the game world is updated
    public void update(double dt) {

    }

    // This is called once every frame after the game world is drawn
    public void draw(Graphics2D g2) {

    }

    public void setPosX(float x) {
        this.mainComp.setPosX(x);
    }

    public void setPosY(float y) {
        this.mainComp.setPosY(y);
    }

    public float getHeight() {
        return mainComp.getHeight();
    }

    public float getWidth() {
        return mainComp.getWidth();
    }

    public float getPosX() {
        return mainComp.getPosX();
    }

    public float getPosY() {
        return mainComp.getPosY();
    }

    protected boolean mouseInButton() {
        return MouseListener.positionScreenCoords().x > mainComp.getPosX() && MouseListener.positionScreenCoords().x < mainComp.getPosX() + mainComp.getWidth() &&
                MouseListener.positionScreenCoords().y > mainComp.getPosY() && MouseListener.positionScreenCoords().y < mainComp.getPosY() + mainComp.getHeight();
    }

}
