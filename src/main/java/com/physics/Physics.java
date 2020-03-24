package com.physics;

import com.component.Bounds;
import com.component.BoxBounds;
import com.dataStructure.Tuple;
import com.jade.GameObject;
import com.jade.Window;
import com.util.Constants;

import java.util.ArrayList;
import java.util.List;

public class Physics {
    List<GameObject> staticObjects;
    List<GameObject> dynamicObjects;

    private Tuple<Integer> tuple;

    public Physics() {
        this.staticObjects = new ArrayList<>();
        this.dynamicObjects = new ArrayList<>();
        this.tuple = new Tuple<>(0, 0, 0);
    }

    public void addGameObject(GameObject go) {
        Bounds bounds = go.getComponent(Bounds.class);
        if (bounds != null) {
            if (bounds.isStatic) {
                this.staticObjects.add(go);
            } else {
                this.dynamicObjects.add(go);
            }
        }
    }

    public void update(double dt) {
        for (GameObject go : dynamicObjects) {
            resolveCollisions(go);
        }
    }

    public void deleteGameObject(GameObject go) {
        staticObjects.remove(go);
        dynamicObjects.remove(go);
    }

    private void resolveCollisions(GameObject go) {
        // Check all boundaries around GameObject for static objects
        // 0 0 0
        // 0 x 0
        // 0 0 0
        Bounds bounds = go.getComponent(Bounds.class);
        if (bounds == null) return;
        if (bounds.isStatic) return;

        Tuple<Integer> gridCoords = go.getGridCoords();
        for (int i=-1; i < 2; i++) {
            for (int j=-1; j < 3; j++) {
                this.tuple.x = gridCoords.x + (Constants.TILE_WIDTH * i);
                this.tuple.y = gridCoords.y + (Constants.TILE_HEIGHT * j);
                this.tuple.z = go.zIndex;

                GameObject otherGo = Window.getScene().getWorldPartition().get(this.tuple);
                if (otherGo != null && otherGo != go) {
                    Bounds otherBounds = otherGo.getComponent(Bounds.class);
                    if (otherBounds != null) {
                        if (Bounds.checkCollision(bounds, otherBounds)) {
                            Collision collision = Bounds.resolveCollision(bounds, otherBounds);
                            if (collision == null) continue;
                            go.collision(collision);

                            // Flip the collision side for the other game object
                            collision.flip(go);

                            otherGo.collision(collision);
                        }
                    }
                }
            }
        }

        // Check for collisions/triggers among dynamic objects
        for (GameObject obj : dynamicObjects) {
            if (obj == go) continue;

            Bounds otherBounds = obj.getComponent(Bounds.class);
            if (Bounds.checkCollision(bounds, otherBounds)) {
                if (bounds.isTrigger() || otherBounds.isTrigger()) {
                    go.trigger(new Trigger(otherBounds.gameObject));
                    otherBounds.gameObject.trigger(new Trigger(go));
                } else {
                    Collision collision = Bounds.resolveCollision(bounds, otherBounds);
                    if (collision == null) continue;
                    go.collision(collision);

                    // Flip the collision side for the other game object
                    collision.flip(go);

                    obj.collision(collision);
                }
            }
        }
    }
}
