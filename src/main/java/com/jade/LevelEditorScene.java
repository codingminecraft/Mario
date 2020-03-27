package com.jade;

import com.component.*;
import com.dataStructure.AssetPool;
import com.dataStructure.Transform;
import com.dataStructure.Tuple;
import com.file.Parser;
import com.physics.BoxBounds;
import com.prefabs.Prefabs;
import com.ui.*;
import com.ui.buttons.*;
import com.util.Constants;
import org.joml.Vector2f;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static org.lwjgl.glfw.GLFW.*;

public class LevelEditorScene extends Scene {
    private Grid grid;
    private CameraControls cameraControls;
    public GameObject mouseCursor;

    public LevelEditorScene(String name) {
        super.Scene(name);
        grid = new Grid();
        cameraControls = new CameraControls();
        mouseCursor = new GameObject("Mouse Cursor", new Transform(new Vector2f(0.0f, 0.0f)), -10);
    }

    @Override
    public void init() {
        if (AssetPool.hasSound("assets/sounds/main-theme-overworld.ogg")) {
            AssetPool.getSound("assets/sounds/main-theme-overworld.ogg").stop();
        }

        Window.getWindow().setColor(Constants.COLOR_WHITE);
        initAssetPool();
        grid.start();
        cameraControls.start();

        mouseCursor.addComponent(new LevelEditorControls(32, 32));
        mouseCursor.addComponent(new SpriteRenderer((Sprite)AssetPool.getSpritesheet("assets/spritesheets/decorationsAndBlocks.png").sprites.get(0).copy()));
        mouseCursor.transform.scale.x = 32;
        mouseCursor.transform.scale.y = 32;
        mouseCursor.getComponent(SpriteRenderer.class).color = Constants.COLOR_CLEAR;
        mouseCursor.start();
        renderer.add(mouseCursor);

        initLevelEditorComponents();
        importLevel(Constants.CURRENT_LEVEL);
    }

    private void initLevelEditorComponents() {
        importLevelEditorData();
        if (this.jWindows.size() != 0) {
            System.out.println("JWindows already initialized!");
            getJWindow("Question Block Type").setPosition(new Vector2f(-1000, 0));
            return;
        }

        Spritesheet decorationsAndBlocks = AssetPool.getSpritesheet("assets/spritesheets/decorationsAndBlocks.png");
        Spritesheet items = AssetPool.getSpritesheet("assets/spritesheets/items.png");
        Spritesheet icons = AssetPool.getSpritesheet("assets/spritesheets/icons.png");

        JWindow pipeSelection = new JWindow("Exit Pipe Selection", new Vector2f(-1000, 0), new Vector2f(140, 115));
        // Add pipes
        for (int i=0; i < 4; i++) {
            pipeSelection.addUIElement(new Button(icons.sprites.get(i + 5), new Vector2f(0, 0), new Vector2f(32, 32), Prefabs.PIPE_EXIT(i)));
        }
        this.addJWindow(pipeSelection);

        JWindow questionBlockSelector = new JWindow("Question Block Type", new Vector2f(-1000, 0), new Vector2f(140, 100));
        questionBlockSelector.addUIElement(new ButtonQuestionBlock(0));
        questionBlockSelector.addUIElement(new ButtonQuestionBlock(1));
        questionBlockSelector.addUIElement(new ButtonQuestionBlock(2));
        this.addJWindow(questionBlockSelector);
        Prefabs.setQuestionBlockWindow(questionBlockSelector);

        JWindow blockSelector = new JWindow("Blocks", new Vector2f(20, 460), new Vector2f(287, 203));
        int current = 0;
        while (current < decorationsAndBlocks.sprites.size()) {
            GameObject tile = new GameObject("Tile", new Transform(new Vector2f(0, 0)), 0);
            tile.transform.scale.x = 32;
            tile.transform.scale.y = 32;
            tile.addComponent(new SpriteRenderer((Sprite)decorationsAndBlocks.sprites.get(current).copy()));
            tile.addComponent(new BoxBounds(Constants.TILE_WIDTH, Constants.TILE_HEIGHT, true, false));
            Button button = new Button(decorationsAndBlocks.sprites.get(current), new Vector2f(0, 0), new Vector2f(16, 16), tile);
            blockSelector.addUIElement(button);
            current++;
        }

        blockSelector.beginTab("Prefabs");
        blockSelector.addUIElement(new Button(icons.sprites.get(0), new Vector2f(0, 0), new Vector2f(32, 32), Prefabs.MARIO_PREFAB()));
        // Add goombas
        for (int i=0; i < 4; i++) {
            blockSelector.addUIElement(new Button(icons.sprites.get(i + 1), new Vector2f(0, 0), new Vector2f(32, 32), Prefabs.GOOMBA_PREFAB(i)));
        }
        blockSelector.addUIElement(new Button(icons.sprites.get(9), new Vector2f(0, 0), new Vector2f(32, 32), Prefabs.QUESTION_BLOCK()));
        blockSelector.addUIElement(new Button(icons.sprites.get(10), new Vector2f(0, 0), new Vector2f(32, 32), Prefabs.BRICK_BLOCK()));
        // Add pipes
        for (int i=0; i < 4; i++) {
            blockSelector.addUIElement(new Button(icons.sprites.get(i + 5), new Vector2f(0, 0), new Vector2f(32, 32), Prefabs.PIPE_ENTRANCE(i)));
        }
        blockSelector.addUIElement(new Button(icons.sprites.get(14), new Vector2f(0, 0), new Vector2f(32, 32), Prefabs.COIN()));
        blockSelector.addUIElement(new Button(icons.sprites.get(15), new Vector2f(0, 0), new Vector2f(32, 32), Prefabs.TURTLE()));
        blockSelector.addUIElement(new Button(items.sprites.get(6), new Vector2f(0, 0), new Vector2f(32, 32), Prefabs.FLAG_TOP()));
        blockSelector.addUIElement(new Button(items.sprites.get(33), new Vector2f(0, 0), new Vector2f(32, 32), Prefabs.FLAG_POLE()));
        blockSelector.endTab();
        this.addJWindow(blockSelector);

        JWindow fileSaver = new JWindow("Level File", new Vector2f(320, 545), new Vector2f(235, 120));
        Constants.CURRENT_LEVEL = "Default";
        fileSaver.addUIElement(new FileExplorerButton(new Vector2f(86, 20), Constants.CURRENT_LEVEL));
        fileSaver.addUIElement(new SaveLevelButton());
        fileSaver.addUIElement(new LineBreak());
        fileSaver.addUIElement(new NewLevelButton());
        fileSaver.addUIElement(new TestLevelButton());
        this.addJWindow(fileSaver);

        JWindow layerIndicator = new JWindow("Layer", new Vector2f(1205, 592), new Vector2f(61, 72));
        layerIndicator.addUIElement(new ZIndexButton());
        this.addJWindow(layerIndicator);
    }

