package com.ui.buttons;

import com.jade.MouseListener;
import com.jade.Window;
import com.ui.JComponent;

import java.awt.event.MouseEvent;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_1;

public abstract class JButton extends JComponent {

    protected boolean active = false;

    private float debounce = 0.1f;
    private float debounceLeft = 0.0f;
    public abstract void clicked();

    @Override
    public void update(double dt) {
        debounceLeft -= dt;
        if (mouseInButton() && MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_1) && !active && debounceLeft < 0.0f) {
            this.active = true;
            this.debounceLeft = debounce;
            clicked();
        } else {
            this.active = false;
        }
    }
}
