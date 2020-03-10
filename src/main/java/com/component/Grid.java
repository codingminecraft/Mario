package com.component;

import com.jade.Camera;
import com.jade.Component;
import com.jade.Window;
import com.util.Constants;

import java.awt.*;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;

public class Grid extends Component {

    Camera camera;
    public int gridWidth, gridHeight;
    private int numYLines = 41;
    private int numXLines = 30;

    public Grid() {

    }

    @Override
    public void start() {
        this.camera = Window.getScene().camera;
        this.gridHeight = Constants.TILE_HEIGHT;
        this.gridWidth = Constants.TILE_WIDTH;
    }

    @Override
    public void update(double dt) {

    }

    @Override
    public void draw(Graphics2D g2) {
        g2.setStroke(new BasicStroke(1f));
        g2.setColor(new Color(0.2f, 0.2f, 0.2f, 0.5f));

        float startX = (float)Math.floor(camera.position.x / gridWidth) * gridWidth - camera.position.x;
        float startY = (float)Math.floor(camera.position.y / gridHeight) * gridHeight - camera.position.y;

        for (int column = 0; column <= numYLines; column++) {
            for (int row = column % 2; row <= numXLines; row += 2) {
                g2.drawRect((int)(startX + column * gridWidth), (int)(startY + row * gridHeight), gridWidth, gridHeight);
            }
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
