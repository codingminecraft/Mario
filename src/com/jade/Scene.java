package com.jade;

import com.dataStructure.Tuple;
import com.dataStructure.Vector2;
import com.file.Parser;
import com.ui.JWindow;

import java.awt.Graphics2D;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public abstract class Scene {
    String name;
    public Camera camera;
    List<GameObject> gameObjects;
    Map<Tuple<Integer>, GameObject> worldPartition;
    List<JWindow> jWindows;
    Renderer renderer;

    public void Scene(String name) {
        this.name = name;
        this.camera = new Camera(new Vector2());
        this.gameObjects = new ArrayList<>();
        this.renderer = new Renderer(this.camera);
        this.worldPartition = new HashMap<>();
        this.jWindows = new ArrayList<>();
    }

    public void init() {

    }

    public void start() {
        for (GameObject g : gameObjects) {
            for (Component c : g.getAllComponents()) {
                c.start();
            }
        }

        for (JWindow win : jWindows) {
            win.start();
        }
    }

    public void addGameObject(GameObject g) {
        gameObjects.add(g);
        renderer.submit(g);
        Tuple<Integer> gridPos = g.getGridCoords();
        worldPartition.put(gridPos, g);
    }

    public void addJWindow(JWindow win) {
        this.jWindows.add(win);
    }

    public void addUIGameObject(GameObject g) {
        gameObjects.add(g);
        renderer.submitUI(g);
    }

    public Map<Tuple<Integer>, GameObject> getWorldPartition() {
        return this.worldPartition;
    }

    public boolean inJWindow(Vector2 position) {
        for (JWindow win : jWindows) {
            if (win.pointInWindow(position)) {
                return true;
            }
        }
        return false;
    }

    public abstract void update(double dt);
    public abstract void draw(Graphics2D g2);

    public void importLevel(String filename) {
        if (gameObjects.size() > 0) {
            gameObjects.clear();
            renderer.reset();
            worldPartition.clear();
        }

        Parser.openLevelFile(filename);
        GameObject go = Parser.parseGameObject();
        while (go != null) {
            addGameObject(go);
            go = Parser.parseGameObject();
        }
    }

    public void export(String filename) {
        try {
            FileOutputStream fos = new FileOutputStream("levels/" + filename + ".zip");
            ZipOutputStream zos = new ZipOutputStream(fos);

            zos.putNextEntry(new ZipEntry(filename + ".json"));

            int i = 0;
            for (GameObject go : gameObjects) {
                String str = go.serialize(0);
                if (str.compareTo("") != 0) {
                    zos.write(str.getBytes());
                    if (i != gameObjects.size() - 1) {
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
}
