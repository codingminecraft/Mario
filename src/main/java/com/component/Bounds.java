package com.component;

import com.jade.Component;
import com.physics.Collision;
import org.joml.Vector2f;

enum BoundsType {
    Box,
}

public abstract class Bounds extends Component {
    public BoundsType type;
    public boolean isStatic;

    /*
     * If true, then collisions with all dynamic objects are ignored, and sent as a 'trigger' instead
     *           Collisions with static objects will still occur though
     * If false, collisions will occur with all dynamic and static objects
     */
    protected boolean isTrigger;

    abstract public float getWidth();
    abstract public float getHeight();
    abstract public boolean raycast(Vector2f position);

    public static boolean checkCollision(Bounds b1, Bounds b2) {
        // We know that at least 1 bounds will always be a box
        if (b1.type == b2.type && b1.type == BoundsType.Box) {
            return BoxBounds.checkCollision((BoxBounds)b1, (BoxBounds)b2);
        }

        return false;
    }

    public static Collision resolveCollision(Bounds b1, Bounds b2) {
        if (b1.type == BoundsType.Box && b2.type == BoundsType.Box) {
            BoxBounds b1Bounds = (BoxBounds)b1;
            return b1Bounds.resolveCollision((BoxBounds)b2);
        }

        return null;
    }

    public boolean isTrigger() {
        return this.isTrigger;
    }
}
