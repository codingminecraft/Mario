package com.ui;

import com.jade.Window;

import java.awt.event.MouseEvent;

public abstract class JButton extends JComponent {

    protected boolean active = false;

    private float debounce = 0.1f;
    private float debounceLeft = 0.0f;
    abstract void clicked();

    @Override
    public void update(double dt) {
        debounceLeft -= dt;
        if (mouseInButton() && Window.mouseListener().mousePressed && Window.mouseListener().mouseButton == MouseEvent.BUTTON1 && !active && debounceLeft < 0.0f) {
            this.active = true;
            this.debounceLeft = debounce;
            clicked();
        } else {
            this.active = false;
        }
    }
}
