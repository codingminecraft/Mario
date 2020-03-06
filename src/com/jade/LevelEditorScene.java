package com.jade;

import com.component.*;
import com.dataStructure.AssetPool;
import com.dataStructure.Transform;
import com.dataStructure.Vector2;
import com.file.Parser;
import com.ui.*;
import com.util.Constants;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

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

        initLevelEditorComponents();
    }

    private void initLevelEditorComponents() {
        importLevelEditorData();
        if (this.jWindows.size() != 0) {
            System.out.println("JWindows already initialized!");
            return;
        }

        JWindow blockSelector = new JWindow("Blocks", new Vector2(0, 30), new Vector2(33 * 16, 400));
        Spritesheet spritesheet = AssetPool.getSpritesheet("assets/marioTilesheet.png");
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

        JWindow fileSaver = new JWindow("Level File", new Vector2(800, 30), new Vector2(400, 100));
        Label filePath = new Label("Default");
        Constants.CURRENT_LEVEL = "Default";
        fileSaver.addUIElement(filePath);
        fileSaver.addUIElement(new FileExplorerButton(filePath.id, new Vector2(50, 14)));

        fileSaver.addUIElement(new LoadLevelButton());
        fileSaver.addUIElement(new SaveLevelButton());
        this.addJWindow(fileSaver);
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

        if (Window.keyListener().isKeyPressed(KeyEvent.VK_ESCAPE)) {
            SnapToGrid snapToGrid = mouseCursor.getComponent(SnapToGrid.class);
            mouseCursor = new GameObject("Mouse Cursor", mouseCursor.transform.copy(), mouseCursor.zIndex);
            mouseCursor.addComponent(snapToGrid);
        }

        // Update level editor components
        for (JWindow win : jWindows) {
            win.update(dt);
        }
    }

    @Override
    public void draw(Graphics2D g2) {
        g2.setColor(Color.WHITE);
        g2.fillRect(0, 0, Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT);
        grid.draw(g2);
        renderer.render(g2);
        mouseCursor.getComponent(SnapToGrid.class).draw(g2);

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
