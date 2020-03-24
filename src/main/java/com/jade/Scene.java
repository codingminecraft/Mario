package com.jade;

import com.component.BoxBounds;
import com.dataStructure.Tuple;
import com.file.Parser;
import com.physics.Physics;
import com.renderer.Renderer;
import com.renderer.UIRenderComponent;
import com.ui.JWindow;
import com.util.Constants;
import org.joml.Vector2f;

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
    public Physics physics;
    List<GameObject> gameObjects;
    Map<Tuple<Integer>, GameObject> worldPartition;
    List<GameObject> objsToDelete;
    List<GameObject> objsToAdd;
    List<JWindow> jWindows;
    Renderer renderer;

    public void Scene(String name) {
        this.name = name;
        this.camera = new Camera(new Vector2f());
        this.gameObjects = new ArrayList<>();
        this.renderer = new Renderer(this.camera);
        this.worldPartition = new HashMap<>();
        this.jWindows = new ArrayList<>();
        this.objsToDelete = new ArrayList<>();
        this.objsToAdd = new ArrayList<>();
        this.physics = new Physics();
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
            for (UIRenderComponent comp : win.getAllRenderComponents()) {
                renderer.add(comp);
            }
        }
    }

    public void moveGameObject(GameObject g, Vector2f direction) {
        Tuple<Integer> oldCoords = g.getGridCoords();
        Tuple<Integer> newCoords = new Tuple<>(oldCoords.x + (int)(direction.x * Constants.TILE_WIDTH),
                oldCoords.y + (int)(direction.y * Constants.TILE_HEIGHT), oldCoords.z);

        if (!worldPartition.containsKey(newCoords)) {
            worldPartition.remove(oldCoords);
            g.transform.position.x = newCoords.x;
            g.transform.position.y = newCoords.y;
            worldPartition.put(newCoords, g);
        }
    }

    public <T extends Component> GameObject findObjectWithComponent(Class<T> c) {
        for (GameObject g : gameObjects) {
            if (g.getComponent(c) != null) {
                return g;
            }
        }
        return null;
    }

    public void addGameObject(GameObject g) {
        if (this instanceof LevelEditorScene) {
            gameObjects.add(g);
            renderer.add(g);
            physics.addGameObject(g);
            Tuple<Integer> gridPos = g.getGridCoords();
            worldPartition.put(gridPos, g);
        } else {
            objsToAdd.add(g);
            g.start();
        }
    }

    public JWindow getJWindow(String title) {
        for (JWindow win : jWindows) {
            if (win.getTitle().equals(title)) {
                return win;
            }
        }
        return null;
    }

    public void addLowUI(UIRenderComponent comp) {
        this.renderer.addLow(comp);
    }

    public void addRenderComponent(UIRenderComponent comp) {
        this.renderer.add(comp);
    }

    public void addJWindow(JWindow win) {
        this.jWindows.add(win);
    }

    public void deleteGameObject(GameObject g) {
        objsToDelete.add(g);
    }

    public Map<Tuple<Integer>, GameObject> getWorldPartition() {
        return this.worldPartition;
    }

    public boolean inJWindow(Vector2f position) {
        for (JWindow win : jWindows) {
            if (win.pointInWindow(position)) {
                return true;
            }
        }
        return false;
    }

    public abstract void update(double dt);

    public void render() {
        renderer.render();
    }

    public void importLevel(String filename) {
        if (gameObjects.size() > 0) {
            gameObjects.clear();
            renderer.resetLevel();
            worldPartition.clear();

            // If we are in a LevelEditorScene make sure to copy the mouse cursor GameObject to the renderer.
            // Maybe find a better way to do this?
            if (this instanceof LevelEditorScene) {
                LevelEditorScene scene = (LevelEditorScene)this;
                renderer.add(scene.mouseCursor);
            }
        }

        Parser.openLevelFile(filename);
        GameObject go = Parser.parseGameObject();
        while (go != null) {
            addGameObject(go);
            go = Parser.parseGameObject();
        }

        for (GameObject g : gameObjects) {
            for (Component c : g.getAllComponents()) {
                c.start();
            }
        }
    }

    public void export(String filename) {
        try {
            FileOutputStream fos = new FileOutputStream("assets/levels/" + filename + ".zip");
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
