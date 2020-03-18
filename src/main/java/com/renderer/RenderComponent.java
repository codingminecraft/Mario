package com.renderer;

import com.jade.GameObject;
import com.jade.Object;
import org.joml.Vector2f;
import org.joml.Vector4f;

public abstract class RenderComponent {
    protected Vector2f[] texCoords = new Vector2f[] {
        new Vector2f(1.0f, 1.0f),
        new Vector2f(1.0f, 0.0f),
        new Vector2f(0.0f, 0.0f),
        new Vector2f(0.0f, 1.0f)
    };
    protected Vector4f color = new Vector4f(1.0f, 1.0f, 1.0f, 1.0f);
    protected Texture texture = null;

    public Object gameObject;
    public boolean isDirty = false;
    public boolean shouldDelete = false;

    public abstract RenderComponent copy();

    public void init(Vector2f[] texCoords, Vector4f color, Texture texture) {
        this.texture = texture;
        this.texCoords = texCoords;
        this.color = color;

        assert texCoords.length == 4 : "Rectangle must have 4 texture coordinates.";
    }

    public void delete() {
        this.shouldDelete = true;
    }
    public Vector4f getColor() {
        return this.color;
    }
    public Vector2f[] getTexCoords() {
        return this.texCoords;
    }
    public float getWidth() {
        return this.gameObject.transform.scale.x;
    }
    public float getHeight() {
        return this.gameObject.transform.scale.y;
    }
    public Texture getTexture() {
        return this.texture;
    }

    public void setColor(float r, float g, float b, float a) {
        this.color.x = r;
        this.color.y = g;
        this.color.z = b;
        this.color.w = a;
        this.isDirty = true;
    }
    public void setColor(Vector4f color) {
        this.isDirty = true;
        this.color.x = color.x;
        this.color.y = color.y;
        this.color.z = color.z;
        this.color.w = color.w;
    }
    public void setTexCoords(Vector2f[] texCoords) {
        this.texCoords = texCoords;
        this.isDirty = true;
    }
}
