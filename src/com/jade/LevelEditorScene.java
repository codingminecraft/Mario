package com.jade;

import com.component.*;
import com.dataStructure.AssetPool;
import com.dataStructure.Transform;
import com.dataStructure.Vector2;
import com.ui.Button;
import com.ui.ButtonContainer;
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
        mouseCursor.addComponent(new SnapToGrid(32, 32));

        GameObject buttonContainer = new GameObject("Button Container", new Transform(new Vector2(0, 0)), 0);
        buttonContainer.addComponent(new ButtonContainer());
        addGameObject(buttonContainer);
    }

    public void initAssetPool() {
        AssetPool.addSpritesheet("assets/marioTilesheet.png", 16, 16, 0, 33, 10 * 33);
    }

    @Override
    public void update(double dt) {
        cameraControls.update(dt);
        grid.update(dt);
        mouseCursor.update(dt);

        for (GameObject go : gameObjects) {
            go.update(dt);
        }
    }

    @Override
    public void draw(Graphics2D g2) {
        g2.setColor(Color.WHITE);
        g2.fillRect(0, 0, Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT);
        grid.draw(g2);
        renderer.render(g2);
        mouseCursor.draw(g2);
    }
}
