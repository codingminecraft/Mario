package com.jade;

import com.dataStructure.Vector2;
import com.ui.JWindow;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class ML extends MouseAdapter {

    public boolean mousePressed = false;
    public boolean mouseDragged = false;
    public int mouseButton = -1;
    public float x = -1.0f, y = -1.0f;
    public float dx = -1.0f, dy = -1.0f;
    public Vector2 position = new Vector2(-1.0f, -1.0f);

    @Override
    public void mousePressed(MouseEvent mouseEvent) {
        this.mousePressed = true;
        this.mouseButton = mouseEvent.getButton();
    }

    @Override
    public void mouseReleased(MouseEvent mouseEvent) {
        this.mousePressed = false;
        this.mouseDragged = false;
        this.dx = 0;
        this.dy = 0;
    }

    @Override
    public void mouseMoved(MouseEvent mouseEvent) {
        this.x = mouseEvent.getX();
        this.y = mouseEvent.getY();
        this.position.x = this.x;
        this.position.y = this.y;
    }

    @Override
    public void mouseDragged(MouseEvent mouseEvent) {
        this.mouseDragged = true;
        this.dx = mouseEvent.getX() - this.x;
        this.dy = mouseEvent.getY() - this.y;
        this.x = mouseEvent.getX();
        this.y = mouseEvent.getY();
        this.position.x = this.x;
        this.position.y = this.y;
    }

    public void endFrame() {
        this.dx = 0;
        this.dy = 0;
    }
}
