package com.component;

import com.jade.Component;
import com.jade.Window;

import java.awt.event.MouseEvent;

public class CameraControls extends Component {

    private float prevMx, prevMy;

    public CameraControls() {
        prevMx = 0.0f;
        prevMy = 0.0f;
    }

    @Override
    public void update(double dt) {
        if (Window.getWindow().mouseListener.mousePressed &&
                Window.getWindow().mouseListener.mouseButton == MouseEvent.BUTTON2) {
            float dx = (Window.getWindow().mouseListener.x + Window.getWindow().mouseListener.dx - prevMx);
            float dy = (Window.getWindow().mouseListener.y + Window.getWindow().mouseListener.dy - prevMy);

            Window.getWindow().getCurrentScene().camera.position.x -= dx;
            Window.getWindow().getCurrentScene().camera.position.y -= dy;
        }

        prevMx = Window.getWindow().mouseListener.x + Window.getWindow().mouseListener.dx;
        prevMy = Window.getWindow().mouseListener.y + Window.getWindow().mouseListener.dy;
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
