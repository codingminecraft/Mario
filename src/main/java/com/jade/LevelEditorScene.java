package com.jade;

import com.component.*;
import com.dataStructure.AssetPool;
import com.dataStructure.Transform;
import com.dataStructure.Tuple;
import com.file.Parser;
import com.prefabs.Prefabs;
import com.ui.*;
import com.ui.buttons.FileExplorerButton;
import com.ui.buttons.NewLevelButton;
import com.ui.buttons.SaveLevelButton;
import com.ui.buttons.TestLevelButton;
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
            return;
        }

        JWindow blockSelector = new JWindow("Blocks", new Vector2f(0, 100), new Vector2f(33 * 16 + (33 * Constants.PADDING.x), 400));
        Spritesheet decorationsAndBlocks = AssetPool.getSpritesheet("assets/spritesheets/decorationsAndBlocks.png");
        Spritesheet defaultAssets = AssetPool.getSpritesheet("assets/spritesheets/defaultAssets.png");
        Spritesheet icons = AssetPool.getSpritesheet("assets/spritesheets/icons.png");

        int current = 0;
        while (current < decorationsAndBlocks.sprites.size()) {
            GameObject tile = new GameObject("Tile", new Transform(new Vector2f(0, 0)), 0);
            tile.transform.scale.x = 32;
            tile.transform.scale.y = 32;
            tile.addComponent(new SpriteRenderer((Sprite)decorationsAndBlocks.sprites.get(current).copy()));
            tile.addComponent(new BoxBounds(Constants.TILE_WIDTH, Constants.TILE_HEIGHT, true));
            Button button = new Button(decorationsAndBlocks.sprites.get(current), new Vector2f(0, 0), new Vector2f(16, 16), tile);
            blockSelector.addUIElement(button);
            current++;
        }

        blockSelector.beginTab("Prefabs");
        blockSelector.addUIElement(new Button(icons.sprites.get(0), new Vector2f(0, 0), new Vector2f(32, 32), Prefabs.MARIO_PREFAB()));
        for (int i=0; i < 4; i++) {
            blockSelector.addUIElement(new Button(icons.sprites.get(i + 1), new Vector2f(0, 0), new Vector2f(32, 32), Prefabs.GOOMBA_PREFAB(i)));
        }
        blockSelector.addUIElement(new Button(icons.sprites.get(9), new Vector2f(0, 0), new Vector2f(32, 32), Prefabs.QUESTION_BLOCK()));
        blockSelector.addUIElement(new Button(icons.sprites.get(10), new Vector2f(0, 0), new Vector2f(32, 32), Prefabs.BRICK_BLOCK()));
        blockSelector.endTab();
        this.addJWindow(blockSelector);

        JWindow fileSaver = new JWindow("Level File", new Vector2f(33 * 16 + (33 * Constants.PADDING.x) + 10, 30), new Vector2f(400, 100));
        Constants.CURRENT_LEVEL = "Default";
        fileSaver.addUIElement(new FileExplorerButton(new Vector2f(86, 20), Constants.CURRENT_LEVEL));
        fileSaver.addUIElement(new SaveLevelButton());
        fileSaver.addUIElement(new LineBreak());
        fileSaver.addUIElement(new NewLevelButton());
        fileSaver.addUIElement(new TestLevelButton());
        this.addJWindow(fileSaver);
//
//        JWindow layerIndicator = new JWindow("Layer", new Vector2(Constants.SCREEN_WIDTH - 110, 30), new Vector2(100, 100));
//        Label layerLabel = new Label("" + Constants.Z_INDEX);
//        layerLabel.isCentered = true;
//        layerIndicator.addUIElement(new ZIndexButton(1, layerLabel.id, defaultAssets.sprites.get(0)));
//        layerIndicator.addUIElement(new LineBreak());
//        layerIndicator.addUIElement(layerLabel);
//        layerIndicator.addUIElement(new LineBreak());
//        layerIndicator.addUIElement(new ZIndexButton(-1, layerLabel.id, defaultAssets.sprites.get(1)));
//        this.addJWindow(layerIndicator);
    }

    public void initAssetPool() {
        // Game Assets
        AssetPool.addSpritesheet("assets/spritesheets/decorationsAndBlocks.png", 16, 16, 0, 7, 49);
        AssetPool.addSpritesheet("assets/spritesheets/items.png", 16, 16, 0, 7, 33);
        AssetPool.addSpritesheet("assets/spritesheets/character_and_enemies_32.png", 16, 16, 0, 14, 26);
        AssetPool.addSpritesheet("assets/spritesheets/icons.png", 32, 32, 0, 7, 15);

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

        for (GameObject go : objsToDelete) {
            gameObjects.remove(go);
            //renderer.gameObjects.get(go.zIndex).remove(go);
            worldPartition.remove(new Tuple<>((int)go.transform.position.x, (int)go.transform.position.y, go.zIndex));
        }
        objsToDelete.clear();
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
