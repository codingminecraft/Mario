package com.jade;

import com.dataStructure.Transform;
import com.dataStructure.Vector2;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Renderer {
    Map<Integer, List<GameObject>> gameObjects;
    Camera camera;

    public Renderer(Camera camera) {
        this.camera = camera;
        this.gameObjects = new HashMap<>();
    }

    public void submit(GameObject gameObject) {
        gameObjects.computeIfAbsent(gameObject.zIndex, k -> new ArrayList<>());
        gameObjects.get(gameObject.zIndex).add(gameObject);
    }

    public void render(Graphics2D g2) {
        int lowestZIndex = Integer.MAX_VALUE;
        int highestZIndex = Integer.MIN_VALUE;
        for (Integer i : gameObjects.keySet()) {
            if (i < lowestZIndex) lowestZIndex = i;
            if (i > highestZIndex) highestZIndex = i;
        }

        int currentZIndex = lowestZIndex;
        while (currentZIndex <= highestZIndex) {
            if (gameObjects.get(currentZIndex) == null) {
                currentZIndex++;
                continue;
            }

            for (GameObject g : gameObjects.get(currentZIndex)) {
                if (g.isUi) {
                    g.draw(g2);
                } else {
                    Transform oldTransform = new Transform(g.transform.position);
                    oldTransform.rotation = g.transform.rotation;
                    oldTransform.scale = new Vector2(g.transform.scale.x, g.transform.scale.y);

                    g.transform.position = new Vector2(g.transform.position.x - camera.position.x,
                            g.transform.position.y - camera.position.y);
                    g.draw(g2);
                    g.transform = oldTransform;
                }
            }

            currentZIndex++;
        }
    }
}
