package com.jade;

import com.component.*;
import com.dataStructure.AssetPool;
import com.dataStructure.Tuple;
import com.physics.BoxBounds;
import com.util.Constants;

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
        AssetPool.getSound("assets/sounds/main-theme-overworld.ogg").play();
    }


    public void initAssetPool() {
        // Game Assets
        AssetPool.addSpritesheet("assets/spritesheets/decorationsAndBlocks.png", 16, 16, 0, 7, 81);
        AssetPool.addSpritesheet("assets/spritesheets/items.png", 16, 16, 0, 7, 34);
        AssetPool.addSpritesheet("assets/spritesheets/character_and_enemies_32.png", 16, 16, 0, 14, 26);
        AssetPool.addSpritesheet("assets/spritesheets/character_and_enemies_64.png", 16, 32, 0, 21, 21 * 2);
        AssetPool.addSpritesheet("assets/spritesheets/icons.png", 32, 32, 0, 7, 15);
        AssetPool.addSpritesheet("assets/spritesheets/turtle.png", 16, 24, 0, 4, 4);

        // Engine Assets
        AssetPool.addSpritesheet("assets/spritesheets/defaultAssets.png", 24, 21, 0, 2, 2);

        // Sounds
        AssetPool.addSound("assets/sounds/main-theme-overworld.ogg", true);
        AssetPool.addSound("assets/sounds/flagpole.ogg", false);
        AssetPool.addSound("assets/sounds/break_block.ogg", false);
        AssetPool.addSound("assets/sounds/bump.ogg", false);
        AssetPool.addSound("assets/sounds/coin.ogg", false);
        AssetPool.addSound("assets/sounds/gameover.ogg", false);
        AssetPool.addSound("assets/sounds/jump-small.ogg", false);
        AssetPool.addSound("assets/sounds/mario_die.ogg", false);
        AssetPool.addSound("assets/sounds/pipe.ogg", false);
        AssetPool.addSound("assets/sounds/powerup.ogg", false);
        AssetPool.addSound("assets/sounds/powerup_appears.ogg", false);
        AssetPool.addSound("assets/sounds/stage_clear.ogg", false);
        AssetPool.addSound("assets/sounds/stomp.ogg", false);
        AssetPool.addSound("assets/sounds/kick.ogg", false);
    }

    @Override
    public void update(double dt) {
        for (GameObject go : gameObjects) {
            if (go.getComponent(PlayerController.class) != null || go.transform.position.x > this.camera.position().x && go.transform.position.x + go.transform.scale.x < this.camera.position().x + 32.0f * 40f + 128) {
                go.update(dt);
            } else if (go.transform.position.x + go.transform.scale.x < this.camera.position().x || go.transform.position.y + go.transform.scale.y < Constants.CAMERA_OFFSET_Y_2) {
                deleteGameObject(go);
            }
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
