package com.renderer;

import com.sun.javafx.geom.Vec2f;
import com.sun.javafx.geom.Vec4f;

public class Quad extends RenderComponent {
    private Vec4f color;
    private Vec2f[] texCoords = {
            new Vec2f(1.0f, 1.0f),
            new Vec2f(0.0f, 1.0f),
            new Vec2f(0.0f, 0.0f),
            new Vec2f(0.0f, 1.0f)
    };

    public Quad(Vec4f color) {
        this.color = color;
    }

    @Override
    public Vec4f getColor() {
        return this.color;
    }

    @Override
    public Vec2f[] getTexCoords() {
        return this.texCoords;
    }

    @Override
    public void setColor(float r, float g, float b, float a) {
        this.color.x = r;
        this.color.y = g;
        this.color.z = b;
        this.color.w = a;
    }
}