    public void initAssetPool() {
        // Game Assets
        AssetPool.addSpritesheet("assets/spritesheets/decorationsAndBlocks.png", 16, 16, 0, 7, 81);
        AssetPool.addSpritesheet("assets/spritesheets/items.png", 16, 16, 0, 7, 34);
        AssetPool.addSpritesheet("assets/spritesheets/character_and_enemies_32.png", 16, 16, 0, 14, 26);
        AssetPool.addSpritesheet("assets/spritesheets/character_and_enemies_64.png", 16, 32, 0, 21, 21 * 2);
        AssetPool.addSpritesheet("assets/spritesheets/icons.png", 32, 32, 0, 7, 16);
        AssetPool.addSpritesheet("assets/spritesheets/pipes.png", 32, 32, 0, 4, 6);
        AssetPool.addSpritesheet("assets/spritesheets/turtle.png", 16, 24, 0, 4, 4);

        // Engine Assets
        AssetPool.addSpritesheet("assets/spritesheets/defaultAssets.png", 24, 21, 0, 2, 2);
    }

    @Override
    public void update(double dt) {
        cameraControls.update(dt);
        grid.update(dt);
        mouseCursor.update(dt);

        for (GameObject go : gameObjects) {
           go.update(dt);
        }

        // Update level editor components
        for (JWindow win : jWindows) {
            win.update(dt);
        }

        if (KeyListener.isKeyPressed(GLFW_KEY_LEFT_CONTROL) && KeyListener.isKeyPressed(GLFW_KEY_S)) {
            export(Constants.CURRENT_LEVEL);
        }

        if (objsToDelete.size() > 0) {
            for (GameObject go : objsToDelete) {
                gameObjects.remove(go);
                renderer.deleteGameObject(go);
                worldPartition.remove(go.getGridCoords());
            }
            objsToDelete.clear();
        }

        if (objsToAdd.size() > 0) {
            for (GameObject g : objsToAdd) {
                gameObjects.add(g);
                renderer.add(g);
                physics.addGameObject(g);
                Tuple<Integer> gridPos = g.getGridCoords();
                worldPartition.put(gridPos, g);
            }

            for (GameObject g : objsToAdd) {
                g.start();
            }
            objsToAdd.clear();
        }
    }

    public void exportLevelEditorData() {
        try {
            FileOutputStream fos = new FileOutputStream("uiLayout.zip");
            ZipOutputStream zos = new ZipOutputStream(fos);

            zos.putNextEntry(new ZipEntry("uiLayout.json"));

            int i = 0;
            for (JWindow win : jWindows) {
                String str = win.serialize(0);
                if (str.compareTo("") != 0) {
                    zos.write(str.getBytes());
                    if (i != jWindows.size() - 1) {
                        zos.write(",\n".getBytes());
                    }
                }
                i++;
            }

            zos.closeEntry();
            zos.close();
            fos.close();
        } catch(IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    public void importLevelEditorData() {
        Parser.openFile("uiLayout");

        JWindow win = Parser.parseJWindow();
        while (win != null) {
            addJWindow(win);
            win = Parser.parseJWindow();
        }
    }

}
