package com.renderer;

import com.jade.GameObject;
import com.sun.javafx.geom.Vec2f;
import com.sun.javafx.geom.Vec4f;

public abstract class RenderComponent {
    public GameObject gameObject;
    public boolean isDirty = false;

    public abstract Vec4f getColor();
    public abstract void setColor(float r, float g, float b, float a);
    public abstract Vec2f[] getTexCoords();
}
