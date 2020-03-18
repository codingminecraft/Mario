package com.jade;

import com.file.Serialize;

import java.awt.Graphics2D;

public abstract class Component extends Serialize {

    public GameObject gameObject;

    public void update(double dt) {
        return;
    }

    public void start() {
        return;
    }

    public abstract Component copy();
}
