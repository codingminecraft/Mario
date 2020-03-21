package com.jade;

import com.component.Bounds;
import com.component.BoxBounds;
import com.dataStructure.AssetPool;
import com.file.Parser;
import com.util.Constants;

import java.awt.Color;
import java.awt.Graphics2D;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;

public class LevelScene extends Scene {

    public LevelScene(String name) {
        super.Scene(name);
    }

    @Override
    public void init() {
        initAssetPool();
        importLevel(Constants.CURRENT_LEVEL);
    }


    public void initAssetPool() {
        AssetPool.addSpritesheet("assets/marioTilesheet.png", 16, 16, 0, 33, 10 * 33);
        AssetPool.addSpritesheet("assets/defaultAssets.png", 24, 21, 0, 2, 2);
        AssetPool.addSpritesheet("assets/character_and_enemies_32.png", 16, 16, 0, 14, 26);
        AssetPool.addSpritesheet("assets/icons.png", 32, 32, 0, 5, 5);
    }

    @Override
    public void update(double dt) {
        for (GameObject go : gameObjects) {
            go.update(dt);
        }

        if (KeyListener.isKeyPressed(GLFW_KEY_ESCAPE)) {
            Window.getWindow().changeScene(0);
        }
    }
}
