package com.component;

import com.dataStructure.Vector2;
import com.jade.Component;
import com.jade.MouseListener;
import com.jade.Window;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_3;

public class CameraControls extends Component {

    private float mouseSensitivity = 15.0f;

    public CameraControls() {

    }

    @Override
    public void update(double dt) {
        if (MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_3)) {
            Window.getScene().camera.position().x += MouseListener.getDx() * dt * mouseSensitivity;
            Window.getScene().camera.position().y -= MouseListener.getDy() * dt * mouseSensitivity;
        }
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
