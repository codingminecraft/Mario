package com.physics;

import com.component.Bounds;
import com.component.BoxBounds;
import com.jade.GameObject;
import org.joml.Vector2f;

public class Collision {
    public enum CollisionSide {
        TOP, LEFT, RIGHT, BOTTOM;
    }

    public GameObject gameObject;
    public CollisionSide side;
    public Vector2f contactPoint; // The center of the other collider on impact with this collider
    public BoxBounds bounds;

    public Collision(GameObject go, CollisionSide side, Vector2f contactPoint, BoxBounds bounds) {
        this.gameObject = go;
        this.side = side;
        this.contactPoint = contactPoint;
        this.bounds = bounds;
    }

    public void flip(GameObject go) {
        if (side == CollisionSide.LEFT || side == CollisionSide.RIGHT) {
            flipHorizontal(go);
        } else {
            flipVertical(go);
        }
        // Game object collision is colliding with
        this.bounds = go.getComponent(BoxBounds.class);
        this.gameObject = go;
    }

    private void flipHorizontal(GameObject otherGo) {
        this.contactPoint.y = otherGo.getComponent(BoxBounds.class).getCenterY();

        if (side == CollisionSide.RIGHT) {
            side = CollisionSide.LEFT;
        } else if (side == CollisionSide.LEFT) {
            side = CollisionSide.RIGHT;
        }
    }

    private void flipVertical(GameObject otherGo) {
        this.contactPoint.x = otherGo.getComponent(BoxBounds.class).getCenterX();

        if (side == CollisionSide.TOP) {
            side = CollisionSide.BOTTOM;
        } else if (side == CollisionSide.BOTTOM) {
            side = CollisionSide.TOP;
        }
    }
}
