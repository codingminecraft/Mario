package com.component;

import com.jade.Component;
import com.jade.MouseListener;
import com.jade.Window;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_2;

public class CameraControls extends Component {

    private float prevMx, prevMy;

    public CameraControls() {
        prevMx = 0.0f;
        prevMy = 0.0f;
    }

    @Override
    public void update(double dt) {
        if (MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_2)) {
            float dx = (MouseListener.getX() + MouseListener.getDx() - prevMx);
            float dy = (MouseListener.getY() + MouseListener.getDy() - prevMy);

            Window.getScene().camera.position.x -= dx;
            Window.getScene().camera.position.y -= dy;
        }

        prevMx = MouseListener.getX() + MouseListener.getDx();
        prevMy = MouseListener.getY() + MouseListener.getDy();
    }

    @Override
    public Component copy() {
        return null;
    }

    @Override
    public String serialize(int tabSize) {
        return "";
    }
}
