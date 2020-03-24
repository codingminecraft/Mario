package com.jade;

import com.file.Serialize;
import com.physics.Collision;
import com.physics.Trigger;

import java.awt.Graphics2D;

public abstract class Component extends Serialize {

    public GameObject gameObject;

    public void update(double dt) {
        return;
    }

    public void start() {
        return;
    }

    public void collision(Collision coll) {
        return;
    }

    public void trigger(Trigger trigger) {
        return;
    }

    public abstract Component copy();
}
