package com.jade;

import com.component.*;
import com.dataStructure.AssetPool;
import com.dataStructure.Transform;
import com.dataStructure.Tuple;
import com.file.Parser;
import com.util.Constants;
import org.joml.Vector2f;

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
        Window.getWindow().setColor(Constants.SKY_COLOR);
    }


    public void initAssetPool() {
        // Game Assets
        AssetPool.addSpritesheet("assets/spritesheets/decorationsAndBlocks.png", 16, 16, 0, 7, 81);
        AssetPool.addSpritesheet("assets/spritesheets/items.png", 16, 16, 0, 7, 33);
        AssetPool.addSpritesheet("assets/spritesheets/character_and_enemies_32.png", 16, 16, 0, 14, 26);
        AssetPool.addSpritesheet("assets/spritesheets/character_and_enemies_64.png", 16, 32, 0, 21, 21 * 2);
        AssetPool.addSpritesheet("assets/spritesheets/icons.png", 32, 32, 0, 7, 15);

        // Engine Assets
        AssetPool.addSpritesheet("assets/spritesheets/defaultAssets.png", 24, 21, 0, 2, 2);
    }

    @Override
    public void update(double dt) {
        for (GameObject go : gameObjects) {
            go.update(dt);
        }

        if (KeyListener.isKeyPressed(GLFW_KEY_ESCAPE)) {
            Window.getWindow().changeScene(0);
        }

        if (objsToDelete.size() > 0) {
            for (GameObject obj : objsToDelete) {
                Tuple<Integer> gridCoords = obj.getGridCoords();
                worldPartition.remove(gridCoords);
                this.gameObjects.remove(obj);
                this.renderer.deleteGameObject(obj);
                this.physics.deleteGameObject(obj);
            }
            objsToDelete.clear();
        }

        if (objsToAdd.size() > 0) {
            for (GameObject g : objsToAdd) {
                gameObjects.add(g);
                renderer.add(g);
                physics.addGameObject(g);

                if (g.getComponent(BoxBounds.class) != null && g.getComponent(BoxBounds.class).isStatic) {
                    Tuple<Integer> gridPos = g.getGridCoords();
                    worldPartition.put(gridPos, g);
                }
            }
            objsToAdd.clear();
        }
    }
}
