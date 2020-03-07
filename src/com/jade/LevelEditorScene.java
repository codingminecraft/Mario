package com.jade;

import com.component.*;
import com.dataStructure.AssetPool;
import com.dataStructure.Transform;
import com.dataStructure.Vector2;
import com.file.Parser;
import com.prefabs.Prefabs;
import com.ui.*;
import com.util.Constants;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class LevelEditorScene extends Scene {
    private Grid grid;
    private CameraControls cameraControls;
    public GameObject mouseCursor;
    GameObject player = new GameObject("Player", new Transform(new Vector2(800, 500)), 0);

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

        initLevelEditorComponents();
    }

    private void initLevelEditorComponents() {
        importLevelEditorData();
        if (this.jWindows.size() != 0) {
            System.out.println("JWindows already initialized!");
            return;
        }

        JWindow blockSelector = new JWindow("Blocks", new Vector2(0, 30), new Vector2(33 * 16 + (33 * Constants.PADDING.x), 400));
        Spritesheet spritesheet = AssetPool.getSpritesheet("assets/marioTilesheet.png");
        Spritesheet defaultAssets = AssetPool.getSpritesheet("assets/defaultAssets.png");
        Spritesheet icons = AssetPool.getSpritesheet("assets/icons.png");
        int totalSprites = 10 * 33;
        int current = 0;
        while (current < totalSprites) {
            GameObject tile = new GameObject("Tile", new Transform(new Vector2(0, 0)), 0);
            tile.transform.scale.x = 2;
            tile.transform.scale.y = 2;
            tile.addComponent(spritesheet.sprites.get(current).copy());
            Button button = new Button(spritesheet.sprites.get(current), new Vector2(0, 0), new Vector2(16, 16), tile);
            blockSelector.addUIElement(button);
            current++;
        }

        this.addJWindow(blockSelector);

        JWindow fileSaver = new JWindow("Level File", new Vector2(33 * 16 + (33 * Constants.PADDING.x) + 10, 30), new Vector2(400, 100));
        Label filePath = new Label("Default");
        Constants.CURRENT_LEVEL = "Default";
        fileSaver.addUIElement(filePath);
        fileSaver.addUIElement(new FileExplorerButton(filePath.id, new Vector2(86, 20)));

        fileSaver.addUIElement(new SaveLevelButton());
        this.addJWindow(fileSaver);

        JWindow layerIndicator = new JWindow("Layer Indicator", new Vector2(Constants.SCREEN_WIDTH - 110, 30), new Vector2(100, 100));
        Label layerLabel = new Label("" + Constants.Z_INDEX);
        layerLabel.isCentered = true;
        layerIndicator.addUIElement(new ZIndexButton(1, layerLabel.id, defaultAssets.sprites.get(0)));
        layerIndicator.addUIElement(new LineBreak());
        layerIndicator.addUIElement(layerLabel);
        layerIndicator.addUIElement(new LineBreak());
        layerIndicator.addUIElement(new ZIndexButton(-1, layerLabel.id, defaultAssets.sprites.get(1)));
        this.addJWindow(layerIndicator);

        JWindow prefabs = new JWindow("Prefabs", new Vector2(0, 300), new Vector2(200, 300));
        prefabs.addUIElement(new Button(icons.sprites.get(0), new Vector2(0, 0), new Vector2(32, 32), Prefabs.MARIO_PREFAB()));
        for (int i=0; i < 4; i++) {
            prefabs.addUIElement(new Button(icons.sprites.get(i + 1), new Vector2(0, 0), new Vector2(32, 32), Prefabs.GOOMBA_PREFAB(i)));
        }
        this.addJWindow(prefabs);
    }

    public void initAssetPool() {
        AssetPool.addSpritesheet("assets/marioTilesheet.png", 16, 16, 0, 33, 10 * 33);
        AssetPool.addSpritesheet("assets/defaultAssets.png", 24, 21, 0, 2, 2);
        AssetPool.addSpritesheet("assets/character_and_enemies_32.png", 16, 16, 0, 14, 26);
        AssetPool.addSpritesheet("assets/icons.png", 32, 32, 0, 5, 5);
    }

    @Override
    public void update(double dt) {
        cameraControls.update(dt);
        grid.update(dt);
        mouseCursor.update(dt);

        player.update(dt);
        if (Window.keyListener().isKeyPressed(KeyEvent.VK_SPACE)) {
            player.getComponent(AnimationMachine.class).trigger("StartJumping");
        } else if (Window.keyListener().isKeyPressed(KeyEvent.VK_RIGHT)) {
            player.getComponent(AnimationMachine.class).trigger("StartRunning");
        } else if (Window.keyListener().isKeyPressed(KeyEvent.VK_LEFT)) {
            player.getComponent(AnimationMachine.class).trigger("StartIdle");
        } else if (Window.keyListener().isKeyPressed(KeyEvent.VK_UP)) {
            player.getComponent(AnimationMachine.class).trigger("StartSwim");
        }

        for (GameObject go : gameObjects) {
            go.update(dt);
        }

        if (Window.keyListener().isKeyPressed(KeyEvent.VK_ESCAPE)) {
            SnapToGrid snapToGrid = mouseCursor.getComponent(SnapToGrid.class);
            mouseCursor = new GameObject("Mouse Cursor", mouseCursor.transform.copy(), mouseCursor.zIndex);
            mouseCursor.addComponent(snapToGrid);
            snapToGrid.gameObjectRemoved();
        }

        // Update level editor components
        for (JWindow win : jWindows) {
            win.update(dt);
        }

        if (Window.keyListener().isKeyPressed(KeyEvent.VK_CONTROL) && Window.keyListener().isKeyPressed(KeyEvent.VK_S)) {
            export(Constants.CURRENT_LEVEL);
        }
    }

    @Override
    public void draw(Graphics2D g2) {
        g2.setRenderingHints(Constants.NO_ANTIALIASING_HINT);
        g2.setColor(Color.WHITE);
        g2.fillRect(0, 0, Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT);
        grid.draw(g2);
        renderer.render(g2);
        mouseCursor.getComponent(SnapToGrid.class).draw(g2);
        player.draw(g2);

        g2.setRenderingHints(Constants.ANTIALIASING_HINT);
        // Draw level editor components
        for (JWindow win : jWindows) {
            win.draw(g2);
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
