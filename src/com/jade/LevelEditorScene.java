package com.jade;

import com.component.CameraControls;
import com.component.Grid;
import com.dataStructure.Transform;
import com.dataStructure.Vector2;
import com.util.Constants;

import java.awt.Color;
import java.awt.Graphics2D;

public class LevelEditorScene extends Scene {
    private Grid grid;
    private CameraControls cameraControls;
    public GameObject mouseCursor;


    public LevelEditorScene(String name) {
        super.Scene(name);
        grid = new Grid();
        cameraControls = new CameraControls();
        mouseCursor = new GameObject("Mouse Cursor", new Transform(new Vector2(0.0f, 0.0f)), -10);
    }

    @Override
    public void init() {
        initAssetPool();
        grid.start();
        cameraControls.start();
    }

    public void initAssetPool() {

    }

    @Override
    public void update(double dt) {
        cameraControls.update(dt);
        grid.update(dt);
        mouseCursor.update(dt);
    }

    @Override
    public void draw(Graphics2D g2) {
        g2.setColor(Color.WHITE);
        g2.fillRect(0, 0, Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT);
        grid.draw(g2);
        renderer.render(g2);
    }
}
